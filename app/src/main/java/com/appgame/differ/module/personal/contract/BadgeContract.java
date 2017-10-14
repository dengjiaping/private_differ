package com.appgame.differ.module.personal.contract;

import com.appgame.differ.base.mvp.BaseContract;

/**
 * Created by lzx on 2017/5/15.
 * 386707112@qq.com
 */

public interface BadgeContract {
    interface View extends BaseContract.BaseView {

        void onGetMineBadgeSuccess();

        void onGetAllBadgeSuccess();

        void changeIsShowBadgeSuccess(int position);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getMineBadge();

        void getAllBadge();

        void changeIsShowBadge(int position, String achieve_id);
    }
}
