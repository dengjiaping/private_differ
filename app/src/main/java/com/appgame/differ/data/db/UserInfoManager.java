package com.appgame.differ.data.db;

import com.appgame.differ.bean.user.AchievesInfo;
import com.appgame.differ.bean.user.RankInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.SpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/4/28.
 * 386707112@qq.com
 */

public class UserInfoManager {


    private final static class HolderClass {
        private final static UserInfoManager INSTANCE = new UserInfoManager();
    }

    public static UserInfoManager getImpl() {
        return HolderClass.INSTANCE;
    }

    /**
     * 清空数据库
     */
    public void clear() {
        SpUtil.getInstance().putString(AppConstants.USER_ID, "");
        SpUtil.getInstance().putString(AppConstants.USER_AVATAR, "");
        SpUtil.getInstance().putString(AppConstants.USER_NICKNAME, "");
    }

    public String getUserId() {
        return SpUtil.getInstance().getString(AppConstants.USER_ID, "");
    }

    public void saveUserId(String userId) {
        SpUtil.getInstance().putString(AppConstants.USER_ID, userId);
    }

    public String getUserAvatar() {
        return SpUtil.getInstance().getString(AppConstants.USER_AVATAR, "");
    }

    public void saveUserAvatar(String avatar) {
        SpUtil.getInstance().putString(AppConstants.USER_AVATAR, avatar);
    }

    public String getNickName() {
        return SpUtil.getInstance().getString(AppConstants.USER_NICKNAME, "");
    }

    public void saveNickName(String nickName) {
        SpUtil.getInstance().putString(AppConstants.USER_NICKNAME, nickName);
    }

    public void saveUserInfo(UserInfo userInfo) {
        saveUserId(userInfo.getUserId());
        saveUserAvatar(userInfo.getAvatar());
        saveNickName(userInfo.getNickName());
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setAvatar(getUserAvatar());
        userInfo.setUserId(getUserId());
        userInfo.setNickName(getNickName());
        return userInfo;
    }

    /**
     * 解析json
     */
    public UserInfo resolveUserInfo(JSONObject object) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(object.optString("id"));
        JSONObject userAttr = object.optJSONObject("attributes");
        userInfo.setUserName(userAttr.optString("username"));
        userInfo.setNickName(userAttr.optString("nickname"));
        userInfo.setAvatar(userAttr.optString("avatar"));
        userInfo.setSex(userAttr.optString("sex"));
        userInfo.setBirthday(userAttr.optString("birthday"));
        userInfo.setRemark(userAttr.optString("remark"));
        userInfo.setFollower(userAttr.optString("follower"));
        userInfo.setFollowing(userAttr.optString("following"));
        userInfo.setPublicFollower(userAttr.optString("public_follower"));
        userInfo.setPublicFollowing(userAttr.optString("public_following"));
        userInfo.setCover(userAttr.optString("cover"));

        userInfo.setLast_appraise(userAttr.optString("last_appraise"));

        JSONObject rankObject = userAttr.optJSONObject("rank");
        if (rankObject != null) {
            JSONObject rankAttr = rankObject.optJSONObject("attributes");
            if (rankAttr != null) {
                RankInfo rankInfo = new RankInfo();
                rankInfo.setId(rankObject.optString("id"));
                rankInfo.setIcon(rankAttr.optString("icon"));
                rankInfo.setName(rankAttr.optString("name"));
                userInfo.setRank(rankInfo);
            }
        }

        userInfo.setFollowed(userAttr.optBoolean("is_followed"));

        JSONArray achArray = userAttr.optJSONArray("achieves");
        if (achArray != null) {
            List<AchievesInfo> achievesInfos = new ArrayList<>();
            for (int i = 0; i < achArray.length(); i++) {
                AchievesInfo info = new AchievesInfo();
                JSONObject achObj = achArray.optJSONObject(i);
                info.setAcId(achObj.optString("id"));
                info.setName(achObj.optJSONObject("attributes").optString("name"));
                info.setIcon(achObj.optJSONObject("attributes").optString("icon"));
                info.setStatus(achObj.optJSONObject("attributes").optBoolean("status"));
                info.setUserId(userInfo.getUserId());
                achievesInfos.add(info);
            }
            userInfo.setAchieves(achievesInfos);
        }

        return userInfo;
    }


}
