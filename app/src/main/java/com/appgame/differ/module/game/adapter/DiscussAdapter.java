package com.appgame.differ.module.game.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
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
import com.appgame.differ.bean.daily.CommentReplies;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.game.EvaluationDetailActivity;
import com.appgame.differ.module.daily.adapter.CommentAdapter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.star.StarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lzx on 17/4/13.
 */

public class DiscussAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<DailyComment> mCommentList;
    private UserAppraise appraise;
    private static final int USER_EVALUATION = 0;
    private static final int USER_DISCUSS = 1;
    private OnLikeClickListener mOnLikeClickListener;
    private OnTagClickListener onTagClickListener;


    private OnChildItemClickListener mOnChildItemClickListener;
    private OnAddTagClickListener mOnAddTagClickListener;
    private OnReplayCommentListener mOnReplayCommentListener;
    private OnFirstLikeClickListener mOnFirstLikeClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private LinearLayout.LayoutParams layoutParams;

    public DiscussAdapter(Context mContext) {
        this.mContext = mContext;
        mCommentList = new ArrayList<>();
        layoutParams = new LinearLayout.LayoutParams(CommonUtil.dip2px(mContext, 25), CommonUtil.dip2px(mContext, 25));
        layoutParams.bottomMargin = CommonUtil.dip2px(mContext,10);
    }

    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        mOnLikeClickListener = onLikeClickListener;
    }

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    public void setOnChildItemClickListener(OnChildItemClickListener onChildItemClickListener) {
        mOnChildItemClickListener = onChildItemClickListener;
    }

    public void setOnAddTagClickListener(OnAddTagClickListener onAddTagClickListener) {
        mOnAddTagClickListener = onAddTagClickListener;
    }

    public void setOnReplayCommentListener(OnReplayCommentListener onReplayCommentListener) {
        mOnReplayCommentListener = onReplayCommentListener;
    }

    public void setOnFirstLikeClickListener(OnFirstLikeClickListener onFirstLikeClickListener) {
        mOnFirstLikeClickListener = onFirstLikeClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setCommentList(List<DailyComment> commentList, UserAppraise appraise, boolean isLoadMore) {
        if (!isLoadMore)
            mCommentList.clear();
        mCommentList.addAll(commentList);
        this.appraise = appraise;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == USER_EVALUATION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_games_detail_evaluaion, parent, false);
            return new UserEvaluationViewHolder(view);
        } else if (viewType == USER_DISCUSS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_about, parent, false);
            return new DiscussViewHolder(view);
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof UserEvaluationViewHolder) {
            UserEvaluationViewHolder holder = (UserEvaluationViewHolder) viewHolder;
            if (appraise != null) {
                Glide.with(mContext).load(appraise.getAuthor().getAvatar()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
                holder.mUserName.setText(CommonUtil.getNickName(appraise.getAuthor().getNickName()));
                holder.mStarView.setIndicate(true);
                holder.mStarView.setStarMark(Float.parseFloat(appraise.getStar()));
                holder.mContent.setText(appraise.getContent());
                holder.mStarMark.setText(String.format("%.1f", Float.parseFloat(appraise.getStar())));
                holder.mLikeCount.setText(TextUtils.isEmpty(appraise.getThumbsUp()) ? "0" : appraise.getThumbsUp());
                holder.mLikeCount.setCompoundDrawablesWithIntrinsicBounds(appraise.getIsThumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0);
                holder.mCommentCount.setText(appraise.getCommented());
                holder.mDiscussTitle.setText("玩家讨论(" + mCommentList.size() + ")");
                holder.mEmptyView.setVisibility(mCommentList.size() > 0 ? View.GONE : View.VISIBLE);

                holder.mFlowLayout.setAdapter(new TagAdapter<TagsInfo>(appraise.getTags()) {
                    @Override
                    public View getView(FlowLayout parent, final int position, final TagsInfo tagsInfo) {
                        final TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tag_flow_layout, parent, false);
                        if (tagsInfo.getName().equals("＋")) {
                            textView.setText(tagsInfo.getName());
                            textView.setBackgroundResource(R.drawable.item_tag_eva);
                            textView.setPadding(CommonUtil.dip2px(mContext, 20), CommonUtil.dip2px(mContext, 2), CommonUtil.dip2px(mContext, 20), CommonUtil.dip2px(mContext, 2));
                            textView.setOnClickListener(v -> {
                                if (mOnAddTagClickListener != null) {
                                    mOnAddTagClickListener.onAddTag(position, tagsInfo);
                                }
                            });
                        } else {
                            textView.setText(Integer.parseInt(tagsInfo.getThumbsUp()) < 99 ? tagsInfo.getName() + "｜" + tagsInfo.getThumbsUp() : tagsInfo.getName() + "｜" + "99+");
                            textView.setEnabled(true);
                            if (tagsInfo.getIsThumb() == 1) {
                                textView.setBackgroundResource(R.drawable.bg_tag_normal_selected);
                                textView.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
                            } else {
                                textView.setBackgroundResource(R.drawable.bg_tag_normal);
                            }
                            textView.setOnClickListener(v -> {
                                if (!(tagsInfo.getIsThumb() == 1 && tagsInfo.getThumbsUp().equals("1"))) {
                                    if (onTagClickListener != null)
                                        onTagClickListener.onTagClick(tagsInfo.getId(), tagsInfo.getIsThumb() == 1 ? 0 : 1, position);
                                }
                                textView.setEnabled(false);
                            });
                        }
                        return textView;
                    }
                });

                holder.mLikeHeaderLayout.removeAllViews();
                List<UserInfo> latestThumbs = appraise.getLatestThumbs();
                //转发头像
                int thumbUserCount = latestThumbs.size() > 3 ? 3 : latestThumbs.size();
                for (int i = 0; i < thumbUserCount; i++) {
                    int index = i;
                    ShapeImageView shapeImageView = new ShapeImageView(mContext);
                    shapeImageView.setShapeMode(2);
                    shapeImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    layoutParams.rightMargin = CommonUtil.dip2px(mContext, 5);
                    shapeImageView.setLayoutParams(layoutParams);
                    Glide.with(mContext).load(latestThumbs.get(index).getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(shapeImageView);
                    shapeImageView.setOnClickListener(v -> IntentHelper.startPersionActivity((EvaluationDetailActivity) mContext, shapeImageView, latestThumbs.get(index).getUserId()));
                    holder.mLikeHeaderLayout.addView(shapeImageView);
                }
                RxView.clicks(holder.mLikeCount).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (mOnFirstLikeClickListener != null) {
                        mOnFirstLikeClickListener.onClick(appraise.getId(), appraise.getIsThumb(), 0);
                    }
                });
            }
        } else if (viewHolder instanceof DiscussViewHolder) {
            DiscussViewHolder holder = (DiscussViewHolder) viewHolder;
            position = position - 1;
            int finalPosition = position;
            DailyComment comment = mCommentList.get(position);
            holder.mAboutDesc.setText(comment.getContent());
            holder.mUserName.setText(CommonUtil.getNickName(comment.getRelationships().getNickName()));
            Glide.with(mContext).load(comment.getRelationships().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
            holder.mUserHeader.setOnClickListener(v -> IntentHelper.startPersionActivity((EvaluationDetailActivity) mContext, holder.mUserHeader, comment.getRelationships().getUserId()));
            holder.mUserName.setOnClickListener(v -> IntentHelper.startPersionActivity((EvaluationDetailActivity) mContext, holder.mUserHeader, comment.getRelationships().getUserId()));
            holder.mAboutTime.setText(CommonUtil.getStandardDate(comment.getCreatedAt()));
            holder.mTagCount.setText(String.valueOf(comment.getReplies().size()));
            holder.mBtnUp.setText(comment.getThumbsUp());
            holder.mBtnUp.setCompoundDrawablesWithIntrinsicBounds(comment.getIs_thumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0);
            RxView.clicks(holder.mBtnUp).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(o -> {
                if (mOnLikeClickListener != null) {
                    mOnLikeClickListener.onLikeClick(comment.getComId(), comment.getIs_thumb()  , finalPosition);
                }
            });
            holder.mAboutDesc.setOnClickListener(v -> {
                if (mOnChildItemClickListener != null) {
                    mOnChildItemClickListener.onItemClick(finalPosition, comment);
                }
            });
            holder.mTagCount.setOnClickListener(v -> {
                if (mOnChildItemClickListener != null) {
                    mOnChildItemClickListener.onItemClick(finalPosition, comment);
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onLongClick(comment, comment.getComId(), finalPosition);
                }
                return false;
            });
            initChildRecycleView(holder, comment);
        }
    }

    public void updateLikeUI(int type, int position) {
        mCommentList.get(position).setIs_thumb(type);
        mCommentList.get(position).setThumbsUp(CommonUtil.getThumbNum(type, mCommentList.get(position).getThumbsUp()));
        notifyItemChanged(position + 1, mCommentList);
    }

    public void updateFirstLikeUI(int type, int position) {
        appraise.setIsThumb(type);
        int thumbs_up = Integer.parseInt(appraise.getThumbsUp());
        if (type == 1) {
            thumbs_up = thumbs_up + 1;
            appraise.getLatestThumbs().add(0, UserInfoManager.getImpl().getUserInfo());
        } else {
            thumbs_up = thumbs_up - 1;
            for (UserInfo userInfo : appraise.getLatestThumbs()) {
//                if (userInfo.getUserId().equals(UserInfoManager.getImpl().getUserInfo().getUserId())) {
//                    appraise.getLatestThumbs().remove(userInfo);
//                    break;
//                }
                if (userInfo.getUserId().equals(UserInfoManager.getImpl().getUserId())) {
                    appraise.getLatestThumbs().remove(userInfo);
                    break;
                }
            }
        }
        appraise.setThumbsUp(String.valueOf(thumbs_up));
        notifyItemChanged(position, appraise);
    }

    public void removeCommentUI(int position) {
        mCommentList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    private void initChildRecycleView(DiscussViewHolder holder, DailyComment comment) {
        CommentAdapter commentAdapter = new CommentAdapter(mContext, comment.getComId() );
        holder.mReplayRecycle.setAdapter(commentAdapter);
        if (comment.getReplies() != null) {
            commentAdapter.setList(comment.getReplies(), comment.isShowAll());

            int lave = comment.getReplies().size();

            holder.mBtnShowAll.setVisibility(comment.getReplies().size() > AppConstants.MAX_COUNT ? View.VISIBLE : View.GONE);
            holder.mBtnShowAll.setText(commentAdapter.getItemCount() > AppConstants.MAX_COUNT ? "收起" : "查看全部" + lave + "条回复");
            holder.mBtnShowAll.setOnClickListener(v -> {
                if (commentAdapter.getItemCount() <= AppConstants.MAX_COUNT) {
                    comment.setShowAll(true);
                    commentAdapter.setList(comment.getReplies(), true);
                } else {
                    comment.setShowAll(false);
                    commentAdapter.setList(comment.getReplies(), false);
                }
                holder.mBtnShowAll.setText(commentAdapter.getItemCount() > AppConstants.MAX_COUNT ? "收起" : "查看全部" + lave + "条回复");
            });

            commentAdapter.setOnItemClickListener((replies, commentId) -> {
                if (mOnReplayCommentListener != null) {
                    mOnReplayCommentListener.onClick(replies, commentId);
                }
            });
            holder.mReCommLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mReCommLayout.setVisibility(View.GONE);
        }
    }

    public interface OnChildItemClickListener {
        void onItemClick(int position, DailyComment guest);
    }

    public interface OnAddTagClickListener {
        void onAddTag(int position, TagsInfo tagsInfo);
    }

    public interface OnTagClickListener {
        void onTagClick(String tagsId, int type, int position);
    }

    public interface OnLikeClickListener {
        void onLikeClick(String tagsId, int type, int position);
    }

    public interface OnReplayCommentListener {
        void onClick(CommentReplies replies, String commentId);
    }

    public interface OnFirstLikeClickListener {
        void onClick(String commend_id, int isThumb, int position);
    }

    public interface OnItemLongClickListener {
        void onLongClick(DailyComment comment, String dynamicId, int position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return USER_EVALUATION;
        } else {
            return USER_DISCUSS;
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size() + 1;
    }

    private class UserEvaluationViewHolder extends BaseViewHolder {

        TextView mUserName, mContent, mStarMark, mLikeCount, mCommentCount, mDiscussTitle;
        CircleImageView mUserHeader;
        StarView mStarView;
        TagFlowLayout mFlowLayout;
        RelativeLayout mLabelLayout;
        CustomEmptyView mEmptyView;
        LinearLayout mLikeHeaderLayout;

        UserEvaluationViewHolder(View view) {
            super(view, mContext, false);
            mUserName = $(R.id.user_name);
            mContent = $(R.id.content);
            mStarMark = $(R.id.star_mark);
            mUserHeader = $(R.id.avatar);
            mStarView = $(R.id.start_bar);
            mFlowLayout = $(R.id.tag_flow_layout);
            mLikeCount = $(R.id.tag_count);
            mCommentCount = $(R.id.comment_count);
            mDiscussTitle = $(R.id.discuss_title);
            mLabelLayout = $(R.id.label_layout);
            mEmptyView = $(R.id.empty_view);
            mLikeHeaderLayout = $(R.id.like_header_layout);
            $(R.id.line1).setVisibility(View.INVISIBLE);
            mCommentCount.setVisibility(View.GONE);
            mLabelLayout.setVisibility(View.VISIBLE);
            mEmptyView.initBigUI();
        }
    }

    private class DiscussViewHolder extends BaseViewHolder {

        CircleImageView mUserHeader;
        TextView mUserName, mAboutTime, mAboutDesc, mBtnShowAll, mTagCount, mBtnUp;
        RecyclerView mReplayRecycle;
        RelativeLayout mReCommLayout, mCommentLayout;

        DiscussViewHolder(View view) {
            super(view, mContext, false);
            mUserHeader = $(R.id.user_header);
            mUserName = $(R.id.user_name);
            mAboutTime = $(R.id.about_time);
            mAboutDesc = $(R.id.about_desc);
            mBtnShowAll = $(R.id.btn_see_more);

            mTagCount = $(R.id.tag_count);
            mBtnUp = $(R.id.up);

            mReplayRecycle = $(R.id.replay_recycle);
            mReplayRecycle.setLayoutManager(new LinearLayoutManager(mContext));
            mReplayRecycle.setFocusable(false);
            mReplayRecycle.setHasFixedSize(true);
            mReCommLayout = $(R.id.re_comm_layout);
            mCommentLayout = $(R.id.comment_layout);
            mCommentLayout.setVisibility(View.VISIBLE);
        }
    }


}
