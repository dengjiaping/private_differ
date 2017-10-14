package com.appgame.differ.module.game.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.evaluation.EvaluationInfo;
import com.appgame.differ.bean.game.TagsInfo;

import java.util.List;

/**
 * Created by lzx on 17/4/20.
 */

public interface EvaDetailContract {
    interface View extends BaseContract.BaseView {
        void onCommentListSuccess(EvaluationInfo result, boolean isLoadMore);

        void onPostTagsSuccess();

        void onPostCommentSuccess();

        void postLikeClickSuccess(int type, int position);

        void postTagsThumbSuccess(int type, int position);

        void repliesCommentSuccess();

        void onRequestError(String msg);

        void onTagListSuccess(List<TagsInfo> tagsInfos, int itemPosition);

        void thumbEvaluationSuccess(int type, int position);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getDiscussList(String target, String target_id, String position, int page_size, boolean isLoadMore);

        void getTagList(String target, String target_id, int position);

        void postComment(String target, String target_id, String content);

        void postTags(String target, String target_id, String name);

        void postDiscussThumb(String discuss_id, final int type, int position);

        void postTagsThumb(String discuss_id, final int type, int position);

        void repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id);

        void thumbEvaluation(String comment_id, int type, int position);

    }
}
