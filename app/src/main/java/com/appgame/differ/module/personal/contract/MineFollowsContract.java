package com.appgame.differ.module.personal.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.mine.MineFollows;

import java.util.List;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public interface MineFollowsContract {
    interface View extends BaseContract.BaseView {

        void requestFollowSuccess(List<MineFollows> mineFollows);

        void loadMoreSuccess(List<MineFollows> mineFollows);

        void loadFinishAllData();

        void showProgressUI();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        void requestFollows(String action, String userId);

        void loadMore(String action, String userId);
    }
}
