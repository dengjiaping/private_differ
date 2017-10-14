package com.appgame.differ.module.personal.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.mine.MineFollows;
import com.appgame.differ.module.search.SearchClassActivity;
import com.appgame.differ.module.personal.view.FollowsActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */
public class MineFollowsAdapter extends LoadMoreAdapter<MineFollows> {

    public MineFollowsAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_follow, parent, false);
        return new MineFollowsHolder(view, mContext);
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        MineFollowsHolder holder = (MineFollowsHolder) viewHolder;
        holder.mLine.setVisibility(position == mDataList.size() - 1 ? View.GONE : View.VISIBLE);
        final MineFollows follows = mDataList.get(position);
        Glide.with(mContext).load(follows.getUserInfo().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
        holder.mUserName.setText(CommonUtil.getNickName(follows.getUserInfo().getNickName()));
        holder.mTextComment.setVisibility(TextUtils.isEmpty(follows.getAppraiseGame()) ? View.GONE : View.VISIBLE);
        holder.mGameName.setVisibility(TextUtils.isEmpty(follows.getAppraiseGame()) ? View.GONE : View.VISIBLE);
        holder.mTextComment.setText("他" + CommonUtil.getStandardDate(follows.getAppraiseTime()) + "评论了");
        holder.mGameName.setText("《" + follows.getAppraiseGame() + "》");
        if (follows.getUserInfo().getRank() != null) {
            String icon = follows.getUserInfo().getRank().getIcon();
            holder.mIconBadge.setVisibility(!TextUtils.isEmpty(icon) ? View.VISIBLE : View.GONE);
            Glide.with(mContext).load(icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
        } else {
            holder.mIconBadge.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v -> {
            if (mContext instanceof FollowsActivity) {
                IntentHelper.startPersionActivity((FollowsActivity) mContext, holder.mUserHeader, follows.getUserInfo().getUserId());
            } else if (mContext instanceof SearchClassActivity) {
                IntentHelper.startPersionActivity((SearchClassActivity) mContext, holder.mUserHeader, follows.getUserInfo().getUserId());
            }
        });
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }


    @Override
    public List<MineFollows> getDataList() {
        return this.mDataList;
    }

    public class MineFollowsHolder extends BaseViewHolder {
        private CircleImageView mUserHeader;
        private TextView mUserName, mTextComment, mGameName;
        private ImageView mBtnDot, mIconBadge;
        private View mLine;

        public MineFollowsHolder(View itemView, Context context) {
            super(itemView, context, false);
            mUserHeader = $(R.id.user_header);
            mUserName = $(R.id.user_name);
            mTextComment = $(R.id.text_comment);
            mGameName = $(R.id.game_name);
            mBtnDot = $(R.id.btn_dot);
            mIconBadge = $(R.id.icon_badge);
            mLine = $(R.id.line);
        }
    }
}
