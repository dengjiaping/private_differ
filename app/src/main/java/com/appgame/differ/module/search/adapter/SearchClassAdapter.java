package com.appgame.differ.module.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.game.GameCategory;
import com.appgame.differ.module.search.GameClassActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.widget.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/5/20.
 */

public class SearchClassAdapter extends RecyclerView.Adapter<SearchClassAdapter.ClassHolder> {

    private Context mContext;
    private List<GameCategory> mGameCategories;
    private List<GameCategory> dataSource;
    private boolean isShowAll;

    public SearchClassAdapter(Context context) {
        mContext = context;
        mGameCategories = new ArrayList<>();
        dataSource = new ArrayList<>();
    }

    public void buildDataSource(List<GameCategory> list) {
        dataSource.clear();
        dataSource.addAll(list);
    }

    public void setList(List<GameCategory> list, boolean isShowAll) {
        this.isShowAll = isShowAll;
        mGameCategories.clear();
        if (isShowAll) {
            mGameCategories.addAll(list);
        } else {
            if (list.size() < 9) {
                mGameCategories.addAll(list);
            } else {
                for (int i = 0; i < 9; i++) {
                    GameCategory category = list.get(i);
                    mGameCategories.add(category);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_class, parent, false);
        return new ClassHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassHolder holder, int position) {
        if (position == getItemCount() - 1) {
             holder.mClassImage.setImageResource(R.drawable.ic_search_more);
            holder.mClassTitle.setText("更多");
            holder.itemView.setVisibility(isShowAll ? View.GONE : View.VISIBLE);
            holder.itemView.setOnClickListener(v -> {
                setList(dataSource, !isShowAll);
                CommonUtil.HideKeyboard(holder.itemView);
            });
        } else {
            GameCategory category = mGameCategories.get(position);
            Glide.with(mContext).load(category.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mClassImage);
            holder.mClassTitle.setText(category.getName());
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, GameClassActivity.class);
                intent.putExtra("typeName", category.getName());
                intent.putExtra("typeId", category.getId());
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGameCategories.size() + 1;
    }

    public class ClassHolder extends BaseViewHolder {

        TextView mClassTitle;
        CircleImageView mClassImage;

        public ClassHolder(View itemView) {
            super(itemView,mContext,false);
            mClassImage = $(R.id.class_icon);
            mClassTitle = $(R.id.class_title);
        }
    }


}
