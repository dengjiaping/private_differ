package com.appgame.differ.bean;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.appgame.differ.R;
import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.module.MainActivity;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

/**
 * Created by lzx on 2017/4/26.
 * 386707112@qq.com
 */
public class NotificationItem extends BaseNotificationItem {

    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;

    public NotificationItem(int id, String title, String desc) {
        super(id, title, desc);
        Intent[] intents = new Intent[2];
        intents[0] = Intent.makeMainActivity(new ComponentName(DifferApplication.getContext(), MainActivity.class));
        intents[1] = new Intent(DifferApplication.getContext(), MainActivity.class);

        this.pendingIntent = PendingIntent.getActivities(DifferApplication.getContext(), 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(FileDownloadHelper.getAppContext());

        builder.setDefaults(Notification.DEFAULT_LIGHTS)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getTitle())
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
    }

    @Override
    public void show(boolean statusChanged, int status, boolean isShowProgress) {

        String desc = getDesc();
        switch (status) {
            case FileDownloadStatus.pending:
                desc += " 等待中...";
                break;
            case FileDownloadStatus.started:
                desc += " 开始下载";
                break;
            case FileDownloadStatus.progress:
                desc += " 正在下载";
                break;
            case FileDownloadStatus.retry:
                desc += " 重新下载";
                break;
            case FileDownloadStatus.error:
                desc += " 下载出错";
                break;
            case FileDownloadStatus.paused:
                desc += " 下载暂停";
                break;
            case FileDownloadStatus.completed:
                desc += " 下载完成";
                break;
            case FileDownloadStatus.warn:
                desc += " 下载出错";
                break;
        }

        builder.setContentTitle(getTitle())
                .setContentText(desc);


        if (statusChanged) {
            builder.setTicker(desc);
        }

        builder.setProgress(getTotal(), getSofar(), !isShowProgress);
        getManager().notify(getId(), builder.build());
    }

}