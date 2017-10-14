package com.appgame.differ.utils;

import com.appgame.differ.data.constants.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lzx on 2017/8/14.
 */

public class LoginUtil {
    private final static class HolderClass {
        private final static LoginUtil INSTANCE = new LoginUtil();
    }

    public static LoginUtil getImpl() {
        return HolderClass.INSTANCE;
    }

    public void saveLoginInfo(JSONObject object) throws JSONException {
        String access_token = object.getString("access_token");
        String refresh_token = object.getString("refresh_token");
        long refresh_token_expires_in = object.getLong("refresh_token_expires_in");

        SpUtil.getInstance().putString(AppConstants.ACCESS_TOKEN, access_token);
        SpUtil.getInstance().putString(AppConstants.REFRESH_TOKEN, refresh_token);
        SpUtil.getInstance().putLong(AppConstants.REFRESH_TOKEN_EXPIRES_IN, refresh_token_expires_in * 1000);
        SpUtil.getInstance().putLong(AppConstants.LAST_REFRESH_TIME, System.currentTimeMillis());//更新时间
    }

    public void clearLoginInfo(){
        SpUtil.getInstance().putBoolean(AppConstants.IS_LOGIN, false);
        SpUtil.getInstance().putBoolean(AppConstants.INCOGNITO_LOGIN, false);
        SpUtil.getInstance().putString(AppConstants.ACCESS_TOKEN, "");
        SpUtil.getInstance().putString(AppConstants.REFRESH_TOKEN, "");
        SpUtil.getInstance().putLong(AppConstants.LAST_REFRESH_TIME, 0);
    }

}
