package com.appgame.differ.module.daily.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.AppGameModel;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.daily.contract.DailyDetailContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

/**
 * Created by lzx on 2017/4/17.
 * 386707112@qq.com
 */

public class DailyDetailPresenter extends RxPresenter<DailyDetailContract.View> implements DailyDetailContract.Presenter<DailyDetailContract.View> {

    private AppGameModel mAppGameModel;

    public DailyDetailPresenter() {
        mAppGameModel = new AppGameModel();
    }

    @Override
    public void requestDailyDetail(String id) {
        BaseSubscriber<DailyInfo> subscriber = mAppGameModel.getArticleDetail(id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<DailyInfo>(mView, false) {
                    @Override
                    public void onSuccess(DailyInfo dailyInfo) {
                        mView.onDailyDetailSuccess(dailyInfo);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestCommentList(String target, String target_id, int position, boolean loadMore) {
        BaseSubscriber<List<DailyComment>> subscriber = mAppGameModel.requestDynamicComment(target, target_id, String.valueOf(position), String.valueOf(1000))
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<DailyComment>>(mView, false) {
                    @Override
                    public void onSuccess(List<DailyComment> dailyComments) {
                        mView.onRequestCommentSuccess(dailyComments);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestTagList(String target, String target_id, int position) {
        BaseSubscriber<List<TagsInfo>> subscriber = mAppGameModel.getTagsList(target, target_id, 1000)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<TagsInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<TagsInfo> tagsInfos) {
                        mView.onRequestTagList(tagsInfos);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void submitComment(String target, String target_id, String content) {
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
        if (!isLogin) {
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
                            mView.onSubmitCommentSuccess();
                        } else {
                            mView.showError("评论失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void putTags(String target, String target_id, String name) {
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
        if (!isLogin) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        if (TextUtils.isEmpty(name)) {
            mView.showError("标签不能为空", false);
            return;
        }
        if (name.length() < 2 || name.length() > 15) {
            mView.showError("标签长度为2到15个字符", false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.putTags(target, target_id, name)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.onPutTagsSuccess();
                        } else {
                            mView.showError("添加失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void putTagsThumb(String tag_id, final int type) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.putTagsThumb(tag_id, type)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.putTagsThumbSuccess(type);
                        } else {
                            mView.showError("操作失败", false);
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
                            mView.repliesCommentSuccess();
                        } else {
                            mView.showError("操作失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void submitCommentsThumb(final DailyComment dailyComment, final int position) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<Boolean> subscriber =
                mAppGameModel.postDiscussThumb(dailyComment.getComId(), dailyComment.getIs_thumb() == 0 ? 1 : 0)
                        .compose(RxUtils.rxSchedulerObservable())
                        .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                if (aBoolean) {
                                    mView.commentsThumbSuccess(dailyComment, position);
                                } else {
                                    mView.showError("操作失败", false);
                                }
                            }
                        });
        addSubscribe(subscriber);
    }

    @Override
    public void followUser(String follow_id, final String action) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mAppGameModel.followUser(follow_id, action)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.onFollowSuccess(action);
                        } else {
                            mView.showError("操作失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }
}
