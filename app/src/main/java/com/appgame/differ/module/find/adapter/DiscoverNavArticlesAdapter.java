package com.appgame.differ.module.find.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.module.daily.DailyDetailActivity;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by lzx on 2017/5/27.
 * 386707112@qq.com
 */
public class DiscoverNavArticlesAdapter extends LoadMoreAdapter<DailyInfo> {

    public DiscoverNavArticlesAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_article_list, parent, false);
        return new DiscoverNavHolder(view, mContext);
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        DiscoverNavHolder holder = (DiscoverNavHolder) viewHolder;
        DailyInfo dailyInfo = mDataList.get(position);
        Glide.with(mContext).load(dailyInfo.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mArticleCover);
        holder.mArticleTitle.setText(dailyInfo.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DailyDetailActivity.class);
            intent.putExtra("dailyId", dailyInfo.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }

    public class DiscoverNavHolder extends BaseViewHolder {

        ImageView mArticleCover;
        TextView mArticleTitle;

        public DiscoverNavHolder(View itemView, Context context) {
            super(itemView, context, false);
            mArticleCover = $(R.id.article_cover);
            mArticleTitle = $(R.id.article_title);
        }
    }

}

