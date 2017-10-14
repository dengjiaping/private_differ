package com.appgame.differ.module.personal.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.guest.UserGuest;

import java.util.List;

/**
 * Created by lzx on 2017/5/10.
 * 386707112@qq.com
 */

public interface LeaveMessageContract {
    interface View extends BaseContract.BaseView {

        void sumbitMsgSuccess();

        void onRequestUserGuest(List<UserGuest> list, boolean isLoadMore);

        void sumbitGuestThumbSuccess(int position, int type);

        void deleteGuestSuccess(int position);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void sumbitMsg(String guest_user_id, String content, String parent_id);

        void requestUserGuest(String user_id, String position, String action, boolean isLoadMore);

        void sumbitGuestThumb( String guest_id, int type, int position);

        void deleteGuest(String id,int position);
    }
}
