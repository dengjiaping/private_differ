package com.appgame.differ.module.login.model;

import com.appgame.differ.bean.AccessToken;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.LoginUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.AccountResponseError;
import com.appgame.differ.utils.network.ErrorAction;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxBus;
import com.google.gson.Gson;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.appgame.differ.utils.network.RetrofitHelper.getAccountAPI;

/**
 * Created by lzx on 2017/6/23.
 */

public class LoginModel {
    /**
     * 三方登录后接口
     */
    public Observable<UserInfo> requestLogin(final String provider_id, String data) {
        Map<String, String> map = new HashMap<>();
        map.put("is_union", "1");
        map.put("provider_id", provider_id);  //渠道
        map.put("data", data);
        map.put("client_id", AppConstants.CLIENT_ID);
        map.put("t", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("sign", CommonUtil.getMd5Value(
                CommonUtil.getMd5Value(data + AppConstants.CLIENT_ID + String.valueOf(System.currentTimeMillis() / 1000)) + AppConstants.CLIENT_SECRET));

        return RetrofitHelper.getAccountAPI().requestProviderLogin(map)
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        String access_token = jsonObject.getString("access_token");
                        //合并匿名用户玩过/不喜欢的游戏
                        collectionMerge(access_token);
                        LoginUtil.getImpl().saveLoginInfo(jsonObject);
                        return RetrofitHelper.getAppGameAPI().requestUserInfoByToken(access_token, CommonUtil.getExtraParam());
                    }
                })
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject userObject = jsonObject.getJSONObject("data");
                    UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(userObject);
                    UserInfoManager.getImpl().saveUserInfo(userInfo);
                    SpUtil.getInstance().putBoolean(AppConstants.IS_LOGIN, true);
                    return userInfo;
                });

    }

    /**
     * 绑定手机(发送验证码)
     */
    public Observable<JSONObject> bindPhoneSendVerCode(String mobile) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("step", "send_mobile_captcha");
        map.put("mobile", mobile);
        return RetrofitHelper.getAccountAPI().requestBindMobile(map)
                .map(responseBody -> new JSONObject(responseBody.string()));
    }

    /**
     * 绑定手机（检验手机验证码）
     */
    public Observable<JSONObject> bindCheckVerCode(String mobile, String captcha) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("step", "verify_mobile_captcha");
        map.put("mobile", mobile);
        map.put("captcha", captcha);
        return RetrofitHelper.getAccountAPI().requestBindMobile(map)
                .map(responseBody -> new JSONObject(responseBody.string()));
    }

    /**
     * 第三方绑定
     */
    public Observable<JSONObject> bindSocial(String provider_id, String data) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("provider_id", provider_id);  //渠道
        map.put("data", data);
        map.put("t", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("sign", CommonUtil.getMd5Value(
                CommonUtil.getMd5Value(data + provider_id + String.valueOf(System.currentTimeMillis() / 1000)) + AppConstants.CLIENT_SECRET));
        return RetrofitHelper.getAccountAPI().requestBindSocial(map)
                .map(responseBody -> new JSONObject(responseBody.string()));
    }

    /**
     * 登录逻辑
     */
    public Observable<UserInfo> loginByPhone(final String phone, final String vercode) {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "mobile");
        map.put("register_type", "verify");
        map.put("client_id", AppConstants.CLIENT_ID);
        map.put("client_secret", AppConstants.CLIENT_SECRET);
        map.put("scope", "userinfo");//userinfo
        map.put("mobile", phone);
        map.put("captcha", vercode);
        map.put("action", "verify");
        return RetrofitHelper.getAccountAPI().requestAccessToken(map)
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {

                    @Override
                    public ObservableSource<ResponseBody> apply(@NonNull ResponseBody responseBody) throws Exception {
                        AccessToken accessToken = new Gson().fromJson(responseBody.string(), AccessToken.class);
                        //合并匿名用户玩过/不喜欢的游戏
                        collectionMerge(accessToken.getAccessToken());

                        SpUtil.getInstance().putString(AppConstants.ACCESS_TOKEN, accessToken.getAccessToken());
                        SpUtil.getInstance().putString(AppConstants.REFRESH_TOKEN, accessToken.getRefreshToken());
                        SpUtil.getInstance().putLong(AppConstants.REFRESH_TOKEN_EXPIRES_IN, accessToken.getRefreshTokenExpiresIn() * 1000);
                        return RetrofitHelper.getAppGameAPI().requestUserInfoByToken(accessToken.getAccessToken(), CommonUtil.getExtraParam());
                    }
                })
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject userObject = jsonObject.getJSONObject("data");
                    UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(userObject);
                    UserInfoManager.getImpl().saveUserInfo(userInfo);
                    SpUtil.getInstance().putBoolean(AppConstants.IS_LOGIN, true);
                    return userInfo;
                });
    }

    /**
     * 发送验证码
     */
    public Observable<Boolean> sendVerCode(String phone) {
        final Map<String, String> map = new HashMap<>();
        map.put("grant_type", "mobile");
        map.put("action", "send");
        map.put("client_id", AppConstants.CLIENT_ID);
        map.put("client_secret", AppConstants.CLIENT_SECRET);
        map.put("scope", "userinfo");
        map.put("mobile", phone);
        return RetrofitHelper.getAccountAPI().sendVerCode(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String status = jsonObject.optString("status");
                    return status.equals("success");
                });
    }

    /**
     * 匿名登陆
     */
    public void incognitoLogin(LifecycleTransformer<ResponseBody> lifecycleTransformer) {
        boolean isIncognitoLogin = SpUtil.getInstance().getBoolean(AppConstants.INCOGNITO_LOGIN, false);
        if (CommonUtil.isLogin()) {
            RxBus.getBus().send(EvenConstant.KEY_INCOGNITO_LOGIN_SUCCESS);
            return;
        }
        if (isIncognitoLogin) {
            RxBus.getBus().send(EvenConstant.KEY_INCOGNITO_LOGIN_SUCCESS);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "register");
        map.put("register_type", "quick");
        map.put("client_id", AppConstants.CLIENT_ID);
        map.put("client_secret", AppConstants.CLIENT_SECRET);
        map.put("scope", "userinfo");
        map.put("password", "123456");
        getAccountAPI().requestRegister(map)
                .compose(lifecycleTransformer)
                .map(responseBody -> new Gson().fromJson(responseBody.string(), AccessToken.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accessToken -> {
                    LogUtil.i("匿名登陆 Token = " + accessToken.getAccessToken());
                    SpUtil.getInstance().putString(AppConstants.ACCESS_TOKEN, accessToken.getAccessToken());
                    SpUtil.getInstance().putBoolean(AppConstants.INCOGNITO_LOGIN, true);
                    SpUtil.getInstance().putString(AppConstants.REFRESH_TOKEN, accessToken.getRefreshToken());
                    SpUtil.getInstance().putLong(AppConstants.REFRESH_TOKEN_EXPIRES_IN, accessToken.getRefreshTokenExpiresIn() * 1000);
                    RxBus.getBus().send(EvenConstant.KEY_INCOGNITO_LOGIN_SUCCESS);
                }, new ErrorAction() {
                    @Override
                    public void call(AccountResponseError error) {
                        LogUtil.i("incognitoLogin = " + error.getErrorDescription());
                    }
                });
    }


    /**
     * 根据id查用户信息
     */
    public Observable<UserInfo> requestUserInfoById(String userId, boolean isOther) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        return RetrofitHelper.getAppGameAPI().requestOtherUserInfo(token, userId, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(data);
                    if (!isOther) {
                        UserInfoManager.getImpl().saveUserInfo(userInfo);
                    }
                    return userInfo;
                });
    }

    /**
     * 根据token查用户信息
     */
    public Observable<UserInfo> requestUserInfoByToken() {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        return RetrofitHelper.getAppGameAPI().requestUserInfoByToken(token, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(data);
                    UserInfoManager.getImpl().saveUserInfo(userInfo);
                    return userInfo;
                });
    }

    /**
     * 合并匿名用户玩过/不喜欢的游戏(绑定失败走正常登陆流程后调用)
     */
    private Observable<Boolean> collectionMerge(String newToken) {
        String oldToken = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        return RetrofitHelper.getAppGameAPI().collectionMerge(newToken, oldToken, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    return status == 200;
                });
    }
}
