package com.appgame.differ.utils.rx;

import com.trello.rxlifecycle2.LifecycleTransformer;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by lzx on 2017/3/29.
 * 386707112@qq.com
 */

public class RxBus {
    private final FlowableProcessor<Object> _bus;
    private volatile static RxBus instance;
    private long lastTime = 0;

    public RxBus() {
        _bus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getBus() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null)
                    instance = new RxBus();
            }
        }
        return instance;
    }

    public void send(Object o) {
        if (System.currentTimeMillis() - lastTime < 100) {
            return;
        }
        lastTime = System.currentTimeMillis();
        _bus.onNext(o);
    }

    /**
     * dosen't designation to use special thread
     * ,It's depending on what the 'send' method use
     *
     * @param lifecycleTransformer rxLifecycle
     * @return
     */
    public Flowable toObservable(LifecycleTransformer lifecycleTransformer) {
        return _bus
                .compose(lifecycleTransformer)
                .onBackpressureDrop();
    }



    /**
     * designation use the MainThread, whatever the 'send' method use
     *
     * @param lifecycleTransformer rxLifecycle
     * @return
     */
    public Flowable toMainThreadObservable(LifecycleTransformer lifecycleTransformer) {
        return _bus
                .observeOn(AndroidSchedulers.mainThread())
                .compose(lifecycleTransformer)
                .onBackpressureDrop();
    }

//    public Flowable<Object> toMainThreadObservable(LifecycleTransformer<Object> lifecycleTransformer) {
//        return _bus
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(lifecycleTransformer)
//                .onBackpressureDrop();
//    }


}
