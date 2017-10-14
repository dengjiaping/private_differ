package com.appgame.differ.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.appgame.differ.data.cache.DataCacheManager;

/**
 * Created by lzx on 2017/6/19.
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String name = "appgame";
    private static final int version = 7;
    private static volatile DbHelper instance;

    public DbHelper(Context context) {
        super(context, name, null, version);
    }


    private final String DATACACHE = "create table "
            + DataCacheManager.cache_tablename + " ( "
            + DataCacheManager.key + " text not null primary key, "
            + DataCacheManager.json + " text);";

    private final String APPINFO = "create table "
            + AppInfoManager.appinfo_tablename + " ( "
            + AppInfoManager.packageName + " text not null primary key, "
            + AppInfoManager.appName + " text, "
            + AppInfoManager.versionCode + " text);";

    private final String EXPLORE = "create table "
            + ExploreManager.explore_tablename + " ( "
            + ExploreManager.exploreId + " text not null primary key);";

    private final String TASKMANAGERMODEL = "create table "
            + GameDownloadManager.download_tablename + " ( "
            + GameDownloadManager.downloadId + " integer not null primary key, "
            + GameDownloadManager.downloadPath + " text, "
            + GameDownloadManager.downloadUrl + " text, "
            + GameDownloadManager.gameName + " text, "
            + GameDownloadManager.gameCategory + " text, "
            + GameDownloadManager.gameCover + " text, "
            + GameDownloadManager.gameIcon + " text, "
            + GameDownloadManager.packageName + " text, "
            + GameDownloadManager.gameSize + " text, "
            + GameDownloadManager.gameId + " text, "
            + GameDownloadManager.downloadLinkId + " text, "
            + GameDownloadManager.downloadFlag + " text, "
            + GameDownloadManager.isDownloadAuto + " text);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATACACHE);
        db.execSQL(APPINFO);
        db.execSQL(EXPLORE);
        db.execSQL(TASKMANAGERMODEL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 7) {
            db.execSQL("DROP TABLE UserInfo;");
            db.execSQL("DROP TABLE RankInfo;");
            db.execSQL("DROP TABLE AchievesInfo;");
        }
    }

    public static DbHelper getIntance(Context context) {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    instance = new DbHelper(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
}
