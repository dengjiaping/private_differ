package com.appgame.differ.module.daily.presenter;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.daily.DailyListInfo;
import com.appgame.differ.module.daily.contract.DailyContract;
import com.appgame.differ.utils.rx.RxUtils;

/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public class DailyPresenter extends RxPresenter<DailyContract.View> implements DailyContract.Presenter<DailyContract.View> {

    private AppGameModel mAppGameModel;

    public DailyPresenter() {
        mAppGameModel = new AppGameModel();
    }

    @Override
    public void requestDailyList(String target, int position, boolean isLoadMore) {
        BaseSubscriber<DailyListInfo> subscriber = mAppGameModel.getDailyList(target, position)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<DailyListInfo>(mView, isLoadMore) {
                    @Override
                    public void onSuccess(DailyListInfo dailyListInfo) {
                        mView.onRequestSuccess(dailyListInfo, isLoadMore);
                    }
                });
        addSubscribe(subscriber);
    }
}
