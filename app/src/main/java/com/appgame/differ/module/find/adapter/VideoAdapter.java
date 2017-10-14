package com.appgame.differ.module.find.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.FindTypeVideo;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.find.VideoPlayActivity;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.JCVideoView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by lzx on 2017/6/23.
 */

public class VideoAdapter extends LoadMoreAdapter<FindListInfo> {

    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_SMALL_VIDEO = 1;

    private RxPermissions mRxPermissions;

    public VideoAdapter(Context context, RxPermissions permissions) {
        super(context);
        this.mRxPermissions = permissions;
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_SMALL_VIDEO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_one_image, parent, false);
            return new GameOneHolder(view);
        } else if (viewType == TYPE_VIDEO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_video, parent, false);
            return new GameVideoHolder(view);
        }
        return null;
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        if (viewHolder instanceof GameOneHolder) {
            GameOneHolder holder = (GameOneHolder) viewHolder;
            FindListInfo findListInfo = mDataList.get(position);

            FindTypeVideo typeVideo = findListInfo.typeVideo;
            holder.mIconPlay.setVisibility(View.VISIBLE);
            holder.mGameLayout.setVisibility(View.VISIBLE);
            initOneImageUI(holder, typeVideo.getPics().size() > 0 ? typeVideo.getPics().get(0) : "", typeVideo.getName(), typeVideo.getGame());
            OnItemClick(holder.mDescLayout, findListInfo, holder.mGameLayout);

        } else if (viewHolder instanceof GameVideoHolder) {
            GameVideoHolder holder = (GameVideoHolder) viewHolder;
            FindListInfo findListInfo = mDataList.get(position);
            FindTypeVideo video = findListInfo.typeVideo;
            String gameTitle = video.getName();
            String videoUrl = video.getUrl();
            String cover = video.getPics().size() > 0 ? video.getPics().get(0) : "";
            GameInfo gameInfo = video.getGame();
            initVideoItemUI(holder, gameTitle, videoUrl, cover, gameInfo);
            OnItemClick(holder.mDescLayout, findListInfo, holder.mGameLayout);
        }
    }

    private void initVideoItemUI(GameVideoHolder holder, String gameTitle, String videoUrl, String cover, GameInfo gameInfo) {
        holder.mGameCover.setVisibility(View.GONE);
        holder.mJCVideoPlayer.setVisibility(View.VISIBLE);
        holder.mJCVideoPlayer.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        Glide.with(mContext).load(cover).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mJCVideoPlayer.thumbImageView);

        holder.mGameTitle.setText(gameTitle);
        Glide.with(mContext).load(cover).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
        if (gameInfo != null) {
            String gameId = gameInfo.getGameId();
            holder.mGameLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(gameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
            holder.mGameName.setText(gameInfo.getGameNameCn());
            String gameDesc = gameInfo.getCategory().size() > 0 ? gameInfo.getCategory().get(0).getName() : "";
            String gameSize = gameInfo.getDownloadInfo() != null ? gameInfo.getDownloadInfo().downloadGameSize + "M" : "";
            holder.mGameSize.setText(gameDesc + " | " + gameSize);
            holder.mGameIcon.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameId));
            holder.mGameName.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameId));
            if (gameInfo.getDownloadInfo() != null) {
                initDownloadGame(gameInfo.getDownloadInfo(), holder.mProgressBar);
            } else {
                holder.mProgressBar.setProgressUI(100, DownLoadFlag.download_normal);
            }
        } else {
            holder.mGameLayout.setVisibility(View.GONE);
        }
    }

    private void OnItemClick(View view, FindListInfo findListInfo, RelativeLayout mGameLayout) {
        mGameLayout.setOnClickListener(v -> {
            String gameId = findListInfo.typeVideo.getGameId();
            if (!TextUtils.isEmpty(gameId)) {
                IntentHelper.startGameDetailActivity((MainActivity) mContext, null, gameId);
            }
        });
        view.setOnClickListener(v -> {
            FindTypeVideo video = findListInfo.typeVideo;
            if (!TextUtils.isEmpty(video.getUrl()) && !TextUtils.isEmpty(video.getName())) {
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra("url", video.getUrl());
                intent.putExtra("title", video.getName());
                intent.putExtra("cover", video.getPics().size() > 0 ? video.getPics().get(0) : "");
                mContext.startActivity(intent);
            }
        });
    }

    private void initOneImageUI(GameOneHolder holder, String pic, String gameTitle, GameInfo gameInfo) {
        Glide.with(mContext).load(pic).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
        holder.mGameTitle.setText(gameTitle);
        if (gameInfo != null) {
            holder.mGameLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(gameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
            holder.mGameName.setText(gameInfo.getGameNameCn());
            String gameDesc = gameInfo.getCategory().size() > 0 ? gameInfo.getCategory().get(0).getName() : "";
            String gameSize = gameInfo.getDownloadInfo() != null ? gameInfo.getDownloadInfo().downloadGameSize + "M" : "";
            holder.mGameSize.setText(gameDesc + " | " + gameSize);
            holder.mGameIcon.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameInfo.getGameId()));
            holder.mGameName.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameInfo.getGameId()));
            if (gameInfo.getDownloadInfo() != null) {
                initDownloadGame(gameInfo.getDownloadInfo(), holder.mProgressBar);
            } else {
                holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            }
        } else {
            holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            holder.mGameLayout.setVisibility(View.GONE);
        }
    }

    private void initDownloadGame(DownloadInfo downloadInfo, DownloadProgressButton mProgressBar) {

        mProgressBar.setTag(downloadInfo.downloadUrl);

        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl); //下载路径
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        downloadManager.initDownloadStatus(downloadInfo.downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, mProgressBar::setProgressUI);
        downloadManager.addUpdater(downloadInfo.downloadUrl, (task, percent, status) -> {
            if (mProgressBar.getTag().equals(downloadInfo.downloadUrl)) {
                mProgressBar.setProgressUI(percent, status);
            }
        });

        RxView.clicks(mProgressBar).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            downloadManager.downloadClick(mContext, aBoolean, mProgressBar.getCurrentText(), downloadInfo, downloadId, downloadPath);
        }));
    }

    /**
     * 自动下载
     */
    public void startAllAutoTasks() {
        for (FindListInfo info : mDataList) {
            FindTypeVideo typeVideo = info.typeVideo;
            GameInfo gameInfo = typeVideo.getGame();
            if (gameInfo != null) {
                startAllAutoTasksImpl(gameInfo);
            }
        }
    }

    private void startAllAutoTasksImpl(GameInfo gameInfo) {
        if (gameInfo != null) {
            DownloadInfo downloadInfo = gameInfo.getDownloadInfo();
            DownloadManager.getImpl().startAllAutoTasks(downloadInfo);
        }
    }

    @Override
    protected int getViewType(int position) {
        FindListInfo listInfo = mDataList.get(position);
        String target = listInfo.target;
        if (target.equals("video")) {
            String layout = listInfo.typeVideo.getLayout();
            if (layout.equals("one")) {
                return TYPE_SMALL_VIDEO;
            } else {
                return TYPE_VIDEO;
            }
        }
        return 0;
    }

    private class GameOneHolder extends BaseViewHolder {
        ImageView mGameCover;
        TextView mGameTitle, mGameName, mGameSize;
        ShapeImageView mGameIcon;
        DownloadProgressButton mProgressBar;
        RelativeLayout mGameLayout, mDescLayout;
        ImageView mIconPlay;

        public GameOneHolder(View itemView) {
            super(itemView, mContext, false);
            mGameCover = $(R.id.game_cover);
            mGameTitle = $(R.id.game_title);
            mGameName = $(R.id.game_name);
            mGameSize = $(R.id.game_size);
            mGameIcon = $(R.id.game_icon);
            mProgressBar = $(R.id.pro_bar);
            mGameLayout = $(R.id.game_layout);
            mDescLayout = $(R.id.desc_layout);
            mIconPlay = $(R.id.icon_play);
        }
    }

    private class GameVideoHolder extends BaseViewHolder {
        TextView mGameTitle, mGameName, mGameSize;
        JCVideoView mJCVideoPlayer;
        ShapeImageView mGameIcon;
        DownloadProgressButton mProgressBar;
        RelativeLayout mGameLayout;
        ImageView mGameCover;
        LinearLayout mDescLayout;

        public GameVideoHolder(View itemView) {
            super(itemView, mContext, false);
            mJCVideoPlayer = $(R.id.video_player);
            mGameTitle = $(R.id.game_title);
            mGameName = $(R.id.game_name);
            mGameSize = $(R.id.game_size);
            mGameIcon = $(R.id.game_icon);
            mProgressBar = $(R.id.pro_bar);
            mGameLayout = $(R.id.game_layout);
            mGameCover = $(R.id.game_cover);
            mDescLayout = $(R.id.desc_layout);
        }
    }
}
