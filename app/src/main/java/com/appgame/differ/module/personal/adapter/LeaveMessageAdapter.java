package com.appgame.differ.module.personal.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.demo.CommentReplayLayout;
import com.appgame.differ.module.personal.view.LeaveMessageActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/10.
 * 386707112@qq.com
 */

public class LeaveMessageAdapter extends RecyclerView.Adapter<LeaveMessageAdapter.MsgHolder> {

    private Context mContext;
    private List<UserGuest> mList;
    private OnItemClickListener mOnItemClickListener;
    private OnChildItemClickListener mOnChildItemClickListener;
    private OnLikeClickListener mOnLikeClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public LeaveMessageAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<UserGuest> list, boolean isLoadMore) {
        if (!isLoadMore)
            mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnChildItemClickListener(OnChildItemClickListener onChildItemClickListener) {
        mOnChildItemClickListener = onChildItemClickListener;
    }

    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        mOnLikeClickListener = onLikeClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public MsgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_about, parent, false);
        return new MsgHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgHolder holder, int position) {
        UserGuest guest = mList.get(position);
        String content = guest.getContent();
        if (content.contains("回复#Replay#")) {
            content = content.replace("#Replay#", "");
        }
        holder.mAboutDesc.setText(content);
        holder.mUserName.setText(CommonUtil.getNickName(guest.getAuthor().getNickName()));
        Glide.with(mContext).load(guest.getAuthor().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
        holder.mUserHeader.setOnClickListener(v -> IntentHelper.startPersionActivity((LeaveMessageActivity) mContext, holder.mUserHeader, guest.getAuthor().getUserId()));
        holder.mUserName.setOnClickListener(v -> IntentHelper.startPersionActivity((LeaveMessageActivity) mContext, holder.mUserHeader, guest.getAuthor().getUserId()));
        holder.mAboutTime.setText(CommonUtil.getStandardDate(guest.getCreatedAt()));
        holder.mTagCount.setText(guest.getChildGuests() != null ? String.valueOf(guest.getChildGuests().size()) : "0");
        holder.mLikeCount.setText(guest.getThumbsUp());
        holder.mLikeCount.setCompoundDrawablesWithIntrinsicBounds(guest.getIsThumb() == 0 ? R.drawable.game_icon_like : R.drawable.game_icon_like_green, 0, 0, 0);
        holder.mAboutDesc.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position, guest);
            }
        });
        holder.mTagCount.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position, guest);
            }
        });
        holder.mLikeCount.setOnClickListener(v -> {
            if (mOnLikeClickListener != null) {
                mOnLikeClickListener.onClick(guest, position);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onLongClick(guest, guest.getId(), position);
            }
            return false;
        });
          initChildRecycleView(holder, guest);
//        if (guest.getChildGuests() != null) {
//            int lave = guest.getChildGuests().size();
//            holder.mCommentReplayLayout.addUserGuests(guest.getChildGuests());
//            holder.mCommentReplayLayout.init(guest.isShowAll, lave);
//            holder.mCommentReplayLayout.setOnCommentReplayListener(isShowAll -> {
//                guest.isShowAll = isShowAll;
//                holder.mCommentReplayLayout.init(isShowAll, lave);
//            });
//            holder.mCommentReplayLayout.setOnUserGuestClickListener((position1, userGuest) -> {
//                if (mOnChildItemClickListener != null) {
//                    mOnChildItemClickListener.onItemClick(position1, guest, userGuest);
//                }
//            });
//            holder.mCommentReplayLayout.setIsShowCommentLayout(true);
//            holder.mCommentReplayLayout.setVisibility(View.VISIBLE);
//        } else {
//            holder.mCommentReplayLayout.setIsShowCommentLayout(false);
//            holder.mCommentReplayLayout.setVisibility(View.GONE);
//        }
    }

    public void updateLikeUI(int position, int type) {
        UserGuest guest = mList.get(position);
        guest.setIsThumb(type);
        guest.setThumbsUp(CommonUtil.getThumbNum(type, guest.getThumbsUp()));
        notifyItemChanged(position, guest);
    }

    private void initChildRecycleView(MsgHolder holder, UserGuest guest) {
        LeaveMessageChildAdapter messageChildAdapter = new LeaveMessageChildAdapter(mContext);
        holder.mReplayRecycle.setHasFixedSize(true);
        holder.mReplayRecycle.setAdapter(messageChildAdapter);
        if (guest.getChildGuests() != null) {
            messageChildAdapter.setUserGuestList(guest.getChildGuests(), guest.isShowAll);
            holder.mBtnShowAll.setVisibility(guest.getChildGuests().size() > AppConstants.MAX_COUNT ? View.VISIBLE : View.GONE);
            int lave = guest.getChildGuests().size();
            holder.mBtnShowAll.setText(messageChildAdapter.getItemCount() > AppConstants.MAX_COUNT ? "收起" : "查看全部" + lave + "条回复");
            holder.mBtnShowAll.setOnClickListener(v -> {
                if (messageChildAdapter.getItemCount() <= AppConstants.MAX_COUNT) {
                    guest.isShowAll = true;
                    messageChildAdapter.setUserGuestList(guest.getChildGuests(), true);
                } else {
                    guest.isShowAll = false;
                    messageChildAdapter.setUserGuestList(guest.getChildGuests(), false);
                }
                holder.mBtnShowAll.setText(messageChildAdapter.getItemCount() > AppConstants.MAX_COUNT ? "收起" : "查看全部" + lave + "条回复");
            });
            messageChildAdapter.setItemClickListener((position, guest1) -> {
                if (mOnChildItemClickListener != null) {
                    mOnChildItemClickListener.onItemClick(position, guest, guest1);
                }
            });

            holder.mReCommLayout.setVisibility(View.VISIBLE);
        } else {
            holder.mReCommLayout.setVisibility(View.GONE);
        }
    }

    public void removeCommentUI(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MsgHolder extends BaseViewHolder {
        CircleImageView mUserHeader;
        TextView mUserName, mAboutTime, mAboutDesc, mBtnShowAll, mTagCount, mLikeCount;
        RecyclerView mReplayRecycle;
        RelativeLayout mReCommLayout;

        CommentReplayLayout mCommentReplayLayout;

        MsgHolder(View itemView) {
            super(itemView, mContext, false);
            mUserHeader = $(R.id.user_header);
            mUserName = $(R.id.user_name);
            mAboutTime = $(R.id.about_time);
            mAboutDesc = $(R.id.about_desc);
            mBtnShowAll = $(R.id.btn_see_more);
            mReplayRecycle = $(R.id.replay_recycle);
            mReplayRecycle.setLayoutManager(new LinearLayoutManager(mContext));
            mReCommLayout = $(R.id.re_comm_layout);
            mTagCount = $(R.id.tag_count);
            mLikeCount = $(R.id.up);
            mCommentReplayLayout = $(R.id.comm_re_layout);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, UserGuest guest);
    }

    public interface OnChildItemClickListener {
        void onItemClick(int position, UserGuest guest, UserGuest childGuest);
    }

    public interface OnLikeClickListener {
        void onClick(UserGuest guest, int position);
    }

    public interface OnItemLongClickListener {
        void onLongClick(UserGuest guest, String guestId, int position);
    }
}
