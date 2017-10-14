package com.appgame.differ.module.dynamic.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.dynamic.contract.DynamicDetailContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

import okhttp3.ResponseBody;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 17/5/16.
 */

public class DynamicDetailPresenter extends RxPresenter<DynamicDetailContract.View> implements DynamicDetailContract.Presenter<DynamicDetailContract.View> {

    private AppGameModel mAppGameModel;
    public String position = "0";
    public boolean isMore;

    public DynamicDetailPresenter() {
        mAppGameModel = new AppGameModel();
    }

    @Override
    public void getDynamicDetail(String dynamic_id) {
        BaseSubscriber<DynamicInfo> subscriber = mAppGameModel.requestDynamicDetail(dynamic_id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<DynamicInfo>(mView, false) {
                    @Override
                    public void onSuccess(DynamicInfo dynamicInfo) {
                        mView.getDynamicDetailSuccess(dynamicInfo);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void getDynamicComment(String target, String target_id) {
        position = "0";
        BaseSubscriber<List<DailyComment>> subscriber = mAppGameModel.requestDynamicComment(target, target_id, position, String.valueOf(page_size))
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<DailyComment>>(mView, false) {
                    @Override
                    public void onSuccess(List<DailyComment> dailyComments) {
                        if (dailyComments.size() > 0)
                            position = dailyComments.get(0).position;
                        isMore = dailyComments.size() >= page_size;
                        mView.getDynamicCommentSuccess(dailyComments);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loadMoreDynamicComment(String target, String target_id) {
        if (isMore) {
            BaseSubscriber<List<DailyComment>> subscriber = mAppGameModel.requestDynamicComment(target, target_id, position, String.valueOf(page_size))
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<DailyComment>>(mView, true) {
                        @Override
                        public void onSuccess(List<DailyComment> dailyComments) {
                            if (dailyComments.size() > 0)
                                position = dailyComments.get(0).position;
                            isMore = dailyComments.size() >= page_size;
                            mView.loadMoreSuccess(dailyComments);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }

    @Override
    public void postComment(String target, String target_id, String content) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            mView.showError("评论内容不能为空", false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.postComment(target, target_id, content)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.postCommentSuccess();
                        } else {
                            mView.showError("评论失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            mView.showError("回复内容不能为空", false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.repliesComment(comment_id, content, is_replied, reply_id, reply_user_id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.postCommentSuccess();
                        } else {
                            mView.showError("回复失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void postDiscussThumb(String discuss_id, int type, int position) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.postDiscussThumb(discuss_id, type)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.postDiscussThumbSuccess(type, position);
                        } else {
                            mView.showError("操作失败", false);
                        }
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

    @Override
    public void deleteDynamic(String dynamic_id) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<ResponseBody> subscriber = mAppGameModel.deleteDynamic(dynamic_id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<ResponseBody>(mView, false) {
                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        mView.onDeleteDynamicSuccess();
                    }
                });
        addSubscribe(subscriber);
    }
}
