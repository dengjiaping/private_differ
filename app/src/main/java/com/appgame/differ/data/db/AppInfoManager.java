package com.appgame.differ.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.appgame.differ.bean.AppInfo;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/5/6.
 */

public class AppInfoManager {

    public static String appinfo_tablename = "AppInfo";
    public static String appName = "appName";
    public static String packageName = "packageName";
    public static String versionCode = "versionCode";

    private DbHelper helper;

    private List<AppInfo> mAppList;

    public AppInfoManager(Context context) {
        helper = DbHelper.getIntance(context);
        mAppList = findAppInfoAll();
    }

    private final static class HolderClass {
        private final static AppInfoManager INSTANCE = new AppInfoManager(DifferApplication.getContext());
    }

    public static AppInfoManager getImpl() {
        return HolderClass.INSTANCE;
    }

    public void updateAppInfo() {
        mAppList = findAppInfoAll();
       LogUtil.i("AppInfoManager#updateAppInfo = " + mAppList.size());
    }

    public List<AppInfo> findAppInfoAll() {
        List<AppInfo> appInfos = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            try {
                Cursor cursor = db.query(appinfo_tablename, null, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    AppInfo dataEntities = new AppInfo();
                    dataEntities.appName = cursor.getString(cursor.getColumnIndex(appName));
                    dataEntities.packageName = cursor.getString(cursor.getColumnIndex(packageName));
                    dataEntities.versionCode = cursor.getInt(cursor.getColumnIndex(versionCode));
                    appInfos.add(dataEntities);
                }
            } catch (Exception e) {
                e.printStackTrace();
                appInfos = new ArrayList<>();
            }
        }
      //  LogUtil.i("AppInfoManager#应用列表 = " + appInfos.size());
        return appInfos;
    }

    public boolean hasPkg(DownloadInfo info) {
        if (info == null) {
            return false;
        } else {
            String pkgName = info.downloadPackageName;
            if (TextUtils.isEmpty(pkgName)) {
                return false;
            } else {
                for (AppInfo appInfo : mAppList) {
                    if (appInfo.getPackageName().equals(pkgName)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public boolean hasPkg(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        } else {
            for (AppInfo appInfo : mAppList) {
                if (appInfo.getPackageName().equals(pkgName)) {
                    return true;
                }
            }
            return false;
        }
    }

//    public void clear(){
//        SQLiteDatabase db = helper.getReadableDatabase();
//        if (db.isOpen()) {
//            db.delete(appinfo_tablename,null,null);
//        }
//    }

    public void resetInfo(List<AppInfo> list) {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            db.delete(appinfo_tablename, null, null);
            for (AppInfo info : list) {
                ContentValues values = new ContentValues();
                values.put(packageName, info.getPackageName());
                values.put(appName, info.getAppName());
                values.put(versionCode, info.getVersionCode());
                db.insert(appinfo_tablename, null, values);
            }
        }
    }


}
