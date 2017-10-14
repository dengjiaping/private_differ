package com.appgame.differ.module.daily;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshFragment;
import com.appgame.differ.bean.daily.DailyList;
import com.appgame.differ.bean.daily.DailyListInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.daily.adapter.DailyListAdapter;
import com.appgame.differ.module.daily.contract.DailyContract;
import com.appgame.differ.module.daily.presenter.DailyPresenter;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;

import java.util.ArrayList;

/**
 * differ日报
 * Created by xian on 2017/4/8.
 */
public class DailyListFragment extends BaseRefreshFragment<DailyContract.Presenter, DailyList> implements DailyContract.View {

    public static DailyListFragment newInstance(String target) {
        Bundle bundle = new Bundle();
        bundle.putString("target", target);
        DailyListFragment fragment = new DailyListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private LinearLayoutManager mLinearLayoutManager;
    private String target = "info";
    private CustomEmptyView mEmptyView;
    private int retryCount = 5;
    private DailyListAdapter mDailyListAdapter;
    private int position = 0;
    private CircleProgressView mProgressView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_differ_list;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DailyPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        target = getArguments().getString("target");
        mDataSource = new ArrayList<>();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mProgressView = (CircleProgressView) $(R.id.load_pro);

        mEmptyView.initBigUI();

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mDailyListAdapter = new DailyListAdapter(getActivity());
        mRecyclerView.setAdapter(mDailyListAdapter);

        visible(mProgressView);

        mPresenter.requestDailyList(target, position, false);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(mRecyclerView)) {
                    position++;
                    loading = true;
                    mPresenter.requestDailyList(target, position, false);
                }
            }
        });

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_DAILY_LIST_REFRESH)) {
                lazyLoadData();
            }
        });
    }

    @Override
    protected void lazyLoadData() {
        mDataSource.clear();
        position = 0;
        retryCount = 5;
        mPresenter.requestDailyList(target, position, false);
    }

    @Override
    public void onRequestSuccess(DailyListInfo dailyListInfo, boolean loadMore) {
        gone(mEmptyView);

        mDataSource.addAll(dailyListInfo.getList());
        if (dailyListInfo.getList().size() < 1 && retryCount > 0) {
            position++;
            mPresenter.requestDailyList(target, position, false);
            retryCount--;
        } else {
            mDailyListAdapter.setData(mDataSource, loadMore);
            mEmptyView.setVisibility(mDailyListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            mProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        mDailyListAdapter.clear();
        mEmptyView.initErrorUI();
        visible(mEmptyView);
        gone(mProgressView);
    }


}
