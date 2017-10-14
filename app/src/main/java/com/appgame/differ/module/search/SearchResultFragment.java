package com.appgame.differ.module.search;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.mine.MineFollows;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.find.adapter.DiscoverNavArticlesAdapter;
import com.appgame.differ.module.personal.adapter.MineFollowsAdapter;
import com.appgame.differ.module.search.adapter.SearchGameAdapter;
import com.appgame.differ.module.search.contract.SearchResultContract;
import com.appgame.differ.module.search.presenter.SearchResultPresenter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CustomEmptyView;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/6/14.
 */
public class SearchResultFragment extends BaseFragment<SearchResultContract.Presenter, String> implements SearchResultContract.View {
    private String searchType = "游戏";
    private String keywords;

    public static SearchResultFragment newInstance(String searchType) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString("searchType", searchType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecyclerView mSearchResultRecycler;
    private SearchGameAdapter mSearchGameAdapter;
    private RxPermissions mRxPermissions;
    private CustomEmptyView mEmptyView;
    private DiscoverNavArticlesAdapter mArticlesAdapter;
    private MineFollowsAdapter mMineFollowsAdapter;

    private List<RecommedInfo> mGameList;
    private List<DailyInfo> mDailyList;
    private List<MineFollows> mUserList;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search_result;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new SearchResultPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        searchType = getArguments().getString("searchType");
        mRxPermissions = new RxPermissions(getActivity());
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mSearchResultRecycler = (RecyclerView) $(R.id.search_result_recycler);
        mSearchResultRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSearchResultRecycler.setHasFixedSize(true);
        mEmptyView.initBigUI();
        mSearchGameAdapter = new SearchGameAdapter(getActivity(), mRxPermissions);
        mArticlesAdapter = new DiscoverNavArticlesAdapter(getActivity());
        mMineFollowsAdapter = new MineFollowsAdapter(getActivity());
    }

    @Override
    protected void initDatas() {
        mSearchGameAdapter.setOnLoadMoreListener(() -> mPresenter.loadMoreGameByKeyWords(keywords));
        mArticlesAdapter.setOnLoadMoreListener(() -> mPresenter.loadMoreArticlesByKeyWords(keywords));
        mMineFollowsAdapter.setOnLoadMoreListener(() -> mPresenter.loadMoreUserByKeyWords(keywords));

        resetAdapter(searchType);
    }

    public void resetAdapter(String searchType) {
        if (TextUtils.isEmpty(searchType))
            return;
        switch (searchType) {
            case "游戏":
                initGame();
                break;
            case "文章":
                initArticle();
                break;
            case "玩家":
                initUser();
                break;
        }
    }

    public void setSearchTypeAndSearch(String searchType, String keywords) {
        this.searchType = searchType;
        this.keywords = keywords;
        if (TextUtils.isEmpty(searchType))
            return;
        switch (searchType) {
            case "游戏":
                mPresenter.searchGameByKeyWords(keywords);
                break;
            case "文章":
                mPresenter.searchArticlesByKeyWords(keywords);
                break;
            case "玩家":
                mPresenter.searchUserByKeyWords(keywords);
                break;
        }
    }

    private void initGame() {
        mSearchResultRecycler.setAdapter(mSearchGameAdapter);
        DownloadManager.getImpl().initServiceConnectListener(() -> getActivity().runOnUiThread(() -> mSearchGameAdapter.notifyDataSetChanged()));
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
    }

    private void initArticle() {
        mSearchResultRecycler.setAdapter(mArticlesAdapter);
    }

    private void initUser() {
        mSearchResultRecycler.setAdapter(mMineFollowsAdapter);
    }

    @Override
    public void onSearchGameSuccess(List<RecommedInfo> recommedInfos) {
        mGameList = recommedInfos;

        mSearchGameAdapter.clearAllData();
        mSearchGameAdapter.setDataList(mGameList);
        mSearchGameAdapter.notifyDataSetChanged();

        mSearchGameAdapter.setShowLoadMore(mGameList.size() >= page_size);

        if (recommedInfos.size() == 0) {
            mSearchGameAdapter.clearAllData();
            mSearchGameAdapter.notifyDataSetChanged();
            mSearchGameAdapter.setShowLoadMore(false);
        }
        mEmptyView.setVisibility(recommedInfos.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void loadMoreGameSuccess(List<RecommedInfo> recommedInfos) {
        mGameList.addAll(recommedInfos);

        BaseDiffUtilCallBack<RecommedInfo> callBack = new BaseDiffUtilCallBack<>(mSearchGameAdapter.getDataList(), mGameList);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getGameId().equals(newData.getGameId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mSearchGameAdapter.setDataList(mGameList);
        diffResult.dispatchUpdatesTo(mSearchGameAdapter);
        mSearchResultRecycler.scrollToPosition(mSearchGameAdapter.getItemCount() - page_size - 2);
        if (recommedInfos.size() == 0) {
            mSearchGameAdapter.setCanLoading(false);
            mSearchGameAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void onSearchArticlesSuccess(List<DailyInfo> dailyInfos) {
        mDailyList = dailyInfos;
        mArticlesAdapter.clearAllData();
        mArticlesAdapter.setDataList(mDailyList);
        mArticlesAdapter.notifyDataSetChanged();
        mArticlesAdapter.setShowLoadMore(mDailyList.size() >= page_size);

        if (dailyInfos.size() == 0) {
            mArticlesAdapter.clearAllData();
            mArticlesAdapter.notifyDataSetChanged();
            mArticlesAdapter.setShowLoadMore(false);
        }

        mEmptyView.setVisibility(dailyInfos.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void loadMoreArticlesSuccess(List<DailyInfo> dailyInfos) {
        mDailyList.addAll(dailyInfos);
        BaseDiffUtilCallBack<DailyInfo> callBack = new BaseDiffUtilCallBack<>(mArticlesAdapter.getDataList(), mDailyList);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getId().equals(newData.getId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mArticlesAdapter.setDataList(mDailyList);
        diffResult.dispatchUpdatesTo(mArticlesAdapter);
        mSearchResultRecycler.scrollToPosition(mArticlesAdapter.getItemCount() - page_size - 2);
        if (dailyInfos.size() == 0) {
            mArticlesAdapter.setCanLoading(false);
            mArticlesAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void onSearchUserSuccess(List<MineFollows> userInfos) {
        mUserList = userInfos;
        mMineFollowsAdapter.clearAllData();
        mMineFollowsAdapter.setDataList(mUserList);
        mMineFollowsAdapter.notifyDataSetChanged();
        mMineFollowsAdapter.setShowLoadMore(mUserList.size() >= page_size);

        if (userInfos.size() == 0) {
            mMineFollowsAdapter.clearAllData();
            mMineFollowsAdapter.notifyDataSetChanged();
            mMineFollowsAdapter.setShowLoadMore(false);
        }
        mEmptyView.setVisibility(userInfos.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void loadMoreUserSuccess(List<MineFollows> userInfos) {
        mUserList.addAll(userInfos);
        BaseDiffUtilCallBack<MineFollows> callBack = new BaseDiffUtilCallBack<>(mMineFollowsAdapter.getDataList(), mUserList);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getUserInfo().getUserId().equals(newData.getUserInfo().getUserId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mMineFollowsAdapter.setDataList(mUserList);
        diffResult.dispatchUpdatesTo(mMineFollowsAdapter);
        mSearchResultRecycler.scrollToPosition(mMineFollowsAdapter.getItemCount() - page_size - 2);
        if (userInfos.size() == 0) {
            mMineFollowsAdapter.setCanLoading(false);
            mMineFollowsAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore){
            if (TextUtils.isEmpty(searchType))
                return;
            switch (searchType) {
                case "游戏":
                    mSearchGameAdapter.setCanLoading(false);
                    break;
                case "文章":
                    mArticlesAdapter.setCanLoading(false);
                    break;
                case "玩家":
                    mMineFollowsAdapter.setCanLoading(false);
                    break;
            }
            ToastUtil.showShort("加载失败");
        }else {
            mEmptyView.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(searchType))
                return;
            switch (searchType) {
                case "游戏":
                    mSearchGameAdapter.clearAllData();
                    mSearchGameAdapter.notifyDataSetChanged();
                    mSearchGameAdapter.setShowLoadMore(false);
                    break;
                case "文章":
                    mArticlesAdapter.clearAllData();
                    mArticlesAdapter.notifyDataSetChanged();
                    mArticlesAdapter.setShowLoadMore(false);
                    break;
                case "玩家":
                    mMineFollowsAdapter.clearAllData();
                    mMineFollowsAdapter.notifyDataSetChanged();
                    mMineFollowsAdapter.setShowLoadMore(false);
                    break;
            }
        }
    }

    @Override
    public void loadGameFinishAllData() {
        if (mGameList.size() >= page_size) {
            mSearchGameAdapter.setCanLoading(false);
            mSearchGameAdapter.showLoadAllDataUI();
        } else {
            mSearchGameAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void loadArticlesFinishAllData() {
        if (mDailyList.size() >= page_size) {
            mArticlesAdapter.setCanLoading(false);
            mArticlesAdapter.showLoadAllDataUI();
        } else {
            mArticlesAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void loadUserFinishAllData() {
        if (mUserList.size() >= page_size) {
            mMineFollowsAdapter.setCanLoading(false);
            mMineFollowsAdapter.showLoadAllDataUI();
        } else {
            mMineFollowsAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchType.equals("游戏")) {
            if (mSearchGameAdapter != null) {
                mSearchGameAdapter.notifyDataSetChanged();
                mSearchGameAdapter.setShowLoadMore(false);
            }
        }
    }

    public void clear() {
        if (TextUtils.isEmpty(searchType))
            return;
        switch (searchType) {
            case "游戏":
                if (mSearchGameAdapter != null)
                    mSearchGameAdapter.clearAllData();
                break;
            case "文章":
                if (mArticlesAdapter != null)
                    mArticlesAdapter.clearAllData();
                break;
            case "玩家":
                if (mMineFollowsAdapter != null)
                    mMineFollowsAdapter.clearAllData();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }
}