package com.appgame.differ.module.personal.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.login.model.LoginModel;
import com.appgame.differ.module.personal.contract.PersonalContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/4/24.
 * 386707112@qq.com
 */

public class PersonalPresenter extends RxPresenter<PersonalContract.View> implements PersonalContract.Presenter<PersonalContract.View> {

    private LoginModel mLoginModel;

    public PersonalPresenter() {
        mLoginModel = new LoginModel();
    }

    @Override
    public void requestUserInfoById(String userId, final boolean isOther) {
        BaseSubscriber<UserInfo> subscriber = mLoginModel.requestUserInfoById(userId, isOther)
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<UserInfo>(mView, false) {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        mView.onUserInfoSuccess(userInfo, isOther);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestUserInfoByToken() {
        BaseSubscriber<UserInfo> subscriber = mLoginModel.requestUserInfoByToken()
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<UserInfo>(mView, false) {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        mView.onUserInfoSuccess(userInfo, false);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestUserGuest(String user_id, String position, String action) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(action) && action.equals("other")) {
            map.put("user_id", user_id);
        } else {
            map.put("access_token", token);
        }
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<List<UserGuest>> subscriber = RetrofitHelper.getAppGameAPI().getUserGuest(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    List<UserGuest> list = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        UserGuest guest = new UserGuest();
                        String position1 = jsonObject.getJSONObject("meta").getString("position");
                        guest.setPosition(position1);
                        guest.setId(object.optString("id"));
                        guest.setContent(object.getJSONObject("attributes").optString("content"));
                        guest.setParentId(object.getJSONObject("attributes").optString("parent_id"));
                        guest.setCreatedAt(object.getJSONObject("attributes").optString("created_at"));
                        guest.setThumbsUp(object.getJSONObject("attributes").optString("thumbs_up"));
                        guest.setIsThumb(object.getJSONObject("attributes").optInt("is_thumb"));
                        JSONObject userOb = object.getJSONObject("relationships").optJSONObject("author");
                        UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(userOb);
                        guest.setAuthor(userInfo);
                        list.add(guest);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<UserGuest>>(mView, false) {
                    @Override
                    public void onSuccess(List<UserGuest> userGuestList) {
                        mView.onRequestUserGuest(userGuestList, position);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void followUser(String follow_id, final String action) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("follow_id", follow_id);
        if (action.equals("cancel")) {
            map.put("action", action);
        }
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<Boolean> subscriber = RetrofitHelper.getAppGameAPI().followUser(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String msg = jsonObject.getJSONObject("meta").getString("message");
                    return status == 1 && msg.equals("success");
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.onFollowSuccess(action);
                        } else {
                            mView.showError("操作失败", false);
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        mView.showError("操作失败", false);
                    }
                });
        addSubscribe(subscriber);
    }


}
