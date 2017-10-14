package com.appgame.differ.module.topic;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshActivity;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.module.find.contract.DiscoverNavContract;
import com.appgame.differ.module.find.presenter.DiscoverNavPresenter;
import com.appgame.differ.module.topic.adapter.TopicListAdapter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/7/4.
 */

public class TopicListActivity extends BaseRefreshActivity<DiscoverNavContract.Presenter,TopicInfo> implements DiscoverNavContract.View {

    private TopicListAdapter mTopicListAdapter;
    private CircleProgressView mProgressView;
    private CustomEmptyView mEmptyView;
    private String id, type;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_list;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DiscoverNavPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mProgressView = (CircleProgressView) findViewById(R.id.load_pro);
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);

        mEmptyView.initBigUI();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mTopicListAdapter = new TopicListAdapter(this);
        mRecyclerView.setAdapter(mTopicListAdapter);

        mTopicListAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore(id, type));
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");

        refreshData();
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        mPresenter.requestDiscoverNavInfo(id, type);
    }

    @Override
    public void onRequestGame(List<RecommedInfo> infoList) {

    }

    @Override
    public void onRequestArticles(List<DailyInfo> dailyInfos) {

    }

    @Override
    public void onRequestTopicList(List<TopicInfo> topicInfos) {
        mDataSource = topicInfos;
        mTopicListAdapter.setDataList(mDataSource);
        mTopicListAdapter.notifyDataSetChanged();

        mTopicListAdapter.setShowLoadMore(mDataSource.size() >= page_size && topicInfos.size() > 0);
    }

    @Override
    public void loadMoreGameSuccess(List<RecommedInfo> infoList) {

    }

    @Override
    public void loadMoreArticlesSuccess(List<DailyInfo> dailyInfos) {

    }

    @Override
    public void loadMoreTopicList(List<TopicInfo> topicInfos) {
        mDataSource.addAll(topicInfos);
        mTopicListAdapter.setDataList(mDataSource);
        mTopicListAdapter.notifyDataSetChanged();
        mTopicListAdapter.setShowLoadMore(mDataSource.size() >= page_size);
        if (topicInfos.size() == 0) {
            mTopicListAdapter.setCanLoading(false);
            mTopicListAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mTopicListAdapter.setCanLoading(false);
            mTopicListAdapter.showLoadAllDataUI();
        } else {
            mTopicListAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showProgressUI() {

    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if(isLoadMore){
            mTopicListAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        }else {
          visible(mEmptyView);
        }
    }

}
