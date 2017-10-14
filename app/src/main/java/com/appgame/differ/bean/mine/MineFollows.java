package com.appgame.differ.bean.mine;

import android.text.TextUtils;

import com.appgame.differ.bean.user.UserInfo;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public class MineFollows {
    public String position;
    private UserInfo mUserInfo;

    private String appraiseTime;
    private String appraiseGame;


    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    public String getAppraiseTime() {
        if (TextUtils.isEmpty(appraiseTime)) {
            appraiseTime = "0";
        }
        return appraiseTime;
    }

    public void setAppraiseTime(String appraiseTime) {
        this.appraiseTime = appraiseTime;
    }

    public String getAppraiseGame() {
        return appraiseGame;
    }

    public void setAppraiseGame(String appraiseGame) {
        this.appraiseGame = appraiseGame;
    }
}
