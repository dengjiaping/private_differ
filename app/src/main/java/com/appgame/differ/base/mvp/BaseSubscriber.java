package com.appgame.differ.base.mvp;

import com.appgame.differ.R;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.observers.ResourceObserver;
import retrofit2.HttpException;

/**
 * Created by lzx on 2017/8/10.
 * 统一处理订阅者
 */
public abstract class BaseSubscriber<T> extends ResourceObserver<T> {

    private BaseContract.BaseView mView;
    private boolean isLoadMore;

    public BaseSubscriber(BaseContract.BaseView view, boolean isLoadMore) {
        mView = view;
        this.isLoadMore = isLoadMore;
    }

    public abstract void onSuccess(T t);

    public void onFailure(int code, String message) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNext(T response) {
        if (mView == null) return;
        mView.complete(); //完成操作
        onSuccess(response);
    }

    @Override
    public void onError(Throwable throwable) {
        if (mView == null) return;
        mView.complete();//完成操作

        if (throwable instanceof HttpException) {
            HttpException exception = (HttpException) throwable;
            try {
                JSONObject jsonObject = new JSONObject(exception.response().errorBody().string());
                LogUtil.i("错误信息 = " + jsonObject.toString());
                JSONArray array = jsonObject.optJSONArray("errors");
                String errorMsg;
                int errorCode;
                if (array != null) {
                    JSONObject errors = array.getJSONObject(0);
                    errorCode = errors.optInt("code", -1);
                    errorMsg = errors.optString("title");
                } else {
                    errorCode = jsonObject.optInt("error_code", -1);
                    errorMsg = jsonObject.optString("error_msg");
                }
                mView.showError(errorMsg, isLoadMore);
                onFailure(errorCode, errorMsg);
            } catch (Exception e) {
                mView.showError("服务器开小差了~", isLoadMore);
                onFailure(-1, "服务器开小差了~");
                e.printStackTrace();
            }
        } else if (throwable instanceof UnknownHostException || throwable instanceof ConnectException || throwable instanceof SocketTimeoutException) {
            mView.showError(DifferApplication.getContext().getString(R.string.network_time_out), isLoadMore);
            onFailure(-1, DifferApplication.getContext().getString(R.string.network_time_out));
        } else if (throwable instanceof IOException) {
            mView.showError("请求失败", isLoadMore);
            onFailure(-1, "请求失败");
        } else {
            mView.showError("服务器开小差了~", isLoadMore);
            onFailure(-1, "服务器开小差了~");
            throwable.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
    }
}
