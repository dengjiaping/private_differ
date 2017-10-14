package com.appgame.differ.module.find;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshFragment;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.NavigationInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.find.adapter.FindAdapter;
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

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * 发现fragment
 * Created by lzx on 2017/5/16.
 * 386707112@qq.com
 */

public class FindFragment extends BaseRefreshFragment<FindContract.Presenter, FindListInfo> implements FindContract.View {

    public static FindFragment newInstance() {
        return new FindFragment();
    }


    private FindAdapter mFindAdapter;
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
        super.initVariables();
        mRxPermissions = new RxPermissions(getActivity());
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mProgressView = (CircleProgressView) $(R.id.load_pro);

        setHasOptionsMenu(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFindAdapter = new FindAdapter(getActivity(), mRxPermissions, mPtrClassicFrameLayout);
        mRecyclerView.setAdapter(mFindAdapter);

        mRecyclerView.addOnScrollListener(new VideoScrollListener());

        DownloadManager.getImpl().initServiceConnectListener(() ->
                getActivity().runOnUiThread(() -> {
                    if (mFindAdapter != null)
                        mFindAdapter.notifyDataSetChanged();
                }));

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        mFindAdapter.startAllAutoTasks();
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
                mFindAdapter.notifyDataSetChanged();
            } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                AppInfoManager.getImpl().updateAppInfo();
                mFindAdapter.notifyDataSetChanged();
            }
        });

        mFindAdapter.setOnLoadMoreListener(() -> mPresenter.requestLoadMore(""));

        mPresenter.requestBanner(true);
    }

    @Override
    protected void lazyLoadData() {
        mPresenter.requestBanner(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDataSource != null) {
            if (mFindAdapter != null) {
                mFindAdapter.refreshAdapter();
            }
        }
    }

    @Override
    public void onRequestBannerSuccess(List<BannerEntity> bannerEntities) {
        mFindAdapter.setBannerEntities(bannerEntities);
        mPresenter.requestNavigation();
    }

    @Override
    public void onRequestNavigationSuccess(List<NavigationInfo> navigationInfos) {
        mFindAdapter.setNavigationInfos(navigationInfos);
        mPresenter.requestConventions("");
    }

    @Override
    public void onRequestConventionsSuccess(List<FindListInfo> findListInfos, int removeCount) {
        gone(mEmptyView);

        mDataSource = findListInfos;
        mFindAdapter.setDataList(mDataSource);
        mFindAdapter.notifyDataSetChanged();
        mFindAdapter.setShowLoadMore(mDataSource.size() + removeCount >= page_size && findListInfos.size() > 0);
    }

    @Override
    public void onLoadMoreSuccess(List<FindListInfo> listInfoList, int removeCount) {
        mDataSource.addAll(listInfoList);
        mFindAdapter.setDataList(mDataSource);
        mFindAdapter.notifyDataSetChanged();

        mFindAdapter.setShowLoadMore(mDataSource.size() + removeCount >= page_size);
        if (listInfoList.size() == 0) {
            mFindAdapter.setCanLoading(false);
            mFindAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mFindAdapter.setCanLoading(false);
            mFindAdapter.showLoadAllDataUI();
        } else {
            mFindAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showProgressUI(boolean isShow) {
        mProgressView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore) {
            mFindAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        } else {
            visible(mEmptyView);
            mEmptyView.initErrorUI();
            mFindAdapter.clear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }
}

