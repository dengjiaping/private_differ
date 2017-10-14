package com.appgame.differ.module.find.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.widget.ShapeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by xian on 2017/5/20.
 */

public class ReserveAdapter extends RecyclerView.Adapter<ReserveAdapter.ReserveHolder> {

    private Context mContext;
    private String[] bannerArray;
    private int layoutSize160;
    private int layoutSize3;
    private int layoutSize15;
    private int layoutSize5;

    public ReserveAdapter(Context context) {
        mContext = context;
        layoutSize160 = CommonUtil.dip2px(mContext, 160);
        layoutSize3 = CommonUtil.dip2px(mContext, 3);
        layoutSize15 = CommonUtil.dip2px(mContext, 15);
        layoutSize5 = CommonUtil.dip2px(mContext, 5);
        bannerArray = new String[]{
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495620431&di=0d0e04d20abf7be4661e4d71f22b3034&imgtype=jpg&er=1&src=http%3A%2F%2Fimg1.ph.126.net%2F2HfqDZOQVaRMmLDEmPLgQg%3D%3D%2F2779002445082210500.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495025934223&di=8ce0e50fae4e9c51b86f70427a03aa5c&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201403%2F20%2F20140320222513_dZf23.jpeg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495025952901&di=538234ce258316effe9bbc3713028c52&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201505%2F12%2F20150512210115_mAK3h.thumb.700_0.jpeg"
        };
    }

    @Override
    public ReserveHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_card, parent, false);
        return new ReserveHolder(view);
    }

    @Override
    public void onBindViewHolder(ReserveHolder holder, int position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layoutSize160, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = layoutSize3;
        if (position == 0) {
            params.leftMargin = layoutSize15;
            params.rightMargin = layoutSize5;
        } else if (position == 9) {
            params.leftMargin = layoutSize5;
            params.rightMargin = layoutSize15;
        } else {
            params.leftMargin = layoutSize5;
            params.rightMargin = layoutSize5;
        }
        holder.itemView.setLayoutParams(params);

        Glide.with(mContext).load(bannerArray[1]).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
        Glide.with(mContext).load(bannerArray[2]).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
        holder.mGameName.setText("天命传说");
        holder.mGameTag.setText("动作格斗、漫画改编");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ReserveHolder extends BaseViewHolder {
        ImageView mGameCover;
        ShapeImageView mGameIcon;
        TextView mGameName, mGameTag, mBtnReserve;

        public ReserveHolder(View itemView) {
            super(itemView,mContext,false);
            mGameCover =  $(R.id.game_cover);
            mGameIcon =  $(R.id.game_icon);
            mGameName =  $(R.id.game_name);
            mGameTag =  $(R.id.game_tag);
            mBtnReserve =  $(R.id.btn_reserve);
        }
    }
}
