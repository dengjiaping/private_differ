package com.appgame.differ.module.msg.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.widget.CircleImageView;

/**
 * Created by lzx on 2017/5/9.
 * 386707112@qq.com
 */

public class MsgReplyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int TYPE_HEADER = 0;
    private int TYPE_ITEM = 1;

    public MsgReplyAdapter(Context context) {
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_mine_about, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_about, parent, false);
            return new MsgReplyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        LinearLayout mBtnAllComm;
        TextView mTitle;

        public HeaderHolder(View itemView) {
            super(itemView);
            mBtnAllComm = (LinearLayout) itemView.findViewById(R.id.btn_all_comm);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTitle.setText("查看原文");
        }
    }


    public class MsgReplyHolder extends RecyclerView.ViewHolder {
        CircleImageView mUserHeader;
        TextView mUserName, mAboutTime, mAboutDesc;
        RecyclerView mReplayRecycle;

        public MsgReplyHolder(View itemView) {
            super(itemView);
            mUserHeader = (CircleImageView) itemView.findViewById(R.id.user_header);
            mUserName = (TextView) itemView.findViewById(R.id.user_name);
            mAboutTime = (TextView) itemView.findViewById(R.id.about_time);
            mAboutDesc = (TextView) itemView.findViewById(R.id.about_desc);
            mReplayRecycle = (RecyclerView) itemView.findViewById(R.id.replay_recycle);
            mReplayRecycle.setLayoutManager(new LinearLayoutManager(mContext));
            mReplayRecycle.setHasFixedSize(true);
        }
    }
}
