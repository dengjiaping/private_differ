package com.appgame.differ.data.db;

import android.content.Context;
import android.text.TextUtils;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.download.TasksManagerModel;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.exception.FileDownloadHttpException;
import com.liulishuo.filedownloader.exception.FileDownloadNetworkPolicyException;
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lzx on 2017/6/5.
 * 386707112@qq.com
 */

public class DownloadManager {


    private final static class HolderClass {
        private final static DownloadManager INSTANCE = new DownloadManager();
    }

    public static DownloadManager getImpl() {
        return HolderClass.INSTANCE;
    }

    private Map<String, DownloadStatusUpdater> updaterList = new HashMap<>();

    /**
     * 开始下载
     */
    private void startDownload(String url, String path, DownloadInfo downloadInfo) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.showShort("敬请期待");
            return;
        }
        BaseDownloadTask task = FileDownloader.getImpl().create(url).setPath(path).setCallbackProgressTimes(100);
        task.setListener(new GameFileDownloadListener());
        TasksManagerModel model = GameDownloadManager.getImpl().addDownLoadTasks(downloadInfo);
        if (model == null) {
            ToastUtil.showShort("下载失败");
            return;
        }
        if (!task.isRunning()) {
            task.start();
        }
    }

    /**
     * 开始下载
     */
    private void startDownload(String url, String path) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.showShort("敬请期待");
            return;
        }
        BaseDownloadTask task = FileDownloader.getImpl().create(url).setPath(path).setCallbackProgressTimes(100);
        task.setListener(new GameFileDownloadListener());
        if (!task.isRunning()) {
            task.start();
        }
    }

    /**
     * 更新下载监听
     *
     * @param url
     * @param path
     */
    public void updateDownloadTask(String url, String path) {
        BaseDownloadTask task = FileDownloader.getImpl().create(url).setPath(path).setCallbackProgressTimes(100);
        int result = FileDownloader.getImpl().replaceListener(url, path, new GameFileDownloadListener());
        if (result == 0) {
            task.setListener(new GameFileDownloadListener());
        }
    }

    /**
     * 添加一个下载监听
     *
     * @param updater
     */
    public void addUpdater(String url, DownloadStatusUpdater updater) {
        if (!updaterList.containsKey(url)) {
            updaterList.put(url, updater);
        }
    }

    /**
     * 移除下载监听
     *
     * @param updater
     * @return
     */
    public boolean removeUpdater(String url, DownloadStatusUpdater updater) {
        updaterList.remove(url);
        return true;
    }

    /**
     * 下载监听
     */
    private class GameFileDownloadListener extends FileDownloadListener {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            float sofar = FileDownloader.getImpl().getSoFar(task.getId());
            float total = FileDownloader.getImpl().getTotal(task.getId());
            float percent = sofar / total;
            updaterDownloadStatus(task, percent, FileDownloadStatus.pending);
            GameDownloadManager.getImpl().updateTaskFlag(FileDownloadStatus.pending, task.getId());
            LogUtil.i("---pending--");
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            float sofar = FileDownloader.getImpl().getSoFar(task.getId());
            float total = FileDownloader.getImpl().getTotal(task.getId());
            float percent = sofar / total;
            updaterDownloadStatus(task, percent, FileDownloadStatus.started);
            GameDownloadManager.getImpl().updateTaskFlag(FileDownloadStatus.started, task.getId());
            LogUtil.i("---started--");
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            float sofar = FileDownloader.getImpl().getSoFar(task.getId());
            float total = FileDownloader.getImpl().getTotal(task.getId());
            float percent = sofar / total;
            updaterDownloadStatus(task, percent, FileDownloadStatus.connected);
            GameDownloadManager.getImpl().changeAutoDownloadFlag(task.getUrl(), false);
            GameDownloadManager.getImpl().updateTaskFlag(FileDownloadStatus.connected, task.getId());
            LogUtil.i("---connected--");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            float sofar = FileDownloader.getImpl().getSoFar(task.getId());
            float total = FileDownloader.getImpl().getTotal(task.getId());
            float percent = sofar / total;
            updaterDownloadStatus(task, percent, FileDownloadStatus.progress);
            LogUtil.i("---progress--" + percent * 100);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            updaterDownloadStatus(task, 100, DownLoadFlag.has_pkg);

            GameDownloadManager.getImpl().updateTaskFlag(DownLoadFlag.has_pkg, task.getId());
            String downloadLinkId = GameDownloadManager.getImpl().getDownloadLinkId(task.getId());
            GameDownloadManager.getImpl().requestDownloadSuccess(downloadLinkId);
            if (FileUtil.isExistsInstallationPackage(task.getUrl())) {
                RxBus.getBus().send(EvenConstant.KEY_DOWNLOAD_RED_DOT);
                try {
                    CommonUtil.installApk(DifferApplication.getContext(), FileUtil.getInstallationPackageFile(task.getUrl()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i("---completed--");
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            float sofar = FileDownloader.getImpl().getSoFar(task.getId());
            float total = FileDownloader.getImpl().getTotal(task.getId());
            float percent = sofar / total;
            updaterDownloadStatus(task, percent, FileDownloadStatus.paused);
            GameDownloadManager.getImpl().updateTaskFlag(FileDownloadStatus.paused, task.getId());
            LogUtil.i("---paused--");
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            updaterDownloadStatus(task, 0, FileDownloadStatus.error);
            if (e instanceof FileDownloadOutOfSpaceException) {
                ToastUtil.showShort("磁盘空间不足，下载失败");
            }
            if (!CommonUtil.isConnected(DifferApplication.getContext())) {
                ToastUtil.showShort(DifferApplication.getContext().getString(R.string.network_time_out));
            }
            if (e instanceof SocketException || e instanceof FileDownloadNetworkPolicyException) { //断网或下载中转为非wifi情况,需要自动下载
                GameDownloadManager.getImpl().changeAutoDownloadFlag(task.getUrl(), true);
            }
            if (e instanceof FileDownloadHttpException) {
                ToastUtil.showShort("下载失败");
            }
            GameDownloadManager.getImpl().updateTaskFlag(FileDownloadStatus.error, task.getId());
            LogUtil.i("---error--" + e.getMessage());

            e.printStackTrace();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            updaterDownloadStatus(task, 0, FileDownloadStatus.warn);
        }
    }

    private void updaterDownloadStatus(final BaseDownloadTask task, float percent, int status) {
        for (Map.Entry<String, DownloadStatusUpdater> entry : updaterList.entrySet()) {
            if (entry.getKey().equals(task.getUrl())) {
                entry.getValue().downloadStatusUI(task, percent, status);
            }
        }
    }

    public interface DownloadStatusUpdater {
        void downloadStatusUI(BaseDownloadTask task, float percent, int status);
    }

    /**
     * 初始化下载状态
     *
     * @param downloadUrl
     * @param packageName
     * @param callBack
     */
    public void initDownloadStatus(String downloadUrl, String packageName, String downloadPath, int downloadId, DownloadStatusCallBack callBack) {
        boolean hasApp = AppInfoManager.getImpl().hasPkg(packageName); //是否存在app
        if (hasApp) {
            callBack.setUI(100, DownLoadFlag.has_app);
        } else {
            boolean hasPkgFile = FileUtil.isExistsInstallationPackage(downloadUrl) || FileUtil.isExistsInstallationPackageTemp(downloadUrl); //是否存在apk
            if (hasPkgFile) {
                int status = FileDownloader.getImpl().getStatus(downloadId, downloadPath);//下载状态
                float sofar = FileDownloader.getImpl().getSoFar(downloadId);
                float total = FileDownloader.getImpl().getTotal(downloadId);
                float percent = sofar / total;

                if (FileUtil.isExistsInstallationPackage(downloadUrl)) {
                    callBack.setUI(100, DownLoadFlag.has_pkg);
                } else if (
                        status == FileDownloadStatus.pending ||
                                status == FileDownloadStatus.started ||
                                status == FileDownloadStatus.connected ||
                                status == FileDownloadStatus.paused ||
                                status == FileDownloadStatus.warn ||
                                status == FileDownloadStatus.progress) {
                    callBack.setUI(percent, status);
                } else if (status == FileDownloadStatus.error) {
                    callBack.setUI(0, status);
                } else {
                    callBack.setUI(0, DownLoadFlag.download_normal);
                }
            } else {
                callBack.setUI(0, DownLoadFlag.download_normal);
            }
        }
    }

    public interface DownloadStatusCallBack {
        void setUI(float percent, int status);
    }

    public FileDownloadConnectListener listener;

    /**
     * 链接下载服务，更新状态
     */
    public void initServiceConnectListener(UpdateAfterAddListenerCallBack callBack) {
        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
            if (listener != null) {
                FileDownloader.getImpl().removeServiceConnectListener(listener);
            }
            listener = new FileDownloadConnectListener() {
                @Override
                public void connected() {
                    callBack.updateUI();
                }

                @Override
                public void disconnected() {
                    callBack.updateUI();
                }
            };
            FileDownloader.getImpl().addServiceConnectListener(listener);
        }
    }

    public interface UpdateAfterAddListenerCallBack {
        void updateUI();
    }

    public void clearFileDownloadConnectListener() {
        if (listener != null) {
            FileDownloader.getImpl().removeServiceConnectListener(listener);
            listener = null;
        }
    }

    /**
     * 点击下载
     */
    public void downloadClick(Context context, boolean hasPermissions, String btnText, DownloadInfo downloadInfo, int downloadId, String path) {

        if (hasPermissions) {
            if (btnText.equals("打开")) {
                if (!TextUtils.isEmpty(downloadInfo.downloadPackageName))
                    CommonUtil.startApp(context, downloadInfo.downloadPackageName);
                else {
                    ToastUtil.showShort("运行失败");
                }
            } else if (btnText.equals("安装")) {
                if (FileUtil.isExistsInstallationPackage(downloadInfo.downloadUrl)) {
                    try {
                        CommonUtil.installApk(context, FileUtil.getInstallationPackageFile(downloadInfo.downloadUrl));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort("找不到安装包");
                }
            } else if (btnText.equals("继续") || btnText.equals("下载") || btnText.equals("好想玩")) {
                if (CommonUtil.isMobile(context)) {
                    OutLoginDialog dialog = new OutLoginDialog(context);
                    dialog.show();
                    dialog.setDialogTitle("您正处于移动网络环境下下载会消耗您的流量，是否继续？");
                    dialog.setDialogBtnText("wifi自动下载", "继续下载");
                    dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                        @Override
                        public void onYes() {
                            startDownload(downloadInfo.downloadUrl, path, downloadInfo);
                        }

                        @Override
                        public void onNo() {
                            GameDownloadManager.getImpl().addDownLoadTasks(downloadInfo);
                            GameDownloadManager.getImpl().changeAutoDownloadFlag(downloadInfo.downloadUrl, true);
                        }
                    });
                } else {
                    startDownload(downloadInfo.downloadUrl, path, downloadInfo);
                }
            } else {
                try {
                    FileDownloader.getImpl().pause(downloadId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtil.showShort("权限拒绝，下载失败");
        }
    }

    /**
     * 点击下载
     */
    public void downloadClick(Context context, boolean hasPermissions, String btnText, String packageName, String downloadUrl, int downloadId, String path) {
        if (hasPermissions) {
            if (btnText.equals("打开")) {
                if (!TextUtils.isEmpty(packageName))
                    CommonUtil.startApp(context, packageName);
                else {
                    ToastUtil.showShort("运行失败");
                }
            } else if (btnText.equals("安装")) {
                if (FileUtil.isExistsInstallationPackage(downloadUrl)) {
                    try {
                        CommonUtil.installApk(context, FileUtil.getInstallationPackageFile(downloadUrl));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showShort("找不到安装包");
                }
            } else if (btnText.equals("继续") || btnText.equals("下载") || btnText.equals("好想玩")) {
                if (CommonUtil.isMobile(context)) {
                    OutLoginDialog dialog = new OutLoginDialog(context);
                    dialog.show();
                    dialog.setDialogTitle("您正处于移动网络环境下下载会消耗您的流量，是否继续？");
                    dialog.setDialogBtnText("wifi自动下载", "继续下载");
                    dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                        @Override
                        public void onYes() {
                            startDownload(downloadUrl, path);
                        }

                        @Override
                        public void onNo() {
                            GameDownloadManager.getImpl().changeAutoDownloadFlag(downloadUrl, true);
                        }
                    });
                } else {
                    startDownload(downloadUrl, path);
                }
            } else {
                try {
                    FileDownloader.getImpl().pause(downloadId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtil.showShort("权限拒绝，下载失败");
        }
    }

    /**
     * 开始自动下载
     */
    public void startAllAutoTasks(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl);
            startAllAutoTasks(downloadInfo.downloadUrl, downloadPath);
        }
    }

    /**
     * 开始自动下载
     */
    public void startAllAutoTasks(String downloadUrl, String downloadPath) {
        if (!TextUtils.isEmpty(downloadUrl) && !TextUtils.isEmpty(downloadPath)) {
            boolean isDownloadAuto = GameDownloadManager.getImpl().isAutoDownload(downloadUrl);
            if (isDownloadAuto) {
                FileDownloader.getImpl().create(downloadUrl).setListener(new GameFileDownloadListener()).setPath(downloadPath).start();
                GameDownloadManager.getImpl().changeAutoDownloadFlag(downloadUrl, false);
            }
        }
    }

    /**
     * 清除所有资源
     */
    public void clear() {
        updaterList.clear();
        if (listener != null) {
            listener = null;
        }
    }
}
