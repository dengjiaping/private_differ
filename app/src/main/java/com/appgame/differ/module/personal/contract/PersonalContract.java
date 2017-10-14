package com.appgame.differ.module.personal.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.user.UserInfo;

import java.util.List;

/**
 * Created by lzx on 2017/4/24.
 * 386707112@qq.com
 */

public interface PersonalContract {
    interface View extends BaseContract.BaseView {

        void onUserInfoSuccess(UserInfo userInfo, boolean isOther);

        void onFollowSuccess(String action);

     //   void onError(String msg);

        void onRequestUserGuest(List<UserGuest> list,String position);

      //  <T> ObservableTransformer<T, T> bindToLifecycle();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestUserInfoById(String userId, boolean isOther);

        void requestUserInfoByToken();

        void requestUserGuest(String user_id, String position,String action);

        void followUser(String follow_id, String action);
    }
}
