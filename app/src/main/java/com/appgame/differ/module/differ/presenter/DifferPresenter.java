package com.appgame.differ.module.differ.presenter;

import android.view.View;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.differ.contract.DifferContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;


/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public class DifferPresenter extends RxPresenter<DifferContract.View> implements DifferContract.Presenter<DifferContract.View> {

    private AppGameModel mAppGameModel;

    public DifferPresenter() {
        mAppGameModel = new AppGameModel();
    }

    @Override
    public void requestRecommendList() {
        boolean isFirstOpen = SpUtil.getInstance().getBoolean(AppConstants.IS_FIRST_OPEN, true);
        if (isFirstOpen) {
            requestRecommendListImpl("");
        } else if (!CommonUtil.isLogin()) {
            requestRecommendListImpl("");
        } else {
            requestRecommendListImpl("");
        }
    }

    private void requestRecommendListImpl(String ids) {
        BaseSubscriber<List<RecommedInfo>> subscriber = mAppGameModel.getRecommendList(ids, "")
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<RecommedInfo> infoList) {
                        SpUtil.getInstance().putBoolean(AppConstants.IS_FIRST_OPEN, false);
                        mView.onRequestSuccess(infoList);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        SpUtil.getInstance().putBoolean(AppConstants.IS_FIRST_OPEN, false);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void unLikeGame(String gameId, View view, int position, boolean isSwiped) {
        BaseSubscriber<Boolean> subscriber = mAppGameModel.collectionGame(gameId, "0")
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.unLikeSuccess(view, position, isSwiped);
                        } else {
                            mView.showError("操作失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }


}
