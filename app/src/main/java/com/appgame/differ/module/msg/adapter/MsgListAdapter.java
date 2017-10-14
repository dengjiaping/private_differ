package com.appgame.differ.module.msg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.MsgInfo;
import com.appgame.differ.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */

public class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MsgListHolder> {

    private Context mContext;
    private List<MsgInfo> mMsgInfos;
    private String msgType;

    public MsgListAdapter(Context context, String msgType) {
        mContext = context;
        this.msgType = msgType;
        mMsgInfos = new ArrayList<>();
    }

    public void setList(List<MsgInfo> list) {
        mMsgInfos = list;
        notifyDataSetChanged();
    }

    @Override
    public MsgListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_msg_center, parent, false);
        return new MsgListHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgListHolder holder, int position) {
        MsgInfo msgInfo = mMsgInfos.get(position);
        if (msgType.equals("system")) {
            holder.mMsgDot.setVisibility(View.INVISIBLE);
        } else {
            holder.mMsgDot.setVisibility(msgInfo.isRead() ? View.INVISIBLE : View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            msgInfo.setRead(true);
            notifyItemChanged(position, msgInfo);
        });
    }

    @Override
    public int getItemCount() {
        return mMsgInfos.size();
    }

    public class MsgListHolder extends RecyclerView.ViewHolder {

        CircleImageView mUserHeader;
        TextView mUserName, mMsgDesc, mMsgTime;
        View mMsgDot;

        public MsgListHolder(View itemView) {
            super(itemView);
            mUserHeader = (CircleImageView) itemView.findViewById(R.id.user_header);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mMsgDesc = (TextView) itemView.findViewById(R.id.msg_desc);
            mMsgTime = (TextView) itemView.findViewById(R.id.msg_time);
            mMsgDot = itemView.findViewById(R.id.msg_dot);
        }
    }
}
