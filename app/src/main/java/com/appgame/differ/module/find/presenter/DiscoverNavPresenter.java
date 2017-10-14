package com.appgame.differ.module.find.presenter;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.find.DiscoverNavInfo;
import com.appgame.differ.module.find.contract.DiscoverNavContract;
import com.appgame.differ.utils.rx.RxUtils;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/5/27.
 * 386707112@qq.com
 */

public class DiscoverNavPresenter extends RxPresenter<DiscoverNavContract.View> implements DiscoverNavContract.Presenter<DiscoverNavContract.View> {


    private AppGameModel mAppGameModel;
    public String position = "0";
    public boolean isMore;

    public DiscoverNavPresenter() {

        mAppGameModel = new AppGameModel();
    }

    @Override
    public void requestDiscoverNavInfo(String id, String type) {
        position = "0";
        BaseSubscriber<DiscoverNavInfo> subscriber = mAppGameModel.requestDiscoverNavInfo(id, position)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<DiscoverNavInfo>(mView, false) {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        mView.showProgressUI();
                    }

                    @Override
                    public void onSuccess(DiscoverNavInfo info) {
                        position = info.position;
                        switch (type) {
                            case "game":
                                isMore = info.getRecommedInfos().size() >= page_size;
                                mView.onRequestGame(info.getRecommedInfos());
                                break;
                            case "article":
                                isMore = info.getDailyInfos().size() >= page_size;
                                mView.onRequestArticles(info.getDailyInfos());
                                break;
                            case "list":
                                isMore = info.getTopicInfos().size() >= page_size;
                                mView.onRequestTopicList(info.getTopicInfos());
                                break;
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMore(String id, String type) {
        if (isMore) {
            BaseSubscriber<DiscoverNavInfo> subscriber = mAppGameModel.requestDiscoverNavInfo(id, position)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<DiscoverNavInfo>(mView, true) {
                        @Override
                        public void onSuccess(DiscoverNavInfo info) {
                            position = info.position;
                            if (type.equals("game")) {
                                isMore = info.getRecommedInfos().size() >= page_size;
                                mView.loadMoreGameSuccess(info.getRecommedInfos());
                            } else if (type.equals("article")) {
                                isMore = info.getDailyInfos().size() >= page_size;
                                mView.loadMoreArticlesSuccess(info.getDailyInfos());
                            } else if (type.equals("list")) {
                                isMore = info.getTopicInfos().size() >= page_size;
                                mView.loadMoreTopicList(info.getTopicInfos());
                            }
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }


}
