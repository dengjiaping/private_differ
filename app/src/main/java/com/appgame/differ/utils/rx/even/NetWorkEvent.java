package com.appgame.differ.utils.rx.even;

/**
 * Created by lzx on 2017/5/2.
 * 386707112@qq.com
 */

public class NetWorkEvent {
    public boolean isNetWorkConnected;
    public boolean isWifi;

    public NetWorkEvent(boolean isNetWorkConnected, boolean isWifi) {
        this.isNetWorkConnected = isNetWorkConnected;
        this.isWifi = isWifi;
    }
}
