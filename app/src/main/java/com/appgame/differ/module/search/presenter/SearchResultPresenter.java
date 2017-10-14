package com.appgame.differ.module.search.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.mine.MineFollows;
import com.appgame.differ.module.search.contract.SearchResultContract;
import com.appgame.differ.module.search.model.SearchModel;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/6/14.
 */

public class SearchResultPresenter extends RxPresenter<SearchResultContract.View> implements SearchResultContract.Presenter<SearchResultContract.View> {

    private SearchModel mModel;

    public SearchResultPresenter() {

        mModel = new SearchModel();
    }

    private String gamePosition = "0";
    private String articlesPosition = "0";
    private String userPosition = "0";
    private boolean gameIsMore;
    private boolean articlesIsMore;
    private boolean userIsMore;

    @Override
    public void searchGameByKeyWords(String keywords) {
        gamePosition = "0";
        BaseSubscriber<List<RecommedInfo>> subscriber = mModel.searchGameByKeyWords(keywords, gamePosition)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<RecommedInfo> info) {
                        if (info.size() > 0)
                            gamePosition = info.get(0).position;
                        gameIsMore = info.size() >= page_size;
                        mView.onSearchGameSuccess(info);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMoreGameByKeyWords(String keywords) {
        if (gameIsMore) {
            BaseSubscriber<List<RecommedInfo>> subscriber = mModel.searchGameByKeyWords(keywords, gamePosition)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, true) {
                        @Override
                        public void onSuccess(List<RecommedInfo> info) {
                            if (info.size() > 0)
                                gamePosition = info.get(0).position;
                            gameIsMore = info.size() >= page_size;
                            mView.loadMoreGameSuccess(info);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadGameFinishAllData();
        }
    }

    @Override
    public void searchArticlesByKeyWords(String keywords) {
        articlesPosition = "0";
        BaseSubscriber<List<DailyInfo>> subscriber = mModel.searchArticlesByKeyWords(keywords, articlesPosition)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<DailyInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<DailyInfo> info) {
                        if (info.size() > 0)
                            articlesPosition = info.get(0).position;
                        articlesIsMore = info.size() >= page_size;
                        mView.onSearchArticlesSuccess(info);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMoreArticlesByKeyWords(String keywords) {
        if (articlesIsMore) {
            BaseSubscriber<List<DailyInfo>> subscriber = mModel.searchArticlesByKeyWords(keywords, articlesPosition)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<DailyInfo>>(mView, true) {
                        @Override
                        public void onSuccess(List<DailyInfo> info) {
                            if (info.size() > 0)
                                articlesPosition = info.get(0).position;
                            articlesIsMore = info.size() >= page_size;
                            mView.loadMoreArticlesSuccess(info);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadArticlesFinishAllData();
        }
    }

    @Override
    public void searchUserByKeyWords(String keywords) {
        userPosition = "0";
        BaseSubscriber<List<MineFollows>> subscriber = mModel.searchUserByKeyWords(keywords, userPosition)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<MineFollows>>(mView, false) {
                    @Override
                    public void onSuccess(List<MineFollows> info) {
                        if (info.size() > 0)
                            userPosition = info.get(0).position;
                        userIsMore = info.size() >= page_size;
                        mView.onSearchUserSuccess(info);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMoreUserByKeyWords(String keywords) {
        if (userIsMore) {
            BaseSubscriber<List<MineFollows>> subscriber = mModel.searchUserByKeyWords(keywords, userPosition)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<MineFollows>>(mView, true) {
                        @Override
                        public void onSuccess(List<MineFollows> info) {
                            if (info.size() > 0)
                                userPosition = info.get(0).position;
                            userIsMore = info.size() >= page_size;
                            mView.loadMoreUserSuccess(info);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadUserFinishAllData();
        }
    }
}
