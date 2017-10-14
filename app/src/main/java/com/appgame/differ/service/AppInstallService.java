package com.appgame.differ.service;

import android.app.IntentService;
import android.content.Intent;

import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.rx.RxBus;

/**
 * 监听app安装卸载服务
 * Created by lzx on 2017/3/10.
 * 386707112@qq.com
 */

public class AppInstallService extends IntentService {

    public AppInstallService() {
        super("AppInstallService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            //重新加载一个APP列表
            startService(new Intent(this, AppLoadService.class));

            String packageName = intent.getData().getSchemeSpecificPart();

            LogUtil.i("packageName = "+packageName);

            if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                RxBus.getBus().send(EvenConstant.KEY_REFRESH_TOPIC_LIST);
                //检查安装后是否删除安装包
                GameDownloadManager.getImpl().deletePkgWhenInstall(packageName);

            } else if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                GameDownloadManager.getImpl().deleteDownloadTasks(packageName);
            }
        }
    }
}
