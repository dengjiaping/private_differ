package com.appgame.differ.module.personal.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.mine.MineFollows;
import com.appgame.differ.module.personal.contract.MineFollowsContract;
import com.appgame.differ.module.personal.model.FollowsAndFansModel;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public class MineFollowsPresenter extends RxPresenter<MineFollowsContract.View> implements MineFollowsContract.Presenter<MineFollowsContract.View> {

    public String position = "0";
    public boolean isMore;
    private FollowsAndFansModel mMineFollowsModel;

    public MineFollowsPresenter() {
        mMineFollowsModel = new FollowsAndFansModel();
    }

    @Override
    public void requestFollows(String action, String userId) {
        position = "0";
        BaseSubscriber<List<MineFollows>> subscriber = mMineFollowsModel.requestFollow(position, action, userId)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<MineFollows>>(mView, false) {

                    @Override
                    protected void onStart() {
                        super.onStart();
                        mView.showProgressUI();
                    }

                    @Override
                    public void onSuccess(List<MineFollows> followsList) {
                        if (followsList.size() > 0)
                            position = followsList.get(0).position;
                        isMore = followsList.size() > page_size;
                        mView.requestFollowSuccess(followsList);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMore(String action, String userId) {
        if (isMore) {
            BaseSubscriber<List<MineFollows>> subscriber = mMineFollowsModel.requestFollow(position, action, userId)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<MineFollows>>(mView, true) {
                        @Override
                        public void onSuccess(List<MineFollows> followsList) {
                            if (followsList.size() > 0)
                                position = followsList.get(0).position;
                            isMore = followsList.size() >= page_size;
                            mView.loadMoreSuccess(followsList);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }
}
