package com.appgame.differ.module.dynamic.presenter;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.dynamic.contract.DynamicContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 17/5/12.
 */

public class DynamicPresenter extends RxPresenter<DynamicContract.View> implements DynamicContract.Presenter<DynamicContract.View> {

    private AppGameModel mAppGameModel;
    public String position = "0";
    public boolean isMore;

    public DynamicPresenter() {
        mAppGameModel = new AppGameModel();
    }

    @Override
    public void getDynamic(String user_id, boolean isShowPro) {
        position = "0";
        BaseSubscriber<List<DynamicInfo>> subscriber = mAppGameModel.getDynamic(user_id, position)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<DynamicInfo>>(mView, false) {

                    @Override
                    protected void onStart() {
                        super.onStart();
                        mView.showProgressUI(isShowPro);
                    }

                    @Override
                    public void onSuccess(List<DynamicInfo> infoList) {
                        mView.showProgressUI(false);
                        if (infoList.size() > 0)
                            position = infoList.get(0).postion;

                        int removeCount = 0;
                        List<DynamicInfo> list = new ArrayList<>();
                        for (DynamicInfo info : infoList) {
                            if (!info.getIsChecked().equals("0")) {
                                list.add(info);
                            } else {
                                removeCount++;
                            }
                        }
                        isMore = CommonUtil.isLogin() && (list.size() + removeCount) >= page_size;
                        mView.onGetDynamicSuccess(list, removeCount);
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
    public void loadMore(String user_id) {
        if (isMore) {
            BaseSubscriber<List<DynamicInfo>> subscriber = mAppGameModel.getDynamic(user_id, position)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<DynamicInfo>>(mView, true) {
                        @Override
                        public void onSuccess(List<DynamicInfo> infoList) {
                            if (infoList.size() > 0)
                                position = infoList.get(0).postion;
                            int removeCount = 0;
                            List<DynamicInfo> list = new ArrayList<>();
                            for (DynamicInfo info : infoList) {
                                if (!info.getIsChecked().equals("0")) {
                                    list.add(info);
                                } else {
                                    removeCount++;
                                }
                            }
                            isMore = CommonUtil.isLogin() && (list.size() + removeCount) >= page_size;
                            mView.loadMoreSuccess(list, removeCount);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }

    @Override
    public void deleteDynamic(String dynamic_id, int position) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<ResponseBody> subscriber = mAppGameModel.deleteDynamic(dynamic_id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<ResponseBody>(mView, false) {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        mView.onDeleteDynamicSuccess(position);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void thumbDynamic(String dynamic_id, int type, int position, String thumbType) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<Boolean> subscriber;
        if (thumbType.equals("appraise")) {
            subscriber = mAppGameModel.thumbEvaluation(dynamic_id, type)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                mView.thumbDynamicSuccess(type, position);
                            } else {
                                mView.showError("操作失败", false);
                            }
                        }
                    });
        } else {
            subscriber = mAppGameModel.thumbDynamic(dynamic_id, type)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            if (aBoolean) {
                                mView.thumbDynamicSuccess(type, position);
                            } else {
                                mView.showError("操作失败", false);
                            }
                        }
                    });
        }
        addSubscribe(subscriber);
    }

}
