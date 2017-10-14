package com.appgame.differ.module.wikis;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.other.WebActivity;
import com.appgame.differ.bean.wikis.WikisInfo;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/9/4.
 */

public class WikisAdapter extends RecyclerView.Adapter<WikisAdapter.WikisHoloder> {
    private Context mContext;
    private List<WikisInfo> mWikisInfoList;
    private RelativeLayout.LayoutParams mParams;
    private int cardHeight, cardWidth;

    public WikisAdapter(Context context) {
        mContext = context;
        mWikisInfoList = new ArrayList<>();
        cardHeight = CommonUtil.getPhoneHeight(mContext) * 896 / 1280;
        cardWidth = CommonUtil.getPhoneWidth(mContext) * 624 / 720;

        mParams = new RelativeLayout.LayoutParams(cardWidth, cardHeight);


    }

    public void setWikisInfoList(List<WikisInfo> list) {
        mWikisInfoList = list;
        notifyDataSetChanged();
    }

    @Override
    public WikisHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wikis_card, parent, false);
        return new WikisHoloder(view);
    }

    @Override
    public void onBindViewHolder(WikisHoloder holder, int position) {
        if (getItemCount() == 1) {
            mParams.leftMargin = CommonUtil.dip2px(mContext, 20);
            mParams.rightMargin = CommonUtil.dip2px(mContext, 0);
        } else if (position == 0) {
            mParams.leftMargin = CommonUtil.dip2px(mContext, 20);
            mParams.rightMargin = CommonUtil.dip2px(mContext, 0);
        } else if (position == getItemCount() - 1) {
            mParams.rightMargin = CommonUtil.dip2px(mContext, 20);
            mParams.leftMargin = CommonUtil.dip2px(mContext, 0);
        } else {
            mParams.leftMargin = CommonUtil.dip2px(mContext, 0);
            mParams.rightMargin = CommonUtil.dip2px(mContext, 0);
        }
        mParams.topMargin = CommonUtil.dip2px(mContext, 100);
        mParams.bottomMargin = CommonUtil.dip2px(mContext, 10);
        holder.mCardView.setLayoutParams(mParams);

        WikisInfo wikisInfo = mWikisInfoList.get(position);
        Glide.with(mContext).load(wikisInfo.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mCardBg);
        holder.mGameName.setText(wikisInfo.getTitle());
        holder.mGameDesc.setText(wikisInfo.getSmallTitle());
        if (TextUtils.isEmpty(wikisInfo.getTitle()) && TextUtils.isEmpty(wikisInfo.getSmallTitle())) {
            holder.shadowView.setVisibility(View.GONE);
        } else {
            holder.shadowView.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> {
            WebActivity.launch(mContext, wikisInfo.getTitle(), wikisInfo.getUrl());
        });
    }

    @Override
    public int getItemCount() {
        return mWikisInfoList.size();
    }


    class WikisHoloder extends BaseViewHolder {

        ImageView mCardBg;
        TextView mGameName, mGameDesc;
        RelativeLayout mCardView;
        View shadowView;

        WikisHoloder(View itemView) {
            super(itemView, mContext, false);
            mCardView = $(R.id.cardView);
            mCardBg = $(R.id.card_bg);
            mGameName = $(R.id.game_name);
            mGameDesc = $(R.id.game_desc);
            shadowView = $(R.id.shadow_view);
        }
    }
}
