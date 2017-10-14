package com.appgame.differ.module.daily.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.CommentReplies;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.daily.DailyDetailActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.star.StarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/4/15.
 * 386707112@qq.com
 */

public class DailyDetailCommentAdapter extends RecyclerView.Adapter<DailyDetailCommentAdapter.DailyViewHolder> {

    private Context mContext;
    private List<DailyComment> mList;
    private OnLikeClickListener mListener;
    private OnCommentListener mOnCommentListener;
    private OnReplayCommentListener mOnReplayCommentListener;
    private OnItemLongClickListener mOnItemLongClickListener;


    public DailyDetailCommentAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<DailyComment> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void removeCommentUI(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public DailyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_daily_comment, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DailyViewHolder holder, final int position) {
        final DailyComment dailyComment = mList.get(position);

        holder.mUserName.setText(CommonUtil.getNickName(dailyComment.getRelationships().getNickName()));

        if (dailyComment.getRelationships().getRank() != null) {
            Glide.with(mContext).load(dailyComment.getRelationships().getRank().getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
        }

        holder.mContent.setText(dailyComment.getContent());
        holder.mCommentNum.setText(String.valueOf(dailyComment.getReplies().size()));
        holder.mTagNum.setText(dailyComment.getThumbsUp());
        holder.mTagNum.setCompoundDrawablesWithIntrinsicBounds(dailyComment.getIs_thumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0); //
        Glide.with(mContext).load(dailyComment.getRelationships().getAvatar()).into(holder.mUserHeader);
        holder.mAboutTime.setText(CommonUtil.getStandardDate(dailyComment.getCreatedAt()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        holder.mRecyclerView.setFocusable(false);
        holder.mRecyclerView.setLayoutManager(layoutManager);
        holder.mRecyclerView.setHasFixedSize(true);
        holder.mRecyclerView.setNestedScrollingEnabled(false);
        final CommentAdapter commentAdapter = new CommentAdapter(mContext, dailyComment.getComId() );
        holder.mRecyclerView.setAdapter(commentAdapter);

        commentAdapter.setList(dailyComment.getReplies(), dailyComment.isShowAll());
        commentAdapter.setOnItemClickListener((replies, commentId) -> {
            if (mOnReplayCommentListener != null) {
                mOnReplayCommentListener.onClick(replies, commentId);
            }
        });
        int lave = dailyComment.getReplies().size();
        holder.mBtnShowAll.setVisibility(dailyComment.getReplies().size() > AppConstants.MAX_COUNT ? View.VISIBLE : View.GONE);
        holder.mBtnShowAll.setText(commentAdapter.getItemCount() > AppConstants.MAX_COUNT ? "收起" : "查看全部" + lave + "条回复");

        holder.mBtnShowAll.setOnClickListener(v -> {
            if (commentAdapter.getItemCount() <= AppConstants.MAX_COUNT) {
                dailyComment.setShowAll(true);
                commentAdapter.setList(dailyComment.getReplies(), true);
            } else {
                dailyComment.setShowAll(false);
                commentAdapter.setList(dailyComment.getReplies(), false);
            }
            holder.mBtnShowAll.setText(commentAdapter.getItemCount() > AppConstants.MAX_COUNT ? "收起" : "查看全部" + lave + "条回复");
        });

        holder.mContent.setOnClickListener(v -> {
            if (mOnCommentListener != null) {
                mOnCommentListener.onClick(dailyComment);
            }
        });
        holder.mCommentNum.setOnClickListener(v -> {
            if (mOnCommentListener != null) {
                mOnCommentListener.onClick(dailyComment);
            }
        });

        holder.mUserHeader.setOnClickListener(v -> IntentHelper.startPersionActivity((DailyDetailActivity) mContext, holder.mUserHeader, dailyComment.getRelationships().getUserId()));
        holder.mUserName.setOnClickListener(v -> IntentHelper.startPersionActivity((DailyDetailActivity) mContext, holder.mUserHeader, dailyComment.getRelationships().getUserId()));
        holder.mTagNum.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(dailyComment, position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onLongClick(dailyComment, dailyComment.getComId(), position);
            }
            return false;
        });
    }


    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        mOnCommentListener = onCommentListener;
    }

    public void setOnReplayCommentListener(OnReplayCommentListener onReplayCommentListener) {
        mOnReplayCommentListener = onReplayCommentListener;
    }

    public interface OnLikeClickListener {
        void onClick(DailyComment dailyComment, int position);
    }

    public interface OnCommentListener {
        void onClick(DailyComment dailyComment);
    }

    public interface OnReplayCommentListener {
        void onClick(CommentReplies replies, String commentId);
    }

    public interface OnItemLongClickListener {
        void onLongClick(DailyComment comment, String dynamicId, int position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class DailyViewHolder extends BaseViewHolder {

        private CircleImageView mUserHeader;
        private TextView mUserName, mContent, mCommentNum, mTagNum, mBtnShowAll, mAboutTime;
        private StarView mStarView;
        private RecyclerView mRecyclerView;
        private ImageView mIconBadge;

        public DailyViewHolder(View itemView) {
            super(itemView, mContext, false);
            mUserHeader = $(R.id.user_header);
            mUserName = $(R.id.user_name);
            mContent = $(R.id.content);
            mCommentNum = $(R.id.comment_num);
            mTagNum = $(R.id.tag_num);
            mBtnShowAll = $(R.id.btn_see_more);
            mAboutTime = $(R.id.about_time);
            mStarView = $(R.id.start_bar);
            mRecyclerView = $(R.id.recycler_view);
            mIconBadge = $(R.id.icon_badge);
        }
    }
}
