package com.appgame.differ.module.personal.contract;

import com.appgame.differ.base.mvp.BaseContract;

import java.util.Map;

/**
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 */

public interface EditUserInfoContract {
    interface View extends BaseContract.BaseView {

        void onUpdateSuccess();

        //void onUpdateError(String msg);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void updateUserInfo(Map<String, String> photo, String nickname, String sex, String birthday, String remark, int public_follower, int public_following);
    }
}
