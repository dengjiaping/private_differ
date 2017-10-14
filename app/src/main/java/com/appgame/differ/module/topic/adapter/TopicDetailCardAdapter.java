package com.appgame.differ.module.topic.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.widget.ExpandableTextView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.appgame.differ.widget.download.DownloadRoundBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */

public class TopicDetailCardAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TOP = 0;
    private static final int TYPE_ITEM = 1;
    private Context mContext;
    private TopicInfo mTopicInfo;
    private List<GameInfo> mGameInfos;
    private String fontColor;

    private RxPermissions mRxPermissions;

    public TopicDetailCardAdapter(RxPermissions rxPermissions, Context context) {
        mContext = context;
        mRxPermissions = rxPermissions;
        mGameInfos = new ArrayList<>();
        mTopicInfo = new TopicInfo();
    }

    public void setData(TopicInfo topicInfo, List<GameInfo> gameInfos) {
        mTopicInfo = topicInfo;
        mGameInfos = gameInfos;
        fontColor = mTopicInfo.getExtraData().getFontColor();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_TOP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_topic_detail_card, null);
                return new TopViewHolder(view);
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic_detail_card, null);
                return new TopicDetailCardViewHolder(view);
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopViewHolder) {
            TopViewHolder topViewHolder = (TopViewHolder) holder;
            Glide.with(mContext).load(mTopicInfo.getCover()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(topViewHolder.mTopicDetailCover);
            if (!TextUtils.isEmpty(fontColor)) {
                topViewHolder.mExpandableTextView.setTextColorWithButton(fontColor);
                topViewHolder.mLine.setBackgroundColor(Color.parseColor(fontColor));
            }
            topViewHolder.mExpandableTextView.setText(mTopicInfo.getContent());
        } else if (holder instanceof TopicDetailCardViewHolder) {
            final TopicDetailCardViewHolder detailHolder = (TopicDetailCardViewHolder) holder;
            position = position - 1;
            final GameInfo gameInfo = mGameInfos.get(position);
            final DownloadInfo downloadInfo = gameInfo.getDownloadInfo();
            detailHolder.mStartApp.setTag(detailHolder);
            String gameCover = gameInfo.getCover();
            String gameIcon = gameInfo.getIcon();
            String coverUrl = TextUtils.isEmpty(gameCover) ? gameIcon : gameCover;
            String gameVideo = gameInfo.getVideo();
            Glide.with(mContext).load(gameIcon).diskCacheStrategy(DiskCacheStrategy.ALL).into(detailHolder.mTopicGameIcon);

            if (TextUtils.isEmpty(gameVideo)) {
                detailHolder.mTopicCover.setVisibility(View.VISIBLE);
                detailHolder.mVideoPlayer.setVisibility(View.GONE);
                Glide.with(mContext).load(coverUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(detailHolder.mTopicCover);
            } else {
                detailHolder.mTopicCover.setVisibility(View.GONE);
                detailHolder.mVideoPlayer.setVisibility(View.VISIBLE);
                detailHolder.mVideoPlayer.setUp(
                        gameVideo, JCVideoPlayer.SCREEN_LAYOUT_LIST,
                        "");
                Glide.with(mContext).load(coverUrl).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(detailHolder.mVideoPlayer.thumbImageView);
            }

            //配置颜色
            if (!TextUtils.isEmpty(fontColor)) {
                detailHolder.mTopicGameTitle.setTextColor(Color.parseColor(fontColor));
                detailHolder.mGameDesc.setTextColor(Color.parseColor(fontColor));
                detailHolder.mRatingText.setTextColor(Color.parseColor(fontColor));
                detailHolder.mMaterialRatingBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor(fontColor)));
                detailHolder.mMaterialRatingBar.setSecondaryProgressTintList(ColorStateList.valueOf(Color.parseColor(fontColor)));
                detailHolder.mMaterialRatingBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor(fontColor)));
                GradientDrawable drawable = (GradientDrawable) detailHolder.mStartApp.getBackground();
                drawable.setStroke(CommonUtil.dip2px(mContext, 1), Color.parseColor(fontColor));
                detailHolder.mStartApp.setTextColor(Color.parseColor(fontColor));
                detailHolder.mDownloadPro.setViewColor(fontColor);
                detailHolder.mLine.setBackgroundColor(Color.parseColor(fontColor));
            } else {
                detailHolder.mMaterialRatingBar.setProgressBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimary)));
            }
            detailHolder.mGameDesc.setText(gameInfo.getIntro());
            detailHolder.mTopicGameTitle.setText(gameInfo.getGameNameCn());
            String start = gameInfo.getGameStar();

            start = String.format("%.1f", Float.parseFloat(start));
            detailHolder.mRatingText.setText(start);
            detailHolder.mMaterialRatingBar.setNumStars(5);
            detailHolder.mMaterialRatingBar.setRating(Float.parseFloat(start) / 2);

            detailHolder.mLine.setVisibility(position == mGameInfos.size() - 1 ? View.INVISIBLE : View.VISIBLE);

            initDownload(gameInfo.getDownloadInfo(), detailHolder.mStartApp, detailHolder.mDownloadPro);

            detailHolder.mTopicGameIcon.setOnClickListener(new OnItemClick(gameInfo.getGameId(), downloadInfo.downloadUrl));
            detailHolder.mTopicGameTitle.setOnClickListener(new OnItemClick(gameInfo.getGameId(), downloadInfo.downloadUrl));
            detailHolder.mTopicCover.setOnClickListener(new OnItemClick(gameInfo.getGameId(), downloadInfo.downloadUrl));
        }
    }

    private void initDownload(DownloadInfo downloadInfo, TextView mStartApp, DownloadRoundBar mDownloadPro) {
        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl); //下载路径
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id

        mDownloadPro.setTag(downloadInfo.downloadUrl);
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        downloadManager.initDownloadStatus(downloadInfo.downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, (percent, status) -> {

            LogUtil.i("status = " + status);

            if (status == DownLoadFlag.has_app || status == DownLoadFlag.has_pkg) {
                mDownloadPro.setVisibility(View.GONE);
                mStartApp.setVisibility(View.VISIBLE);
                mStartApp.setText(DownloadProgressButton.getDownloadBtnString(status));
            } else if (status == FileDownloadStatus.paused) {
                mDownloadPro.setData((int) (percent * 360));
                mDownloadPro.setVisibility(View.GONE);
                mStartApp.setVisibility(View.VISIBLE);
                mStartApp.setText("继续");
            } else {
                mDownloadPro.setData(0);
                mDownloadPro.setVisibility(View.GONE);
                mStartApp.setVisibility(View.VISIBLE);
                mStartApp.setText("下载");
            }
        });

        downloadManager.addUpdater(downloadInfo.downloadUrl, (task, percent, status) -> {
            if (mDownloadPro.getTag().equals(downloadInfo.downloadUrl)) {
                if (status == DownLoadFlag.has_pkg) {
                    mDownloadPro.setVisibility(View.GONE);
                    mStartApp.setVisibility(View.VISIBLE);
                    mStartApp.setText("安装");
                } else if (status == FileDownloadStatus.error) {
                    mDownloadPro.setData(0);
                    mDownloadPro.setVisibility(View.GONE);
                    mStartApp.setVisibility(View.VISIBLE);
                    mStartApp.setText("下载");
                } else {
                    if (status == FileDownloadStatus.paused) {
                        mDownloadPro.setData((int) (percent * 360));
                        mDownloadPro.setVisibility(View.GONE);
                        mStartApp.setVisibility(View.VISIBLE);
                        mStartApp.setText("继续");
                    } else {
                        mDownloadPro.setData((int) (percent * 360));
                        mDownloadPro.setVisibility(View.VISIBLE);
                        mStartApp.setVisibility(View.GONE);
                    }
                }
            }
        });
        RxView.clicks(mStartApp).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            downloadManager.downloadClick(mContext, aBoolean, mStartApp.getText().toString(), downloadInfo, downloadId, downloadPath);
        }));
        RxView.clicks(mDownloadPro).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
            FileDownloader.getImpl().pause(downloadId);
            mDownloadPro.setVisibility(View.GONE);
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

    private class OnItemClick implements View.OnClickListener {
        private String gameId, downloadUrl;

        public OnItemClick(String gameId, String downloadUrl) {
            this.gameId = gameId;
            this.downloadUrl = downloadUrl;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, GameDetailsActivity.class);
            intent.putExtra("game_id", gameId);
            intent.putExtra("downloadUrl", downloadUrl);
            mContext.startActivity(intent);
        }
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
        return mGameInfos.size() + 1;
    }


    private class TopViewHolder extends BaseViewHolder {
        ImageView mTopicDetailCover;
        ExpandableTextView mExpandableTextView;
        View mLine;

        TopViewHolder(View itemView) {
            super(itemView, mContext, false);
            mTopicDetailCover = $(R.id.topic_detail_cover);
            mExpandableTextView = $(R.id.expandable_view);
            mLine = $(R.id.line);
        }
    }

    private class TopicDetailCardViewHolder extends BaseViewHolder {
        ImageView mTopicCover;
        ShapeImageView mTopicGameIcon;
        TextView mStartApp, mTopicGameTitle, mRatingText, mGameDesc;
        DownloadRoundBar mDownloadPro;
        RelativeLayout mItemLayout;
        MaterialRatingBar mMaterialRatingBar;
        JCVideoPlayerStandard mVideoPlayer;
        View mLine;

        TopicDetailCardViewHolder(View itemView) {
            super(itemView, mContext, false);
            mTopicCover = $(R.id.topic_cover);
            mTopicGameIcon = $(R.id.topic_game_icon);
            mStartApp = $(R.id.start_app);
            mTopicGameTitle = $(R.id.topic_game_title);
            mRatingText = $(R.id.rating_text);
            mGameDesc = $(R.id.game_desc);
            mDownloadPro = $(R.id.download_pro);
            mMaterialRatingBar = $(R.id.rating_bar);
            mLine = $(R.id.line);
            mItemLayout = $(R.id.item_layout);
            mVideoPlayer = $(R.id.video_player);
        }
    }
}
