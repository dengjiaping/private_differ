package com.appgame.differ.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lzx on 2017/4/26.
 * 386707112@qq.com
 */

public class ExploreManager {

    public static String explore_tablename = "Explore";
    public static String exploreId = "exploreId";

    private DbHelper helper;

    public ExploreManager(Context context) {
        helper = DbHelper.getIntance(context);
    }

    private final static class HolderClass {
        private final static ExploreManager INSTANCE = new ExploreManager(DifferApplication.getContext());
    }

    public static ExploreManager getImpl() {
        return HolderClass.INSTANCE;
    }


    public void insertRankInfo(String exploreId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(exploreId, exploreId);
            db.insert(explore_tablename, null, values);
        }
    }

    public void addExploreId(List<RecommedInfo> recommedInfos) {
        if (recommedInfos == null || recommedInfos.size() == 0) {
            return;
        }
        for (RecommedInfo info : recommedInfos) {
//            Explore explore = getById(info.getGameId());
//            if (explore == null) {
//                Explore ex = new Explore();
//                ex.exploreId = info.getGameId();
//                ex.save();
//            }
            if (TextUtils.isEmpty(getById(info.getGameId()))) {
                insertExploreInfo(info.getGameId());
            }
        }
    }

    public void addExploreId(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (TextUtils.isEmpty(getById(id))) {
            insertExploreInfo(id);
            LogUtil.i("- 添加成功 -");
        }
//        Explore explore = getById(id);
//        if (explore == null) {
//            Explore ex = new Explore();
//            ex.exploreId = id;
//            ex.save();
//            LogUtil.i("- 添加成功 -");
//        }
    }

    public void insertExploreInfo(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(exploreId, id);
            db.insert(explore_tablename, null, values);
        }
    }

    public void getExploreIdAnsy(GetGameIdsRequest request) {
        Observable.create((ObservableOnSubscribe<List<String>>) e -> e.onNext(query()))
                .map(strings -> strings.toString().replace(" ", "").replace("[", "").replace("]", ""))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(request::result, throwable -> {
                    throwable.printStackTrace();
                    request.result("");
                });

    }

    public interface GetGameIdsRequest {
        void result(String ids);
    }

    public String getExploreId() {
        List<String> explores = query();
        return explores.toString().replace(" ", "").replace("[", "").replace("]", "");
    }

    public void clear() {
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            db.delete(explore_tablename, null, null);
        }
    }

    private String getById(String id) {
        List<String> modelList = query();
        for (String model : modelList) {
            if (model.equals(id)) {
                return model;
            }
        }
        return null;
    }

    private List<String> query(String id) {
        List<String> dataEntitiesList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            dataEntitiesList = new ArrayList<>();
            try {
                Cursor cursor = db.query(explore_tablename, null, exploreId + " = ? ", new String[]{exploreId}, null, null, null, null);
                while (cursor.moveToNext()) {
                    String explore_Id = cursor.getString(cursor.getColumnIndex(exploreId));
                    dataEntitiesList.add(explore_Id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                dataEntitiesList = new ArrayList<>();
            }
        }
        return dataEntitiesList;
    }

    private List<String> query() {
        List<String> dataEntitiesList = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            dataEntitiesList = new ArrayList<>();
            try {
                Cursor cursor = db.query(explore_tablename, null, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    String explore_Id = cursor.getString(cursor.getColumnIndex(exploreId));
                    dataEntitiesList.add(explore_Id);
                }
            } catch (Exception e) {
                e.printStackTrace();
                dataEntitiesList = new ArrayList<>();
            }
        }
        return dataEntitiesList;
    }

}
