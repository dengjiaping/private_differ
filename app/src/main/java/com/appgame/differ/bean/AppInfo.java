package com.appgame.differ.bean;

/**
 * 手机应用信息
 * Created by lzx on 2017/3/10.
 * 386707112@qq.com
 */

public class AppInfo  {
    public String appName;
    public String packageName;
    public int versionCode;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
