package com.appgame.differ.module.game.model;

import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.data.constants.AppConstants;
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
 * Created by lzx on 2017/6/21.
 */

public class GameDetailModel {

    public Observable<GameInfo> getGameDetail(String game_id) {
        Map<String, String> options = new HashMap<>();
        options.put("game_id", game_id);
        options.put("access_token", SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN));
        options.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameDetail(options)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    GameInfo gameInfo = new GameInfo();
                    gameInfo.resolveGameInfo(jsonObject.getJSONObject("data"));
                    return gameInfo;
                });
    }

    public Observable<List<UserAppraise>> getEvaluationList(String game_id, String order, String position, int page_size) {
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("game_id", game_id);
        map.put("access_token", access_token);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("order", order);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameComment(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    List<UserAppraise> list = new ArrayList<UserAppraise>();
                    JSONArray listArray = data.optJSONArray("list");
                    if (listArray != null) {
                        for (int i = 0; i < listArray.length(); i++) {
                            JSONObject object = listArray.optJSONObject(i);
                            UserAppraise appraise = new UserAppraise();
                            appraise.resolveJson(object);
                            appraise.position = jsonObject.getJSONObject("meta").getString("position");
                            appraise.count = jsonObject.getJSONObject("meta").getString("count");
                            list.add(appraise);
                        }
                    }
                    return list;
                });
    }

    public Observable<UserAppraise> getUserAppraise(String game_id, String order, String position) {
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("game_id", game_id);
        map.put("access_token", access_token);
        map.put("position", position);
        map.put("page_size", String.valueOf(page_size));
        map.put("order", order);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().getGameComment(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    UserAppraise appraise = new UserAppraise();
                    try {
                        appraise.resolveJson(data.getJSONObject("user_appraise"));
                        return appraise;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new UserAppraise();
                    }
                });
    }

    public Observable<Boolean> thumbEvaluation(String comment_id, int type) {
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("appraise_id", comment_id);
        map.put("type", type);
        map.put("extra", CommonUtil.getExtraParam());
        return RetrofitHelper.getAppGameAPI().submitThumb(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").optInt("status", -1);
                    return status == 200;
                });
    }


}
