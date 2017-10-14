package com.appgame.differ.utils;

import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xian on 2017/6/4.
 */

public class SchedulersUtil {
    public static <T> ObservableTransformer<T, T> applySchedulersIO(){
        return observable -> observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io());
    }
}
