package com.appgame.differ.module.dynamic.presenter;

import android.text.TextUtils;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.collection.CollectionInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.module.dynamic.contract.SelectGameContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appgame.differ.utils.network.RetrofitHelper.getAppGameAPI;

/**
 * Created by lzx on 2017/5/22.
 * 386707112@qq.com
 */

public class SelectGamePresenter extends RxPresenter<SelectGameContract.View> implements SelectGameContract.Presenter<SelectGameContract.View> {


    @Override
    public void getPlayingGame(String type, String position, String userId) {
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
        BaseSubscriber<List<CollectionInfo>> subscriber = getAppGameAPI().requestMineCollections(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<CollectionInfo> tempList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String json = jsonArray.getJSONObject(i).getJSONObject("relationships").getJSONObject("game").toString();
                        int is_appraised = jsonArray.getJSONObject(i).getJSONObject("attributes").getInt("is_appraised");
                        CollectionInfo info = new Gson().fromJson(json, CollectionInfo.class);
                        info.setIs_appraised(is_appraised);
                        tempList.add(info);
                    }
                    List<CollectionInfo> list = new ArrayList<CollectionInfo>();
                    for (CollectionInfo info : tempList) {
                        boolean hasPkg = AppInfoManager.getImpl().hasPkg(info.getAttributes().getDownloadInfo());
                        info.getAttributes().setDownloadStatus(hasPkg ? 1 : 0);
                        info.position = jsonObject.getJSONObject("meta").getString("position");
                        list.add(info);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<CollectionInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<CollectionInfo> collectionInfos) {
                        mView.onPlayingGameSuccess(collectionInfos);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void getGameBySearch(String keyWord) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("keywords", keyWord);
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<List<CollectionInfo>> subscriber = RetrofitHelper.getAppGameAPI().searchGame(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<CollectionInfo> infoList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String json = jsonArray.getJSONObject(i).toString();
                        CollectionInfo info = new Gson().fromJson(json, CollectionInfo.class);
                        infoList.add(info);
                    }
                    return infoList;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<CollectionInfo>>(mView, false) {
                    @Override
                    public void onSuccess(List<CollectionInfo> collectionInfos) {
                        mView.onSearchGameSuccess(collectionInfos);
                    }
                });
        addSubscribe(subscriber);
    }
}