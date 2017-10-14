package com.appgame.differ.module.find.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.NavigationInfo;
import com.appgame.differ.module.find.contract.FindContract;
import com.appgame.differ.module.find.model.FindModel;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.rx.RxUtils;
import com.appgame.differ.widget.banner.BannerEntity;

import java.util.ArrayList;
import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/5/24.
 * 386707112@qq.com
 */

public class FindPresenter extends RxPresenter<FindContract.View> implements FindContract.Presenter<FindContract.View> {

    private FindModel mFindModel;
    public String position = "0";
    public boolean isMore;

    public FindPresenter() {
        mFindModel = new FindModel();
    }

    @Override
    public void requestBanner(boolean isShowPro) {
        position = "0";
        BaseSubscriber<List<BannerEntity>> subscriber = mFindModel.requestBanner()
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<BannerEntity>>(mView, false) {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        mView.showProgressUI(isShowPro);
                    }

                    @Override
                    public void onSuccess(List<BannerEntity> bannerEntities) {
                        LogUtil.i("requestBanner = " + bannerEntities.size());
                        mView.showProgressUI(false);
                        mView.onRequestBannerSuccess(bannerEntities);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        mView.showProgressUI(false);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestNavigation() {
        position = "0";
        BaseSubscriber<List<NavigationInfo>> subscriber = mFindModel.requestNavigation()
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<NavigationInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<NavigationInfo> info) {
                        LogUtil.i("requestNavigation = "+info.size());
                        mView.onRequestNavigationSuccess(info);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        mView.showProgressUI(false);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestConventions(String target) {
        position = "0";
        BaseSubscriber<List<FindListInfo>> subscriber = mFindModel.requestConventions(position, target)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<FindListInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<FindListInfo> findListInfos) {
                        mView.showProgressUI(false);
                        int removeCount = 0;
                        List<FindListInfo> list = new ArrayList<>();
                        for (FindListInfo info : findListInfos) {
                            if (info.status == 1) {
                                list.add(info);
                            } else {
                                removeCount++;
                            }
                        }
                        if (list.size() > 0)
                            position = list.get(0).position;
                        isMore = list.size() + removeCount >= page_size;

                        LogUtil.i("requestConventions = "+findListInfos.size());

                        mView.onRequestConventionsSuccess(list, removeCount);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        mView.showProgressUI(false);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestLoadMore(String target) {
        if (isMore) {
            BaseSubscriber<List<FindListInfo>> subscriber = mFindModel.requestConventions(position, target)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<FindListInfo>>(mView, true) {
                        @Override
                        public void onSuccess(List<FindListInfo> findListInfos) {
                            int removeCount = 0;
                            List<FindListInfo> list = new ArrayList<>();
                            for (FindListInfo info : findListInfos) {
                                if (info.status == 1) {
                                    list.add(info);
                                } else {
                                    removeCount++;
                                }
                            }
                            if (list.size() > 0)
                                position = list.get(0).position;
                            isMore = list.size() + removeCount >= page_size;
                            mView.onLoadMoreSuccess(list, removeCount);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }
}
