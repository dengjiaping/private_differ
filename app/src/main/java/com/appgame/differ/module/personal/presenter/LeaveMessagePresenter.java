package com.appgame.differ.module.personal.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.personal.contract.LeaveMessageContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lzx on 2017/5/10.
 * 386707112@qq.com
 */

public class LeaveMessagePresenter extends RxPresenter<LeaveMessageContract.View> implements LeaveMessageContract.Presenter<LeaveMessageContract.View> {

    @Override
    public void sumbitMsg(String guest_user_id, String content, String parent_id) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            mView.showError("请输入留言内容", false);
            return;
        }

        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("guest_user_id", guest_user_id);
        map.put("content", content);
        map.put("parent_id", parent_id);
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<Boolean> subscriber = RetrofitHelper.getAppGameAPI().sumbitUserGuest(map)
                .compose(RxUtils.rxSchedulerObservable())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject meta = jsonObject.optJSONObject("meta");
                    int status = meta.optInt("status", -1);
                    String message1 = meta.optString("message");
                    return status == 200 && message1.equals("success");
                })
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.sumbitMsgSuccess();
                        } else {
                            mView.showError("留言失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void requestUserGuest(String user_id, String position, String action, boolean isLoadMore) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        if (!TextUtils.isEmpty(action) && action.equals("other")) {
            map.put("user_id", user_id);
        } else {
            map.put("access_token", token);
        }
        map.put("position", position);
        map.put("page_size", "20");
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<List<UserGuest>> subscriber = RetrofitHelper.getAppGameAPI().getUserGuest(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    List<UserGuest> list = new ArrayList<UserGuest>();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        UserGuest guest = new UserGuest();
                        guest.setPosition(jsonObject.getJSONObject("meta").getString("position"));
                        guest.setId(object.optString("id"));
                        JSONObject guesAttr = object.getJSONObject("attributes");
                        guest.setContent(guesAttr.optString("content"));
                        guest.setParentId(guesAttr.optString("parent_id"));
                        guest.setCreatedAt(guesAttr.optString("created_at"));
                        guest.setThumbsUp(guesAttr.optString("thumbs_up"));
                        guest.setIsThumb(guesAttr.optInt("is_thumb"));
                        JSONObject userOb = object.getJSONObject("relationships").optJSONObject("author");
                        UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(userOb);
                        guest.setAuthor(userInfo);

                        JSONArray childGuests = guesAttr.optJSONArray("childGuests");
                        if (childGuests != null) {
                            List<UserGuest> childList = new ArrayList<UserGuest>();
                            for (int j = 0; j < childGuests.length(); j++) {
                                JSONObject childObject = childGuests.getJSONObject(j);
                                UserGuest childGuest = new UserGuest();
                                childGuest.setId(childObject.optString("id"));
                                JSONObject childAttr = childObject.getJSONObject("attributes");
                                childGuest.setContent(childAttr.optString("content"));
                                childGuest.setParentId(childAttr.optString("parent_id"));
                                childGuest.setCreatedAt(childAttr.optString("created_at"));
                                JSONObject childUserOb = childObject.getJSONObject("relationships").optJSONObject("author");
                                UserInfo childUserInfo = UserInfoManager.getImpl().resolveUserInfo(childUserOb);
                                childGuest.setAuthor(childUserInfo);

                                JSONArray twoChildGuests = childAttr.optJSONArray("childGuests");
                                if (twoChildGuests != null) {
                                    List<UserGuest> twohildList = new ArrayList<UserGuest>();
                                    for (int k = 0; k < twoChildGuests.length(); k++) {
                                        JSONObject twoChildObject = twoChildGuests.getJSONObject(k);
                                        UserGuest twoChildGuest = new UserGuest();
                                        twoChildGuest.setId(twoChildObject.optString("id"));
                                        JSONObject twoAttr = twoChildObject.getJSONObject("attributes");
                                        twoChildGuest.setContent(twoAttr.optString("content"));
                                        twoChildGuest.setParentId(twoAttr.optString("parent_id"));
                                        twoChildGuest.setCreatedAt(twoAttr.optString("created_at"));
                                        JSONObject twoChildUserOb = twoChildObject.getJSONObject("relationships").optJSONObject("author");
                                        UserInfo twoChildUserInfo = UserInfoManager.getImpl().resolveUserInfo(twoChildUserOb);
                                        twoChildGuest.setAuthor(twoChildUserInfo);
                                        twohildList.add(twoChildGuest);
                                    }
                                    guest.setTwoChildGuests(twohildList);
                                }
                                childList.add(childGuest);
                            }
                            if (guest.getTwoChildGuests() != null) {
                                childList.addAll(guest.getTwoChildGuests());
                            }
                            guest.setChildGuests(childList);
                        }
                        list.add(guest);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<UserGuest>>(mView, false) {
                    @Override
                    public void onSuccess(List<UserGuest> userGuestList) {
                        mView.onRequestUserGuest(userGuestList, isLoadMore);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void sumbitGuestThumb(String guest_id, int type, int position) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        BaseSubscriber<Boolean> subscriber = RetrofitHelper.getAppGameAPI().sumbitGuestThumb(token, guest_id, type, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    String message = jsonObject.getJSONObject("meta").getString("message");
                    return status == 200 && message.equals("success");
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.sumbitGuestThumbSuccess(position, type);
                        } else {
                            ToastUtil.showShort("操作失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void deleteGuest(String id, int position) {
        if (!CommonUtil.isLogin()) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        BaseSubscriber<Boolean> subscriber = RetrofitHelper.getAppGameAPI().guestDelete(token, id, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    return status == 200;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.deleteGuestSuccess(position);
                        } else {
                            ToastUtil.showShort("操作失败");
                        }
                    }
                });
        addSubscribe(subscriber);
    }
}
