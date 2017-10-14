package com.appgame.differ.module.daily.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.daily.DailyList;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.module.daily.DailyDetailActivity;
import com.appgame.differ.module.topic.TopicDetailCardActivity;
import com.appgame.differ.module.topic.TopicDetailListActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.UserNameView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public class DailyListAdapter extends RecyclerView.Adapter {

    //   public static final int TYPE_FIRST = 0;
    public static final int TYPE_HAS_TIME = 1;
    public static final int TYPE_NORMAL = 2;

    private Context mContext;
    private List<DailyList> mDailyLists;

    private int cardWidth;

    public DailyListAdapter(Context context) {
        mContext = context;
        mDailyLists = new ArrayList<>();
        cardWidth = CommonUtil.getPhoneWidth(mContext) - CommonUtil.dip2px(mContext, 20);
    }

    public void clear(){
        mDailyLists.clear();
        notifyDataSetChanged();
    }

    public void setData(List<DailyList> list, boolean isLoadMore) {
        if (!isLoadMore)
            mDailyLists.clear();
        mDailyLists.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_differ_big, parent, false);
            return new NormalViewHolder(itemView);
        } else {
            View itemTime = LayoutInflater.from(mContext).inflate(R.layout.item_differ_big_time, parent, false);
            return new TimeViewHolder(itemTime);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final DailyList dailyList = mDailyLists.get(position);

        if (viewHolder instanceof NormalViewHolder) {
            NormalViewHolder holder = (NormalViewHolder) viewHolder;
            initData(dailyList, holder.mDifferImage, holder.mUserHeader,
                    holder.mTagNum, holder.mCommentNum, holder.mDifferTitle, holder.mTagBtn, holder.mGameDesc, holder.mUserNameView);

        } else if (viewHolder instanceof TimeViewHolder) {
            TimeViewHolder holder = (TimeViewHolder) viewHolder;

          //  LogUtil.i("--"+dailyList.getDate());

            holder.mDifferTime.setText(dailyList.getDate());

            initData(dailyList, holder.mDifferImage, holder.mUserHeader,
                    holder.mTagNum, holder.mCommentNum, holder.mDifferTitle, holder.mTagBtn, holder.mGameDesc, holder.mUserNameView);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (dailyList.getTarget().equals("article")) {
                intent = new Intent(mContext, DailyDetailActivity.class);
                intent.putExtra("dailyId", dailyList.getDailyInfo().getId());
                mContext.startActivity(intent);
            } else if (dailyList.getTarget().equals("topic")) {
                String layout = dailyList.getTopicInfo().getLayout();
                if (layout.equals("table")) {
                    intent = new Intent(mContext, TopicDetailListActivity.class);
                } else {
                    intent = new Intent(mContext, TopicDetailCardActivity.class);
                }
                intent.putExtra("topicId", dailyList.getTopicInfo().getTopicId());
                mContext.startActivity(intent);
            }
        });
    }


    private void initData(DailyList dailyList, ImageView mDifferImage, CircleImageView mUserHeader,
                          TextView mTagNum, TextView mCommentNum, TextView mDifferTitle,
                          TextView mTagBtn, TextView mGameDesc, UserNameView mUserNameView) {
        String cover = "";
        String userHeader = "";
        String userName = "";
        String tagNum = "";
        String commentNum = "";
        String title = "";
        String content = "";

        switch (dailyList.getTarget()) {
            case "article":
                DailyInfo dailyInfo = dailyList.getDailyInfo();
                mTagNum.setVisibility(View.VISIBLE);
                mCommentNum.setVisibility(View.VISIBLE);

                mTagBtn.setText(dailyInfo.getFrom());
                cover = dailyInfo.getCover();
                userHeader = dailyInfo.getUser().getAvatar();
                userName = dailyInfo.getUser().getNickName();
                tagNum = dailyInfo.getTaged();
                commentNum = dailyInfo.getCommented();
                title = dailyInfo.getTitle();
                content = dailyInfo.getDescription();
                mUserNameView.setUserName(userName).setRankInfo(dailyInfo.getUser().getRank());
                break;
            case "topic":
                TopicInfo topicInfo = dailyList.getTopicInfo();
                mTagNum.setVisibility(View.INVISIBLE);
                mCommentNum.setVisibility(View.INVISIBLE);
                mTagBtn.setVisibility(View.GONE);
                cover = topicInfo.getCover();
                userHeader = topicInfo.getUser().getAvatar();
                userName = CommonUtil.getNickName(topicInfo.getUser().getNickName());
                commentNum = topicInfo.getCommented();
                title = topicInfo.getTitle();
                content = topicInfo.getContent();
                mUserNameView.setUserName(userName).setRankInfo(topicInfo.getUser().getRank());
                break;
        }
        Glide.with(mContext).load(cover).diskCacheStrategy(DiskCacheStrategy.ALL).into(mDifferImage);
        Glide.with(mContext).load(userHeader).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserHeader);

        mTagNum.setText(tagNum);
        mCommentNum.setText(commentNum);
        mDifferTitle.setText(title);
        mGameDesc.setText(content);
    }

    @Override
    public int getItemViewType(int position) {
        DailyList dailyList = mDailyLists.get(position);
        if (mDailyLists.size() > 0) {
            if (position == 0) {
                return TYPE_HAS_TIME;
            } else {
                int index = position - 1;
                if (!mDailyLists.get(index).getDate().equals(dailyList.getDate())) {
                    return TYPE_HAS_TIME;
                } else {
                    return TYPE_NORMAL;
                }
            }

        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mDailyLists.size();
    }

    public class TimeViewHolder extends BaseViewHolder {
        private CircleImageView mUserHeader;
        private TextView mTagBtn, mDifferTitle, mTagNum, mCommentNum, mDifferTime, mGameDesc;
        private ImageView mDifferImage;
        private UserNameView mUserNameView;

        public TimeViewHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mTagBtn = $(R.id.tag_btn);
            mDifferTitle = $(R.id.differ_title);
            mTagNum = $(R.id.tag_num);
            mCommentNum = $(R.id.comment_num);
            mDifferTime = $(R.id.differ_time);

            mGameDesc = $(R.id.game_desc);
            mDifferImage = $(R.id.differ_image);

            mDifferImage.getLayoutParams().height = cardWidth * 9 / 16;
            mDifferImage.requestLayout();
        }
    }

    public class NormalViewHolder extends BaseViewHolder {
        private CircleImageView mUserHeader;
        private TextView mTagBtn, mDifferTitle, mTagNum, mCommentNum, mGameDesc;
        private ImageView mDifferImage;
        private UserNameView mUserNameView;

        public NormalViewHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mTagBtn = $(R.id.tag_btn);
            mDifferTitle = $(R.id.differ_title);
            mTagNum = $(R.id.tag_num);
            mCommentNum = $(R.id.comment_num);
            mDifferImage = $(R.id.differ_image);
            mGameDesc = $(R.id.game_desc);

            mDifferImage.getLayoutParams().height = cardWidth * 9 / 16;
            mDifferImage.requestLayout();
        }
    }
}
