package com.appgame.differ.module.home.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.constants.AppConstants;
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

/**
 * Created by lzx on 17/4/27.
 */

public class MyGamePresenter extends RxPresenter<MyGameContract.View> implements MyGameContract.Presenter<MyGameContract.View> {


    @Override
    public void getGameList(String type, String position, String userId, boolean isLoadMore) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        if (TextUtils.isEmpty(userId)) {
            map.put("access_token", token);
        } else {
            map.put("user_id", userId);
        }
        map.put("position", position);
        map.put("page_size", "1000");
        map.put("type", type);
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<List<RecommedInfo>> subscriber = RetrofitHelper.getAppGameAPI().requestMineCollections(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<RecommedInfo> list = new ArrayList<RecommedInfo>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        RecommedInfo recommedInfo = new RecommedInfo();
                        recommedInfo.resolveJson(object.getJSONObject("relationships").getJSONObject("game"));
                        recommedInfo.setInfoId(object.getString("id"));
                        recommedInfo.position = jsonObject.getJSONObject("meta").getString("position");
                        list.add(recommedInfo);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<RecommedInfo> infoList) {
                        mView.onGameListSuccess(infoList, isLoadMore);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void getRecommendList() {
        BaseSubscriber<List<RecommedInfo>> subscriber = RetrofitHelper.getAppGameAPI().getRecommendList("", "", "", CommonUtil.getExtraParam())
                .map(responseBody -> {
                    List<RecommedInfo> list = new ArrayList<RecommedInfo>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject info = dataArray.getJSONObject(i);
                        RecommedInfo recommedInfo = new RecommedInfo();
                        recommedInfo.resolveJson(info);
                        if (i < 4) {
                            list.add(recommedInfo);
                        }
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<RecommedInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<RecommedInfo> infoList) {
                        mView.onGetRecommendListSuccess(infoList);
                    }
                });
        addSubscribe(subscriber);
    }
}
