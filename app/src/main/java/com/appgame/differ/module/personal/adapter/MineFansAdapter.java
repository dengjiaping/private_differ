package com.appgame.differ.module.personal.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.module.personal.view.FansActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */
public class MineFansAdapter extends LoadMoreAdapter<UserInfo> {

    public MineFansAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_follow, parent, false);
        return new MineFansHolder(view, mContext);
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        MineFansHolder holder = (MineFansHolder) viewHolder;
        holder.mTextComment.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.mLine.setVisibility(position == mDataList.size() - 1 ? View.GONE : View.VISIBLE);
        holder.mGameName.setVisibility(View.GONE);
        holder.mTextComment.setText("游戏玩家");
        final UserInfo userInfo = mDataList.get(position);
        Glide.with(mContext).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
        holder.mUserName.setText(CommonUtil.getNickName(userInfo.getNickName()));
        if (userInfo.getRank() != null) {
            String icon = userInfo.getRank().getIcon();
            holder.mIconBadge.setVisibility(!TextUtils.isEmpty(icon) ? View.VISIBLE : View.GONE);
            Glide.with(mContext).load(icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
        } else {
            holder.mIconBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> IntentHelper.startPersionActivity((FansActivity) mContext, holder.mUserHeader, userInfo.getUserId()));
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }

    public class MineFansHolder extends BaseViewHolder {
        private CircleImageView mUserHeader;
        private TextView mUserName, mTextComment, mGameName;
        private ImageView mBtnDot, mIconBadge;
        private View mLine;

        public MineFansHolder(View itemView, Context context) {
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
//public class MineFansAdapter extends RecyclerView.Adapter<MineFansAdapter.MineFansHolder> {
//
//    private Context mContext;
//    private List<UserInfo> mList;
//
//    public MineFansAdapter(Context context) {
//        mContext = context;
//        mList = new ArrayList<>();
//    }
//
//    public void setList(List<UserInfo> list, boolean isLoadMore) {
//        if (!isLoadMore)
//            mList.clear();
//        mList.addAll(list);
//        notifyDataSetChanged();
//    }
//
//    public void clear() {
//        mList.clear();
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public MineFansHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mine_follow, parent, false);
//        return new MineFansHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final MineFansHolder holder, int position) {
//        holder.mTextComment.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        holder.mLine.setVisibility(position == mList.size() - 1 ? View.GONE : View.VISIBLE);
//        holder.mGameName.setVisibility(View.GONE);
//        holder.mTextComment.setText("游戏玩家");
//
//        final UserInfo userInfo = mList.get(position);
//        Glide.with(mContext).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mUserHeader);
//        holder.mUserName.setText(CommonUtil.getNickName(userInfo.getNickName()));
//        if (userInfo.getRank() != null) {
//            String icon = userInfo.getRank().getIcon();
//            holder.mIconBadge.setVisibility(!TextUtils.isEmpty(icon) ? View.VISIBLE : View.GONE);
//            Glide.with(mContext).load(icon).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mIconBadge);
//        } else {
//            holder.mIconBadge.setVisibility(View.GONE);
//        }
//
//        holder.itemView.setOnClickListener(v -> IntentHelper.startPersionActivity((FansActivity) mContext, holder.mUserHeader, userInfo.getUserId()));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }
//
//    public class MineFansHolder extends RecyclerView.ViewHolder {
//        private CircleImageView mUserHeader;
//        private TextView mUserName, mTextComment, mGameName;
//        private ImageView mBtnDot, mIconBadge;
//        private View mLine;
//
//        public MineFansHolder(View itemView) {
//            super(itemView);
//            mUserHeader = (CircleImageView) itemView.findViewById(R.id.user_header);
//            mUserName = (TextView) itemView.findViewById(R.id.user_name);
//            mTextComment = (TextView) itemView.findViewById(R.id.text_comment);
//            mGameName = (TextView) itemView.findViewById(R.id.game_name);
//            mBtnDot = (ImageView) itemView.findViewById(R.id.btn_dot);
//            mIconBadge = (ImageView) itemView.findViewById(R.id.icon_badge);
//            mLine = itemView.findViewById(R.id.line);
//        }
//    }
//}
