package com.appgame.differ.utils;

import android.util.Log;

import com.appgame.differ.data.constants.AppConstants;

/**
 * Created by lzx on 2017/2/21.
 * 386707112@qq.com
 */
//by zhengxiaobo 2017-3-27
//修改日记格式
//类定位多少行(GamesDetailPresenter.java:293)
public class LogUtil {
    private static final String TAG = "LogUtil";

    private static boolean isShow = AppConstants.IS_DEBUG;

    public static boolean isShow() {
        return isShow;
    }

    public static void setShow(boolean show) {
        isShow = show;
    }

    public static void i(String tag, String msg) {
        if (isShow) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isShow) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {

        if (isShow) {
            Log.e(tag, msg);
        }
    }


    public static void all(String msg) {

        if (isShow) {
            Log.e("all", msg);
        }
    }


    public static void i(String msg) {
        if (isShow) {
            Log.i(TAG, msg);
        }
    }


    public static void w(String msg) {

        if (isShow) {
            Log.w(TAG, msg);
        }
    }


    public static void e(String msg) {

        if (isShow) {
            Log.e(TAG, msg);
        }
    }


    public static void v(String msg) {

        e(msg);
    }


    public static void d(String msg) {

        v(msg);
    }
}
