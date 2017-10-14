package com.appgame.differ.module.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.user.AchievesInfo;
import com.appgame.differ.module.personal.view.BadgeActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/5.
 * 386707112@qq.com
 */

public class BadgeAdapter extends RecyclerView.Adapter<BadgeAdapter.BadgeHolder> {

    private Context mContext;
    private String action;
    private List<AchievesInfo> mAchievesInfos;
    private onItemClickListener mItemClickListener;

    public BadgeAdapter(Context context, String action) {
        mContext = context;
        this.action = action;
        mAchievesInfos = new ArrayList<>();
    }

    public void setBadgeInfoList(List<AchievesInfo> badgeInfoList) {
        mAchievesInfos = badgeInfoList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(onItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public BadgeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_badge, parent, false);
        return new BadgeHolder(view);
    }

    @Override
    public void onBindViewHolder(BadgeHolder holder, int position) {
        AchievesInfo badgeInfo = mAchievesInfos.get(position);
        Glide.with(mContext).load(badgeInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
        if (action.equals("all")) {
            holder.mBtnShow.setVisibility(View.GONE);
            holder.mBadgeDesc.setVisibility(View.VISIBLE);
            holder.mBadgeDesc.setVisibility(View.VISIBLE);
        } else {
            holder.mShowAll.setVisibility(position == mAchievesInfos.size() - 1 ? View.VISIBLE : View.GONE);
            holder.mBtnShow.setVisibility(View.VISIBLE);
            holder.mBadgeDesc.setVisibility(View.GONE);

            if (badgeInfo.isStatus()) {
                holder.mBtnShow.setText("取消展示");
                holder.mBtnShow.setTextColor(ContextCompat.getColor(mContext, R.color.black_alpha_50));
                holder.mBtnShow.setBackgroundResource(R.drawable.bg_frame_gray_corner_3);
            } else {
                holder.mBtnShow.setText("展示");
                holder.mBtnShow.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
                holder.mBtnShow.setBackgroundResource(R.drawable.bg_frame_green_corner_3);
            }

            holder.mShowAll.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, BadgeActivity.class);
                intent.putExtra("action", "all");
                mContext.startActivity(intent);
            });
            holder.mBtnShow.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position, badgeInfo.getAcId());
                }
            });
        }
    }

    public void updateItemChange(int position) {
        for (int i = 0; i < mAchievesInfos.size(); i++) {
            if (mAchievesInfos.get(position).isStatus()) {
                mAchievesInfos.get(position).setStatus(false);
            //    UserInfoManager.getImpl().updateAchievesInfoStatus(mAchievesInfos.get(position).getAcId(), false);
            } else {
                mAchievesInfos.get(i).setStatus(i == position);
            //    UserInfoManager.getImpl().updateAchievesInfoStatus(mAchievesInfos.get(i).getAcId(), i == position);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mAchievesInfos.size();
    }

    public interface onItemClickListener {
        void onItemClick(int position, String achieve_id);
    }

    public class BadgeHolder extends RecyclerView.ViewHolder {
        ImageView mIconBadge;
        TextView mBadgeTitle, mBadgeDesc, mBtnShow, mShowAll;

        public BadgeHolder(View itemView) {
            super(itemView);
            mIconBadge = (ImageView) itemView.findViewById(R.id.icon_badge);
            mBadgeTitle = (TextView) itemView.findViewById(R.id.badge_title);
            mBadgeDesc = (TextView) itemView.findViewById(R.id.badge_desc);
            mBtnShow = (TextView) itemView.findViewById(R.id.btn_show);
            mShowAll = (TextView) itemView.findViewById(R.id.show_all);
            mShowAll.setVisibility(View.GONE);
        }
    }
}
