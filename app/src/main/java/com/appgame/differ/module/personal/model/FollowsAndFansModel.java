package com.appgame.differ.module.personal.model;

import android.text.TextUtils;

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
 * Created by lzx on 2017/6/12.
 */

public class FollowsAndFansModel {

    public Observable<List<MineFollows>> requestFollow(String position, String action, String userId) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(action) && action.equals("other")) {
            map.put("user_id", userId);
        } else {
            map.put("access_token", token);
        }
        map.put("position", position);
        map.put("page_size", page_size);
        map.put("extra", CommonUtil.getExtraParam());

        return RetrofitHelper.getAppGameAPI().getFollowingList(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    return makeFollowList(jsonObject);
                });
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<MineFollows>>() {
//                    @Override
//                    public void accept(@NonNull List<MineFollows> mineFollows) throws Exception {
//                        callBack.requestSuccess(mineFollows);
//                    }
//                }, new ErrorActionAppGame() {
//                    @Override
//                    public void call(AppGameResponseError error) {
//                        callBack.requestFail(error.getTitle());
//                    }
//                });
    }

    private List<MineFollows> makeFollowList(JSONObject jsonObject) {
        List<MineFollows> list = new ArrayList<MineFollows>();
        JSONArray array = jsonObject.optJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            JSONObject attr = array.optJSONObject(i);
            MineFollows mineFollows = new MineFollows();
            UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(attr);
            mineFollows.setUserInfo(userInfo);
            String time = attr.optJSONObject("attributes").optString("appraise_time");
            String game = attr.optJSONObject("attributes").optString("appraise_game");
            mineFollows.setAppraiseTime(time);
            mineFollows.setAppraiseGame(game);
            mineFollows.position = jsonObject.optJSONObject("meta").optString("position");
            list.add(mineFollows);
        }
        return list;
    }

    public Observable<List<UserInfo>> requestFans(String position, String action, String userId) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, Object> map = new HashMap<>();
        if (!TextUtils.isEmpty(action) && action.equals("other")) {
            map.put("user_id", userId);
        } else {
            map.put("access_token", token);
        }
        map.put("position", position);
        map.put("page_size", page_size);
        map.put("extra", CommonUtil.getExtraParam());
        // callBack.start();
        return RetrofitHelper.getAppGameAPI().getFollowerList(map)
                .map(responseBody -> {
                    List<UserInfo> list = new ArrayList<UserInfo>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attr = array.getJSONObject(i);
                        UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(attr);
                        userInfo.position = jsonObject.getJSONObject("meta").getString("position");
                        list.add(userInfo);
                    }
                    return list;
                });
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<List<UserInfo>>() {
//                    @Override
//                    public void accept(@NonNull List<UserInfo> userInfos) throws Exception {
//                        callBack.requestSuccess(userInfos);
//                    }
//                }, new ErrorActionAppGame() {
//                    @Override
//                    public void call(AppGameResponseError error) {
//                        callBack.requestFail(error.getTitle());
//                    }
//                });
    }


}
