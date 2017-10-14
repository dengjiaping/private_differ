package com.appgame.differ.module.search.model;

import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.mine.MineFollows;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by lzx on 2017/6/20.
 */

public class SearchModel {

    public Observable<List<RecommedInfo>> searchGameByKeyWords(String keywords, String position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("keywords", keywords);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().searchGameByKeywords(map)
                .map(responseBody -> {
                    List<RecommedInfo> list = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject object = dataArray.getJSONObject(i);
                        RecommedInfo recommedInfo = new RecommedInfo();
                        recommedInfo.resolveJson(object);
                        recommedInfo.position = jsonObject.getJSONObject("meta").getString("position");
                        list.add(recommedInfo);
                    }
                    return list;
                });
    }


    public Observable<List<DailyInfo>> searchArticlesByKeyWords(String keywords, String position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("keywords", keywords);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().searchArticleByKeywords(map)
                .map(responseBody -> {
                    List<DailyInfo> list = new ArrayList<DailyInfo>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DailyInfo dailyInfo = new DailyInfo();
                        dailyInfo.resolveJson(jsonArray.getJSONObject(i));
                        dailyInfo.position = jsonObject.getJSONObject("meta").getString("position");
                        list.add(dailyInfo);
                    }
                    return list;
                });
    }


    public Observable<List<MineFollows>> searchUserByKeyWords(String keywords, String position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("keywords", keywords);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().searchUserByKeywords(map)
                .map(responseBody -> {
                    List<MineFollows> list = new ArrayList<MineFollows>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject attr = jsonArray.getJSONObject(i);
                        MineFollows follows = new MineFollows();
                        UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(attr);
                        follows.setUserInfo(userInfo);
                        String time = attr.optJSONObject("attributes").optString("appraise_time");
                        String game = attr.optJSONObject("attributes").optString("appraise_game");
                        follows.setAppraiseTime(time);
                        follows.setAppraiseGame(game);
                        follows.position = jsonObject.getJSONObject("meta").getString("position");
                        list.add(follows);
                    }
                    return list;
                });
    }


    /**
     * 加载游戏分类
     */
    public Observable<List<RecommedInfo>> requestGameClass(String typeId, String position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("id", typeId);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameTagsGames(map)
                .map(responseBody -> {
                    List<RecommedInfo> list = new ArrayList<RecommedInfo>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String currPosition = jsonObject.getJSONObject("meta").getString("position");
                    JSONArray data = jsonObject.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject object = data.getJSONObject(i);
                        RecommedInfo recommedInfo = new RecommedInfo();
                        recommedInfo.position = currPosition;
                        recommedInfo.resolveJson(object);
                        list.add(recommedInfo);
                    }
                    return list;
                });
    }

}
