package com.appgame.differ.module.mine;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshFragment;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.home.presenter.MyGameContract;
import com.appgame.differ.module.home.presenter.MyGamePresenter;
import com.appgame.differ.module.personal.adapter.MineGameAdapter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

/**
 * Created by lzx on 2017/5/27.
 * 386707112@qq.com
 */

public class InstalledFragment extends BaseRefreshFragment<MyGameContract.Presenter, String> implements MyGameContract.View {
    @Override
    public int getLayoutId() {
        return R.layout.layout_mine_game;
    }

    private String userId;
    private String type;

    public static InstalledFragment newInstance(String userId, String type) {
        InstalledFragment fragment = new InstalledFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private MineGameAdapter mMineGameAdapter;
    private String position = "0";
    private RxPermissions mRxPermissions;
    public FileDownloadConnectListener listener;
    private CircleProgressView mProgressView;
    private CustomEmptyView mEmptyView;

    @Override
    protected void initPresenter() {
        mPresenter = new MyGamePresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        userId = getArguments().getString("userId");
        type = getArguments().getString("type");
        if (TextUtils.isEmpty(userId)) {
            this.userId = UserInfoManager.getImpl().getUserId();
        }
        mRxPermissions = new RxPermissions(getActivity());
        mMineGameAdapter = new MineGameAdapter(getActivity(), mRxPermissions);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mProgressView = (CircleProgressView) $(R.id.load_pro);
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mEmptyView.initBigUI();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mMineGameAdapter.getItemViewType(position) == MineGameAdapter.TYPE_EMPTY) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mMineGameAdapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        GameDownloadManager.getImpl().uploadPhoneAppPackage(bindToLifecycle());
        DownloadManager.getImpl().initServiceConnectListener(() -> getActivity().runOnUiThread(() -> {
            mMineGameAdapter.notifyDataSetChanged();
        }));
        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        mMineGameAdapter.startAllAutoTasks();
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
                mMineGameAdapter.startAllAutoTasks();
            } else if (o instanceof String) {
                String event = (String) o;
                switch (event) {
                    case EvenConstant.KEY_RELOAD_APP_LIST:
                        AppInfoManager.getImpl().updateAppInfo();
                        mMineGameAdapter.notifyDataSetChanged();
                        break;
                    case EvenConstant.KEY_INCOGNITO_LOGIN_SUCCESS:
                    case EvenConstant.KEY_LOGIN_SUCCESS:
                        GameDownloadManager.getImpl().uploadPhoneAppPackage(bindToLifecycle());
                        break;
                    case EvenConstant.KEY_UPDATE_PKGS_SUCCESS:
                        mPresenter.getGameList("installed", position, userId, false);
                        break;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMineGameAdapter != null) {
            mMineGameAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void lazyLoadData() {
        super.lazyLoadData();
        this.userId = UserInfoManager.getImpl().getUserId();
        GameDownloadManager.getImpl().uploadPhoneAppPackage(bindToLifecycle());
        mPresenter.getGameList("installed", position, userId, false);
    }

    @Override
    public void onGameListSuccess(List<RecommedInfo> recommedInfos, boolean isLoadMore) {
        gone(mProgressView, mEmptyView);
        if (recommedInfos.size() == 0) {
            mPresenter.getRecommendList();
        } else {
            mMineGameAdapter.setInfoList(recommedInfos, isLoadMore, false);
        }
    }

    @Override
    public void onGetRecommendListSuccess(List<RecommedInfo> recommedInfos) {
        gone(mProgressView, mEmptyView);
        recommedInfos.add(new RecommedInfo());
        mMineGameAdapter.setInfoList(recommedInfos, false, true);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        gone(mProgressView);
        mEmptyView.initErrorUI();
        visible(mEmptyView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }
}
