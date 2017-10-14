package com.appgame.differ.utils.network;

import com.appgame.differ.R;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 手游社用户信息错误bean
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 *
 * 弃用了，看 {@link com.appgame.differ.base.mvp.BaseSubscriber}
 */

@Deprecated
public class AppGameResponseError {
    private int code;
    private String title;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static AppGameResponseError handle(Throwable throwable) {
        AppGameResponseError responseError = new AppGameResponseError();
        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            try {
                JSONObject jsonObject = new JSONObject(exception.response().errorBody().string());
                LogUtil.i("错误信息 = " + jsonObject.toString());
                JSONArray array = jsonObject.optJSONArray("errors");
                if (array != null) {
                    JSONObject errors = array.getJSONObject(0);
                    responseError.setCode(errors.optInt("code", -1));
                    responseError.setTitle(errors.optString("title", ""));
                } else {
                    responseError.setCode(jsonObject.optInt("error_code", -1));
                    responseError.setTitle(jsonObject.optString("error_msg"));
                }
            } catch (Exception e) {
                responseError.setCode(-1);
                responseError.setTitle("服务器开小差了~");
                e.printStackTrace();
            }
        } else if (throwable instanceof UnknownHostException || throwable instanceof ConnectException || throwable instanceof SocketTimeoutException) {
            responseError.setCode(-1);
            responseError.setTitle(DifferApplication.getContext().getString(R.string.network_time_out));
        } else if (throwable instanceof IOException) {
            responseError.setCode(-1);
            responseError.setTitle("请求失败");
        } else {
            responseError.setCode(0);
            responseError.setTitle("服务器开小差了~");
            throwable.printStackTrace();
        }
        return responseError;
    }
}
