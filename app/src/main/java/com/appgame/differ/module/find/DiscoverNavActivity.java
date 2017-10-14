package com.appgame.differ.module.find;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshActivity;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.find.adapter.DiscoverNavArticlesAdapter;
import com.appgame.differ.module.find.contract.DiscoverNavContract;
import com.appgame.differ.module.find.presenter.DiscoverNavPresenter;
import com.appgame.differ.module.search.adapter.SearchGameAdapter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CustomEmptyView;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/5/27.
 * 386707112@qq.com
 */
public class DiscoverNavActivity extends BaseRefreshActivity<DiscoverNavContract.Presenter, String> implements DiscoverNavContract.View {

    private CustomEmptyView mEmptyView;
    private SearchGameAdapter mGameAdapter;
    private DiscoverNavArticlesAdapter mArticlesAdapter;
    private RxPermissions mRxPermissions;
    private String id, type, title;
    private DownloadManager mDownloadManager;
    private List<RecommedInfo> mGameList;
    private List<DailyInfo> mDailyList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_discover_nav;
    }

    public static Intent launch(Context context, String id, String title, String type) {
        Intent mIntent = new Intent(context, DiscoverNavActivity.class);
        mIntent.putExtra("id", id);
        mIntent.putExtra("title", title);
        mIntent.putExtra("type", type);
        return mIntent;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DiscoverNavPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);

        mEmptyView.initBigUI();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGameAdapter = new SearchGameAdapter(this, mRxPermissions);
        mArticlesAdapter = new DiscoverNavArticlesAdapter(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
        title = getIntent().getStringExtra("title");

        setBarTitleUI(title);

        mDownloadManager = new DownloadManager();
        mRxPermissions = new RxPermissions(this);

        if (type.equals("game")) {
            mRecyclerView.setAdapter(mGameAdapter);
            mDownloadManager.initServiceConnectListener(() -> runOnUiThread(() -> {
                if (mGameAdapter != null)
                    mGameAdapter.notifyDataSetChanged();
            }));
            RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
                if (o instanceof NetWorkEvent) {
                    NetWorkEvent event = (NetWorkEvent) o;
                    if (event.isNetWorkConnected) {
                        if (event.isWifi) {
                            mGameAdapter.startAllAutoTasks();
                        } else {
                            FileDownloader.getImpl().pauseAll();
                            ToastUtil.showShort("当前处于移动网络，wifi自动下载");
                        }
                    } else {
                        FileDownloader.getImpl().pauseAll();
                        ToastUtil.showShort(getString(R.string.network_time_out));
                    }
                    mGameAdapter.notifyDataSetChanged();
                } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                    AppInfoManager.getImpl().updateAppInfo();
                    mGameAdapter.notifyDataSetChanged();
                }
            });
            mGameAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore(id, type));
        } else if (type.equals("article")) {
            mRecyclerView.setAdapter(mArticlesAdapter);
            mArticlesAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore(id, type));
        }
        mPresenter.requestDiscoverNavInfo(id, type);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (type.equals("game")) {
            if (mGameAdapter != null) {
                mGameAdapter.notifyDataSetChanged();
                if (mGameList != null)
                    mGameAdapter.setShowLoadMore(mGameList.size() >= page_size);
            }
        }
    }

    @Override
    public void onRequestGame(List<RecommedInfo> infoList) {
        mGameList = infoList;
        mGameAdapter.setDataList(mGameList);
        mGameAdapter.notifyDataSetChanged();
        mGameAdapter.setShowLoadMore(mGameList.size() >= page_size && infoList.size() > 0);
    }

    @Override
    public void onRequestArticles(List<DailyInfo> dailyInfos) {
        mDailyList = dailyInfos;
        mArticlesAdapter.setDataList(mDailyList);
        mArticlesAdapter.notifyDataSetChanged();
        mArticlesAdapter.setShowLoadMore(mDailyList.size() >= page_size && dailyInfos.size() > 0);
    }

    @Override
    public void onRequestTopicList(List<TopicInfo> topicInfos) {

    }

    @Override
    public void loadMoreGameSuccess(List<RecommedInfo> infoList) {
        mGameList.addAll(infoList);
        mGameAdapter.setDataList(mGameList);
        mGameAdapter.notifyDataSetChanged();
        mGameAdapter.setShowLoadMore(mGameList.size() >= page_size);
        if (infoList.size() == 0) {
            mGameAdapter.setCanLoading(false);
            mGameAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void loadMoreArticlesSuccess(List<DailyInfo> dailyInfos) {
        mDailyList.addAll(dailyInfos);
        mArticlesAdapter.setDataList(mDailyList);
        mArticlesAdapter.notifyDataSetChanged();
        mArticlesAdapter.setShowLoadMore(mDailyList.size() >= page_size);
        if (dailyInfos.size() == 0) {
            mArticlesAdapter.setCanLoading(false);
            mArticlesAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void loadMoreTopicList(List<TopicInfo> topicInfos) {

    }

    @Override
    public void loadFinishAllData() {
        if (type.equals("game")) {
            if (mGameList.size() >= page_size) {
                mGameAdapter.setCanLoading(false);
                mGameAdapter.showLoadAllDataUI();
            } else {
                mGameAdapter.setShowLoadMore(false);
            }
        } else if (type.equals("article")) {
            if (mDailyList.size() >= page_size) {
                mArticlesAdapter.setCanLoading(false);
                mArticlesAdapter.showLoadAllDataUI();
            } else {
                mArticlesAdapter.setShowLoadMore(false);
            }
        }
    }

    @Override
    public void showProgressUI() {

    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore) {
            if (type.equals("game")) {
                mGameAdapter.setCanLoading(false);
            } else if (type.equals("article")) {
                mArticlesAdapter.setCanLoading(false);
            }
            ToastUtil.showShort("加载失败");
        } else {
            visible(mEmptyView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDownloadManager.clearFileDownloadConnectListener();
    }
}

