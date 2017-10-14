package com.appgame.differ.module.find;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshFragment;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.NavigationInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.find.adapter.VideoAdapter;
import com.appgame.differ.module.find.contract.FindContract;
import com.appgame.differ.module.find.presenter.FindPresenter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.VideoScrollListener;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.banner.BannerEntity;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/6/22.
 */

public class VideoFragment extends BaseRefreshFragment<FindContract.Presenter, FindListInfo> implements FindContract.View {

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }


    private VideoAdapter mVideoAdapter;
    private RxPermissions mRxPermissions;
    private CustomEmptyView mEmptyView;
    private CircleProgressView mProgressView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new FindPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        mRxPermissions = new RxPermissions(getActivity());
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);
        mProgressView = (CircleProgressView) $(R.id.load_pro);

        mEmptyView.initBigUI();

        initRefreshLayout();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mVideoAdapter = new VideoAdapter(getActivity(), mRxPermissions);
        mRecyclerView.setAdapter(mVideoAdapter);

        mRecyclerView.addOnScrollListener(new VideoScrollListener());

        DownloadManager.getImpl().initServiceConnectListener(() -> getActivity().runOnUiThread(() -> {
            if (mVideoAdapter != null)
                mVideoAdapter.notifyDataSetChanged();
        }));

        RxBus.getBus().toMainThreadObservable(bindUntilEvent(FragmentEvent.STOP)).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        mVideoAdapter.startAllAutoTasks();
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
                mVideoAdapter.notifyDataSetChanged();
            } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                AppInfoManager.getImpl().updateAppInfo();
                mVideoAdapter.notifyDataSetChanged();
            } else if (o.equals(EvenConstant.KEY_RELEASE_FIND_VIDEOS)) {
                JCVideoPlayer.releaseAllVideos();
            }
        });

        mVideoAdapter.setOnLoadMoreListener(() -> mPresenter.requestLoadMore("video"));

        mPresenter.requestConventions("video");
    }

    @Override
    protected void lazyLoadData() {
        mPresenter.requestConventions("video");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDataSource != null) {
            if (mVideoAdapter != null) {
                mVideoAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestBannerSuccess(List<BannerEntity> bannerEntities) {
        //DO NOTING...
    }

    @Override
    public void onRequestNavigationSuccess(List<NavigationInfo> navigationInfos) {
        //DO NOTING...
    }

    @Override
    public void onRequestConventionsSuccess(List<FindListInfo> findListInfos, int removeCount) {
        gone(mEmptyView);
        mDataSource = findListInfos;
        mVideoAdapter.setDataList(mDataSource);
        mVideoAdapter.notifyDataSetChanged();
        mVideoAdapter.setShowLoadMore(mDataSource.size() + removeCount >= page_size);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore) {
            mVideoAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        } else {
            mVideoAdapter.clearAllData();
            visible(mEmptyView);
            mEmptyView.initErrorUI();
        }
    }

    @Override
    public void onLoadMoreSuccess(List<FindListInfo> listInfoList, int removeCount) {
        mDataSource.addAll(listInfoList);
        mVideoAdapter.setDataList(mDataSource);
        mVideoAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mVideoAdapter.setCanLoading(false);
            mVideoAdapter.showLoadAllDataUI();
        } else {
            mVideoAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showProgressUI(boolean isShow) {
        mProgressView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }

}
