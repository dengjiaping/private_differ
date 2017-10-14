package com.appgame.differ.module.personal.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.module.personal.contract.MineFansContract;
import com.appgame.differ.module.personal.model.FollowsAndFansModel;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public class MineFansPresenter extends RxPresenter<MineFansContract.View> implements MineFansContract.Presenter<MineFansContract.View> {

    public String position = "0";
    public boolean isMore;
    private FollowsAndFansModel mFansModel;

    public MineFansPresenter() {
        mFansModel = new FollowsAndFansModel();
    }

    @Override
    public void requestFans(String action, String userId) {
        position = "0";
        BaseSubscriber<List<UserInfo>> subscriber = mFansModel.requestFans(position, action, userId)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<UserInfo>>(mView, false) {

                    @Override
                    protected void onStart() {
                        super.onStart();
                        mView.showProgressUI();
                    }

                    @Override
                    public void onSuccess(List<UserInfo> datalist) {
                        if (datalist.size() > 0)
                            position = datalist.get(0).position;
                        isMore = datalist.size() > page_size;
                        mView.requestFansSuccess(datalist);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMore(String action, String userId) {
        if (isMore) {
            BaseSubscriber<List<UserInfo>> subscriber = mFansModel.requestFans(position, action, userId)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<UserInfo>>(mView, true) {

                        @Override
                        public void onSuccess(List<UserInfo> datalist) {
                            if (datalist.size() > 0)
                                position = datalist.get(0).position;
                            isMore = datalist.size() >= page_size;
                            mView.loadMoreSuccess(datalist);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }
}
