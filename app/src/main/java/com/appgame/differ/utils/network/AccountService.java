package com.appgame.differ.utils.network;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 帐号中心相关API
 * Created by lzx on 2017/2/23.
 * 386707112@qq.com
 */

public interface AccountService {

    /**------------------------通过用户名与密码直接登录流程-------------------------------**/

    /**
     * 获取AccessToken
     */
    @FormUrlEncoded
    @POST("oauth/access_token")
    Observable<ResponseBody> requestAccessToken(@FieldMap Map<String, String> map);

    /**
     * 刷新AccessToken
     */
    @FormUrlEncoded
    @POST("oauth/access_token")
    Observable<ResponseBody> refreshAccessToken(@FieldMap Map<String, String> map);

    /**--------------------------用户注册接口-------------------------------------**/
    /**
     * 普通注册、手机注册,获取手机验证码、手机注册,检验手机验证码、完成注册、一键注册
     */
    @FormUrlEncoded
    @POST("oauth/register")
    Observable<ResponseBody> requestRegister(@FieldMap Map<String, String> map);

    /**
     * 修改密码
     */
    @FormUrlEncoded
    @POST("resource/user_change_password")
    Observable<ResponseBody> requestChangePassword(@FieldMap Map<String, String> map);

    /**
     * 修改用户信息
     */
    @FormUrlEncoded
    @POST("resource/userinfo/edit")
    Observable<ResponseBody> requestEditUserInfo(@FieldMap Map<String, String> map);

    /**
     * 退出登录
     */
    @POST("oauth/logout")
    Observable<ResponseBody> requestLoginOut(@Field("access_token") String access_token);


    /**
     *  绑定手机相关
     *  发送手机验证码、检验手机验证码
     */
    @FormUrlEncoded
    @POST("resource/bind_mobile")
    Observable<ResponseBody> requestBindMobile(@FieldMap Map<String, String> map);

    /**
     * 解绑手机相关
     * 发送手机验证码、检验手机验证码
     */
    @POST("resource/unbind_mobile")
    Observable<ResponseBody> requestUnBindMobile(@FieldMap Map<String, String> map);

    /**
     * 第三方绑定
     */
    @FormUrlEncoded
    @POST("social/bind")
    Observable<ResponseBody> requestBindSocial(@FieldMap Map<String, String> map);

    /**
     * 手机找回密码
     * 获取手机验证码、检验手机验证码、修改密码
     */
    @FormUrlEncoded
    @POST("oauth/oauth_forgot_mobile")
    Observable<ResponseBody> requestForgotPassword(@FieldMap Map<String, String> map);

    /**
     * 邮箱找回密码
     */
    @POST("oauth/oauth_forgot_email")
    Observable<ResponseBody> requestForgotEmail(@FieldMap Map<String, String> map);

    /**
     * 修改邮箱
     */
    @FormUrlEncoded
    @POST("resource/user_change_email")
    Observable<ResponseBody> requestChangeEmail(@FieldMap Map<String, String> map);


    /**
     * 第三方登陆
     */
    @FormUrlEncoded
    @POST("social/provider-login")
    Observable<ResponseBody> requestProviderLogin(@FieldMap Map<String, String> map);

    /**
     * 手机短信验证码登录
     *  发送验证码
     */
    @FormUrlEncoded
    @POST("oauth/access_token")
    Observable<ResponseBody> sendVerCode(@FieldMap Map<String, String> map);

}
