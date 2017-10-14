package com.appgame.differ.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.appgame.differ.bean.AppInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.rx.RxBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载手机上的所有应用
 * Created by lzx on 2017/3/10.
 * 386707112@qq.com
 */

public class AppLoadService extends IntentService {

    public AppLoadService() {
        super("AppLoadService");
    }

    private PackageManager mPackageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mPackageManager = getPackageManager();
    }

    @Override
    protected void onHandleIntent(Intent handleIntent) {
        synchronized (AppLoadService.class) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, 0);
            try {
                List<AppInfo> mList = new ArrayList<>();
                for (ResolveInfo resolveInfo : resolveInfos) {
                    AppInfo appInfo = new AppInfo();
                    String pkgName = resolveInfo.activityInfo.packageName;
                    ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES);

                    String appName = mPackageManager.getApplicationLabel(applicationInfo).toString();
                    int versionCode = mPackageManager.getPackageInfo(pkgName, 0).versionCode;
                    appInfo.setPackageName(pkgName);
                    appInfo.setAppName(appName);
                    appInfo.setVersionCode(versionCode);

                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) { //判断是否是系统应用 ApplicationInfo#isSystemApp()
                    } else {
                        mList.add(appInfo);
                    }
                }

                AppInfoManager.getImpl().resetInfo(mList);
                RxBus.getBus().send(EvenConstant.KEY_RELOAD_APP_LIST);
            } catch (Exception e) {
                LogUtil.i("AppLoadService = " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
