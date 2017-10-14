package com.appgame.differ.module.home.adapter;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.TasksManagerModel;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadServiceProxy;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by xian on 2017/5/6.
 */

public class DownloadManagerAdapter extends RecyclerView.Adapter<DownloadManagerAdapter.ManagerHolder> {

    private Context mContext;
    private List<TasksManagerModel> list;
    private RxPermissions mRxPermissions;
    private OutLoginDialog dialog;
    private OnDeleteListener mListener;
    private OnDownloadErrorListener mErrorListener;


    public DownloadManagerAdapter(Context context, RxPermissions rxPermissions) {
        mContext = context;
        list = new ArrayList<>();
        mRxPermissions = rxPermissions;
        dialog = new OutLoginDialog(mContext);
    }

    public void setList(List<TasksManagerModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ManagerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_manager, parent, false);
        return new ManagerHolder(view);
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        mListener = listener;
    }

    public void setErrorListener(OnDownloadErrorListener errorListener) {
        mErrorListener = errorListener;
    }

    @Override
    public void onBindViewHolder(ManagerHolder holder, int position) {
        TasksManagerModel model = list.get(position);
        Glide.with(mContext).load(model.gameIcon).into(holder.mGameIcon);
        holder.mGameName.setText(model.gameName);
        holder.mTextPro.setText("已下载" + getFormatPercent(FileDownloader.getImpl().getSoFar(model.downloadId)) + "M/" + model.gameSize + "M");

        holder.mProgressBar.setTag(model.downloadUrl);
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.updateDownloadTask(model.downloadUrl, model.downloadPath);
        downloadManager.initDownloadStatus(model.downloadUrl, model.packageName, model.downloadPath, model.downloadId, (percent, status) -> {

            if (status == DownLoadFlag.has_app || status == DownLoadFlag.has_pkg) {
                initDownloadCompleted(holder.mProgressBar, holder.mTextPro, holder.mBtnAction, model.gameSize + "M", status == DownLoadFlag.has_app ? "打开" : "安装");
            } else if (status == FileDownloadStatus.paused) {
                holder.mProgressBar.setProgress((int) (percent * 100));
                holder.mBtnAction.setText("继续");
                boolean isDownloadAuto = GameDownloadManager.getImpl().isAutoDownload(model.downloadUrl);
                holder.mTextPro.setText(!isDownloadAuto ? "已暂停" : "等待wifi链接开始任务");
            } else {
                initReady(holder.mProgressBar, holder.mTextPro, holder.mBtnAction, model, DownloadProgressButton.getDownloadBtnString(status));
            }
        });

        downloadManager.addUpdater(model.downloadUrl, (task, percent, status) -> {
            if (status == DownLoadFlag.has_pkg) {
                initDownloadCompleted(holder.mProgressBar, holder.mTextPro, holder.mBtnAction, getFormatPercent(task.getLargeFileTotalBytes()) + "M", "安装");
            } else if (status == FileDownloadStatus.error) {
                initPauseOrProgress(holder.mProgressBar, holder.mBtnAction, holder.mTextPro, model, "继续", R.color.black_alpha_50, R.drawable.bg_frame_gray_corner_3);
            } else {
                holder.mBtnAction.setText(DownloadProgressButton.getDownloadBtnString(status));
                if (holder.mProgressBar.getTag().equals(model.downloadUrl)) {
                    holder.mProgressBar.setProgress((int) (percent * 100));
                }
                holder.mProgressBar.setVisibility(View.VISIBLE);
                if (status == FileDownloadStatus.progress) {
                    float sofar = FileDownloader.getImpl().getSoFar(task.getId());
                    float total = FileDownloader.getImpl().getTotal(task.getId());
                    holder.mTextPro.setText("已下载" + getFormatPercent(sofar) + "M/" + getFormatPercent(total) + "M");
                } else {
                    holder.mTextPro.setText("已暂停");
                }
            }
        });

        RxView.clicks(holder.mBtnAction).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o ->
                mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                    downloadManager.downloadClick(mContext, aBoolean, holder.mBtnAction.getText().toString(), model.packageName, model.downloadUrl, model.downloadId, model.downloadPath);
                }));

        holder.mBtnDelete.setOnClickListener(v -> initDelete(model, position));
    }

    public void startAllAutoTasks() {
        for (TasksManagerModel model : list) {
            DownloadManager.getImpl().startAllAutoTasks(model.downloadUrl, model.downloadPath);
        }
    }

    private void initDelete(TasksManagerModel model, int position) {
        dialog.show();
        dialog.setDialogTitle("确定删除下载任务及安装包？");
        dialog.setDialogBtnText("取消", "确定");
        dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
            @Override
            public void onYes() {
                FileDownloader.getImpl().pause(model.downloadId);
                FileDownloadServiceProxy.getImpl().clearTaskData(model.downloadId);
                if (FileUtil.isExistsInstallationPackageTemp(model.downloadUrl)) {
                    String path = FileUtil.getTempDownloadPath(model.downloadUrl);
                    new File(path).delete();
                }
                FileUtil.deleteGamePkgFile(model.downloadUrl);
                GameDownloadManager.getImpl().deleteDownloadTasks(model.downloadUrl, model.packageName);

                list.remove(model);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                if (mListener != null) {
                    mListener.onDelete();
                }
            }

            @Override
            public void onNo() {

            }
        });
    }

    private void initDownloadCompleted(ProgressBar mProgressBar, TextView mTextPro, TextView mBtnAction, String size, String text) {
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.GONE);
        mTextPro.setText(size);
        mBtnAction.setText(text);
    }

    private void initReady(ProgressBar mProgressBar, TextView mTextPro, TextView mBtnAction, TasksManagerModel model, String text) {
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);
        mBtnAction.setText(text);
        mBtnAction.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
        mBtnAction.setBackgroundResource(R.drawable.bg_frame_green_corner_3);
        if (text.equals("暂停")) {
            float sofar = FileDownloader.getImpl().getSoFar(model.downloadId);
            float total = FileDownloader.getImpl().getTotal(model.downloadId);
            float percent = sofar / total;
            mProgressBar.setProgress((int) (percent * 100));
            mTextPro.setText("已下载" + getFormatPercent(sofar) + "M/" + getFormatPercent(total) + "M");
        }
    }

    private void initPauseOrProgress(ProgressBar mProgressBar, TextView mBtnAction, TextView mTextPro, TasksManagerModel model, String text, int textColor, int bgColor) {
        mProgressBar.setVisibility(View.VISIBLE);
        float sofar = FileDownloader.getImpl().getSoFar(model.downloadId);
        float total = FileDownloader.getImpl().getTotal(model.downloadId);
        float percent = sofar / total;
        mProgressBar.setProgress((int) (percent * 100));
        mBtnAction.setText(text);
        mBtnAction.setTextColor(ContextCompat.getColor(mContext, textColor));
        mBtnAction.setBackgroundResource(bgColor);
        if (text.equals("继续")) {
            boolean isDownloadAuto = GameDownloadManager.getImpl().isAutoDownload(model.downloadUrl);
            if (!isDownloadAuto) {
                mTextPro.setText("已暂停");
            } else {
                mTextPro.setText("等待wifi链接开始任务");
            }
        } else {
            mTextPro.setText("已下载" + getFormatPercent(sofar) + "M/" + getFormatPercent(total) + "M");
        }
    }

    private String getFormatPercent(float percent) {
        float curr = percent / 1024 / 1024;
        String proText = String.valueOf(curr);
        if (proText.contains(".")) {
            proText = proText.substring(0, proText.indexOf("."));
        }
        return proText;
    }

    public interface OnDeleteListener {
        void onDelete();
    }

    public interface OnDownloadErrorListener {
        void onDownloadException(BaseDownloadTask task, Throwable e);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ManagerHolder extends RecyclerView.ViewHolder {
        ShapeImageView mGameIcon;
        TextView mGameName, mTextPro;
        ProgressBar mProgressBar;
        TextView mBtnAction;
        ImageView mBtnDelete;

        public ManagerHolder(View itemView) {
            super(itemView);
            mGameIcon = (ShapeImageView) itemView.findViewById(R.id.game_icon);
            mGameName = (TextView) itemView.findViewById(R.id.game_name);
            mTextPro = (TextView) itemView.findViewById(R.id.text_pro);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            mBtnAction = (TextView) itemView.findViewById(R.id.pro_bar);
            mBtnDelete = (ImageView) itemView.findViewById(R.id.btn_delete);
            mProgressBar.setMax(100);
        }
    }
}