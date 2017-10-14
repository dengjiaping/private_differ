package com.appgame.differ.module.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
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
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.user.AchievesInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.module.personal.view.BadgeActivity;
import com.appgame.differ.module.personal.view.LeaveMessageActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/4.
 * 386707112@qq.com
 */

public class AboutAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LIST = 1;
    private static final int TYPE_FOOTER = 2;

    private List<UserGuest> mList;
    private Context mContext;
    private String action;
    private UserInfo mUserInfo;
    private int msgNum = 0;

    public AboutAdapter(Context context, String action, String userId) {
        mContext = context;
        mList = new ArrayList<>();
        this.action = action;

    }

    public void setList(List<UserGuest> list) {
        msgNum = list.size();
        mList.clear();
        if (list.size() > 3) {
            mList.add(list.get(0));
            mList.add(list.get(1));
            mList.add(list.get(2));
        } else {
            mList = list;
        }
        notifyDataSetChanged();
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_mine_about, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_about, parent, false);
            return new AboutHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_mine_about, parent, false);
            return new FooterHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {

            HeaderHolder headerHolder = (HeaderHolder) holder;
            boolean isOther = !TextUtils.isEmpty(action) && action.equals("other");  //organization
            headerHolder.mSummaryTitle.setText(isOther ? "签名" : "签名");

            if (mUserInfo != null) {
                headerHolder.mSummary.setText(TextUtils.isEmpty(mUserInfo.getRemark()) ? "空空de签名无法燃烧起我的小宇宙" : mUserInfo.getRemark());
                if (mUserInfo.getRank() != null) {
                    headerHolder.mApproveLayout.setVisibility(View.VISIBLE);
                    headerHolder.mline1.setVisibility(View.VISIBLE);
                    headerHolder.mDifferTitle.setText(mUserInfo.getRank().getName());
                    Glide.with(mContext).load(mUserInfo.getRank().getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(headerHolder.mApproveIcon);
                } else {
                    headerHolder.mApproveLayout.setVisibility(View.GONE);
                    headerHolder.mline1.setVisibility(View.GONE);
                }

                List<AchievesInfo> achievesInfos = mUserInfo.getAchieves();
                if (achievesInfos != null) {
                    if (achievesInfos.size() != 0) {
                        headerHolder.mBadgeLayout.removeAllViews();
                        for (AchievesInfo info : achievesInfos) {
                            ImageView imageView = new ImageView(mContext);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CommonUtil.dp2px(mContext, 35), CommonUtil.dp2px(mContext, 35));
                            params.leftMargin = CommonUtil.dp2px(mContext, 12);
                            imageView.setLayoutParams(params);
                            Glide.with(mContext).load(info.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
                            headerHolder.mBadgeLayout.addView(imageView);
                        }
//                        headerHolder.mBadgeItem.setVisibility(View.VISIBLE);
//                        headerHolder.mBadgeTitle.setVisibility(View.VISIBLE);
                    } else {
//                        headerHolder.mBadgeItem.setVisibility(View.GONE);
//                        headerHolder.mBadgeTitle.setVisibility(View.GONE);
                    }
                } else {
//                    headerHolder.mBadgeItem.setVisibility(View.GONE);
//                    headerHolder.mBadgeTitle.setVisibility(View.GONE);
                }
            }
            headerHolder.mBadgeItem.setVisibility(View.GONE);
            headerHolder.mMsgNum.setText(msgNum + "条留言");
            headerHolder.mBadgeItem.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, BadgeActivity.class);
                intent.putExtra("isOther", isOther);
                intent.putExtra("action", "mine");
                intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) mUserInfo.getAchieves());
                mContext.startActivity(intent);
            });

        } else if (holder instanceof AboutHolder) {
            AboutHolder aboutHolder = (AboutHolder) holder;
            aboutHolder.mReCommLayout.setVisibility(View.GONE);
            if (mList.size() != 0) {
                position = position - 1;
                if (position != getItemCount() - 2) {
                    UserGuest userGuest = mList.get(position);
                    String content = userGuest.getContent();
                    if (content.contains("回复#Replay#")) {
                        content = content.replace("#Replay#", "");
                    }

                    aboutHolder.mAboutDesc.setText(content);
                    aboutHolder.mUserName.setText(CommonUtil.getNickName(userGuest.getAuthor().getNickName()));
                    aboutHolder.mAboutTime.setText(CommonUtil.getStandardDate(userGuest.getCreatedAt()));
                    Glide.with(mContext).load(userGuest.getAuthor().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(aboutHolder.mUserHeader);
                    aboutHolder.mTagCount.setText(userGuest.getChildGuests() != null ? String.valueOf(userGuest.getChildGuests().size()) : "0");
                    aboutHolder.mLikeCount.setText(userGuest.getThumbsUp());
                    aboutHolder.mLikeCount.setCompoundDrawablesWithIntrinsicBounds(userGuest.getIsThumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0);

                    aboutHolder.mUserHeader.setOnClickListener(v -> {
                        IntentHelper.startPersionActivity((PersonalActivity) mContext, aboutHolder.mUserHeader, userGuest.getAuthor().getUserId());
                    });
                    // aboutHolder.mline.setVisibility(View.INVISIBLE);
                } else {
                    // aboutHolder.mline.setVisibility(View.VISIBLE);
                }
            }
        } else if (holder instanceof FooterHolder) {
            FooterHolder footerHolder = (FooterHolder) holder;
            if (mList.size() == 0) {
                footerHolder.mTitle.setText("空虚寂寞冷，谁来温暖TA？");
                footerHolder.mTitle.setTextColor(ContextCompat.getColor(mContext, R.color.black_alpha_50));
                footerHolder.mTitle.setTextSize(18);
            } else {
                footerHolder.mTitle.setText("全部留言");
                footerHolder.mTitle.setTextColor(Color.parseColor("#FF59C5CA"));
                footerHolder.mTitle.setTextSize(16);
            }
            footerHolder.mBtnAllComm.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, LeaveMessageActivity.class);
                intent.putExtra("userId", mUserInfo.getUserId());
                intent.putExtra("action", action);
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_LIST;
        }
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        TextView mSummary, mDifferTitle, mEmptyTitle, mSummaryTitle, mApprove, mBadgeTitle, mMsgNum;
        LinearLayout mBadgeLayout;
        RelativeLayout mBadgeItem, mApproveLayout;
        ImageView mApproveIcon, mIconBadge;
        View mline1;

        public HeaderHolder(View itemView) {
            super(itemView);
            mSummaryTitle = (TextView) itemView.findViewById(R.id.summary_title);
            mSummary = (TextView) itemView.findViewById(R.id.summary);
            mDifferTitle = (TextView) itemView.findViewById(R.id.differ_title);
            mMsgNum = (TextView) itemView.findViewById(R.id.msg_num);
            mEmptyTitle = (TextView) itemView.findViewById(R.id.empty_title);
            mApprove = (TextView) itemView.findViewById(R.id.approve);
            mBadgeTitle = (TextView) itemView.findViewById(R.id.badge_title);
            mBadgeLayout = (LinearLayout) itemView.findViewById(R.id.badge_layout);
            mBadgeItem = (RelativeLayout) itemView.findViewById(R.id.badge_item);
            mApproveIcon = (ImageView) itemView.findViewById(R.id.approve_icon);
            mIconBadge = (ImageView) itemView.findViewById(R.id.icon_badge);
            mApproveLayout = (RelativeLayout) itemView.findViewById(R.id.approve_layout);
            mline1 = itemView.findViewById(R.id.line1);
        }
    }

    public class FooterHolder extends RecyclerView.ViewHolder {
        LinearLayout mBtnAllComm;
        TextView mTitle;

        public FooterHolder(View itemView) {
            super(itemView);
            mBtnAllComm = (LinearLayout) itemView.findViewById(R.id.btn_all_comm);
            mTitle = (TextView) itemView.findViewById(R.id.title);

        }
    }

    public class AboutHolder extends RecyclerView.ViewHolder {
        CircleImageView mUserHeader;
        TextView mUserName, mAboutTime, mAboutDesc, mBtnShowAll,mTagCount,mLikeCount;
        RelativeLayout mReCommLayout;
        View mline;

        public AboutHolder(View itemView) {
            super(itemView);
            mline = itemView.findViewById(R.id.line);
            mBtnShowAll = (TextView) itemView.findViewById(R.id.btn_see_more);
            mUserHeader = (CircleImageView) itemView.findViewById(R.id.user_header);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mAboutTime = (TextView) itemView.findViewById(R.id.about_time);
            mAboutDesc = (TextView) itemView.findViewById(R.id.about_desc);
            mReCommLayout = (RelativeLayout) itemView.findViewById(R.id.re_comm_layout);
            mTagCount = (TextView) itemView.findViewById(R.id.tag_count);
            mLikeCount = (TextView) itemView.findViewById(R.id.up);
        }
    }
}
