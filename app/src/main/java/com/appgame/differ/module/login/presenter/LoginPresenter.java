package com.appgame.differ.module.login.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.appgame.differ.R;
import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.login.contract.LoginContract;
import com.appgame.differ.module.login.model.LoginModel;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by lzx on 2017/2/24.
 * 386707112@qq.com
 */
public class LoginPresenter extends RxPresenter<LoginContract.View> implements LoginContract.Presenter<LoginContract.View> {

    private LoginModel mLoginModel;
    private boolean isBindPhone = false;
    private Context mContext;
    private UMShareAPI mShareAPI;

    public LoginPresenter(Context context) {
        mLoginModel = new LoginModel();
        mContext = context;
        mShareAPI = UMShareAPI.get(context);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(context).setShareConfig(config);
    }

    @Override
    public void bindPhoneSendVerCode(String mobile) {
        if (!CommonUtil.checkMobileNum(mobile)) {
            mView.showError(mContext.getString(R.string.login_phone_error), false);
            return;
        }
        BaseSubscriber<JSONObject> subscriber = mLoginModel.bindPhoneSendVerCode(mobile)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<JSONObject>(mView, false) {
                    @Override
                    public void onSuccess(JSONObject object) {
                        String status = object.optString("status");
                        if (status.equals("success")) {
                            mView.sendVerCodeSuccess();
                        } else {
                            String error = object.optString("error");
                            if (error.equals("mobile_has_been_bind")) {
                                isBindPhone = true;
                                sendVerCode(mobile);
                            } else {
                                mView.showError(object.optString("error_description"), false);
                            }
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void bindCheckVerCode(String mobile, String captcha) {
        if (!CommonUtil.checkMobileNum(mobile)) {
            mView.showError(mContext.getString(R.string.login_phone_error), false);
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            mView.showError(mContext.getString(R.string.login_vercode_error), false);
            return;
        }
        BaseSubscriber<JSONObject> subscriber = mLoginModel.bindCheckVerCode(mobile, captcha)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<JSONObject>(mView, false) {
                    @Override
                    public void onSuccess(JSONObject object) {
                        String status = object.optString("status");
                        if (status.equals("success")) {
                            getUserInfo();
                        } else {
                            String error = object.optString("error");
                            if (error.equals("mobile_has_been_bind")) {
                                loginByPhone(mobile, captcha);
                            } else {
                                mView.showError(object.optString("error_description"), false);
                            }
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void bindSocial(String provider_id, String data) {
        BaseSubscriber<JSONObject> subscriber = mLoginModel.bindSocial(provider_id, data)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<JSONObject>(mView, false) {
                    @Override
                    public void onSuccess(JSONObject object) {
                        String status = object.optString("status");
                        if (status.equals("failed")) {
                            int code = object.optInt("code");
                            if (code == 40002) {
                                requestLogin(provider_id, data);
                            } else {
                                mView.showError("登陆失败", false);
                            }
                        } else {
                            getUserInfo();
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loginByPhone(String phone, String vercode) {
        if (!CommonUtil.checkMobileNum(phone)) {
            mView.showError(mContext.getString(R.string.login_phone_error), false);
            return;
        }
        if (TextUtils.isEmpty(vercode)) {
            mView.showError(mContext.getString(R.string.login_vercode_error), false);
            return;
        }
        BaseSubscriber<UserInfo> subscriber = mLoginModel.loginByPhone(phone, vercode)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<UserInfo>(mView, false) {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        //友盟统计
                        MobclickAgent.onProfileSignIn(userInfo.getUserId());
                        mView.loginSuccess(userInfo);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loginDiffer(String phone, String vercode) {
        if (isBindPhone) {
            loginByPhone(phone, vercode);
        } else {
            bindCheckVerCode(phone, vercode);
        }
    }

    @Override
    public void sendVerCode(String phone) {
        if (!CommonUtil.checkMobileNum(phone)) {
            mView.showError(mContext.getString(R.string.login_phone_error), false);
            return;
        }
        BaseSubscriber<Boolean> subscriber = mLoginModel.sendVerCode(phone)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.sendVerCodeSuccess();
                        } else {
                            mView.showError(mContext.getString(R.string.login_send_ver_error), false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestLogin(String provider_id, String data) {
        BaseSubscriber<UserInfo> subscriber = mLoginModel.requestLogin(provider_id, data)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<UserInfo>(mView, false) {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        //友盟统计
                        MobclickAgent.onProfileSignIn(provider_id, userInfo.getUserId());
                        mView.loginSuccess(userInfo);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void getUserInfo() {
        BaseSubscriber<UserInfo> subscriber = mLoginModel.requestUserInfoByToken()
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<UserInfo>(mView, false) {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        SpUtil.getInstance().putBoolean(AppConstants.IS_LOGIN, true);
                        mView.loginSuccess(userInfo);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void loginBySocial(LoginActivity context, SHARE_MEDIA social) {
        mShareAPI.getPlatformInfo(context, social, umAuthListener);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (platform == SHARE_MEDIA.SINA) {
                try {
                    String uid = data.get("uid");
                    String accessToken = data.get("accessToken");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("uid", uid);
                    jsonObject.put("access_token", accessToken);
                    String base64Data = CommonUtil.Base64Encode(jsonObject.toString());
                    //showProgress();
                    mView.isShowProgress(true);
                    requestLogin("Weibo", base64Data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                try {
                    String accessToken = data.get("accessToken");
                    String openid = data.get("openid");

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("openid", openid);
                    jsonObject.put("access_token", accessToken);
                    String base64Data = CommonUtil.Base64Encode(jsonObject.toString());
                    mView.isShowProgress(true);
                    requestLogin("Weixin", base64Data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (platform == SHARE_MEDIA.QQ) {
                try {
                    String unionid = data.get("unionid");
                    String openid = data.get("uid");
                    String appid = AppConstants.QQ_APP_ID;
                    String accessToken = data.get("accessToken");
                    JSONObject json = new JSONObject();
                    json.put("openid", openid);
                    json.put("app_id", appid);
                    json.put("access_token", accessToken);
                    json.put("is_union", "1");
                    final String base64Data = CommonUtil.Base64Encode(json.toString());
                    mView.isShowProgress(true);
                    requestLogin("Qq", base64Data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            mView.isShowProgress(false);
            ToastUtil.showShort("登录失败");
            t.printStackTrace();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            mView.isShowProgress(false);
            ToastUtil.showShort("登录取消");
        }
    };

    @Override
    public void clearUMShareAPI() {
        mShareAPI = null;
        umAuthListener = null;
    }
}