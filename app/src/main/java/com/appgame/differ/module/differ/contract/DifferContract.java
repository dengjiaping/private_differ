package com.appgame.differ.module.differ.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.home.RecommedInfo;

import java.util.List;

/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public interface DifferContract {
    interface View extends BaseContract.BaseView {

        void unLikeSuccess( android.view.View view,  int position,boolean isSwiped);

        void onRequestSuccess(List<RecommedInfo> gameInfos);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestRecommendList();

        void unLikeGame(String gameId, android.view.View view, int position,boolean isSwiped);
    }
}
