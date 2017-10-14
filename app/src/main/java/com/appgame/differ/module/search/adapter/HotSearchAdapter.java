package com.appgame.differ.module.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.search.HotSearch;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/5/20.
 */

public class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.HotSearchHolder> {
    private LinearLayout.LayoutParams mParams;
    private Context mContext;
    private String[] bgColor = new String[]{
            "#3BC5AB94", "#3BF4C2C8", "#3BBEE990", "#3BF9C9A5", "#3B99DDFB", "#3BA4C7F0", "#3BC4C8D1"
    };

    private List<HotSearch> hotSearches;

    private OnItemClickListener mOnItemClickListener;

    public HotSearchAdapter(Context context) {
        mContext = context;
        mParams = new LinearLayout.LayoutParams(CommonUtil.dip2px(mContext, 75), CommonUtil.dip2px(mContext, 22));
        hotSearches = new ArrayList<>();
    }

    public void setHotSearches(List<HotSearch> hotSearches) {
        this.hotSearches = hotSearches;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public HotSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_search, parent, false);
        return new HotSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(HotSearchHolder holder, int position) {
        HotSearch hotSearch = hotSearches.get(position);
        holder.mGameName.setText(hotSearch.getGameName());
        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(hotSearch.getGameName());
                CommonUtil.HideKeyboard(holder.mGameName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotSearches.size();
    }

    public class HotSearchHolder extends BaseViewHolder {

        TextView mGameName;
        CardView mCardView;

        public HotSearchHolder(View itemView) {
            super(itemView,mContext,false);
            mCardView =  $(R.id.card_view);
            mGameName =  $(R.id.game_name);
            mGameName.setBackgroundColor(Color.parseColor(bgColor[(int) (Math.random() * bgColor.length)]));
        }
    }

    public interface OnItemClickListener {
        void onClick(String gameName);
    }

}
