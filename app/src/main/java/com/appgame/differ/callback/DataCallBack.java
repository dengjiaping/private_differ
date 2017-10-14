package com.appgame.differ.callback;

/**
 * Created by lzx on 2017/6/15.
 */

public interface DataCallBack<T> {
    void start();

    void requestSuccess(T info);

    void requestFail(String msg);
}
