package com.appgame.differ.module.topic.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.NotificationItem;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.widget.ExpandableTextView;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.appgame.differ.widget.download.DownloadRoundBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */

public class TopicDetailLisAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private static final int TYPE_TOP = 0;
    private static final int TYPE_ITEM = 1;
    private TopicInfo mTopicInfo;
    private List<GameInfo> mGameInfos;
    private LinearLayout.LayoutParams params;
    private RxPermissions mRxPermissions;
    public FileDownloadNotificationHelper<NotificationItem> notificationHelper = new FileDownloadNotificationHelper<>();

    public TopicDetailLisAdapter(Context context, RxPermissions mRxPermissions) {
        mContext = context;
        this.mRxPermissions = mRxPermissions;
        mGameInfos = new ArrayList<>();
        mTopicInfo = new TopicInfo();
    }

    public void setData(TopicInfo topicInfo, List<GameInfo> gameInfos) {
        mTopicInfo = topicInfo;
        mGameInfos = gameInfos;

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < gameInfos.size() + 1; i++) {
            if (i % 2 == 0) {
                params.leftMargin = CommonUtil.dip2px(mContext, 6);
                params.rightMargin = CommonUtil.dip2px(mContext, 12);
            } else {
                params.leftMargin = CommonUtil.dip2px(mContext, 12);
                params.rightMargin = CommonUtil.dip2px(mContext, 6);
            }
            params.bottomMargin = CommonUtil.dip2px(mContext, 15);
        }

        notifyDataSetChanged();
    }

    public FileDownloadNotificationHelper<NotificationItem> getNotificationHelper() {
        return notificationHelper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_TOP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_topic_detail_list, null);
                return new TopViewHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_detail_list, null);
                return new TopicDetailLisViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopViewHolder) {
            TopViewHolder topViewHolder = (TopViewHolder) holder;
            Glide.with(mContext).load(mTopicInfo.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(topViewHolder.mTopicDetailCover);

            //topViewHolder.mTopicDetailTime.setText(CommonUtil.formatYMD(mTopicInfo.getPublishedAt()));
            topViewHolder.mTopicDetailTitle.setText(mTopicInfo.getTitle());
            topViewHolder.mExpandableTextView.setText(mTopicInfo.getContent());

        } else if (holder instanceof TopicDetailLisViewHolder) {
            final TopicDetailLisViewHolder detailHolder = (TopicDetailLisViewHolder) holder;
            //设置布局
            detailHolder.mItemLayout.setLayoutParams(params);
            position = position - 1;
            //赋值

            final GameInfo gameInfo = mGameInfos.get(position);
            final DownloadInfo downloadInfo = gameInfo.getDownloadInfo();
            detailHolder.mTopicDownload.setTag(detailHolder);
            //给控件赋值
            String gameCover = gameInfo.getCover();
            String gameIcon = gameInfo.getIcon();
            String coverUrl = TextUtils.isEmpty(gameCover) ? gameIcon : gameCover;
            Glide.with(mContext).load(coverUrl).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(detailHolder.mTopicCover);
            detailHolder.mTopicTitle.setText(gameInfo.getGameNameCn());
            detailHolder.mTopicSize.setText(downloadInfo.downloadGameSize + "M");
            detailHolder.mTopicDownload.setVisibility(View.VISIBLE);
            detailHolder.mDownloadPro.setVisibility(View.GONE);
            detailHolder.mStartApp.setVisibility(View.GONE);
            detailHolder.mStartApp.setText("");
            initDownload(downloadInfo, detailHolder.mTopicDownload, detailHolder.mStartApp, detailHolder.mDownloadPro);
            //点击图片
            detailHolder.mTopicCover.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, GameDetailsActivity.class);
                intent.putExtra("game_id", gameInfo.getGameId());
                intent.putExtra("downloadUrl", downloadInfo.downloadUrl);
                mContext.startActivity(intent);
            });
        }
    }

    private void initDownload(DownloadInfo downloadInfo, ImageView mTopicDownload, TextView mStartApp, DownloadRoundBar mDownloadPro) {
        mDownloadPro.setTag(downloadInfo.downloadUrl);
        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl); //下载路径
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        downloadManager.initDownloadStatus(downloadInfo.downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, (percent, status) -> {
            if (status == DownLoadFlag.has_app || status == DownLoadFlag.has_pkg) {
                mDownloadPro.setVisibility(View.GONE);
                mTopicDownload.setVisibility(View.GONE);
                mStartApp.setVisibility(View.VISIBLE);
                mStartApp.setText(DownloadProgressButton.getDownloadBtnString(status));
            } else if (status == FileDownloadStatus.paused) {
                mDownloadPro.setVisibility(View.GONE);
                mTopicDownload.setVisibility(View.GONE);
                mStartApp.setVisibility(View.VISIBLE);
                mStartApp.setText("继续");
            } else {
                mDownloadPro.setData(0);
                mDownloadPro.setVisibility(View.GONE);
                mStartApp.setVisibility(View.GONE);
                mTopicDownload.setVisibility(View.VISIBLE);
            }
        });

        downloadManager.addUpdater(downloadInfo.downloadUrl, (task, percent, status) -> {
            if (mDownloadPro.getTag().equals(downloadInfo.downloadUrl)) {
                if (status == DownLoadFlag.has_pkg) {
                    mDownloadPro.setVisibility(View.GONE);
                    mTopicDownload.setVisibility(View.GONE);
                    mStartApp.setVisibility(View.VISIBLE);
                    mStartApp.setText("安装");
                } else if (status == FileDownloadStatus.error) {
                    mStartApp.setText("");
                    mStartApp.setVisibility(View.GONE);
                    mDownloadPro.setVisibility(View.GONE);
                    mDownloadPro.setData(0);
                    mTopicDownload.setVisibility(View.VISIBLE);
                } else {
                    if (status == FileDownloadStatus.paused) {
                        mDownloadPro.setData((int) (percent * 360));
                        mDownloadPro.setVisibility(View.GONE);
                        mTopicDownload.setVisibility(View.GONE);
                        mStartApp.setVisibility(View.VISIBLE);
                        mStartApp.setText("继续");
                    } else {
                        mDownloadPro.setVisibility(View.VISIBLE);
                        mTopicDownload.setVisibility(View.GONE);
                        mStartApp.setVisibility(View.GONE);
                        mDownloadPro.setData((int) (percent * 360));
                    }
                }
            }
        });
        RxView.clicks(mTopicDownload).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            downloadManager.downloadClick(mContext, aBoolean, "下载", downloadInfo, downloadId, downloadPath);
        }));
        RxView.clicks(mStartApp).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            downloadManager.downloadClick(mContext, aBoolean, mStartApp.getText().toString(), downloadInfo, downloadId, downloadPath);
        }));
        //点击暂停
        RxView.clicks(mDownloadPro).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
            FileDownloader.getImpl().pause(downloadId);
            mDownloadPro.setVisibility(View.GONE);
            mTopicDownload.setVisibility(View.GONE);
            mStartApp.setVisibility(View.VISIBLE);
            mStartApp.setText("继续");
        });
    }

    public void startAllAutoTasks() {
        for (GameInfo info : mGameInfos) {
            DownloadInfo downloadInfo = info.getDownloadInfo();
            DownloadManager.getImpl().startAllAutoTasks(downloadInfo);
        }
    }

    public int getSpanSize(int pos) {
        int viewType = getItemViewType(pos);
        switch (viewType) {
            case TYPE_TOP:
                return 12;
            case TYPE_ITEM:
                return 6;
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return 1 + mGameInfos.size();
    }

    private class TopViewHolder extends RecyclerView.ViewHolder {
        public ImageView mTopicDetailCover;
        public TextView mTopicDetailTime, mTopicDetailTitle;
        public ExpandableTextView mExpandableTextView;

        public TopViewHolder(View itemView) {
            super(itemView);
            mTopicDetailCover = (ImageView) itemView.findViewById(R.id.topic_detail_cover);

            mTopicDetailTime = (TextView) itemView.findViewById(R.id.topic_detail_time);
            mTopicDetailTitle = (TextView) itemView.findViewById(R.id.topic_detail_title);
            mExpandableTextView = (ExpandableTextView) itemView.findViewById(R.id.expandable_view);
        }
    }

    public class TopicDetailLisViewHolder extends RecyclerView.ViewHolder {
        public ImageView mTopicCover, mTopicDownload;
        public TextView mTopicTitle, mTopicSize, mStartApp;
        public DownloadRoundBar mDownloadPro;
        public RelativeLayout mItemLayout;

        public TopicDetailLisViewHolder(View itemView) {
            super(itemView);
            mTopicCover = (ImageView) itemView.findViewById(R.id.topic_cover);
            mTopicTitle = (TextView) itemView.findViewById(R.id.topic_title);
            mTopicSize = (TextView) itemView.findViewById(R.id.topic_size);
            mStartApp = (TextView) itemView.findViewById(R.id.start_app);
            mDownloadPro = (DownloadRoundBar) itemView.findViewById(R.id.download_pro);
            mTopicDownload = (ImageView) itemView.findViewById(R.id.topic_download);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
        }
    }

}