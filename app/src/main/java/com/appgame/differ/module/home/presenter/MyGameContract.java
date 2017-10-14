package com.appgame.differ.module.home.presenter;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.home.RecommedInfo;

import java.util.List;

/**
 * Created by yukunlin on 17/4/27.
 */

public interface MyGameContract {

    interface View extends BaseContract.BaseView {

        void onGameListSuccess(List<RecommedInfo> recommedInfos, boolean isLoadMore);

        void onGetRecommendListSuccess(List<RecommedInfo> recommedInfos);

//        void onRequestError(String msg);
//
//        <T> ObservableTransformer<T, T> bindToLifecycle();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getGameList(String type, String position, String userId, boolean isLoadMore);

        void getRecommendList();
    }
}
