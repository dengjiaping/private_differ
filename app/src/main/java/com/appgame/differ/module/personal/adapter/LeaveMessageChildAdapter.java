package com.appgame.differ.module.personal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/11.
 * 386707112@qq.com
 */

public class LeaveMessageChildAdapter extends RecyclerView.Adapter<LeaveMessageChildAdapter.ChildHolder> {

    private Context mContext;
    private List<UserGuest> mUserGuestList;
    private OnItemClickListener mItemClickListener;

    public LeaveMessageChildAdapter(Context context) {
        mContext = context;
        mUserGuestList = new ArrayList<>();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setUserGuestList(List<UserGuest> userGuestList, boolean isShowAll) {
        mUserGuestList.clear();
        if (userGuestList.size() > AppConstants.MAX_COUNT) {
            if (isShowAll) {
                mUserGuestList.addAll(userGuestList);
            } else {
                mUserGuestList.add(userGuestList.get(0));
                mUserGuestList.add(userGuestList.get(1));
            }
        } else {
            mUserGuestList.addAll(userGuestList);
        }
        notifyDataSetChanged();
    }

    @Override
    public ChildHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_daily_comment_item, parent, false);
        return new ChildHolder(view);
    }

    @Override
    public void onBindViewHolder(ChildHolder holder, int position) {
        UserGuest guest = mUserGuestList.get(position);
        holder.mUserName.setText(CommonUtil.getNickName(guest.getAuthor().getNickName()) + ":");
        String content = guest.getContent();
        if (content.contains("回复#Replay#")) {
            content = content.replace("#Replay#", "");
        }
        holder.mCommentDetail.setText(content);
        holder.mCommentDetail.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position, guest);
            }
        });
    }

    public interface OnItemClickListener {
        void onItemClick(int position, UserGuest guest);
    }

    @Override
    public int getItemCount() {
        return mUserGuestList.size();
    }

    public class ChildHolder extends RecyclerView.ViewHolder {

        private TextView mUserName, mCommentDetail;

        public ChildHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mCommentDetail = (TextView) itemView.findViewById(R.id.comment_detail);
        }
    }

}
