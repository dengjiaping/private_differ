package com.appgame.differ.module.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.bean.game.SimpleGame;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.module.ErrorActivity;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.daily.DailyDetailActivity;
import com.appgame.differ.module.dynamic.DynamicDetailActivity;
import com.appgame.differ.module.dynamic.PostDynamicActivity;
import com.appgame.differ.module.find.VideoPlayActivity;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.module.game.GameImagePageActivity;
import com.appgame.differ.module.login.view.LoginActivity;
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
import com.appgame.differ.widget.dialog.ReportDialog;
import com.appgame.differ.widget.popupwindow.DynamicMorePopWindow;
import com.appgame.differ.widget.star.StarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

/**
 * Created by lzx on 17/5/3.
 */

public class DynamicAdapter extends LoadMoreAdapter<DynamicInfo> {
    private OnLikeClickListener mOnLikeClickListener;
    private OnMoreBtnClickListener mOnMoreBtnClickListener;
    private static final int TYPE_DYNAMIC_NORMAL = 0;
    private static final int TYPE_DYNAMIC_FORWARD = 1;
    private static final int TYPE_ARTICLE_NORMAL = 2;
    private static final int TYPE_ARTICLE_FORWARD = 3;
    private static final int TYPE_APPRAISE_NORMAL = 4;
    private static final int TYPE_APPRAISE_FORWARD = 5;
    private static final int TYPE_DELETE = 6;
    private static final int TYPE_LOGIN = 7;

    private boolean isNeedShowLogin = true;


    public DynamicAdapter(Context context, boolean isNeedShowLogin) {
        super(context);
        this.isNeedShowLogin = isNeedShowLogin;
    }

    public void clear() {
        this.mDataList.clear();
        notifyDataSetChanged();
    }

    public void setOnMoreBtnClickListener(OnMoreBtnClickListener onMoreBtnClickListener) {
        this.mOnMoreBtnClickListener = onMoreBtnClickListener;
    }

    public void setOnLikeClickListener(OnLikeClickListener likeClickListener) {
        mOnLikeClickListener = likeClickListener;
    }

    @Override
    protected int getViewType(int position) {
        if (!CommonUtil.isLogin()) {
            if (isNeedShowLogin) {
                if (position == getItemCount() - 1) {
                    return TYPE_LOGIN;
                } else {
                    return getAdapterViewType(position);
                }
            } else {
                return getAdapterViewType(position);
            }
        } else {
            return getAdapterViewType(position);
        }
    }

    private int getAdapterViewType(int position) {
        DynamicInfo info = mDataList.get(position);
        String target = info.getTarget();
        String isForward = info.getIsForward();
        switch (target) {
            case "dynamic":
                if (info.getTargetId().equals("0")) {
                    if (isForward.equals("1")) { //转发
                        if (info.getForward().getIsChecked().equals("0")) {
                            return TYPE_DELETE; //已删除
                        } else {
                            return TYPE_DYNAMIC_FORWARD; //转发
                        }
                    } else {
                        if (info.getIsChecked().equals("0")) {
                            return TYPE_DELETE; //已删除
                        } else {
                            return TYPE_DYNAMIC_NORMAL; //转发
                        }
                    }
                } else {
                    if (isForward.equals("1")) { //转发
                        if (info.getForward().getIsChecked().equals("0")) {
                            return TYPE_DELETE; //已删除
                        } else {
                            return TYPE_APPRAISE_FORWARD; //转发
                        }
                    } else {
                        if (info.getIsChecked().equals("0")) {
                            return TYPE_DELETE; //已删除
                        } else {
                            return TYPE_APPRAISE_NORMAL; //转发
                        }
                    }
                }
            case "appraise":
                if (isForward.equals("1")) { //转发
                    if (info.getForward().getIsChecked().equals("0")) {
                        return TYPE_DELETE; //已删除
                    } else {
                        return TYPE_APPRAISE_FORWARD; //转发
                    }
                } else {
                    if (info.getIsChecked().equals("0")) {
                        return TYPE_DELETE; //已删除
                    } else {
                        return TYPE_APPRAISE_NORMAL; //转发
                    }
                }
            case "article":
                if (isForward.equals("1")) { //转发
                    if (info.getForward().getIsChecked().equals("0")) {
                        return TYPE_DELETE; //已删除
                    } else {
                        return TYPE_ARTICLE_FORWARD; //转发
                    }
                } else {
                    if (info.getIsChecked().equals("0")) {
                        return TYPE_DELETE; //已删除
                    } else {
                        return TYPE_ARTICLE_NORMAL; //转发
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
        } else if (viewType == TYPE_DELETE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_delete, parent, false);
            return new DeleteHolder(view);
        } else if (viewType == TYPE_LOGIN) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_login, parent, false);
            return new LoginHolder(view);
        }
        return null;
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        if (viewHolder instanceof DynamicNormalHolder) {
            //动态正常
            DynamicInfo dynamicInfo = mDataList.get(position);
            DynamicNormalHolder holder = (DynamicNormalHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initDynamicNormal(holder, dynamicInfo);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
            initGameInfo(dynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
        } else if (viewHolder instanceof DynamicForwardHolder) {
            //动态转发
            DynamicInfo dynamicInfo = mDataList.get(position);
            DynamicForwardHolder holder = (DynamicForwardHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initDynamicForward(holder, dynamicInfo);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
            initGameInfo(dynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
        } else if (viewHolder instanceof ArticleNormalHolder) {
            //文章正常
            DynamicInfo dynamicInfo = mDataList.get(position);
            ArticleNormalHolder holder = (ArticleNormalHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initArticleNormal(holder, dynamicInfo);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
        } else if (viewHolder instanceof ArticleForwardHolder) {
            //文章转发
            DynamicInfo dynamicInfo = mDataList.get(position);
            ArticleForwardHolder holder = (ArticleForwardHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initArticleForward(holder, dynamicInfo);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
        } else if (viewHolder instanceof AppraiseNormalHolder) {
            //评论正常
            DynamicInfo dynamicInfo = mDataList.get(position);
            AppraiseNormalHolder holder = (AppraiseNormalHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initAppraiseNormal(holder, dynamicInfo);
            initGameInfo(dynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
        } else if (viewHolder instanceof AppraiseForwardHolder) {
            //评论转发
            DynamicInfo dynamicInfo = mDataList.get(position);
            AppraiseForwardHolder holder = (AppraiseForwardHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initAppraiseForward(holder, dynamicInfo);
            initGameInfo(dynamicInfo, holder.mGameIcon, holder.mGameTitle, holder.mFlowLayout, holder.mGameLayout);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
        } else if (viewHolder instanceof DeleteHolder) {
            //动态删除
            DynamicInfo dynamicInfo = mDataList.get(position);
            DeleteHolder holder = (DeleteHolder) viewHolder;
            initPublicUI(holder.itemView, holder.mUserHeader, holder.mDyTime, holder.mUserNameView, holder.mExpandableTextView, holder.mUserAction, dynamicInfo, position);
            initDelete(holder, dynamicInfo);
            initUserLayout(dynamicInfo, position, holder.mBtnLike, holder.mCommentNum, holder.mShare, holder.mBtnMore);
        } else if (viewHolder instanceof LoginHolder) {
            //登陆
            LoginHolder holder = (LoginHolder) viewHolder;

            holder.itemView.setVisibility(!CommonUtil.isConnected(mContext) ? View.GONE : View.VISIBLE);

            holder.mLogin.setOnClickListener(v -> LoginActivity.launch(mContext, "DynamicActivity"));
        }
    }

    /**
     * 公共UI
     */
    private void initPublicUI(View itemView, CircleImageView mUserHeader, TextView mDyTime, UserNameView mUserNameView, ExpandableTextView mDyContent, TextView mUserAction, DynamicInfo dynamicInfo, int position) {
        Glide.with(mContext).load(dynamicInfo.getAuthor().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserHeader);
        mDyTime.setText(CommonUtil.getStandardDate(dynamicInfo.getUpdateAt()));
        mUserNameView.setUserName(dynamicInfo.getAuthor().getNickName()).setRankInfo(dynamicInfo.getAuthor().getRank());
        mDyContent.setText(CommonUtil.getDynamicContent(dynamicInfo.getContent()));
        String target = dynamicInfo.getTarget();

        switch (target) {
            case "appraise":
                mUserAction.setText(dynamicInfo.getIsForward().equals("0") ? "评价游戏" : "转发");
                break;
            case "article":
                mUserAction.setText(dynamicInfo.getIsForward().equals("0") ? "分享文章" : "转发");
                break;
            default:
                mUserAction.setText(dynamicInfo.getIsForward().equals("0") ? "分享游戏" : "转发");
                break;
        }
        mUserHeader.setOnClickListener(v -> {
            if (mContext instanceof MainActivity) {
                IntentHelper.startPersionActivity((MainActivity) mContext, mUserHeader, dynamicInfo.getAuthor().getUserId());
            } else if (mContext instanceof DynamicDetailActivity) {
                IntentHelper.startPersionActivity((DynamicDetailActivity) mContext, mUserHeader, dynamicInfo.getAuthor().getUserId());
            }
        });
        mDyContent.setOnContentTextClickListener(() -> {
            if (getAdapterViewType(position) != TYPE_DELETE) {
                gotoDetailActivity(target, dynamicInfo);
            }
        });
        itemView.setOnClickListener(v -> gotoDetailActivity(target, dynamicInfo));
    }

    private void gotoDetailActivity(String target, DynamicInfo dynamicInfo) {

        Intent intent = new Intent(mContext, DynamicDetailActivity.class);
        intent.putExtra("dynamic_id", dynamicInfo.getId());
        intent.putExtra("userId", dynamicInfo.getAuthor().getUserId());
        mContext.startActivity(intent);
    }


    /**
     * 点赞，评论，分享等
     */
    private void initUserLayout(DynamicInfo dynamicInfo, int position, TextView mBtnLike, TextView mCommentNum, TextView mShare, ImageView mBtnMore) {
        //喜欢
        mBtnLike.setText(dynamicInfo.getThumbsUp());
        mBtnLike.setCompoundDrawablesWithIntrinsicBounds(dynamicInfo.getIsThumb() == 0 ? R.drawable.ic_like_big_def : R.drawable.ic_like_big_pre, 0, 0, 0);
        mBtnLike.setOnClickListener(v -> {
            if (mOnLikeClickListener != null)
                mOnLikeClickListener.onClick(dynamicInfo.getTarget().equals("appraise") ? dynamicInfo.getTargetId() : dynamicInfo.getId(), dynamicInfo.getIsThumb(), position, dynamicInfo.getTarget());
        });
        mCommentNum.setText(dynamicInfo.getCommented());
        mShare.setText(dynamicInfo.getShared());
        mShare.setOnClickListener(v -> {
            Intent intent;
            if (CommonUtil.isLogin()) {
                RecommedInfo game = dynamicInfo.getGameInfo();
                SimpleGame simpleGame = new SimpleGame();

                simpleGame.setGameId(game != null ? game.getGameId() : dynamicInfo.getArticle().getId());
                simpleGame.setGameNameCn(game != null ? game.getGameNameCn() : "");
                simpleGame.setGameIcon(game != null ? game.getIcon() : "");
                simpleGame.setTagsInfos(game != null ? game.getTags() : new ArrayList<>());

                String shareContent = dynamicInfo.getIsForward().equals("0") ? "" :
                        CommonUtil.getShareContent(dynamicInfo.getAuthor().getNickName(), dynamicInfo.getContent());
                intent = new Intent(mContext, PostDynamicActivity.class);
                intent.putExtra("dynamic_id", dynamicInfo.getId());
                intent.putExtra("postType", "share");
                intent.putExtra("shareContent", shareContent);
                intent.putExtra("game", simpleGame);
                String target, targetId;
                switch (dynamicInfo.getTarget()) {
                    case "article":
                        target = "article";
                        targetId = dynamicInfo.getTargetId();
                        break;
                    case "appraise":
                        target = "dynamic";// "appraise";

                        targetId = dynamicInfo.getTargetId();
                        break;
                    default:
                        target = "dynamic";
                        targetId = "0";
                        break;
                }
                intent.putExtra("target", target);
                intent.putExtra("targetId", targetId);
            } else {
                intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra("login_action", "DynamicActivity");
            }
            mContext.startActivity(intent);
        });
        mBtnMore.setOnClickListener(v -> {
            DynamicMorePopWindow popWindow = new DynamicMorePopWindow(mContext, dynamicInfo.getAuthor().getUserId());
            int[] location = new int[2];
            mBtnMore.getLocationOnScreen(location);
            popWindow.showAtLocation(mBtnMore, Gravity.NO_GRAVITY,
                    location[0] - popWindow.getWidth(), location[1] + mBtnMore.getHeight() / 2 - popWindow.getHeight() / 2);
            popWindow.setOnClickListener(type -> {
                if (CommonUtil.isLogin()) {
                    if (type.equals("report")) {
                        ReportDialog reportListFragment = ReportDialog.newInstance(dynamicInfo.getId(), "dynamic");
                        reportListFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "reportFragment");
                    } else {
                        if (mOnMoreBtnClickListener != null)
                            mOnMoreBtnClickListener.onMoreBtnClick(dynamicInfo.getId(), position);
                    }
                } else {
                    LoginActivity.launch(mContext, "DynamicActivity");
                }
                popWindow.dismiss();
            });
        });
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
            String status = dynamicInfo.getGameInfo().getStatus();
            Intent intent;
            if (status.equals("0")) {
                intent = new Intent(mContext, ErrorActivity.class);
                intent.putExtra("barTitle", "游戏详情");
            } else {
                intent = new Intent(mContext, GameDetailsActivity.class);
                intent.putExtra("game_id", dynamicInfo.getGameInfo().getGameId());
            }
            mContext.startActivity(intent);
        });
    }

    /**
     * 动态正常
     */
    private void initDynamicNormal(DynamicNormalHolder holder, DynamicInfo dynamicInfo) {

        holder.mNineImageView.setImageUrls(dynamicInfo.getImages());
        holder.mNineImageView.setOnClickItemListener((position, url) -> {
            Intent intent = GameImagePageActivity.luanch(mContext, "", position, dynamicInfo.getImages());
            mContext.startActivity(intent);
        });
    }

    /**
     * 动态转发
     */
    private void initDynamicForward(DynamicForwardHolder holder, DynamicInfo dynamicInfo) {
        String forwardCon = dynamicInfo.getForward().getContent();
        Spanned forwardContent = Html.fromHtml(CommonUtil.getUserNameWithColor(dynamicInfo.getForward().getAuthor().getNickName()) + forwardCon);
        holder.mExpForwardTextView.setText(forwardContent);
        holder.mExpForwardTextView.setOnContentTextClickListener(() -> {
            gotoDetailActivity(dynamicInfo.getTarget(), dynamicInfo);
        });
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

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        super.onViewRecycled(holder);
    }

    /**
     * 文章转发
     */
    private void initArticleForward(ArticleForwardHolder holder, DynamicInfo dynamicInfo) {
        String forwardCon = dynamicInfo.getForward().getContent();
        Spanned forwardContent = Html.fromHtml(CommonUtil.getUserNameWithColor(dynamicInfo.getForward().getAuthor().getNickName()) + forwardCon);

        holder.mExpandableTextView.setText(Html.fromHtml(dynamicInfo.getContent()));

        holder.mForwardTitle.setText(forwardContent);
        Glide.with(mContext).load(dynamicInfo.getForward().getArticle().getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mArticleCover);
        holder.mArticleForwardTitle.setText(dynamicInfo.getForward().getArticle().getTitle());
        String dailyId = dynamicInfo.getForward().getArticle().getId();
        String status = dynamicInfo.getForward().getArticle().getStatus();
        holder.mArticleCover.setOnClickListener(v -> gotoDailyDetailActivity(dailyId, status));
        holder.mArticleForwardTitle.setOnClickListener(v -> gotoDailyDetailActivity(dailyId, status));
        holder.mForwardTitle.setOnContentTextClickListener(() -> gotoDailyDetailActivity(dailyId, status));
    }

    private void gotoDailyDetailActivity(String dailyId, String status) {
        Intent intent;
        if (status.equals("0")) {
            intent = new Intent(mContext, ErrorActivity.class);
            intent.putExtra("barTitle", "文章详情");
        } else {
            intent = new Intent(mContext, DailyDetailActivity.class);
            intent.putExtra("dailyId", dailyId);
        }
        mContext.startActivity(intent);
    }

    /**
     * 评论正常
     */
    private void initAppraiseNormal(AppraiseNormalHolder holder, DynamicInfo dynamicInfo) {
        holder.mStarView.setIndicate(true);
        holder.mStarView.setStarMark(Float.parseFloat(dynamicInfo.getStar()));
        holder.mStarMark.setText(dynamicInfo.getStar());
        String cover = TextUtils.isEmpty(dynamicInfo.getGameInfo().getCover()) ? dynamicInfo.getGameInfo().getIcon() : dynamicInfo.getGameInfo().getCover();
        if (TextUtils.isEmpty(dynamicInfo.getGameInfo().getVideo())) {
            holder.mIconPlay.setVisibility(View.GONE);
            holder.mAppraiseCover.setOnClickListener(null);
        } else {
            holder.mIconPlay.setVisibility(View.VISIBLE);
            holder.mAppraiseCover.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra("url", dynamicInfo.getGameInfo().getVideo());
                intent.putExtra("title", dynamicInfo.getGameInfo().getGameNameCn());
                intent.putExtra("cover", dynamicInfo.getGameInfo().getCover());
                mContext.startActivity(intent);
            });
        }
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
        holder.mForwardUserName.setText("@" + CommonUtil.getNickName(dynamicInfo.getForward().getAuthor().getNickName()));
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
        if (TextUtils.isEmpty(dynamicInfo.getForward().getGameInfo().getCover())) {
            Glide.with(mContext).load(dynamicInfo.getForward().getGameInfo().getIcon()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mAppraiseCover);
        } else {
            Glide.with(mContext).load(dynamicInfo.getForward().getGameInfo().getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mAppraiseCover);
        }
    }

    /**
     * 删除动态
     */
    private void initDelete(DeleteHolder holder, DynamicInfo dynamicInfo) {
        holder.itemView.setOnClickListener(null);
    }

    public void updateLikeUI(int position, int thumb) {
        mDataList.get(position).setIsThumb(thumb);
        mDataList.get(position).setThumbsUp(CommonUtil.getThumbNum(thumb, mDataList.get(position).getThumbsUp()));
        notifyItemChanged(position, mDataList);
    }

    @Override
    public int getItemCount() {
        if (CommonUtil.isLogin()) {
            return super.getItemCount();
        } else {
            showLoadMore = false;
            if (mDataList != null) {
                if (isNeedShowLogin) {
                    return mDataList.size() + 1;
                } else {
                    return mDataList.size();
                }
            } else {
                return 0;
            }
        }
    }

    private class DynamicNormalHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,   mGameTitle, mBtnLike, mCommentNum, mShare;
        NineImageView mNineImageView;
        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        LinearLayout mUserLayout;
        RelativeLayout mGameLayout;
        LinearLayout mGameLayoutBig;
        TextView mForwardContent;
        View mline;
        CustomEmptyView mEmptyView;
        ImageView mBtnMore;
        ExpandableTextView mExpandableTextView;


        public DynamicNormalHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mExpandableTextView = $(R.id.expandable_view);
            mGameTitle = $(R.id.game_title);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
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

            mEmptyView.initBigUI();
        }
    }

    private class DynamicForwardHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        TextView mUserAction, mDyTime,   mGameTitle, mBtnLike, mCommentNum, mShare;
        NineImageView mNineImageView;
        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        LinearLayout mUserLayout;
        RelativeLayout mGameLayout;
        LinearLayout mGameLayoutBig;
        View mline;
        CustomEmptyView mEmptyView;
        ImageView mBtnMore;
        UserNameView mUserNameView;
        ExpandableTextView mExpandableTextView, mExpForwardTextView;

        public DynamicForwardHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mExpandableTextView = $(R.id.expandable_view);
            mGameTitle = $(R.id.game_title);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
            mNineImageView = $(R.id.nine_image);
            mFlowLayout = $(R.id.tag_flow_layout);
            mGameIcon = $(R.id.game_icon);
            mUserLayout = $(R.id.user_layout);
            mGameLayout = $(R.id.game_layout);
            mGameLayoutBig = $(R.id.game_layout_big);
            mline = $(R.id.line);
            mEmptyView = $(R.id.empty_view);
            mBtnMore = $(R.id.btn_more);
            mExpForwardTextView = $(R.id.forward_content);

            mEmptyView.initBigUI();
        }
    }

    private class ArticleNormalHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime,    mArticleTitle, mBtnLike, mCommentNum, mShare;
        ImageView mArticleCover;
        View mline;
        ImageView mBtnMore;
        ExpandableTextView mExpandableTextView;

        public ArticleNormalHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mExpandableTextView = $(R.id.expandable_view);
            mArticleCover = $(R.id.article_cover);
            mArticleTitle = $(R.id.article_title);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
        }
    }

    private class ArticleForwardHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime, mArticleForwardTitle, mBtnLike, mCommentNum, mShare;
        View mline;
        ImageView mBtnMore;
        ImageView mArticleCover;
        ExpandableTextView mExpandableTextView, mForwardTitle;

        public ArticleForwardHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mExpandableTextView = $(R.id.expandable_view);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mForwardTitle = $(R.id.forward_title);
            mArticleCover = $(R.id.article_cover);
            mArticleForwardTitle = $(R.id.article_forward_title);
        }
    }

    private class AppraiseNormalHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime, mBtnLike, mCommentNum, mShare, mStarMark, mGameTitle;
        View mline;
        ImageView mBtnMore, mAppraiseCover,mIconPlay;
        StarView mStarView;
        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        RelativeLayout mGameLayout;
        //JCVideoView mVideoPlayer;
        ExpandableTextView mExpandableTextView;

        public AppraiseNormalHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mStarView = $(R.id.start_bar);
            mStarMark = $(R.id.star_mark);
            mAppraiseCover = $(R.id.appraise_cover);
            mGameIcon = $(R.id.game_icon);
            mGameTitle = $(R.id.game_title);
            mGameLayout = $(R.id.game_layout);
            mFlowLayout = $(R.id.tag_flow_layout);
            mIconPlay = $(R.id.icon_play);
            mExpandableTextView = $(R.id.expandable_view);
        }
    }

    private class AppraiseForwardHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime, mBtnLike, mCommentNum, mShare, mGameTitle, mForwardUserName, mForwardStarMark;
        View mline;
        ImageView mBtnMore, mAppraiseCover, mIconPlay;
        StarView mForwardStartBar;
        ShapeImageView mGameIcon;
        TagFlowLayout mFlowLayout;
        RelativeLayout mGameLayout;
        ExpandableTextView mExpandableTextView, mForwardContent;

        AppraiseForwardHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mExpandableTextView = $(R.id.expandable_view);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mAppraiseCover = $(R.id.appraise_cover);
            mGameIcon = $(R.id.game_icon);
            mGameTitle = $(R.id.game_title);
            mGameLayout = $(R.id.game_layout);
            mFlowLayout = $(R.id.tag_flow_layout);
            mForwardUserName = $(R.id.forward_user_name);
            mForwardStarMark = $(R.id.forward_star_mark);
            mForwardStartBar = $(R.id.forward_start_bar);
            mIconPlay = $(R.id.icon_play);
            mForwardContent = $(R.id.forward_content);
        }
    }

    private class DeleteHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        UserNameView mUserNameView;
        TextView mUserAction, mDyTime, mBtnLike, mCommentNum, mShare;
        View mline;
        ImageView mBtnMore;
        ExpandableTextView mExpandableTextView;

        DeleteHolder(View itemView) {
            super(itemView, mContext, false);
            mUserNameView = $(R.id.user_name_layout);
            mUserHeader = $(R.id.user_header);
            mUserAction = $(R.id.user_action);
            mDyTime = $(R.id.dy_time);
            mBtnLike = $(R.id.like);
            mCommentNum = $(R.id.comment);
            mShare = $(R.id.share);
            mline = $(R.id.line);
            mBtnMore = $(R.id.btn_more);
            mExpandableTextView = $(R.id.expandable_view);
        }
    }

    private class LoginHolder extends BaseViewHolder {
        private TextView mLogin;

        LoginHolder(View itemView) {
            super(itemView, mContext, false);
            mLogin = $(R.id.btn_login);
            mLogin.setOnClickListener(v -> {
                LoginActivity.launch(mContext, "DynamicAdapter");
            });
        }
    }

    public interface OnLikeClickListener {
        void onClick(String dynamic_id, int isThumb, int position, String thumbType);
    }

    public interface OnMoreBtnClickListener {
        void onMoreBtnClick(String dynamic_id, int position);
    }
}
