package com.appgame.differ.module.game.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.evaluation.EvaluationInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.game.contract.EvaDetailContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

/**
 * Created by lzx on 17/4/20.
 */

public class EvaDetailPresenter extends RxPresenter<EvaDetailContract.View> implements EvaDetailContract.Presenter<EvaDetailContract.View> {

    private AppGameModel mAppGameModel;

    public EvaDetailPresenter() {
        mAppGameModel = new AppGameModel();
    }

    @Override
    public void getDiscussList(String target, String target_id, String position, int page_size, boolean isLoadMore) {
        BaseSubscriber<EvaluationInfo> subscriber = mAppGameModel.getDiscussList(target, target_id, position, page_size)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<EvaluationInfo>(mView, false) {
                    @Override
                    public void onSuccess(EvaluationInfo evaluationInfo) {
                        mView.onCommentListSuccess(evaluationInfo, isLoadMore);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void getTagList(String target, String target_id, final int position) {
        BaseSubscriber<List<TagsInfo>> subscriber = mAppGameModel.getTagsList(target, target_id, 100)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<TagsInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<TagsInfo> tagsInfos) {
                        mView.onTagListSuccess(tagsInfos, position);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void postComment(String target, String target_id, String content) {
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
        if (!isLogin) {
            mView.onRequestError(AppConstants.NOT_LOGIN);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            mView.onRequestError("评论内容不能为空");
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.postComment(target, target_id, content)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.onPostCommentSuccess();
                        } else {
                            mView.onRequestError("评论失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void postTags(String target, String target_id, String name) {
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
        if (!isLogin) {
            mView.onRequestError(AppConstants.NOT_LOGIN);
            return;
        }
        if (TextUtils.isEmpty(name)) {
            mView.onRequestError("标签不能为空");
            return;
        } else if (name.length() > 15) {
            mView.onRequestError("请输入15字以内的标签");
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.putTags(target, target_id, name)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.onPostTagsSuccess();
                        } else {
                            mView.onRequestError("添加失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void postDiscussThumb(String discuss_id, final int type, final int position) {
        if (!CommonUtil.isLogin()) {
            mView.onRequestError(AppConstants.NOT_LOGIN);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.postDiscussThumb(discuss_id, type)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.postLikeClickSuccess(type, position);
                        } else {
                            mView.onRequestError("操作失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void postTagsThumb(String tags_id, final int type, final int position) {
        if (!CommonUtil.isLogin()) {
            mView.onRequestError(AppConstants.NOT_LOGIN);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.putTagsThumb(tags_id, type)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.postTagsThumbSuccess(type, position);
                        } else {
                            mView.onRequestError("操作失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id) {
        if (!CommonUtil.isLogin()) {
            mView.onRequestError(AppConstants.NOT_LOGIN);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            mView.onRequestError("回复内容不能为空");
            return;
        }

        BaseSubscriber<Boolean> subscriber = mAppGameModel.repliesComment(comment_id, content, is_replied, reply_id, reply_user_id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.repliesCommentSuccess();
                        } else {
                            mView.onRequestError("操作失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void thumbEvaluation(String comment_id, int type, int position) {
        if (!CommonUtil.isLogin()) {
            mView.onRequestError(AppConstants.NOT_LOGIN);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.thumbEvaluation(comment_id, type)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.thumbEvaluationSuccess(type, position);
                        } else {
                            ToastUtil.showShort("操作失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }
}
