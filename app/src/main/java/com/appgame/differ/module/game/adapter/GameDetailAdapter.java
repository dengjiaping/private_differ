package com.appgame.differ.module.game.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
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
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.GameRequire;
import com.appgame.differ.bean.game.RelatedGame;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.module.game.EvaluationDetailActivity;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.module.game.GameImagePageActivity;
import com.appgame.differ.module.game.PostEvaluationActivity;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.ExpandTextView;
import com.appgame.differ.widget.JCVideoView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.star.StarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding2.view.RxView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

import static com.appgame.differ.data.constants.AppConstants.page_size;


/**
 * Created by lzx on 2017/6/21.
 */

public class GameDetailAdapter extends LoadMoreAdapter<UserAppraise> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONDITION = 1;
    private static final int TYPE_INTRODUCTION = 2;
    private static final int TYPE_ABOUT = 3;
    private static final int TYPE_HOT_COM_TITLE = 4;
    private static final int TYPE_COMMENT = 5;
    private static final int TYPE_USER_COMMENT = 6;
    private static final int TYPE_ALL_COM_TITLE = 7;

    private LinearLayout.LayoutParams heightParams, widthParams;
    private OnLikeClickListener mOnLikeClickListener;

    private GameInfo mGameInfo;
    private UserAppraise mUserAppraise;
    private int hotAppraiseCount = 0;
    private boolean hasHotAppraise = false;

    public GameDetailAdapter(Context context) {
        super(context);
        heightParams = new LinearLayout.LayoutParams(CommonUtil.dp2px(mContext, 85), CommonUtil.dp2px(mContext, 150));
        heightParams.setMargins(0, 0, CommonUtil.dp2px(mContext, 10), 0);

        widthParams = new LinearLayout.LayoutParams(CommonUtil.dp2px(mContext, 150), CommonUtil.dp2px(mContext, 85));
        widthParams.setMargins(0, 0, CommonUtil.dp2px(mContext, 10), 0);
    }

    public void setGameInfo(GameInfo gameInfo) {
        mGameInfo = gameInfo;
    }

    public void setUserAppraise(UserAppraise userAppraise) {
        mUserAppraise = userAppraise;
    }

    public void setHotAppraiseCount(int hotAppraiseCount) {
        this.hotAppraiseCount = hotAppraiseCount;
    }

    public void setHasHotAppraise(boolean hasHotAppraise) {
        this.hasHotAppraise = hasHotAppraise;
    }

    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        mOnLikeClickListener = onLikeClickListener;
    }

    private boolean isNull() {
        return mGameInfo == null;
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_game_details, parent, false);
            return new GameHolder(view);
        } else if (viewType == TYPE_CONDITION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_details_condition, parent, false);
            return new ConditionHolder(view);
        } else if (viewType == TYPE_INTRODUCTION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_details_introduction, parent, false);
            return new IntroductionHolder(view);
        } else if (viewType == TYPE_ABOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_details_about, parent, false);
            return new AboutHolder(view);
        } else if (viewType == TYPE_HOT_COM_TITLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_details_comtitle, parent, false);
            return new UserComTitleHolder(view);
        } else if (viewType == TYPE_COMMENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_games_detail_evaluaion, parent, false);
            return new EvaluationHolder(view);
        } else if (viewType == TYPE_USER_COMMENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_games_detail_user_evaluaion, parent, false);
            return new UserEvaluationHolder(view);
        } else if (viewType == TYPE_ALL_COM_TITLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_details_eva_title, parent, false);
            return new ComTitleHolder(view);
        }
        return null;
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        if (viewHolder instanceof GameHolder) {
            GameHolder holder = (GameHolder) viewHolder;
            if (mGameInfo != null) {
                String videoUrl = mGameInfo.getVideo();
                String coverUrl = mGameInfo.getCover();
                //初始化视频
                if (!TextUtils.isEmpty(videoUrl)) {
                    holder.mGameCover.setImageResource(R.color.white);
                    holder.mJCVideoView.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(coverUrl).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mJCVideoView.thumbImageView);
                    holder.mJCVideoView.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
                    //wifi自动播放
                    if (CommonUtil.isWifi(mContext)) {
                        holder.mJCVideoView.startVideo();
                    }
                } else if (!TextUtils.isEmpty(coverUrl)) {
                    holder.mJCVideoView.setVisibility(View.GONE);
                    Glide.with(mContext).load(coverUrl).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
                }
                //初始化游戏信息
                holder.mGameName.setText(mGameInfo.getGameNameCn());
                holder.mGameSize.setText(mGameInfo.getDownloadInfo() == null ? "0MB" : mGameInfo.getDownloadInfo().downloadGameSize + "MB");
                @SuppressLint("DefaultLocale") String start = String.format("%.1f", Float.parseFloat(mGameInfo.getGameStar()));
                holder.mGameScore.setText(start);
                Glide.with(mContext).load(mGameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
                //标签
                if (mGameInfo.getTags() != null) {
                    holder.mTagFlowLayout.setVisibility(mGameInfo.getTags().size() == 0 ? View.GONE : View.VISIBLE);
                    holder.mTagFlowLayout.setAdapter(new TagAdapter<TagsInfo>(CommonUtil.getTagsInfoWithNum(mGameInfo.getTags(), 6)) {
                        @Override
                        public View getView(com.zhy.view.flowlayout.FlowLayout parent, int position, TagsInfo tagsInfo) {
                            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_game_detail_tab, holder.mTagFlowLayout, false);
                            textView.setText(tagsInfo.getName());
                            return textView;
                        }
                    });
                } else {
                    holder.mTagFlowLayout.setVisibility(View.GONE);
                }
                //图片
                holder.mImageContainer.removeAllViews();
                for (int i = 0; i < mGameInfo.getPic().size(); i++) {
                    final ImageView imageView = new ImageView(mContext);
                    final int finalI = i;
                    Glide.with(mContext).load(mGameInfo.getPic().get(i)).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            if (resource.getIntrinsicWidth() < resource.getIntrinsicHeight()) {
                                imageView.setLayoutParams(heightParams);
                            } else {
                                imageView.setLayoutParams(widthParams);
                            }
                            return false;
                        }
                    }).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

                    imageView.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, GameImagePageActivity.class);
                        intent.putStringArrayListExtra("imgUrl", (ArrayList<String>) mGameInfo.getPic());
                        intent.putExtra("position", finalI);
                        intent.putExtra("name", mGameInfo.getGameNameCn());
                        mContext.startActivity(intent);
                    });
                    holder.mImageContainer.addView(imageView);
                }
            }
        } else if (viewHolder instanceof ConditionHolder) {
            ConditionHolder holder = (ConditionHolder) viewHolder;
            if (mGameInfo != null) {
                List<GameRequire> requires = mGameInfo.getGameRequires();
                holder.itemView.setVisibility(requires.size() > 0 ? View.VISIBLE : View.GONE);
                holder.mTagFlowLayout.setAdapter(new TagAdapter<GameRequire>(requires) {

                    @Override
                    public View getView(FlowLayout parent, int position, GameRequire require) {
                        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_game_detail_condition, holder.mTagFlowLayout, false);
                        ImageView mConIcon = (ImageView) view.findViewById(R.id.con_icon);
                        TextView mConTitle = (TextView) view.findViewById(R.id.con_title);
                        mConTitle.setText(require.getTitle());
                        mConIcon.setImageResource(require.getResId());
                        return view;
                    }
                });
                holder.mTagFlowLayout.setOnTagClickListener((view, position1, parent) -> false);
            } else {
                holder.itemView.setVisibility(View.GONE);
            }
        } else if (viewHolder instanceof IntroductionHolder) {
            IntroductionHolder holder = (IntroductionHolder) viewHolder;
            //游戏介绍
            if (mGameInfo != null) {
                holder.mExpandTextView.setText(mGameInfo.getIntro(), position);
            }
        } else if (viewHolder instanceof AboutHolder) {
            AboutHolder holder = (AboutHolder) viewHolder;
            if (mGameInfo != null) {
                holder.itemView.setVisibility(mGameInfo.getRelayGames().size() == 0 ? View.GONE : View.VISIBLE);
                List<RelatedGame> relay_games = mGameInfo.getRelayGames();
                RelayGamesAdapter relayGamesAdapter = new RelayGamesAdapter(relay_games);
                holder.mAboutRecycler.setAdapter(relayGamesAdapter);
            }
        } else if (viewHolder instanceof UserEvaluationHolder) {
            UserEvaluationHolder holder = (UserEvaluationHolder) viewHolder;
            if (mGameInfo != null) {
                if (mUserAppraise != null) {
                    holder.mBtnComment.setVisibility(View.GONE);
                    holder.mUserCommentLayout.setVisibility(View.VISIBLE);
                    holder.mBtnAmendComment.setVisibility(View.VISIBLE);
                    initEvaluationUI(holder.itemView, mUserAppraise, holder.mUserHeader, holder.mStarView, holder.mFlowLayout,
                            holder.mUserName, holder.mContent, holder.mStarMark, holder.mLikeCount, holder.mCommentCount);
                    holder.mBtnAmendComment.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, PostEvaluationActivity.class);
                        intent.putExtra("content", mUserAppraise.getContent());
                        intent.putExtra("star", mUserAppraise.getStar());
                        intent.putExtra("id", mUserAppraise.getId());
                        intent.putExtra("game_id", mGameInfo.getGameId());
                        mContext.startActivity(intent);
                    });
                    int finalPosition = position;
                    RxView.clicks(holder.mLikeCount).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                        if (mOnLikeClickListener != null) {
                            mOnLikeClickListener.onClick(mUserAppraise.getId(), mUserAppraise.getIsThumb(), finalPosition, true);
                        }
                    });
                } else {
                    holder.mBtnComment.setVisibility(View.VISIBLE);
                    holder.mUserCommentLayout.setVisibility(View.GONE);
                    holder.mBtnComment.setOnClickListener(v -> {
                        if (mGameInfo != null) {
                            if (CommonUtil.isLogin()) {
                                Intent intent = new Intent(mContext, PostEvaluationActivity.class);
                                intent.putExtra("game_id", mGameInfo.getGameId());
                                mContext.startActivity(intent);
                            } else {
                                LoginActivity.launch(mContext, "GameDetailActivity");
                            }
                        }
                    });
                }
            }
        } else if (viewHolder instanceof EvaluationHolder) {
            EvaluationHolder holder = (EvaluationHolder) viewHolder;
            if (mGameInfo != null) {
                List<GameRequire> requires = mGameInfo.getGameRequires();
                int totalCount = 6;
                if (requires.size() <= 0) {
                    totalCount--;
                }
                position = position - totalCount;
                if (hasHotAppraise) {
                    if (position > hotAppraiseCount) {
                        position = position - 1;
                    }
                }
                UserAppraise appraise = mDataList.get(position);
                initEvaluationUI(holder.itemView, appraise, holder.mUserHeader, holder.mStarView, holder.mFlowLayout,
                        holder.mUserName, holder.mContent, holder.mStarMark, holder.mLikeCount, holder.mCommentCount);

                int finalPosition = position;
                RxView.clicks(holder.mLikeCount).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
                    if (mOnLikeClickListener != null) {
                        mOnLikeClickListener.onClick(appraise.getId(), appraise.getIsThumb(), finalPosition, false);
                    }
                });
            }
        } else if (viewHolder instanceof ComTitleHolder) {
            ComTitleHolder holder = (ComTitleHolder) viewHolder;
            if (mGameInfo != null) {
                int currPos = 5;
                List<GameRequire> requires = mGameInfo.getGameRequires();
                if (requires.size() <= 0) {
                    currPos--;
                }
                if (hasHotAppraise) {
                    if (position == currPos) {
                        holder.mComTitle.setText("精彩评价");
                    } else if (position == currPos + hotAppraiseCount + 1) {
                        String comCount = mDataList.size() > 0 ? mDataList.get(0).count : "0";
                        holder.mComTitle.setText("全部评价( " + comCount + " )");
                    }
                    holder.mCustomEmptyView.setVisibility(hotAppraiseCount == 0 ? View.VISIBLE : View.GONE);
                } else {
                    String comCount = mDataList.size() > 0 ? mDataList.get(0).count : "0";
                    holder.mComTitle.setText("全部评价( " + comCount + " )");
                    holder.mCustomEmptyView.setVisibility(mDataList.size() == 0 ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    private void initEvaluationUI(View itemView, UserAppraise appraise, CircleImageView mUserHeader, StarView mStarView, TagFlowLayout mFlowLayout, TextView... mTextView) {
        TextView mUserName, mContent, mStarMark, mLikeCount, mCommentCount;//, mBtnAmendComment;
        mUserName = mTextView[0];
        mContent = mTextView[1];
        mStarMark = mTextView[2];
        mLikeCount = mTextView[3];
        mCommentCount = mTextView[4];
        Glide.with(mContext).load(appraise.getAuthor().getAvatar()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserHeader);
        mUserName.setText(CommonUtil.getNickName(appraise.getAuthor().getNickName()));
        mStarView.setIndicate(true);
        mStarView.setStarMark(Float.parseFloat(appraise.getStar()));
        mContent.setText(appraise.getContent());
        mStarMark.setText(String.format("%.1f", Float.parseFloat(appraise.getStar())));
        mLikeCount.setText(appraise.getThumbsUp());
        mLikeCount.setCompoundDrawablesWithIntrinsicBounds(appraise.getIsThumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0);
        mCommentCount.setText(appraise.getCommented());
        mFlowLayout.setAdapter(new TagAdapter<TagsInfo>(CommonUtil.getTagsInfoWithNum(appraise.getTags(), 8)) {
            @Override
            public View getView(com.zhy.view.flowlayout.FlowLayout parent, final int position, final TagsInfo tagsInfo) {
                TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tag_flow_layout, parent, false);
                String text = Integer.parseInt(tagsInfo.getThumbsUp()) < 99 ? tagsInfo.getName() + "｜" + tagsInfo.getThumbsUp() : tagsInfo.getName() + "｜" + "99+";
                textView.setText(text);
                if (tagsInfo.getIsThumb() == 1) {
                    textView.setBackgroundResource(R.drawable.bg_tag_normal_selected);
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
                } else {
                    textView.setBackgroundResource(R.drawable.bg_tag_normal);
                }
                return textView;
            }
        });
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EvaluationDetailActivity.class);
            intent.putExtra("game_info", mGameInfo);
            intent.putExtra("commendId", appraise.getId());
            intent.putExtra("userId", appraise.getAuthor().getUserId());
            mContext.startActivity(intent);
        });
        mUserName.setOnClickListener(v -> IntentHelper.startPersionActivity((GameDetailsActivity) mContext, mUserHeader, appraise.getAuthor().getUserId()));
        mUserHeader.setOnClickListener(v -> IntentHelper.startPersionActivity((GameDetailsActivity) mContext, mUserHeader, appraise.getAuthor().getUserId()));
    }

    @Override
    protected int getViewType(int position) {
        return getType(position);
    }

    private int getType(int position) {
        if (!isNull()) {
            List<GameRequire> requires = mGameInfo.getGameRequires();
            boolean hasCondition = requires.size() > 0; //是否有game条件
            if (position == 0) {
                return TYPE_HEADER; //头部
            } else {
                position = position - 1;
                if (hasCondition) {
                    if (position == 0) {
                        return TYPE_CONDITION;
                    } else if (position == 1) {
                        return TYPE_INTRODUCTION;
                    } else if (position == 2) {
                        return TYPE_HOT_COM_TITLE;
                    } else if (position == 3) {
                        return TYPE_USER_COMMENT;
                    } else {
                        position = position - 4;
                        if (hasHotAppraise) {
                            if (position == 0 || position == hotAppraiseCount + 1) {
                                return TYPE_ALL_COM_TITLE;  //热门评价title
                            } else {
                                return TYPE_COMMENT;  //评价
                            }
                        } else {
                            return position == 0 ? TYPE_ALL_COM_TITLE : TYPE_COMMENT;
                        }
                    }
                } else {
                    if (position == 0) {
                        return TYPE_INTRODUCTION;
                    } else if (position == 1) {
                        return TYPE_HOT_COM_TITLE;
                    } else if (position == 2) {
                        return TYPE_USER_COMMENT;
                    } else {
                        position = position - 3;
                        if (hasHotAppraise) {
                            if (position == 0 || position == hotAppraiseCount + 1) {
                                return TYPE_ALL_COM_TITLE;  //热门评价title
                            } else {
                                return TYPE_COMMENT;  //评价
                            }
                        } else {
                            return position == 0 ? TYPE_ALL_COM_TITLE : TYPE_COMMENT;
                        }
                    }
                }
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    private int getCount() {
        //正常加7，showLoadMore加8
        if (!isNull()) {
            int totalCount = showLoadMore ? mDataList.size() + 8 : mDataList.size() + 7;
            List<GameRequire> requires = mGameInfo.getGameRequires();
            if (requires.size() <= 0) {
                totalCount--;
            }
            if (!hasHotAppraise) {
                totalCount--;
            }
            return totalCount;
        } else {
            return 0;
        }
    }

    public void updateLikeUI(int position, int thumb, boolean isUserComment) {
        if (!isNull()) {
            List<GameRequire> requires = mGameInfo.getGameRequires();
            boolean hasCondition = requires.size() > 0; //是否有game条件
            if (hasCondition) {
                updateLikeUIImpl(4, position, thumb, isUserComment);
            } else {
                updateLikeUIImpl(3, position, thumb, isUserComment);
            }
        }
    }

    private void updateLikeUIImpl(int UserPosition, int position, int thumb, boolean isUserComment) {
        if (isUserComment) {
            if (mUserAppraise != null) {
                mUserAppraise.setIsThumb(thumb);
                mUserAppraise.setThumbsUp(CommonUtil.getThumbNum(thumb, mUserAppraise.getThumbsUp()));
            }
        } else {
            UserAppraise appraise = mDataList.get(position);
            appraise.setIsThumb(thumb);
            appraise.setThumbsUp(CommonUtil.getThumbNum(thumb, appraise.getThumbsUp()));
        }

        notifyDataSetChanged();
        setShowLoadMore(mDataList.size() >= page_size);
    }

    public interface OnLikeClickListener {
        void onClick(String commend_id, int isThumb, int position, boolean isUserComment);
    }

    private class GameHolder extends BaseViewHolder {
        ImageView mGameCover;
        JCVideoView mJCVideoView;
        ShapeImageView mGameIcon;
        TextView mGameName, mGameSize, mGameScore, mAboutTitle;
        TagFlowLayout mTagFlowLayout;
        LinearLayout mImageContainer;
        View mAboutLine;

        public GameHolder(View itemView) {
            super(itemView, mContext, false);
            mGameCover = $(R.id.video_preview);
            mJCVideoView = $(R.id.video_container);
            mGameIcon = $(R.id.game_icon);
            mGameName = $(R.id.game_title);
            mGameSize = $(R.id.game_size);
            mGameScore = $(R.id.game_score);
            mTagFlowLayout = $(R.id.tag_flow_layout);
            mImageContainer = $(R.id.container);
            mAboutTitle = $(R.id.about_title);
            mAboutLine = $(R.id.about_line);
        }
    }

    private class ConditionHolder extends BaseViewHolder {
        TagFlowLayout mTagFlowLayout;

        public ConditionHolder(View itemView) {
            super(itemView, mContext, false);
            mTagFlowLayout = $(R.id.tag_flow_condition);
        }
    }

    private class IntroductionHolder extends BaseViewHolder {
        ExpandTextView mExpandTextView;

        public IntroductionHolder(View itemView) {
            super(itemView, mContext, false);
            mExpandTextView = $(R.id.expandable_view);
        }
    }

    private class AboutHolder extends BaseViewHolder {
        RecyclerView mAboutRecycler;

        public AboutHolder(View itemView) {
            super(itemView, mContext, false);
            mAboutRecycler = $(R.id.recycler_about);
            mAboutRecycler.setLayoutManager(new GridLayoutManager(mContext, 2));
            mAboutRecycler.setFocusable(false);
        }
    }

    private class UserComTitleHolder extends BaseViewHolder {

        public UserComTitleHolder(View itemView) {
            super(itemView, mContext, false);
        }
    }

    private class ComTitleHolder extends BaseViewHolder {
        TextView mComTitle;
        CustomEmptyView mCustomEmptyView;

        public ComTitleHolder(View itemView) {
            super(itemView, mContext, false);
            mComTitle = $(R.id.com_title);
            mCustomEmptyView = $(R.id.comment_empty_view);
            mCustomEmptyView.initBigUI();
        }
    }

    private class EvaluationHolder extends BaseViewHolder {

        TextView mUserName, mContent, mStarMark, mLikeCount, mCommentCount, mBtnAmendComment;
        CircleImageView mUserHeader;
        StarView mStarView;
        TagFlowLayout mFlowLayout;

        EvaluationHolder(View view) {
            super(view, mContext, false);
            mUserName = $(R.id.user_name);
            mContent = $(R.id.content);
            mStarMark = $(R.id.star_mark);
            mLikeCount = $(R.id.tag_count);
            mCommentCount = $(R.id.comment_count);
            mUserHeader = $(R.id.avatar);
            mStarView = $(R.id.start_bar);
            mFlowLayout = $(R.id.tag_flow_layout);
            mBtnAmendComment = $(R.id.btn_amend_comment);
        }
    }

    private class UserEvaluationHolder extends EvaluationHolder {

        TextView mBtnComment;
        RelativeLayout mUserCommentLayout;

        UserEvaluationHolder(View view) {
            super(view);
            mBtnComment = $(R.id.btn_comment);
            mUserCommentLayout = $(R.id.user_comment_layout);
        }
    }

    public class RelayGamesAdapter extends RecyclerView.Adapter<RelayGamesAdapter.RelayGamesHolder> {

        private List<RelatedGame> mRelatedGames;

        public RelayGamesAdapter(List<RelatedGame> relatedGames) {
            mRelatedGames = relatedGames;
        }

        @Override
        public RelayGamesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_relate_game, parent, false);
            return new RelayGamesHolder(view);
        }

        @Override
        public void onBindViewHolder(RelayGamesHolder holder, int position) {
            RelatedGame relatedGame = mRelatedGames.get(position);
            Glide.with(mContext).load(relatedGame.getAttributes().getCover()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
            holder.mGameName.setText(relatedGame.getAttributes().getGame_name_cn());
            holder.mGameType.setText(relatedGame.getAttributes().getCategory().get(0).getName());
            holder.mStarMark.setText(String.format("%.1f", Float.parseFloat(relatedGame.getAttributes().getAvg_appraise_star())));
            holder.mStarView.setIndicate(true);
            holder.mStarView.setStarMark(Float.parseFloat(relatedGame.getAttributes().getAvg_appraise_star()));
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, GameDetailsActivity.class);
                intent.putExtra("game_id", relatedGame.getId());
                mContext.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return mRelatedGames.size();
        }

        class RelayGamesHolder extends BaseViewHolder {
            ImageView mGameCover;
            TextView mGameName, mGameType, mStarMark;
            StarView mStarView;

            public RelayGamesHolder(View itemView) {
                super(itemView, mContext, false);
                mGameCover = $(R.id.cover);
                mGameName = $(R.id.title);
                mGameType = $(R.id.game_type);
                mStarMark = $(R.id.star_mark);
                mStarView = $(R.id.start_bar);
            }
        }
    }

}
