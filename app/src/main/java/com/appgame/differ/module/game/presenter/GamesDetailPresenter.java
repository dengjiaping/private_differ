package com.appgame.differ.module.game.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.game.contract.GamesDetailContract;
import com.appgame.differ.module.game.model.GameDetailModel;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.rx.RxUtils;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 17/2/28.
 */

public class GamesDetailPresenter extends RxPresenter<GamesDetailContract.View> implements GamesDetailContract.Presenter<GamesDetailContract.View> {
    private GameDetailModel mGameDetailModel;
    public String position = "0";
    public boolean isMore;

    public GamesDetailPresenter() {
        mGameDetailModel = new GameDetailModel();
    }

    @Override
    public void getGameData(String game_id) {
        BaseSubscriber<GameInfo> subscriber = mGameDetailModel.getGameDetail(game_id)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<GameInfo>(mView, false) {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        mView.showProgressUI(true);
                    }

                    @Override
                    public void onSuccess(GameInfo gameInfo) {
                        mView.onGetGameDetailSuccess(gameInfo);
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
    public void getEvaluationList(String game_id, String order) {
        position = "0";

        BaseSubscriber<List<UserAppraise>> subscriber = mGameDetailModel.getEvaluationList(game_id, order, position, page_size)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<UserAppraise>>(mView, false) {
                    @Override
                    public void onSuccess(List<UserAppraise> info) {
                        if (info.size() > 0)
                            position = info.get(0).position;
                        isMore = info.size() >= page_size;
                        mView.onGetEvaluationListSuccess(info);
                        mView.showProgressUI(false);
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
    public void getHotEvaluationList(String game_id, String order) {
        BaseSubscriber<List<UserAppraise>> subscriber = mGameDetailModel.getEvaluationList(game_id, order, "0", 15)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<UserAppraise>>(mView, false) {
                    @Override
                    public void onSuccess(List<UserAppraise> list) {
                        mView.onGetHotEvaluationListSuccess(list);
                        mView.showProgressUI(false);
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
    public void getUserAppraise(String game_id, String order) {
        BaseSubscriber<UserAppraise> subscriber = mGameDetailModel.getUserAppraise(game_id, order, "0")
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<UserAppraise>(mView, false) {
                    @Override
                    public void onSuccess(UserAppraise userAppraise) {
                        if (TextUtils.isEmpty(userAppraise.getStar())) {
                            mView.onGetUserAppraiseSuccess(null);
                        } else {
                            mView.onGetUserAppraiseSuccess(userAppraise);
                        }
                        mView.showProgressUI(false);
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
    public void loadMoreEvaluationList(String game_id, String order) {
        LogUtil.i("isMore = " + isMore);
        if (isMore) {
            BaseSubscriber<List<UserAppraise>> subscriber = mGameDetailModel.getEvaluationList(game_id, order, position, page_size)
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<List<UserAppraise>>(mView, true) {
                        @Override
                        public void onSuccess(List<UserAppraise> info) {
                            if (info.size() > 0)
                                position = info.get(0).position;
                            isMore = info.size() >= page_size;
                            mView.loadMoreEvaluationListSuccess(info);
                        }
                    });
            addSubscribe(subscriber);
        } else {
            mView.loadFinishAllData();
        }
    }

    @Override
    public void thumbEvaluation(String comment_id, int type, int position, boolean isUserComment) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mGameDetailModel.thumbEvaluation(comment_id, type)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.thumbEvaluationSuccess(type, position, isUserComment);
                        } else {
                            mView.showError("操作失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

}
