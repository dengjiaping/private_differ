package com.appgame.differ.module.msg.presenter;

import com.appgame.differ.base.mvp.BaseContract;

/**
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */

public interface MsgCenterContract {
    interface View extends BaseContract.BaseView {
        void requestUserMsgSuccess();

        void requestSystemMsgSuccess();

        void requestOfficialMsgSuccess();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getUserMsg();

        void getSystemMsg();

        void getOfficialMsg();
    }
}
