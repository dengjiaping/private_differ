package com.appgame.differ.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.appgame.differ.service.AppInstallService;
import com.appgame.differ.service.AppLoadService;

/**
 * 监听app安装卸载
 * Created by lzx on 2017/3/10.
 * 386707112@qq.com
 */

public class AppInstallReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, AppLoadService.class));

        intent = new Intent(intent);
        intent.setClass(context, AppInstallService.class);
        context.startService(intent);
    }
}
