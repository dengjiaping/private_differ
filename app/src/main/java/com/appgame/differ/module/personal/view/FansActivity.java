package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshActivity;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.personal.adapter.MineFansAdapter;
import com.appgame.differ.module.personal.contract.MineFansContract;
import com.appgame.differ.module.personal.presenter.MineFansPresenter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * 我的粉丝
 * Created by lzx on 2017/2/27.
 * 386707112@qq.com
 */

public class FansActivity extends BaseRefreshActivity<MineFansContract.Presenter, UserInfo> implements MineFansContract.View {
    private CustomEmptyView mEmptyView;
    private CircleProgressView mProgressView;
    private MineFansAdapter mFansAdapter;

    private String action = "";
    private String userId = "";

   // private boolean mIsRefreshing = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MineFansPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mProgressView = (CircleProgressView) findViewById(R.id.load_pro);

        mEmptyView.initBigUI();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(FansActivity.this));
        mFansAdapter = new MineFansAdapter(FansActivity.this);
        mRecyclerView.setAdapter(mFansAdapter);
      //  mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = getIntent().getStringExtra("action");
        userId = getIntent().getStringExtra("userId");

        refreshData();

        mFansAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore(action, userId));

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_REFRESH_FANS_LIST)) {
                refreshData();
            }
        });
    }

    @Override
    protected void refreshData() {
        mPresenter.requestFans(action, userId);
    }

    @Override
    public void requestFansSuccess(List<UserInfo> userInfos) {
        gone(mProgressView);
        mDataSource = userInfos;
        BaseDiffUtilCallBack<UserInfo> callBack = new BaseDiffUtilCallBack<>(mFansAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getUserId().equals(newData.getUserId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack);
        mFansAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mFansAdapter);

        mFansAdapter.setShowLoadMore(mDataSource.size() >= page_size);

        if (mFansAdapter.getDataList().size() == 0) {
            visible(mEmptyView);
        } else {
            gone(mEmptyView);
        }
    }

    @Override
    public void loadMoreSuccess(List<UserInfo> userInfos) {
        gone(mProgressView);
        mDataSource.addAll(userInfos);
        BaseDiffUtilCallBack<UserInfo> callBack = new BaseDiffUtilCallBack<>(mFansAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getUserId().equals(newData.getUserId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mFansAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mFansAdapter);
        mRecyclerView.scrollToPosition(mFansAdapter.getItemCount() - page_size - 2);
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mFansAdapter.setCanLoading(false);
            mFansAdapter.showLoadAllDataUI();
        } else {
            mFansAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore) {
            gone(mProgressView);
            mFansAdapter.setCanLoading(false);
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
