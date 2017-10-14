package com.appgame.differ.module.daily.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.game.TagsInfo;

import java.util.List;

/**
 * Created by lzx on 2017/4/17.
 * 386707112@qq.com
 */

public interface DailyDetailContract {

    interface View extends BaseContract.BaseView {
        void onDailyDetailSuccess(DailyInfo dailyInfo);

        void onSubmitCommentSuccess();

        void onRequestCommentSuccess(List<DailyComment> dailyComments);

        void onRequestTagList(List<TagsInfo> tagsInfos);

        void onPutTagsSuccess();

        void putTagsThumbSuccess(int type);

        void repliesCommentSuccess();

        void commentsThumbSuccess(DailyComment dailyComment, int position);

        void onFollowSuccess(String action);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        //获取文章详情
        void requestDailyDetail(String id);

        //获取评论列表
        void requestCommentList(String target, String target_id, int position, boolean loadMore);

        //获取标签列表
        void requestTagList(String target, String target_id, int position);

        //提交评论
        void submitComment(String target, String target_id, String content);

        //提交标签
        void putTags(String target, String target_id, String name);

        //顶标签
        void putTagsThumb(String tag_id, int type);

        //回复评论
        void repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id);

        //评论点赞
        void submitCommentsThumb(DailyComment dailyComment, int position);

        //关注/取消关注
        void followUser(String follow_id, String action);
    }
    /*
    interface View extends BaseView<DailyDetailContract.Presenter> {
        void onDailyDetailSuccess(DailyInfo dailyInfo);

        void onSubmitCommentSuccess();

        void onRequestCommentSuccess(List<DailyComment> dailyComments);

        void onRequestTagList(List<TagsInfo> tagsInfos);

        void onRequestError(String msg);

        void onPutTagsSuccess();

        void putTagsThumbSuccess(int type);

        void repliesCommentSuccess();

        void commentsThumbSuccess(DailyComment dailyComment, int position);

        <T> ObservableTransformer<T, T> bindToLifecycle();

        void onFollowSuccess(String action);
    }

    interface Presenter extends BasePresenter {
        //获取文章详情
        void requestDailyDetail(String id);

        //获取评论列表
        void requestCommentList(String target, String target_id, int position, boolean loadMore);

        //获取标签列表
        void requestTagList(String target, String target_id, int position);

        //提交评论
        void submitComment(String target, String target_id, String content);

        //提交标签
        void putTags(String target, String target_id, String name);

        //顶标签
        void putTagsThumb(String tag_id, int type);

        //回复评论
        void repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id);

        //评论点赞
        void submitCommentsThumb(DailyComment dailyComment, int position);

        //关注/取消关注
        void followUser(String follow_id, String action);
    }*/
}
