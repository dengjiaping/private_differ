package com.appgame.differ.module.dynamic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.collection.CollectionInfo;
import com.appgame.differ.bean.game.SimpleGame;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.widget.ShapeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

/**
 * Created by yukunlin on 17/5/4.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context mContext;
    private List<CollectionInfo> mData;

    private OnItemClickListener mOnItemClickListener;

    public List<CollectionInfo> getData() {
        return mData;
    }

    public void setData(List<CollectionInfo> data) {
        this.mData = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public SearchAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        CollectionInfo info = mData.get(position);
        holder.mTitle.setText(info.getAttributes().getGameNameCn());
        Glide.with(mContext).load(info.getAttributes().getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(holder.mGameIcon);
        holder.mTagFlowLayout.setAdapter(new TagAdapter<TagsInfo>(
                info.getAttributes().getTags().size() > 3 ?
                        info.getAttributes().getTags().subList(0, 3) : info.getAttributes().getTags()) {
            @Override
            public View getView(FlowLayout parent, int position, TagsInfo tagsInfo) {
                TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_tag, parent, false);
                textView.setText(tagsInfo.getName());
                return textView;
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(GameToSimpleGame(mData.get(position)), "post");
            }
        });
    }

    public interface OnItemClickListener {
        void onClick(SimpleGame simpleGame, String postType);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class SearchViewHolder extends BaseViewHolder {
        ShapeImageView mGameIcon;
        TextView mTitle;
        TagFlowLayout mTagFlowLayout;

        public SearchViewHolder(View itemView) {
            super(itemView, mContext, false);
            mGameIcon = $(R.id.game_icon);
            mTitle = $(R.id.title);
            mTagFlowLayout = $(R.id.tag_flow_layout);
        }
    }

    private SimpleGame GameToSimpleGame(CollectionInfo gameInfo) {
        SimpleGame simpleGame = new SimpleGame();
        simpleGame.setGameId(gameInfo.getId());
        simpleGame.setGameIcon(gameInfo.getAttributes().getIcon());
        simpleGame.setGameNameCn(gameInfo.getAttributes().getGameNameCn());
        simpleGame.setTagsInfos(gameInfo.getAttributes().getTags());
        return simpleGame;
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }
}
