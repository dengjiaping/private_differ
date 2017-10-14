package com.appgame.differ.module.game.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;

import java.util.List;


/**
 * Created by lzx on 17/2/28.
 */

public interface GamesDetailContract {
    interface View extends BaseContract.BaseView {
        void onGetGameDetailSuccess(GameInfo result);

        void onGetEvaluationListSuccess(List<UserAppraise> list);

        void onGetHotEvaluationListSuccess(List<UserAppraise> list);

        void onGetUserAppraiseSuccess(UserAppraise userAppraise);

        void loadMoreEvaluationListSuccess(List<UserAppraise> list);

     //   void loadMoreFailed(String msg);

        void showProgressUI(boolean isShowPro);

        void loadFinishAllData();

        void thumbEvaluationSuccess(int type, int position,boolean isUserComment);

     //   void onFailed(String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getGameData(String game_id);

        void getEvaluationList(String game_id, String order);

        void getHotEvaluationList(String game_id, String order);

        void getUserAppraise(String game_id, String order);

        void loadMoreEvaluationList(String target, String target_id);

        void thumbEvaluation(String comment_id, int type, int position, boolean isUserComment);
    }
}
