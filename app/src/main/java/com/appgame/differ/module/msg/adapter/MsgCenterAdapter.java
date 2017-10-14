package com.appgame.differ.module.msg.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.module.msg.MsgListActivity;
import com.appgame.differ.module.msg.MsgReplyActivity;
import com.appgame.differ.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */

public class MsgCenterAdapter extends RecyclerView.Adapter {

    private final static int TYEP_HEADER = 0;
    private final static int TYEP_ITEM = 1;
    private Context mContext;
    private List<String> mList;

    public MsgCenterAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public void setList(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYEP_HEADER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.header_msg_center, parent, false);
            return new MsgHeaderHolder(view);
        } else if (viewType == TYEP_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_center, parent, false);
            return new MsgCenterHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MsgHeaderHolder) {
            MsgHeaderHolder headerHolder = (MsgHeaderHolder) holder;
            headerHolder.mItemSystemMsg.setOnClickListener(v -> gotoMsgListActivity("system", "系统消息"));
            headerHolder.mItemOfficialMsg.setOnClickListener(v -> gotoMsgListActivity("official", "differ官方"));
        } else if (holder instanceof MsgCenterHolder) {
            MsgCenterHolder msgCenterHolder = (MsgCenterHolder) holder;
            msgCenterHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, MsgReplyActivity.class);
                mContext.startActivity(intent);
            });
        }
    }

    private void gotoMsgListActivity(String msgType, String title) {
        Intent intent = new Intent(mContext, MsgListActivity.class);
        intent.putExtra("msgType", msgType);
        intent.putExtra("title", title);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYEP_HEADER;
        } else {
            return TYEP_ITEM;
        }
    }

    public class MsgHeaderHolder extends RecyclerView.ViewHolder {
        View mMsgSystemDot, mMsgOfficialDot;
        TextView mSystemMsgTitle, mOfficialMsgTitle;
        LinearLayout mItemSystemMsg, mItemOfficialMsg;

        public MsgHeaderHolder(View itemView) {
            super(itemView);
            mMsgSystemDot = itemView.findViewById(R.id.msg_system_dot);
            mMsgOfficialDot = itemView.findViewById(R.id.msg_official_dot);
            mSystemMsgTitle = (TextView) itemView.findViewById(R.id.system_msg_title);
            mOfficialMsgTitle = (TextView) itemView.findViewById(R.id.official_msg_title);
            mItemSystemMsg = (LinearLayout) itemView.findViewById(R.id.item_system_msg);
            mItemOfficialMsg = (LinearLayout) itemView.findViewById(R.id.item_official_msg);
        }
    }

    public class MsgCenterHolder extends RecyclerView.ViewHolder {

        CircleImageView mUserHeader;
        TextView mUserName, mMsgDesc, mMsgTime;

        public MsgCenterHolder(View itemView) {
            super(itemView);
            mUserHeader = (CircleImageView) itemView.findViewById(R.id.user_header);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mMsgDesc = (TextView) itemView.findViewById(R.id.msg_desc);
            mMsgTime = (TextView) itemView.findViewById(R.id.msg_time);
        }
    }
}
