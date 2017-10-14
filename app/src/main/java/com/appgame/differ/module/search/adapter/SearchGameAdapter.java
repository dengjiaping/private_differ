package com.appgame.differ.module.search.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.appgame.differ.widget.star.StarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by lzx on 2017/5/25.
 * 386707112@qq.com
 */
public class SearchGameAdapter extends LoadMoreAdapter<RecommedInfo> {
    private RxPermissions mRxPermissions;
    private DecimalFormat format;

    public SearchGameAdapter(Context context, RxPermissions rxPermissions) {
        super(context);
        this.mRxPermissions = rxPermissions;
        format = new DecimalFormat("#0.0");
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_game, parent, false);
        return new SearchGameHolder(view, mContext);
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        SearchGameHolder holder = (SearchGameHolder) viewHolder;
        RecommedInfo info = mDataList.get(position);
        Glide.with(mContext).load(info.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
        holder.mGameName.setText(info.getGameNameCn());
        holder.mStarView.setIndicate(true);
        holder.mStarView.setStarMark(Float.parseFloat(info.getGameStar()));
        holder.mStarMark.setText(format.format(Float.parseFloat(info.getGameStar())));
        holder.mTagFlowLayout.setAdapter(new TagAdapter<TagsInfo>(info.getTags()) {
            @Override
            public View getView(FlowLayout parent, int position, TagsInfo tagsInfo) {
                TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_tag, parent, false);
                textView.setText(tagsInfo.getName());
                return textView;
            }
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, GameDetailsActivity.class);
            intent.putExtra("game_id", info.getGameId());
            mContext.startActivity(intent);
        });

        initDownload(holder.mProgressBar, info.getDownloadInfo());
    }

    private void initDownload(DownloadProgressButton mProgressBar, DownloadInfo downloadInfo) {
        //解决复用问题
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
        for (RecommedInfo model : mDataList) {
            DownloadInfo downloadInfo = model.getDownloadInfo();
            DownloadManager.getImpl().startAllAutoTasks(downloadInfo);
        }
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }

    private class SearchGameHolder extends BaseViewHolder {
        ShapeImageView mGameIcon;
        TextView mGameName, mStarMark;
        StarView mStarView;
        TagFlowLayout mTagFlowLayout;
        DownloadProgressButton mProgressBar;

        SearchGameHolder(View itemView, Context context) {
            super(itemView, context, false);
            mGameIcon = $(R.id.game_icon);
            mGameName = $(R.id.game_name);
            mStarMark = $(R.id.star_mark);
            mStarView = $(R.id.start_bar);
            mTagFlowLayout = $(R.id.tag_flow_layout);
            mProgressBar = $(R.id.pro_bar);
        }
    }
}

