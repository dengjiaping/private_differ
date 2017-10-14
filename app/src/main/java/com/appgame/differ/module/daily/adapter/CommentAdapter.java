package com.appgame.differ.module.daily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.CommentReplies;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/4/15.
 * 386707112@qq.com
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<CommentReplies> mList;
    private String commentId = "";
    private OnItemClickListener mOnItemClickListener;

    public CommentAdapter(Context context, String commentId) {
        mContext = context;
        this.commentId = commentId;
        mList = new ArrayList<>();
    }

    public void setList(List<CommentReplies> list, boolean isShowAll) {
        mList.clear();
        if (list.size() > AppConstants.MAX_COUNT) {
            if (isShowAll) {
                mList.addAll(list);
            } else {
                mList.add(list.get(0));
                mList.add(list.get(1));
            }
        } else {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_daily_comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        final CommentReplies replies = mList.get(position);
        holder.mUserName.setText(CommonUtil.getNickName(replies.getRelationships().getNickName()) + ":");
        String content = replies.getContent();

        if (content.contains("回复#Replay#")) {
            content = content.replace("#Replay#", "");
            holder.mCommentDetail.setText(content);
        } else {
            holder.mCommentDetail.setText(content);
        }
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(replies,commentId);
            }
        });
    }

    public interface OnItemClickListener {
        void onClick(CommentReplies replies,String commentId);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class CommentViewHolder extends BaseViewHolder {

        private TextView mUserName, mCommentDetail;

        CommentViewHolder(View itemView) {
            super(itemView,mContext,false);
            mUserName =$(R.id.user_name);
            mCommentDetail = $(R.id.comment_detail);
        }
    }

}
