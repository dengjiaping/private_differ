package com.appgame.differ.module.topic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.module.topic.TopicDetailCardActivity;
import com.appgame.differ.module.topic.TopicDetailListActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by lzx on 2017/7/4.
 */

public class TopicListAdapter extends LoadMoreAdapter<TopicInfo> {
    public TopicListAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_topic, parent, false);
        return new TopicListHolder(view);
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        TopicListHolder holder = (TopicListHolder) viewHolder;
        TopicInfo topicInfo = mDataList.get(position);
        Glide.with(mContext).load(topicInfo.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mTopicCover);
        holder.mTopicTitle.setText(topicInfo.getTitle());
        holder.mTopicTime.setText(CommonUtil.formatDate(topicInfo.getCreatedAt()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (topicInfo.getLayout().equals("table")) {
                intent = new Intent(mContext, TopicDetailListActivity.class).putExtra("topicId", topicInfo.getTopicId());
            } else {
                intent = new Intent(mContext, TopicDetailCardActivity.class).putExtra("topicId", topicInfo.getTopicId());
            }
            mContext.startActivity(intent);
        });
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }

    private class TopicListHolder extends BaseViewHolder {
        ImageView mTopicCover;
        TextView mTopicTitle, mTopicTime;

        TopicListHolder(View itemView) {
            super(itemView, mContext, true);
            mTopicCover = $(R.id.topic_cover);
            mTopicTitle = $(R.id.topic_title);
            mTopicTime = $(R.id.topic_time);
        }
    }

}
