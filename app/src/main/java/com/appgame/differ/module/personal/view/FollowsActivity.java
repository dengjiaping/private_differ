package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshActivity;
import com.appgame.differ.bean.mine.MineFollows;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.personal.adapter.MineFollowsAdapter;
import com.appgame.differ.module.personal.contract.MineFollowsContract;
import com.appgame.differ.module.personal.presenter.MineFollowsPresenter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * 我的关注
 * Created by lzx on 2017/2/27.
 * 386707112@qq.com
 */

public class FollowsActivity extends BaseRefreshActivity<MineFollowsContract.Presenter, MineFollows> implements MineFollowsContract.View {

    private String action = "";
    private String userId = "";
    private CustomEmptyView mEmptyView;
    private CircleProgressView mProgressView;
    private MineFollowsAdapter mFollowsAdapter;
    private boolean mIsRefreshing = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MineFollowsPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mProgressView = (CircleProgressView) findViewById(R.id.load_pro);

        mEmptyView.initBigUI();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFollowsAdapter = new MineFollowsAdapter(this);
        mRecyclerView.setAdapter(mFollowsAdapter);

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = getIntent().getStringExtra("action");
        userId = getIntent().getStringExtra("userId");

        refreshData();

        mFollowsAdapter.setOnLoadMoreListener(() -> {
            mPresenter.loadMore(action, userId);
        });
        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_REFRESH_FANS_LIST)) {
                mPresenter.requestFollows(action, userId);
            }
        });
    }

    @Override
    protected void refreshData() {
        mPresenter.requestFollows(action, userId);
    }

    @Override
    public void requestFollowSuccess(List<MineFollows> mineFollows) {
        gone(mProgressView);
        mDataSource = mineFollows;
        BaseDiffUtilCallBack<MineFollows> callBack = new BaseDiffUtilCallBack<>(mFollowsAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getUserInfo().getUserId().equals(newData.getUserInfo().getUserId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack);
        mFollowsAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mFollowsAdapter);
        mFollowsAdapter.setShowLoadMore(mDataSource.size() >= page_size);
        if (mFollowsAdapter.getDataList().size() == 0) {
            visible(mEmptyView);
        } else {
            gone(mEmptyView);
        }
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mFollowsAdapter.setCanLoading(false);
            mFollowsAdapter.showLoadAllDataUI();
        } else {
            mFollowsAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void loadMoreSuccess(List<MineFollows> mineFollows) {
        gone(mProgressView);
        mDataSource.addAll(mineFollows);
        BaseDiffUtilCallBack<MineFollows> callBack = new BaseDiffUtilCallBack<>(mFollowsAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getUserInfo().getUserId().equals(newData.getUserInfo().getUserId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mFollowsAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mFollowsAdapter);
        mRecyclerView.scrollToPosition(mFollowsAdapter.getItemCount() - page_size - 2);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        gone(mProgressView);
        if (isLoadMore) {
            mFollowsAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        } else {
            visible(mEmptyView);
            if (msg.equals("10021")) {
                mEmptyView.initBigUI().setBigUI(R.drawable.ic_privacy_dis, "对方设置了隐私权限");
            } else {
                mEmptyView.initErrorUI().setErrorTitle(msg);
            }
        }
    }

    @Override
    public void showProgressUI() {
        visible(mProgressView);
    }
}
