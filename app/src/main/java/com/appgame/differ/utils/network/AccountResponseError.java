package com.appgame.differ.utils.network;

import com.appgame.differ.R;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 用户信息错误bean
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 *  弃用了，看 {@link com.appgame.differ.base.mvp.BaseSubscriber}
 */
@Deprecated
public class AccountResponseError {
    private String error;
    @SerializedName("error_description")
    private String errorDescription;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public static AccountResponseError handle(Throwable throwable) {
        AccountResponseError responseError = null;
        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            try {
                responseError = new Gson().fromJson(exception.response().errorBody().string(), AccountResponseError.class);
                LogUtil.i("responseError = " + responseError.errorDescription);
            } catch (IOException e) {
                responseError = new AccountResponseError();
                responseError.setError(e.getMessage());
                responseError.setErrorDescription(e.getLocalizedMessage());
            }
        } else if (throwable instanceof UnknownHostException || throwable instanceof ConnectException || throwable instanceof SocketTimeoutException) {
            responseError = new AccountResponseError();
            responseError.setError(DifferApplication.getContext().getString(R.string.network_time_out));
            responseError.setErrorDescription(DifferApplication.getContext().getString(R.string.network_time_out));
        } else {
            responseError = new AccountResponseError();
            responseError.setError("服务器开小差了~");
            responseError.setErrorDescription(throwable.getLocalizedMessage());
        }
        return responseError;
    }
}
