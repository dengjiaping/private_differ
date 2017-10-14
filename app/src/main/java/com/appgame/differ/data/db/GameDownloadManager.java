package com.appgame.differ.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.appgame.differ.bean.AppInfo;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.download.TasksManagerModel;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxBus;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * 游戏下载管理类
 * Created by xian on 2017/4/22.
 */

public class GameDownloadManager {


    public static String download_tablename = "TasksManagerModel";
    public static String downloadId = "downloadId";
    public static String downloadPath = "downloadPath";
    public static String downloadUrl = "downloadUrl";
    public static String gameName = "gameName";
    public static String gameCategory = "gameCategory";
    public static String gameCover = "gameCover";
    public static String gameIcon = "gameIcon";
    public static String packageName = "packageName";
    public static String gameSize = "gameSize";
    public static String gameId = "gameId";
    public static String downloadLinkId = "downloadLinkId";
    public static String downloadFlag = "downloadFlag";
    public static String isDownloadAuto = "isDownloadAuto";  //0 true 1 false  (String)


    private DbHelper helper;

    public GameDownloadManager(Context context) {
        helper = DbHelper.getIntance(context);
    }

    private final static class HolderClass {
        private final static GameDownloadManager INSTANCE = new GameDownloadManager(DifferApplication.getContext());
    }

    public static GameDownloadManager getImpl() {
        return HolderClass.INSTANCE;
    }

    public List<TasksManagerModel> findAll() {
        List<TasksManagerModel> list = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        if (db.isOpen()) {
            list = new ArrayList<>();
            try {
                Cursor cursor = db.query(download_tablename, null, null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    TasksManagerModel dataEntities = new TasksManagerModel();
                    setData(cursor, dataEntities);
                    list.add(dataEntities);
                }
            } catch (Exception e) {
                e.printStackTrace();
                list = new ArrayList<>();
            }
        }
        return list;
    }

    private void setData(Cursor cursor, TasksManagerModel dataEntities) {
        dataEntities.setDownloadId(cursor.getInt(cursor.getColumnIndex(downloadId)));
        dataEntities.setDownloadPath(cursor.getString(cursor.getColumnIndex(downloadPath)));
        dataEntities.setDownloadUrl(cursor.getString(cursor.getColumnIndex(downloadUrl)));
        dataEntities.setGameName(cursor.getString(cursor.getColumnIndex(gameName)));
        dataEntities.setGameCategory(cursor.getString(cursor.getColumnIndex(gameCategory)));
        dataEntities.setGameCover(cursor.getString(cursor.getColumnIndex(gameCover)));
        dataEntities.setGameIcon(cursor.getString(cursor.getColumnIndex(gameIcon)));
        dataEntities.setPackageName(cursor.getString(cursor.getColumnIndex(packageName)));
        dataEntities.setGameSize(cursor.getString(cursor.getColumnIndex(gameSize)));
        dataEntities.setGameId(cursor.getString(cursor.getColumnIndex(gameId)));
        dataEntities.setDownloadLinkId(cursor.getString(cursor.getColumnIndex(downloadLinkId)));
        dataEntities.setDownloadFlag(cursor.getInt(cursor.getColumnIndex(downloadFlag)));
        dataEntities.isDownloadAuto = cursor.getString(cursor.getColumnIndex(isDownloadAuto));
    }

    public boolean insert(TasksManagerModel info) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(downloadId, info.getDownloadId());
            values.put(downloadPath, info.getDownloadPath());
            values.put(downloadUrl, info.getDownloadUrl());
            values.put(gameName, info.getGameName());
            values.put(gameCategory, info.getGameCategory());
            values.put(gameCover, info.getGameCover());
            values.put(gameIcon, info.getGameIcon());
            values.put(packageName, info.getPackageName());
            values.put(gameSize, info.getGameSize());
            values.put(gameId, info.getGameId());
            values.put(downloadLinkId, info.getDownloadLinkId());
            values.put(downloadFlag, info.getDownloadFlag());
            values.put(isDownloadAuto, info.isDownloadAuto);
            long num = db.insert(download_tablename, null, values);
            return num != -1;
        }
        return false;
    }

    public int update(ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            return db.update(download_tablename, values, whereClause, whereArgs);
        }
        return 0;
    }


    /**
     * 添加下载任务
     */
    public TasksManagerModel addDownLoadTasks(DownloadInfo mDownloadInfo) {
        if (mDownloadInfo == null) {
            return null;
        }
        String savePath = FileUtil.createPath(mDownloadInfo.downloadUrl);
        if (TextUtils.isEmpty(mDownloadInfo.downloadUrl) || TextUtils.isEmpty(savePath)) {
            return null;
        }
        final int id = FileDownloadUtils.generateId(mDownloadInfo.downloadUrl, savePath);
        TasksManagerModel model = getById(id);  //根据id查找是否已存在下载任务
        if (model != null) {
            return model;
        }
        return addTaskToDb(mDownloadInfo, savePath);
    }

    /**
     * 根据id查找下载任务
     */
    private TasksManagerModel getById(int id) {
        List<TasksManagerModel> modelList = findAll();
        for (TasksManagerModel model : modelList) {
            if (model.downloadId == id) {
                return model;
            }
        }
        return null;
    }

    /**
     * 保存任务到数据库
     */
    private TasksManagerModel addTaskToDb(DownloadInfo info, final String path) {
        if (TextUtils.isEmpty(info.downloadUrl) || TextUtils.isEmpty(path)) {
            return null;
        }
        final int id = FileDownloadUtils.generateId(info.downloadUrl, path);
        TasksManagerModel model = new TasksManagerModel();
        model.downloadId = id;
        model.downloadPath = path;
        model.downloadUrl = info.downloadUrl;
        model.gameName = info.downloadGameName;
        model.gameCategory = info.downloadGameCategory;
        model.gameCover = info.downloadGameCover;
        model.gameIcon = info.downloadGameIcon;
        model.packageName = info.downloadPackageName;
        model.gameSize = info.downloadGameSize;
        model.gameId = info.downloadGameId;
        model.downloadLinkId = info.downloadLinkId;
        model.isDownloadAuto = "false";
        boolean succeed = insert(model);
        if (succeed) {
            RxBus.getBus().send(EvenConstant.KEY_DOWNLOAD_RED_DOT);
        }
        return succeed ? model : null;
    }

    /**
     * 更新下载任务状态
     */
    public boolean updateTaskFlag(int flag, int downloadId) {
        TasksManagerModel model = getById(downloadId); //查找当前下载任务
        if (model != null) { //如果存在
            ContentValues values = new ContentValues();
            values.put("downloadFlag", flag);
            int num = update(values, "downloadId = ?", new String[]{String.valueOf(downloadId)});
            return num == 1;
        }
        return false;
    }

    /**
     * 检查文件夹，更新下载状态
     */
    public void checkFileToUpdateTaskFlag() {
        List<TasksManagerModel> tasks = findAll();
        if (tasks == null || tasks.size() == 0) {
            return;
        }
        for (TasksManagerModel model : tasks) {
            if (!FileUtil.isExistsInstallationPackage(model.downloadUrl)) {
                updateTaskFlag(FileDownloadStatus.pending, model.downloadId);
            }
        }
    }

    /**
     * 得到下载id
     */
    public int getDownloadId(String downloadUrl) {
        if (TextUtils.isEmpty(downloadUrl)) {
            return -1;
        }
        String savePath = FileUtil.createPath(downloadUrl);
        if (TextUtils.isEmpty(savePath)) {
            return -1;
        }
        return FileDownloadUtils.generateId(downloadUrl, savePath);
    }

    /**
     * 改变自动下载标志
     */
    public void changeAutoDownloadFlag(String downloadUrl, boolean isAutoDownload) {
        ContentValues values = new ContentValues();
        values.put("isDownloadAuto", isAutoDownload ? "true" : "false");
        int num = update(values, "downloadUrl = ?", new String[]{downloadUrl});
    }

    /**
     * 查看是否需要自动下载
     */
    public boolean isAutoDownload(String downloadUrl) {
        List<TasksManagerModel> list = getDownloadTasks();
        for (TasksManagerModel model : list) {
            if (model.downloadUrl.equals(downloadUrl)) {
                return model.isDownloadAuto.equals("true");
            }
        }
        return false;
    }

    /**
     * 得到下载链接id
     */
    public String getDownloadLinkId(int downloadId) {
        String downloadLinkId = "";
        List<TasksManagerModel> tasks = findAll();
        if (tasks == null || tasks.size() == 0) {
            return "";
        }
        for (TasksManagerModel model : tasks) {
            if (model.downloadId == downloadId) {
                downloadLinkId = model.downloadLinkId;
                break;
            }
        }
        return downloadLinkId;
    }

    /**
     * 获取下载列表
     */
    public List<TasksManagerModel> getDownloadTasks() {
        return findAll();
    }

    /**
     * 获取下载列表(异步)
     */
    public void getDownloadTasksAnsy(DownloadTasksCallBack callBack) {
        Observable.create((ObservableOnSubscribe<List<TasksManagerModel>>) e -> e.onNext(findAll()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::getTasks);
    }

    /**
     * 获取下载任务列表（除去已安装）
     */
    public void getDownloadTasksExceptFinishAnsy(DownloadTasksCallBack callBack) {
        Observable.create((ObservableOnSubscribe<List<TasksManagerModel>>) e -> e.onNext(findAll()))
                .map(tasksManagerModels -> {
                    Iterator<TasksManagerModel> iterator = tasksManagerModels.iterator();
                    while (iterator.hasNext()) {
                        TasksManagerModel model = iterator.next();
                        boolean hasApp = AppInfoManager.getImpl().hasPkg(model.packageName);
                        if (hasApp) {
                            iterator.remove();
                        }
                    }
                    return tasksManagerModels;
                })
                .map(tasksManagerModels -> {
                    Collections.reverse(tasksManagerModels);
                    return tasksManagerModels;
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::getTasks);
    }

    public interface DownloadTasksCallBack {
        void getTasks(List<TasksManagerModel> tasks);
    }

    /**
     * 判断是否需要显示小红点
     */
    public void isShowRedDot(IsShowRedDotCallBack callBack) {
        //除了列表全部显示安装按钮，其他都显示小红点
        Observable.create((ObservableOnSubscribe<List<TasksManagerModel>>) e -> e.onNext(findAll()))
                .map(tasksManagerModels -> {
                    Iterator<TasksManagerModel> iterator = tasksManagerModels.iterator();
                    while (iterator.hasNext()) {
                        TasksManagerModel model = iterator.next();
                        boolean hasApp = AppInfoManager.getImpl().hasPkg(model.packageName);
                        if (hasApp) {
                            iterator.remove();
                        }
                    }
                    return tasksManagerModels;
                })
                .map(tasksManagerModels -> {
                    if (tasksManagerModels.size() == 0) {
                        return false;
                    } else {
                        List<String> list = new ArrayList<>();
                        for (TasksManagerModel model : tasksManagerModels) {
                            if (model.downloadFlag == DownLoadFlag.has_pkg) {
                                list.add(model.gameName);
                            }
                        }
                        return list.size() != tasksManagerModels.size();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::isShowDot);
    }

    public interface IsShowRedDotCallBack {
        void isShowDot(boolean isShow);
    }

    /**
     * 得到下载游戏名字
     */
    public String getDownloadGameName(int downloadId) {
        String gameName = "";
        List<TasksManagerModel> tasks = findAll();
        if (tasks == null || tasks.size() == 0) {
            gameName = "";
        }
        for (TasksManagerModel model : tasks) {
            if (model.downloadId == downloadId) {
                gameName = model.gameName;
                break;
            }
        }
        return gameName;
    }

    /**
     * 得到下载路径
     */
    public String getDownloadPath(String downloadUrl) {
        String savePath = FileUtil.createPath(downloadUrl);
        if (TextUtils.isEmpty(savePath)) {
            return "";
        }
        return savePath;
    }

    /**
     * 删除安装包
     */
    public void deletePkgWhenInstall(String packageName) {

        boolean isDeletePkg = SpUtil.getInstance().getBoolean(AppConstants.IS_DELETE_PKG, true);
        if (!isDeletePkg) {
            return;
        }
        List<TasksManagerModel> tasks = findAll();
        if (tasks == null || tasks.size() == 0) {
            return;
        }
        for (TasksManagerModel model : tasks) {
            String pkg = model.packageName;
            if (!TextUtils.isEmpty(pkg)) {
                if (pkg.equals(packageName)) {
                    boolean isSuccess = FileUtil.deleteGamePkgFile(model.downloadUrl);
                    if (isSuccess) {
                        ToastUtil.showShort("安装包已删除");
                    }
                    break;
                }
            }
        }
    }

    /**
     * 删除一条下载记录
     */
    public void deleteDownloadTasks(String downloadUrl, String packageName) {
        //   DataSupport.deleteAll(TasksManagerModel.class, "downloadUrl = ? and packageName = ?", downloadUrl, packageName);
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(download_tablename, "downloadUrl = ? and packageName = ?", new String[]{downloadUrl, packageName});
        }
    }

    public void deleteDownloadTasks(String packageName) {
        //   DataSupport.deleteAll(TasksManagerModel.class, "packageName = ?", packageName);
        SQLiteDatabase db = helper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(download_tablename, "packageName = ?", new String[]{packageName});
        }
    }

    /**
     * 得到游戏id集合
     */
    public void getDownloadGameId(GetGameIdsRequest request) {
        Observable.create((ObservableOnSubscribe<List<TasksManagerModel>>) e -> {
            List<TasksManagerModel> tasks = findAll();
            e.onNext(tasks);
        }).map(tasksManagerModels -> {
            String idsString;
            List<String> ids = new ArrayList<>();
            if (tasksManagerModels == null || tasksManagerModels.size() == 0) {
                idsString = "";
            } else {
                boolean hasPkg;
                for (TasksManagerModel model : tasksManagerModels) {
                    hasPkg = !TextUtils.isEmpty(model.packageName) && AppInfoManager.getImpl().hasPkg(model.packageName);
                    if (model.downloadFlag == DownLoadFlag.has_pkg || hasPkg) {
                        ids.add(model.gameId);
                    }
                }
            }
            idsString = ids.toString().replace(" ", "").replace("[", "").replace("]", "");
            return idsString;
        }).map(s -> {
            if (TextUtils.isEmpty(s)) {
                return ExploreManager.getImpl().getExploreId();
            } else {
                return s + "," + ExploreManager.getImpl().getExploreId();
            }
        })
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

    /**
     * 下载完成请求
     */
    public void requestDownloadSuccess(String downlink_id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
        Map<String, String> map = new HashMap<>();
        map.put("downlink_id", downlink_id);
        if (isLogin) {
            map.put("access_token", token);
        }
        map.put("extra", CommonUtil.getExtraParam());
        RetrofitHelper.getAppGameAPI().afterDownloadSuccess(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> LogUtil.i("下载完成回调 = " + responseBody.string()), throwable -> LogUtil.i("下载完成回调Throwable = " + throwable.getMessage()));
    }


    public void initApiInfo(LifecycleTransformer<ResponseBody> lifecycleTransformer) {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            //检查文件夹，更新下载状态
            GameDownloadManager.getImpl().checkFileToUpdateTaskFlag();
            e.onNext(true);
        }).map(aBoolean -> {
            List<AppInfo> list = AppInfoManager.getImpl().findAppInfoAll();
            List<String> pkglist = new ArrayList<>();
            for (AppInfo appInfo : list) {
                pkglist.add(appInfo.getPackageName());
            }
            return pkglist;
        }).filter(strings -> CommonUtil.isLogin()).flatMap(new Function<List<String>, ObservableSource<ResponseBody>>() {

            @Override
            public ObservableSource<ResponseBody> apply(@NonNull List<String> strings) throws Exception {
                final String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
                String package_names = new Gson().toJson(strings);
                Map<String, String> map = new HashMap<>();
                map.put("access_token", token);
                map.put("platform", "android");
                map.put("package_names", CommonUtil.Base64Encode(package_names));
                map.put("extra", CommonUtil.getExtraParam());
                return RetrofitHelper.getAppGameAPI().collectionImport(map);
            }
        })
                .compose(lifecycleTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> LogUtil.i("初始化信息 = " + responseBody.string()), throwable -> {
                    throwable.printStackTrace();
                    LogUtil.i("初始化信息 = " + throwable.getMessage());
                });
    }

    /**
     * 上传包名
     */
    public void uploadPhoneAppPackage(LifecycleTransformer<ResponseBody> lifecycleTransformer) {
        List<AppInfo> list = AppInfoManager.getImpl().findAppInfoAll();
        List<String> strings = new ArrayList<>();
        for (AppInfo appInfo : list) {
            strings.add(appInfo.getPackageName());
        }
        final String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        String package_names = new Gson().toJson(strings);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("platform", "android");
        map.put("package_names", CommonUtil.Base64Encode(package_names));
        map.put("extra", CommonUtil.getExtraParam());
        RetrofitHelper.getAppGameAPI().collectionImport(map)
                .compose(lifecycleTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    if (status == 200) {
                        LogUtil.i("上传包名成功");
                        RxBus.getBus().send(EvenConstant.KEY_UPDATE_PKGS_SUCCESS);
                    }
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        LogUtil.i("上传包名 = " + error.getTitle());
                    }
                });
    }
}
