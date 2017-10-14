package com.appgame.differ.module.personal.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.personal.contract.EditUserInfoContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

import static com.appgame.differ.utils.network.RetrofitHelper.getAppGameAPI;

/**
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 */

public class EditUserInfoPresenter extends RxPresenter<EditUserInfoContract.View> implements EditUserInfoContract.Presenter<EditUserInfoContract.View> {



    @Override
    public void updateUserInfo(Map<String, String> photoPath, final String nickname, final String sex,
                               final String birthday, final String remark,
                               final int public_follower, final int public_following) {
        if (TextUtils.isEmpty(nickname)) {
            mView.showError("昵称不能为空", false);
            return;
        }
        if (nickname.length() > 20 || nickname.length() < 1) {
            mView.showError("请输入1~20字的昵称", false);
            return;
        }
        if (nickname.contains(" ")) {
            mView.showError("昵称不能包含空格", false);
            return;
        }
        final String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        if (photoPath.size() == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("access_token", token);
            map.put("nickname", nickname);
            map.put("sex", sex.equals("男") ? 1 : 2);
            map.put("birthday", birthday);
            map.put("remark", TextUtils.isEmpty(remark) ? "" : remark);
            map.put("public_follower", public_follower);
            map.put("public_following", public_following);
            map.put("extra", CommonUtil.getExtraParam());
            BaseSubscriber<Integer> subscriber = RetrofitHelper.getAppGameAPI().updateUserInfo(map)
                    .map(responseBody -> {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        LogUtil.i("jsonObject = " + jsonObject.toString());
                        JSONObject meta = jsonObject.getJSONObject("meta");
                        return meta.optInt("status", 0);
                    })
                    .compose(RxUtils.rxSchedulerObservable())
                    .subscribeWith(new BaseSubscriber<Integer>(mView, false) {
                        @Override
                        public void onSuccess(Integer integer) {
                            if (integer == 1) {
                                mView.onUpdateSuccess();
                            } else {
                                mView.showError("更改失败", false);
                            }
                        }
                    });
            addSubscribe(subscriber);
        } else {
            Observable<ResponseBody> observable;
            if (!TextUtils.isEmpty(photoPath.get("user_header")) && TextUtils.isEmpty(photoPath.get("user_cover"))) {
                Map<String, Object> map = new HashMap<>();
                map.put("access_token", token);
                map.put("image_data", photoPath.get("user_header"));
                map.put("extra", CommonUtil.getExtraParam());
                observable = RetrofitHelper.getAppGameAPI().uploadUserHeader(map);
            } else if (TextUtils.isEmpty(photoPath.get("user_header")) && !TextUtils.isEmpty(photoPath.get("user_cover"))) {
                Map<String, String> map = new HashMap<>();
                map.put("access_token", token);
                map.put("image_data", photoPath.get("user_cover"));
                map.put("extra", CommonUtil.getExtraParam());
                observable = RetrofitHelper.getAppGameAPI().uploadUserCover(map);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("access_token", token);
                map.put("image_data", photoPath.get("user_header"));
                map.put("extra", CommonUtil.getExtraParam());
                observable = RetrofitHelper.getAppGameAPI().uploadUserHeader(map)
                        .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                            @Override
                            public ObservableSource<ResponseBody> apply(ResponseBody responseBody) throws Exception {
                                JSONObject jsonObject = new JSONObject(responseBody.string());
                                JSONObject meta = jsonObject.getJSONObject("meta");
                                int status = meta.optInt("status", 0);
                                if (status == 200) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("access_token", token);
                                    map.put("image_data", photoPath.get("user_cover"));
                                    return getAppGameAPI().uploadUserCover(map);
                                } else {
                                    return null;
                                }
                            }
                        });
            }
            if (observable != null) {
                addSubscribe(observable.flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {

                    @Override
                    public ObservableSource<ResponseBody> apply(@NonNull ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONObject meta = jsonObject.getJSONObject("meta");
                        int status = meta.optInt("status", 0);
                        if (status == 200) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("access_token", token);
                            map.put("nickname", nickname);
                            map.put("sex", sex.equals("男") ? 1 : 2);
                            map.put("birthday", birthday);
                            map.put("remark", TextUtils.isEmpty(remark) ? "" : remark);
                            map.put("public_follower", public_follower);
                            map.put("public_following", public_following);
                            return getAppGameAPI().updateUserInfo(map);
                        }
                        return null;
                    }
                })
                        .map(responseBody -> {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            JSONObject meta = jsonObject.getJSONObject("meta");
                            return meta.optInt("status", 0);
                        })
                        .compose(RxUtils.rxSchedulerObservable())
                        .subscribeWith(new BaseSubscriber<Integer>(mView, false) {
                            @Override
                            public void onSuccess(Integer integer) {
                                if (integer == 1) {
                                    mView.onUpdateSuccess();
                                } else {
                                    mView.showError("更改失败", false);
                                }
                            }
                        }));
            } else {
                LogUtil.i("AccountResponseError   observable = null");
            }
        }
    }
}
