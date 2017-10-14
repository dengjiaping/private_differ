package com.appgame.differ.module.search.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.module.search.contract.GameClassContract;
import com.appgame.differ.module.search.model.SearchModel;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/8/10.
 */

public class GameClassPresenter extends RxPresenter<GameClassContract.View> implements GameClassContract.Presenter<GameClassContract.View> {

    private String position = "0";
    private boolean isMore;
    private SearchModel mSearchModel;

    public GameClassPresenter() {
        mSearchModel = new SearchModel();
    }

    @Override
    public void getGameClassList(String typeId) {
        position = "0";
        BaseSubscriber<List<RecommedInfo>> subscriber = mSearchModel.requestGameClass(typeId, position)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<RecommedInfo> info) {

                        LogUtil.i("list = " + info.size());

                        if (info.size() > 0) {
                            position = info.get(0).position;
                        }
                        isMore = info.size() >= page_size;
                        mView.requestGameClassListSuccess(info);
                    }
                });
        addSubscribe(subscriber);
    }


    @Override
    public void loadMoreGameClassList(String typeId) {
        if (isMore) {
            BaseSubscriber<List<RecommedInfo>> subscriber = mSearchModel.requestGameClass(typeId, position)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, true) {
                        @Override
                        public void onSuccess(List<RecommedInfo> info) {
                            if (info.size() > 0) {
                                position = info.get(0).position;
                            }
                            isMore = info.size() >= page_size;
                            mView.loadMoreGameClassListSuccess(info);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }


}
