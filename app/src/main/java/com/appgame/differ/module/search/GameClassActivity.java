package com.appgame.differ.module.search;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.search.adapter.SearchGameAdapter;
import com.appgame.differ.module.search.contract.GameClassContract;
import com.appgame.differ.module.search.presenter.GameClassPresenter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CustomEmptyView;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * 游戏分类
 * Created by lzx on 2017/5/25.
 * 386707112@qq.com
 */
public class GameClassActivity extends BaseActivity<GameClassContract.Presenter, RecommedInfo> implements GameClassContract.View {

    private String typeName;
    private RecyclerView mRecyclerView;
    private CustomEmptyView mEmptyView;
    private SearchGameAdapter mSearchGameAdapter;
    private String typeId;
    private RxPermissions mRxPermissions;
    private DownloadManager mDownloadManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_class;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyView.initBigUI();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mSearchGameAdapter = new SearchGameAdapter(this, mRxPermissions);
        mRecyclerView.setAdapter(mSearchGameAdapter);
    }

    @Override
    protected void initPresenter() {
        mPresenter = new GameClassPresenter();
        super.initPresenter();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        typeName = getIntent().getStringExtra("typeName");
        typeId = getIntent().getStringExtra("typeId");
        mRxPermissions = new RxPermissions(this);
        mDownloadManager = new DownloadManager();
        setBarTitleUI(typeName);

        mPresenter.getGameClassList(typeId);

        mDownloadManager.initServiceConnectListener(() -> runOnUiThread(() -> mSearchGameAdapter.notifyDataSetChanged()));

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        mSearchGameAdapter.startAllAutoTasks();
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
                mSearchGameAdapter.startAllAutoTasks();
            } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                AppInfoManager.getImpl().updateAppInfo();
                mSearchGameAdapter.notifyDataSetChanged();
            }
        });

        mSearchGameAdapter.setOnLoadMoreListener(() -> mPresenter.loadMoreGameClassList(typeId));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSearchGameAdapter != null) {
            mSearchGameAdapter.notifyDataSetChanged();
            if (mDataSource != null)
                mSearchGameAdapter.setShowLoadMore(mDataSource.size() >= page_size);
        }
    }

    @Override
    public void requestGameClassListSuccess(List<RecommedInfo> list) {
        mDataSource = list;
        BaseDiffUtilCallBack<RecommedInfo> callBack = new BaseDiffUtilCallBack<RecommedInfo>(mSearchGameAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getGameId().equals(newData.getGameId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack);
        mSearchGameAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mSearchGameAdapter);
        mSearchGameAdapter.setShowLoadMore(mDataSource.size() >= page_size);
        mEmptyView.setVisibility(mDataSource.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void loadMoreGameClassListSuccess(List<RecommedInfo> list) {
        mDataSource.addAll(list);
        BaseDiffUtilCallBack<RecommedInfo> callBack = new BaseDiffUtilCallBack<RecommedInfo>(mSearchGameAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getGameId().equals(newData.getGameId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mSearchGameAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mSearchGameAdapter);
        mRecyclerView.scrollToPosition(mDataSource.size() - page_size - 2);
        mSearchGameAdapter.setShowLoadMore(mDataSource.size() >= page_size);
        if (list.size() == 0) {
            mSearchGameAdapter.setCanLoading(false);
            mSearchGameAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        if (isLoadMore) {
            mSearchGameAdapter.setCanLoading(false);
        }
        visible(mEmptyView);
        ToastUtil.showShort(msg);
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mSearchGameAdapter.setCanLoading(false);
            mSearchGameAdapter.showLoadAllDataUI();
        } else {
            mSearchGameAdapter.setShowLoadMore(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }

}


