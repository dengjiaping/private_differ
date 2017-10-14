package com.appgame.differ.data.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.data.db.DbHelper;
import com.appgame.differ.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存管理类
 * Created by lzx on 2017/6/9.
 */

public class DataCacheManager {

    public static String cache_tablename = "DataCacheInfo";
    public static String key = "key";
    public static String json = "json";

    private DbHelper helper;

    public DataCacheManager(Context context) {
        helper = DbHelper.getIntance(context);
    }

    private final static class HolderClass {
        private final static DataCacheManager INSTANCE = new DataCacheManager(DifferApplication.getContext());
    }

    public static DataCacheManager getImpl() {
        return HolderClass.INSTANCE;
    }


    /**
     * 是否存在缓存
     */
    public boolean hasCache(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        } else {
            DataCacheInfo info = getDataCacheInfo(key);
            return info != null;
        }
    }


    public List<DataCacheInfo> findDataCacheInfoAll() {
        List<DataCacheInfo> dataCacheInfos = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            dataCacheInfos = new ArrayList<>();
            try {
                Cursor cursor = db.query(cache_tablename, null, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    DataCacheInfo dataEntities = new DataCacheInfo();
                    dataEntities.key = cursor.getString(cursor.getColumnIndex(key));
                    dataEntities.json = cursor.getString(cursor.getColumnIndex(json));
                    dataCacheInfos.add(dataEntities);
                }
            } catch (Exception e) {
                e.printStackTrace();
                dataCacheInfos = new ArrayList<>();
            }
        }
        return dataCacheInfos;
    }

    public DataCacheInfo getDataCacheInfo(String key) {
        List<DataCacheInfo> list = findDataCacheInfoAll();
        for (DataCacheInfo cacheInfo : list) {
            if (cacheInfo.key.equals(key)) {
                return cacheInfo;
            }
        }
        return null;
    }

    public JSONObject getCacheJSONObject(String key) {
        String json = getDataCacheInfo(key).json;
        try {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int update(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            return db.update(cache_tablename, values, whereClause, whereArgs);
        }
        return 0;
    }

    public void insert(DataCacheInfo info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(key, info.key);
            values.put(json, info.json);
            db.insert(cache_tablename, null, values);
        }
    }

    public boolean updateDataCacheInfo(String key, String json) {
        DataCacheInfo cacheInfo = getDataCacheInfo(key);
        if (cacheInfo != null) {
            ContentValues values = new ContentValues();
            values.put("json", json);
            int num = update(values, "key = ?", new String[]{key});
            LogUtil.i("缓存更新 key = " + key + " 是否成功 = " + (num == 1));
            return num == 1;
        }
        return false;
    }

    public void addDataCacheInfo(String key, String json) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(json)) {
            boolean hasCache = hasCache(key);
            if (!hasCache) {
                DataCacheInfo cacheInfo = new DataCacheInfo();
                cacheInfo.key = key;
                cacheInfo.json = json;
                insert(cacheInfo);
            } else {
                updateDataCacheInfo(key, json);
            }
        }
    }

    public void deleteDataCacheInfo(String key) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(cache_tablename, "key = ?", new String[]{key});
        }
    }

    public void clearAllCache() {
        // DataSupport.deleteAll(DataCacheInfo.class);
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(cache_tablename, "", null);
        }
    }


}
