package com.appgame.differ.module.login.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.module.login.view.LoginActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by lzx on 2017/2/24.
 * 386707112@qq.com
 */
public interface LoginContract {
    interface View extends BaseContract.BaseView {
        void loginSuccess(UserInfo userInfo);

        void sendVerCodeSuccess();

        void isShowProgress(boolean isShow);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {

        /**
         * 绑定手机(发送验证码)
         */
        void bindPhoneSendVerCode(String mobile);

        /**
         * 绑定手机（检验手机验证码）
         */
        void bindCheckVerCode(String mobile, String captcha);

        void loginByPhone(String phone, String pwd);

        void loginDiffer(String phone, String vercode);

        void sendVerCode(String phone);

        /**
         * 三方登录后接口
         */
        void requestLogin(String provider_id, String data);

        void getUserInfo();

        void loginBySocial(LoginActivity context, SHARE_MEDIA social);

        void clearUMShareAPI();

        void bindSocial(String provider_id, String data);

    }
}
