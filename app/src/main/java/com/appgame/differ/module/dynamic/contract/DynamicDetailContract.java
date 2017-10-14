package com.appgame.differ.module.dynamic.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.dynamic.DynamicInfo;

import java.util.List;

/**
 * Created by lzx on 17/5/16.
 */

public interface DynamicDetailContract {


    interface View extends BaseContract.BaseView {
        void getDynamicDetailSuccess(DynamicInfo info);

        void loadMoreSuccess(List<DailyComment> dailyComments);

        void loadFinishAllData();

        void showProgressUI();

        void postCommentSuccess();

        void postDiscussThumbSuccess(int type, int position);

        void thumbDynamicSuccess(int type, int position);

        void onDeleteDynamicSuccess();

        void getDynamicCommentSuccess(List<DailyComment> info);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getDynamicDetail(String dynamic_id);

        void getDynamicComment(String target, String target_id);

        void loadMoreDynamicComment(String target, String target_id);

        void postComment(String target, String target_id, String content);

        void repliesComment(String comment_id, String content, String is_replied, String reply_id, String reply_user_id);

        void postDiscussThumb(String discuss_id, final int type, int position);

        void thumbDynamic(String dynamic_id, int type, int position, String thumbType);

        void deleteDynamic(String dynamic_id);
    }

}
