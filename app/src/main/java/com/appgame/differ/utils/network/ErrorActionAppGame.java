package com.appgame.differ.utils.network;

import io.reactivex.functions.Consumer;

/**
 * 用户接口请求返回非200处理类
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 *
 *  * 弃用了，看 {@link com.appgame.differ.base.mvp.BaseSubscriber}
 */

@Deprecated
public abstract class ErrorActionAppGame implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) throws Exception {
        call(AppGameResponseError.handle(throwable));
    }

    public abstract void call(AppGameResponseError error);

}
