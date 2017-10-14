package com.appgame.differ.module.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
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
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.ErrorActivity;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.daily.DailyDetailActivity;
import com.appgame.differ.module.daily.adapter.CommentAdapter;
import com.appgame.differ.module.dynamic.DynamicDetailActivity;
import com.appgame.differ.module.find.VideoPlayActivity;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.module.game.GameImagePageActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.ExpandableTextView;
import com.appgame.differ.widget.NineImageView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.UserNameView;
import com.appgame.differ.widget.star.StarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.concurrent.TimeUnit;

/**
 * Created by lzx on 17/5/4.
 */

public class DynamicDetailAdapter extends LoadMoreAdapter<DailyComment> {

    private static final int TYPE_DYNAMIC_NORMAL = 0;
    private static final int TYPE_DYNAMIC_FORWARD = 1;
    private static final int TYPE_ARTICLE_NORMAL = 2;
    private static final int TYPE_ARTICLE_FORWARD = 3;
    private static final int TYPE_APPRAISE_NORMAL = 4;
    private static final int TYPE_APPRAISE_FORWARD = 5;

    private static final int TYPE_NORMAL = 6;

    private OnLikeClickListener mOnLikeClickListener;
    private OnCommentLikeListener mOnCommentLikeListener;
    private OnCommentClickListener mOnCommentClickListener;
    private OnReplayClickListener mOnReplayClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private DynamicInfo mDynamicInfo;

    public DynamicDetailAdapter(Context context) {
        super(context);
    }

    public void setDynamicInfo(DynamicInfo dynamicInfo) {
        mDynamicInfo = dynamicInfo;
    }

    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        mOnLikeClickListener = onLikeClickListener;
    }

    public void setOnCommentLikeListener(OnCommentLikeListener onCommentLikeListener) {
        mOnCommentLikeListener = onCommentLikeListener;
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        mOnCommentClickListener = onCommentClickListener;
    }

    public void setOnReplayClickListener(OnReplayClickListener onReplayClickListener) {
        mOnReplayClickListener = onReplayClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    protected int getViewType(int position) {
        if (position == 0) {
            if (mDynamicInfo != null) {
                return getAdapterViewType();
            } else {
                return TYPE_NORMAL;
            }
        } else {
            return TYPE_NORMAL;
        }
    }

    private int getAdapterViewType() {
        String target = mDynamicInfo.getTarget();
        switch (target) {
            case "dynamic": {
                String isForward = mDynamicInfo.getIsForward();
                if (isForward.equals("1")) { //转发
                    return TYPE_DYNAMIC_FORWARD; //转发
                } else {
                    return TYPE_DYNAMIC_NORMAL;
                }
            }
            case "appraise": {
                String isForward = mDynamicInfo.getIsForward();
                if (isForward.equals("1")) { //转发
                    return TYPE_APPRAISE_FORWARD; //转发
                } else {
                    return TYPE_APPRAISE_NORMAL;
                }
            }
            case "article": {
                String isForward = mDynamicInfo.getIsForward();
                if (isForward.equals("1")) { //转发
                    return TYPE_ARTICLE_FORWARD; //转发
                } else {
                    return TYPE_ARTICLE_NORMAL;
                }
            }
        }
        return -1;
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_DYNAMIC_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_normal, parent, false);
            return new DynamicNormalHolder(view);
        } else if (viewType == TYPE_DYNAMIC_FORWARD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_forward, parent, false);
            return new DynamicForwardHolder(view);
        } else if (viewType == TYPE_ARTICLE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_normal, parent, false);
            return new ArticleNormalHolder(view);
        } else if (viewType == TYPE_ARTICLE_FORWARD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article_forward, parent, false);
            return new ArticleForwardHolder(view);
        } else if (viewType == TYPE_APPRAISE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appraise_normal, parent, false);
            return new AppraiseNormalHolder(view);
        } else if (viewType == TYPE_APPRAISE_FORWARD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appraise_forward, parent, false);
            return new AppraiseForwardHolder(view);
        } else if (viewType == TYPE_NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_about, parent, false);
            return new DiscussViewHolder(view);
        }
        return null;
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        if (mDynamicInfo == null)
            return;
        if (viewHolder instanceof DynamicNormalHolder) {
            //动态正常
            DynamicNormalHolder holder = (DynamicNormalHolder) viewHolder;

            initPublicUI(holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, mDynamicInfo);
            initDynamicNormal(holder, mDynamicInfo);
            initGameInfo(mDynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
            initUserLayout(mDynamicInfo, holder.mBtnLike, holder.mCommentNum, holder.mUserHeaderLayout, holder.mEmptyView);
        } else if (viewHolder instanceof DynamicForwardHolder) {
            //动态转发
            DynamicForwardHolder holder = (DynamicForwardHolder) viewHolder;
            initPublicUI(holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, mDynamicInfo);
            initDynamicForward(holder, mDynamicInfo);
            initGameInfo(mDynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
            initUserLayout(mDynamicInfo, holder.mBtnLike, holder.mCommentNum, holder.mUserHeaderLayout, holder.mEmptyView);
        } else if (viewHolder instanceof ArticleNormalHolder) {
            //文章正常
            ArticleNormalHolder holder = (ArticleNormalHolder) viewHolder;
            initPublicUI(holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, mDynamicInfo);
            initArticleNormal(holder, mDynamicInfo);
            initUserLayout(mDynamicInfo, holder.mBtnLike, holder.mCommentNum, holder.mUserHeaderLayout, holder.mEmptyView);
        } else if (viewHolder instanceof ArticleForwardHolder) {
            //文章转发
            ArticleForwardHolder holder = (ArticleForwardHolder) viewHolder;
            initPublicUI(holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, mDynamicInfo);
            initArticleForward(holder, mDynamicInfo);
            initUserLayout(mDynamicInfo, holder.mBtnLike, holder.mCommentNum, holder.mUserHeaderLayout, holder.mEmptyView);
        } else if (viewHolder instanceof AppraiseNormalHolder) {
            //评论正常
            AppraiseNormalHolder holder = (AppraiseNormalHolder) viewHolder;
            initPublicUI(holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, mDynamicInfo);
            initAppraiseNormal(holder, mDynamicInfo);
            initGameInfo(mDynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
            initUserLayout(mDynamicInfo, holder.mBtnLike, holder.mCommentNum, holder.mUserHeaderLayout, holder.mEmptyView);
        } else if (viewHolder instanceof AppraiseForwardHolder) {
            //评论转发
            AppraiseForwardHolder holder = (AppraiseForwardHolder) viewHolder;
            initPublicUI(holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, mDynamicInfo);
            initAppraiseForward(holder, mDynamicInfo);
            initGameInfo(mDynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
            initUserLayout(mDynamicInfo, holder.mBtnLike, holder.mCommentNum, holder.mUserHeaderLayout, holder.mEmptyView);
        } else if (viewHolder instanceof DiscussViewHolder) {
            DiscussViewHolder holder = (DiscussViewHolder) viewHolder;
            position = position - 1;
            DailyComment comment = mDataList.get(position);
            holder.mAboutDesc.setText(comment.getContent());
            holder.mUserName.setText(CommonUtil.getNickName(comment.getRelationships().getNickName()));
            if (comment.getRelationships().getRank() != null) {
                String icon = comment.getRelationships().getRank().getIcon();
                holder.mIconBadge.setVisibility(!TextUtils.isEmpty(icon) ? View.VISIBLE : View.GONE);
                Glide.with(mContext).load(icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
            } else {
                holder.mIconBadge.setVisibility(View.GONE);
            }
            Glide.with(mContext).load(comment.getRelationships().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
            holder.mUserHeader.setOnClickListener(v -> IntentHelper.startPersionActivity((DynamicDetailActivity) mContext, holder.mUserHeader, comment.getRelationships().getUserId()));
            holder.mUserName.setOnClickListener(v -> IntentHelper.startPersionActivity((DynamicDetailActivity) mContext, holder.mUserHeader, comment.getRelationships().getUserId()));
            holder.mAboutTime.setText(CommonUtil.getStandardDate(comment.getCreatedAt()));
            if (comment.getRelationships().getRank() != null) {
                Glide.with(mContext).load(comment.getRelationships().getRank().getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
            }
            holder.mBtnLike.setText(comment.getThumbsUp());
            holder.mBtnLike.setCompoundDrawablesWithIntrinsicBounds(comment.getIs_thumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0);
            holder.mTagCount.setText(String.valueOf(comment.getReplies().size()));
            int finalPosition = position;
            holder.mBtnLike.setOnClickListener(v -> {
                if (mOnCommentLikeListener != null) {
                    mOnCommentLikeListener.onClick(comment.getComId(), comment.getIs_thumb(), finalPosition);
                }
            });
            holder.itemView.setOnClickListener(v -> {
                if (mOnCommentClickListener != null) {
                    mOnCommentClickListener.onClick(comment, finalPosition);
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onLongClick(comment, mDynamicInfo.getId(), finalPosition);
                }
                return false;
            });
            initChildRecycleView(holder, comment);
        }
    }

    public void removeCommentUI(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    /**
     * 公共UI
     */
    private void initPublicUI(CircleImageView mUserHeader, TextView mDyTime, UserNameView mUserNameView, ExpandableTextView mDyContent, TextView mUserAction, DynamicInfo dynamicInfo) {
        Glide.with(mContext).load(dynamicInfo.getAuthor().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserHeader);
        mDyTime.setText(CommonUtil.getStandardDate(dynamicInfo.getUpdateAt()));
        mUserNameView.setUserName(dynamicInfo.getAuthor().getNickName()).setRankInfo(dynamicInfo.getAuthor().getRank());
        mDyContent.setMaxCollapsedLines(100);
        mDyContent.setText(CommonUtil.getDynamicContent(dynamicInfo.getContent()));
        String target = dynamicInfo.getTarget();
        if (target.equals("appraise")) {
            mUserAction.setText(dynamicInfo.getIsForward().equals("0") ? "评价游戏" : "转发");
        } else if (target.equals("article")) {
            mUserAction.setText(dynamicInfo.getIsForward().equals("0") ? "分享文章" : "转发");
        } else {
            mUserAction.setText(dynamicInfo.getIsForward().equals("0") ? "分享游戏" : "转发");
        }
        mUserHeader.setOnClickListener(v -> {
            if (mContext instanceof MainActivity) {
                IntentHelper.startPersionActivity((MainActivity) mContext, mUserHeader, dynamicInfo.getAuthor().getUserId());
            } else if (mContext instanceof DynamicDetailActivity) {
                IntentHelper.startPersionActivity((DynamicDetailActivity) mContext, mUserHeader, dynamicInfo.getAuthor().getUserId());
            }
        });
    }

    /**
     * 点赞头像
     */
    private void initUserLayout(DynamicInfo dynamicInfo, TextView mBtnLike, TextView mCommentNum, LinearLayout mUserHeaderLayout, CustomEmptyView mEmptyView) {
        mBtnLike.setText(dynamicInfo.getThumbsUp());
        mBtnLike.setCompoundDrawablesWithIntrinsicBounds(dynamicInfo.getIsThumb() == 0 ? R.drawable.ic_like_big_def : R.drawable.ic_like_big_pre, 0, 0, 0);

        RxView.clicks(mBtnLike).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(o -> {
            if (mOnLikeClickListener != null)
                mOnLikeClickListener.onClick(dynamicInfo.getTarget().equals("appraise") ? dynamicInfo.getTargetId() : dynamicInfo.getId(), dynamicInfo.getIsThumb(), 0, dynamicInfo.getTarget());
        });

        //评论数
        mCommentNum.setText(mDataList == null ? "玩家讨论(0)" : "玩家讨论(" + mDataList.size() + ")");
        if (mDataList != null) {
            mEmptyView.setVisibility(mDataList.size() > 0 ? View.GONE : View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
        //转发头像
        int thumbUserCount = dynamicInfo.getLatestThumbs().size() > 3 ? 3 : dynamicInfo.getLatestThumbs().size();
        mUserHeaderLayout.removeAllViews();
        for (int i = 0; i < thumbUserCount; i++) {
            int index = i;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(CommonUtil.dip2px(mContext, 25), CommonUtil.dip2px(mContext, 25));
            ShapeImageView shapeImageView = new ShapeImageView(mContext);
            shapeImageView.setShapeMode(2);
            shapeImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            layoutParams.rightMargin = CommonUtil.dip2px(mContext, 5);
            shapeImageView.setLayoutParams(layoutParams);
            Glide.with(mContext).load(dynamicInfo.getLatestThumbs().get(index).getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(shapeImageView);
            shapeImageView.setOnClickListener(v -> IntentHelper.startPersionActivity((FragmentActivity) mContext, shapeImageView, dynamicInfo.getLatestThumbs().get(index).getUserId()));
            mUserHeaderLayout.addView(shapeImageView);
        }
    }

    /**
     * 游戏UI
     */
    private void initGameInfo(DynamicInfo dynamicInfo, ShapeImageView mGameIcon, TextView mGameTitle, TagFlowLayout mFlowLayout, RelativeLayout mGameLayout) {
        Glide.with(mContext).load(dynamicInfo.getGameInfo().getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mGameIcon);
        mGameTitle.setText(dynamicInfo.getGameInfo().getGameNameCn());
        mFlowLayout.setAdapter(new TagAdapter<TagsInfo>(dynamicInfo.getGameInfo().getTags()) {
            @Override
            public View getView(FlowLayout parent, int position, TagsInfo s) {
                TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_tag, parent, false);
                textView.setText(s.getName());
                return textView;
            }
        });
        mGameLayout.setOnClickListener(v -> {
            String gameId = dynamicInfo.getGameInfo().getGameId();
            String status = dynamicInfo.getGameInfo().getStatus();
            gotoGameDetailsActivity(gameId, status);
        });
    }

    /**
     * 动态正常
     */
    private void initDynamicNormal(DynamicNormalHolder holder, DynamicInfo dynamicInfo) {
//        if (dynamicInfo.getImages().size() > 0) {
//            holder.mRecyclerView.setVisibility(View.VISIBLE);
//            PictureAdapter pictureAdapter = new PictureAdapter(mContext, dynamicInfo.getImages());
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 12);
//            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    return pictureAdapter.getSpanSize();
//                }
//            });
//            holder.mRecyclerView.setLayoutManager(gridLayoutManager);
//            holder.mRecyclerView.setAdapter(pictureAdapter);
//            pictureAdapter.setOnItemClickListener((position, urls, holder1) -> {
//                List<String> data = pictureAdapter.getData();
//                Intent intent = GameImagePageActivity.luanch(mContext, "", position, data);
//                mContext.startActivity(intent);
//            });
//        } else {
//            holder.mRecyclerView.setVisibility(View.GONE);
//        }

        holder.mNineImageView.setImageUrls(dynamicInfo.getImages());
        holder.mNineImageView.setOnClickItemListener((i, url) -> {
            Intent intent = GameImagePageActivity.luanch(mContext, "", i, dynamicInfo.getImages());
            mContext.startActivity(intent);
        });
    }

    /**
     * 动态转发
     */
    private void initDynamicForward(DynamicForwardHolder holder, DynamicInfo dynamicInfo) {
        String forwardCon = dynamicInfo.getForward().getContent();
        Spanned forwardContent = Html.fromHtml(CommonUtil.getUserNameWithColor(dynamicInfo.getForward().getAuthor().getNickName()) + forwardCon);
        holder.mForwardContent.setText(forwardContent);
//        if (dynamicInfo.getForward().getImages().size() > 0) {
//            holder.mChildRecycle.setVisibility(View.VISIBLE);
//            PictureAdapter pictureAdapter = new PictureAdapter(mContext, dynamicInfo.getForward().getImages());
//            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 12);
//            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    return pictureAdapter.getSpanSize();
//                }
//            });
//            holder.mChildRecycle.setLayoutManager(gridLayoutManager);
//            holder.mChildRecycle.setAdapter(pictureAdapter);
//            pictureAdapter.setOnItemClickListener((position1, urls, holder1) -> {
//                List<String> data = pictureAdapter.getData();
//                Intent intent = GameImagePageActivity.luanch(mContext, "", position1, data);
//                mContext.startActivity(intent);
//            });
//        } else {
//            holder.mChildRecycle.setVisibility(View.GONE);
//        }
        holder.mNineImageView.setImageUrls(dynamicInfo.getForward().getImages());
        holder.mNineImageView.setOnClickItemListener((i, url) -> {
            Intent intent = GameImagePageActivity.luanch(mContext, "", i, dynamicInfo.getForward().getImages());
            mContext.startActivity(intent);
        });
    }

    /**
     * 文章正常
     */
    private void initArticleNormal(ArticleNormalHolder holder, DynamicInfo dynamicInfo) {
        Glide.with(mContext).load(dynamicInfo.getArticle().getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mArticleCover);
        holder.mArticleTitle.setText(dynamicInfo.getArticle().getTitle());
        holder.mArticleCover.setOnClickListener(v -> {
            gotoDailyDetailActivity(dynamicInfo.getArticle().getId(), dynamicInfo.getArticle().getStatus());
        });
        holder.mArticleTitle.setOnClickListener(v -> {
            gotoDailyDetailActivity(dynamicInfo.getArticle().getId(), dynamicInfo.getArticle().getStatus());
        });
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

    private void gotoDailyDetailActivity(String dailyId, String status) {
        Intent intent;
        if (status.equals("0")) {
            intent = new Intent(mContext, ErrorActivity.class);
            intent.putExtra("barTitle", "动态详情");
        } else {
            intent = new Intent(mContext, DailyDetailActivity.class);
            intent.putExtra("dailyId", dailyId);
        }
        mContext.startActivity(intent);
    }

    /**
     * 文章转发
     */
    private void initArticleForward(ArticleForwardHolder holder, DynamicInfo dynamicInfo) {
        String forwardCon = dynamicInfo.getForward().getContent();
        Spanned forwardContent = Html.fromHtml(CommonUtil.getUserNameWithColor(dynamicInfo.getForward().getAuthor().getNickName()) + forwardCon);
        holder.mForwardTitle.setText(forwardContent);
        Glide.with(mContext).load(dynamicInfo.getForward().getArticle().getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mArticleCover);
        holder.mArticleForwardTitle.setText(dynamicInfo.getForward().getArticle().getTitle());
        String dailyId = dynamicInfo.getForward().getArticle().getId();
        String status = dynamicInfo.getForward().getArticle().getStatus();
        holder.mArticleCover.setOnClickListener(v -> gotoDailyDetailActivity(dailyId, status));
        holder.mArticleForwardTitle.setOnClickListener(v -> gotoDailyDetailActivity(dailyId, status));
        holder.mForwardTitle.setOnContentTextClickListener(() -> gotoDailyDetailActivity(dailyId, status));
    }

    /**
     * 评论正常
     */
    private void initAppraiseNormal(AppraiseNormalHolder holder, DynamicInfo dynamicInfo) {
        holder.mStarView.setIndicate(true);
        holder.mStarView.setStarMark(Float.parseFloat(dynamicInfo.getStar()));
        holder.mStarMark.setText(dynamicInfo.getStar());
        holder.mAppraiseCover.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, VideoPlayActivity.class);
            intent.putExtra("url", dynamicInfo.getGameInfo().getVideo());
            intent.putExtra("title", dynamicInfo.getGameInfo().getGameNameCn());
            intent.putExtra("cover", dynamicInfo.getGameInfo().getCover());
            mContext.startActivity(intent);
        });
        if (TextUtils.isEmpty(dynamicInfo.getGameInfo().getCover())) {
            Glide.with(mContext).load(dynamicInfo.getGameInfo().getIcon()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mAppraiseCover);
        } else {
            Glide.with(mContext).load(dynamicInfo.getGameInfo().getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mAppraiseCover);
        }
    }

    /**
     * 评论转发
     */
    private void initAppraiseForward(AppraiseForwardHolder holder, DynamicInfo dynamicInfo) {
        holder.mForwardStartBar.setIndicate(true);
        holder.mForwardStartBar.setStarMark(Float.parseFloat(dynamicInfo.getForward().getStar()));
        holder.mForwardStarMark.setText(dynamicInfo.getForward().getStar());

        holder.mForwardUserName.setText(CommonUtil.getNickName(dynamicInfo.getForward().getAuthor().getNickName()));

        holder.mForwardContent.setVisibility(TextUtils.isEmpty(dynamicInfo.getForward().getContent()) ? View.GONE : View.VISIBLE);
        holder.mForwardContent.setText(dynamicInfo.getForward().getContent());

        if (TextUtils.isEmpty(dynamicInfo.getForward().getGameInfo().getVideo())) {
            holder.mIconPlay.setVisibility(View.GONE);
            holder.mAppraiseCover.setOnClickListener(null);
        } else {
            holder.mIconPlay.setVisibility(View.VISIBLE);
            holder.mAppraiseCover.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra("url", dynamicInfo.getForward().getGameInfo().getVideo());
                intent.putExtra("title", dynamicInfo.getForward().getGameInfo().getGameNameCn());
                intent.putExtra("cover", dynamicInfo.getForward().getGameInfo().getCover());
                mContext.startActivity(intent);
            });
        }
        String cover = TextUtils.isEmpty(dynamicInfo.getForward().getGameInfo().getCover()) ? dynamicInfo.getForward().getGameInfo().getIcon() : dynamicInfo.getForward().getGameInfo().getCover();
        Glide.with(mContext).load(cover).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mAppraiseCover);
    }

    private void initChildRecycleView(DiscussViewHolder holder, DailyComment dailyComment) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        holder.mReplayRecycle.setFocusable(false);
        holder.mReplayRecycle.setLayoutManager(layoutManager);
        holder.mReplayRecycle.setHasFixedSize(true);
        holder.mReplayRecycle.setNestedScrollingEnabled(false);
        final CommentAdapter commentAdapter = new CommentAdapter(mContext, dailyComment.getComId());
        holder.mReplayRecycle.setAdapter(commentAdapter);

        commentAdapter.setList(dailyComment.getReplies(), dailyComment.isShowAll());
        commentAdapter.setOnItemClickListener((replies, commentId) -> {
            if (mOnReplayClickListener != null) {
                mOnReplayClickListener.onClick(replies, commentId);
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


    }

    public void updateLikeUI(int position, int thumb) {
        mDynamicInfo.setIsThumb(thumb);
        int thumbs_up = Integer.parseInt(mDynamicInfo.getThumbsUp());
        if (thumb == 1) {
            thumbs_up = thumbs_up + 1;
            mDynamicInfo.getLatestThumbs().add(0, UserInfoManager.getImpl().getUserInfo());
        } else {
            thumbs_up = thumbs_up - 1;
            for (UserInfo userInfo : mDynamicInfo.getLatestThumbs()) {
//                if (userInfo.getUserId().equals(UserInfoManager.getImpl().getUserInfo().getUserId())) {
//                    mDynamicInfo.getLatestThumbs().remove(userInfo);
//                    break;
//                }
                if (userInfo.getUserId().equals(UserInfoManager.getImpl().getUserId())) {
                    mDynamicInfo.getLatestThumbs().remove(userInfo);
                    break;
                }
            }
        }
        mDynamicInfo.setThumbsUp(String.valueOf(thumbs_up));
        notifyItemChanged(position, mDynamicInfo);
    }

    public void updateCommentLike(int type, int position) {
        DailyComment comment = mDataList.get(position);
        int thumbs_up = Integer.parseInt(comment.getThumbsUp());
        if (type == 1) {
            thumbs_up = thumbs_up + 1;
        } else {
            thumbs_up = thumbs_up - 1;
        }
        comment.setThumbsUp(String.valueOf(thumbs_up));
        comment.setIs_thumb(type);
        notifyItemChanged(position + 1, comment);
    }


    @Override
    public int getItemCount() {
        if (showLoadMore) {
            return mDynamicInfo != null ? mDataList.size() + 2 : 0;
        } else {
            return mDynamicInfo != null ? mDataList.size() + 1 : 0;
        }
    }

    private class DynamicNormalHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,
                mGameTitle, mBtnLike, mCommentNum, mShare;
        //  RecyclerView mRecyclerView;
        NineImageView mNineImageView;

        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        RelativeLayout mGameLayout;
        LinearLayout mGameLayoutBig;
        TextView mForwardContent;
        View mline;
        CustomEmptyView mEmptyView;
        ImageView mBtnMore;
        RelativeLayout mDynamicDetailBottom;
        LinearLayout mUserLayout, mUserHeaderLayout;
        ExpandableTextView mExpandableTextView;

        public DynamicNormalHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mGameTitle = $(R.id.game_title);
            mBtnLike = $(R.id.btn_like);
            mCommentNum = $(R.id.comment_num);
            mShare = $(R.id.share);
            //     mRecyclerView = $(R.id.recycler_view);
            mNineImageView = $(R.id.nine_image);

            mFlowLayout = $(R.id.tag_flow_layout);
            mGameIcon = $(R.id.game_icon);
            mUserLayout = $(R.id.user_layout);
            mGameLayout = $(R.id.game_layout);
            mGameLayoutBig = $(R.id.game_layout_big);
            mForwardContent = $(R.id.forward_content);
            mline = $(R.id.line);
            mEmptyView = $(R.id.empty_view);
            mBtnMore = $(R.id.btn_more);
            mDynamicDetailBottom = $(R.id.dynamic_detail_bottom);
            mUserHeaderLayout = $(R.id.user_header_layout);
            mExpandableTextView = $(R.id.expandable_view);
            //     mRecyclerView.setFocusable(false);
            mDynamicDetailBottom.setVisibility(View.VISIBLE);
            mUserLayout.setVisibility(View.GONE);
            mline.setVisibility(View.GONE);

            mEmptyView.initBigUI();
        }
    }

    private class DynamicForwardHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        TextView mUserAction, mDyTime,
                mGameTitle, mBtnLike, mCommentNum, mShare;
        // RecyclerView mChildRecycle;
        NineImageView mNineImageView;

        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        LinearLayout mUserLayout, mUserHeaderLayout;
        RelativeLayout mGameLayout;
        LinearLayout mGameLayoutBig;
        ExpandableTextView mForwardContent;
        View mline;
        CustomEmptyView mEmptyView;
        ImageView mBtnMore;
        UserNameView mUserNameView;
        RelativeLayout mDynamicDetailBottom;
        ExpandableTextView mExpandableTextView;

        public DynamicForwardHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mGameTitle = $(R.id.game_title);
            mBtnLike = $(R.id.btn_like);
            mCommentNum = $(R.id.comment_num);
            mShare = $(R.id.share);
            //     mChildRecycle = $(R.id.child_recycle);
            mNineImageView = $(R.id.nine_image);

            mFlowLayout = $(R.id.tag_flow_layout);
            mGameIcon = $(R.id.game_icon);
            mUserLayout = $(R.id.user_layout);
            mGameLayout = $(R.id.game_layout);
            mGameLayoutBig = $(R.id.game_layout_big);
            mForwardContent = $(R.id.forward_content);
            mline = $(R.id.line);
            mEmptyView = $(R.id.empty_view);
            mBtnMore = $(R.id.btn_more);
            mDynamicDetailBottom = $(R.id.dynamic_detail_bottom);
            mUserHeaderLayout = $(R.id.user_header_layout);
            mExpandableTextView = $(R.id.expandable_view);
            //  mChildRecycle.setFocusable(false);
            mDynamicDetailBottom.setVisibility(View.VISIBLE);
            mUserLayout.setVisibility(View.GONE);
            mline.setVisibility(View.GONE);

            mEmptyView.initBigUI();
        }
    }

    private class ArticleNormalHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,
                mArticleTitle, mBtnLike, mCommentNum, mShare;
        ImageView mArticleCover;
        View mline;
        ImageView mBtnMore;
        RelativeLayout mDynamicDetailBottom;
        LinearLayout mUserLayout, mUserHeaderLayout;
        CustomEmptyView mEmptyView;
        ExpandableTextView mExpandableTextView;

        public ArticleNormalHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mArticleCover = $(R.id.article_cover);
            mArticleTitle = $(R.id.article_title);
            mBtnLike = $(R.id.btn_like);
            mCommentNum = $(R.id.comment_num);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mEmptyView = $(R.id.empty_view);
            mDynamicDetailBottom = $(R.id.dynamic_detail_bottom);
            mUserLayout = $(R.id.user_layout);
            mUserHeaderLayout = $(R.id.user_header_layout);
            mExpandableTextView = $(R.id.expandable_view);
            mDynamicDetailBottom.setVisibility(View.VISIBLE);
            mUserLayout.setVisibility(View.GONE);
            mline.setVisibility(View.GONE);

            mEmptyView.initBigUI();
        }
    }

    private class ArticleForwardHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,
                mArticleForwardTitle, mBtnLike, mCommentNum, mShare;
        View mline;
        ImageView mBtnMore;
        ImageView mArticleCover;
        CustomEmptyView mEmptyView;
        RelativeLayout mDynamicDetailBottom;
        LinearLayout mUserLayout, mUserHeaderLayout;
        ExpandableTextView mExpandableTextView, mForwardTitle;

        public ArticleForwardHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mBtnLike = $(R.id.btn_like);
            mCommentNum = $(R.id.comment_num);
            mShare = $(R.id.share);
            mEmptyView = $(R.id.empty_view);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mForwardTitle = $(R.id.forward_title);
            mArticleCover = $(R.id.article_cover);
            mArticleForwardTitle = $(R.id.article_forward_title);
            mDynamicDetailBottom = $(R.id.dynamic_detail_bottom);
            mUserLayout = $(R.id.user_layout);
            mUserHeaderLayout = $(R.id.user_header_layout);
            mExpandableTextView = $(R.id.expandable_view);
            mDynamicDetailBottom.setVisibility(View.VISIBLE);
            mUserLayout.setVisibility(View.GONE);
            mline.setVisibility(View.GONE);

            mEmptyView.initBigUI();
        }
    }

    private class AppraiseNormalHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,
                mBtnLike, mCommentNum, mShare, mStarMark, mGameTitle;
        View mline;
        ImageView mBtnMore, mAppraiseCover;
        StarView mStarView;
        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        RelativeLayout mGameLayout;
        CustomEmptyView mEmptyView;
        RelativeLayout mDynamicDetailBottom;
        LinearLayout mUserLayout, mUserHeaderLayout;

        ExpandableTextView mExpandableTextView;

        public AppraiseNormalHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mBtnLike = $(R.id.btn_like);
            mCommentNum = $(R.id.comment_num);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mStarView = $(R.id.start_bar);
            mStarMark = $(R.id.star_mark);
            mAppraiseCover = $(R.id.appraise_cover);
            mGameIcon = $(R.id.game_icon);
            mGameTitle = $(R.id.game_title);
            mEmptyView = $(R.id.empty_view);
            mGameLayout = $(R.id.game_layout);
            mFlowLayout = $(R.id.tag_flow_layout);
            mDynamicDetailBottom = $(R.id.dynamic_detail_bottom);
            mUserLayout = $(R.id.user_layout);
            mUserHeaderLayout = $(R.id.user_header_layout);

            mExpandableTextView = $(R.id.expandable_view);
            mDynamicDetailBottom.setVisibility(View.VISIBLE);
            mUserLayout.setVisibility(View.GONE);
            mline.setVisibility(View.GONE);

            mEmptyView.initBigUI();

        }
    }

    private class AppraiseForwardHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,
                mBtnLike, mCommentNum, mShare, mGameTitle, mForwardUserName, mForwardStarMark;
        View mline;
        ImageView mBtnMore, mAppraiseCover, mIconPlay;
        StarView mForwardStartBar;
        CustomEmptyView mEmptyView;
        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        RelativeLayout mGameLayout;
        RelativeLayout mDynamicDetailBottom;
        LinearLayout mUserLayout, mUserHeaderLayout;
        ExpandableTextView mExpandableTextView, mForwardContent;

        public AppraiseForwardHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mBtnLike = $(R.id.btn_like);
            mCommentNum = $(R.id.comment_num);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mAppraiseCover = $(R.id.appraise_cover);
            mGameIcon = $(R.id.game_icon);
            mGameTitle = $(R.id.game_title);
            mGameLayout = $(R.id.game_layout);
            mFlowLayout = $(R.id.tag_flow_layout);
            mEmptyView = $(R.id.empty_view);
            mForwardUserName = $(R.id.forward_user_name);
            mForwardStarMark = $(R.id.forward_star_mark);
            mForwardStartBar = $(R.id.forward_start_bar);
            mDynamicDetailBottom = $(R.id.dynamic_detail_bottom);
            mUserLayout = $(R.id.user_layout);
            mUserHeaderLayout = $(R.id.user_header_layout);
            mIconPlay = $(R.id.icon_play);
            mForwardContent = $(R.id.forward_content);
            mExpandableTextView = $(R.id.expandable_view);
            mDynamicDetailBottom.setVisibility(View.VISIBLE);
            mUserLayout.setVisibility(View.GONE);
            mline.setVisibility(View.GONE);

            mEmptyView.initBigUI();
        }
    }

    private class DiscussViewHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        TextView mUserName, mAboutTime, mAboutDesc, mBtnShowAll, mBtnLike, mTagCount;
        RecyclerView mReplayRecycle;
        RelativeLayout mReCommLayout;
        ImageView mIconBadge;

        public DiscussViewHolder(View itemView) {
            super(itemView, mContext, false);
            mUserHeader = $(R.id.user_header);
            mUserName = $(R.id.user_name);
            mAboutTime = $(R.id.about_time);
            mAboutDesc = $(R.id.about_desc);
            mBtnShowAll = $(R.id.btn_see_more);
            mReplayRecycle = $(R.id.replay_recycle);
            mReplayRecycle.setLayoutManager(new LinearLayoutManager(mContext));
            mReCommLayout = $(R.id.re_comm_layout);
            mBtnLike = $(R.id.up);
            mTagCount = $(R.id.tag_count);
            mIconBadge = $(R.id.icon_badge);
            mTagCount.setVisibility(View.GONE);
        }
    }

    public interface OnLikeClickListener {
        void onClick(String dynamic_id, int isThumb, int position, String target);
    }

    public interface OnCommentLikeListener {
        void onClick(String tagsId, int isThumb, int position);
    }

    public interface OnCommentClickListener {
        void onClick(DailyComment comment, int position);
    }

    public interface OnReplayClickListener {
        void onClick(CommentReplies replies, String commentId);
    }

    public interface OnItemLongClickListener {
        void onLongClick(DailyComment comment, String dynamicId, int position);
    }
}
