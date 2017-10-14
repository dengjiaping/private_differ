package com.appgame.differ.module.personal.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.ErrorActivity;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lzx on 2017/5/4.
 * 386707112@qq.com
 */

public class MineGameAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<RecommedInfo> mInfoList;
    private RxPermissions mRxPermissions;
    public static final int TYPE_EMPTY = 0;
    public static final int TYPE_ITEM = 1;
    private boolean isRecom;
    private RelativeLayout.LayoutParams mParams;
    private int margin7, margin14, margin15, margin10;

    public MineGameAdapter(Context context, RxPermissions rxPermissions) {
        mContext = context;
        mRxPermissions = rxPermissions;
        mInfoList = new ArrayList<>();

        margin7 = CommonUtil.dip2px(mContext, 7);
        margin15 = CommonUtil.dip2px(mContext, 10);
        margin14 = CommonUtil.dip2px(mContext, 14);
        margin10 = CommonUtil.dip2px(mContext, 5);

        mParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mParams.bottomMargin = margin7;

    }

    public void setInfoList(List<RecommedInfo> infoList, boolean isLoadMore, boolean isRecom) {
        this.isRecom = isRecom;
        if (!isLoadMore)
            mInfoList.clear();
        mInfoList.addAll(infoList);
        notifyDataSetChanged();


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_game, parent, false);
            return new GameHolder(view);
        } else if (viewType == TYPE_EMPTY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_mine_game_empty, parent, false);
            return new EmptyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof GameHolder) {
            if (isRecom) {
                position = position - 1;
            }
            GameHolder holder = (GameHolder) viewHolder;
            if (position % 2 == 0) {
                mParams.leftMargin = margin15;
                mParams.rightMargin = margin10;
            } else {
                mParams.leftMargin = margin10;
                mParams.rightMargin = margin15;
            }
            if (position == 0 || position == 1) {
                mParams.topMargin = margin14;
            } else {
                mParams.topMargin = margin7;
            }
            viewHolder.itemView.setLayoutParams(mParams);

            RecommedInfo info = mInfoList.get(position);
            Glide.with(mContext).load(info.getCover()).placeholder(R.color.default_image_color).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
            holder.mGameName.setText(info.getGameNameCn());
            List<TagsInfo> categories = info.getTags();
            String tag = "";
            for (int i = 0; i < categories.size(); i++) {
                if (i < 2) {
                    tag += categories.get(i).getName() + "、";
                }
            }
            if (tag.length() > 1) {
                tag = tag.substring(0, tag.length() - 1);
            }
            holder.mGameTag.setText(tag);
            holder.mGameCover.setOnClickListener(v -> {
                String gameId = info.getGameId();
                String status = info.getStatus();
                gotoGameDetailsActivity(gameId, status);
            });
            initDownload(info.getDownloadInfo(), holder.mProgressBar);
        } else if (viewHolder instanceof EmptyHolder) {
            if (position != getItemCount() - 1) {

            }
        }
    }

    private void gotoGameDetailsActivity(String game_id, String status) {
        Intent intent;
        if (status.equals("0")) {
            intent = new Intent(mContext, ErrorActivity.class);
            intent.putExtra("barTitle", "游戏详情");
        } else {
            intent = new Intent(mContext, GameDetailsActivity.class);
            intent.putExtra("game_id", game_id);
        }
        mContext.startActivity(intent);
    }

    @Override
    public int getItemViewType(int position) {
        int lastPosition = getItemCount() - 1;
        RecommedInfo recommedInfo = mInfoList.get(lastPosition);
        if (recommedInfo != null) {
            if (!TextUtils.isEmpty(recommedInfo.getCover())) {
                return TYPE_ITEM;
            } else {
                if (position == 0) {
                    return TYPE_EMPTY;
                } else {
                    return TYPE_ITEM;
                }
            }
        } else {
            if (position == 0) {
                return TYPE_EMPTY;
            } else {
                return TYPE_ITEM;
            }
        }
    }

    private void initDownload(DownloadInfo downloadInfo, DownloadProgressButton mProgressBar) {

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

    public void startAllAutoTasks() {
        for (RecommedInfo info : mInfoList) {
            DownloadInfo downloadInfo = info.getDownloadInfo();
            DownloadManager.getImpl().startAllAutoTasks(downloadInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    private class GameHolder extends RecyclerView.ViewHolder {
        ImageView mGameCover;
        TextView mGameName, mGameTag;
        DownloadProgressButton mProgressBar;
        CardView mRootLayout;

        GameHolder(View itemView) {
            super(itemView);
            mGameCover = (ImageView) itemView.findViewById(R.id.game_cover);
            mGameName = (TextView) itemView.findViewById(R.id.game_name);
            mGameTag = (TextView) itemView.findViewById(R.id.game_tag);
            mProgressBar = (DownloadProgressButton) itemView.findViewById(R.id.pro_bar);
            mRootLayout = (CardView) itemView.findViewById(R.id.root_layout);
        }
    }

    private class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}
