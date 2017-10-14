package com.appgame.differ.module.dynamic.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.game.SimpleGame;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.base.other.SimpleTextWatcher;
import com.appgame.differ.module.dynamic.SelectGameActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.widget.ShapeImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.bean.ImageItem;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yukunlin on 17/5/4.
 */

public class PostDynamicAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private final int TYPE_TEXT = 0;
    private final int TYPE_PICTURE = 1;
    private final int TYPE_GAME = 2;
    private int maxImgCount = 9;
    private SimpleGame simpleGame;
    private String dynamicContent;
    private String postType;
    private String shareContent;
    private List<ImageItem> images = new ArrayList<>();
    private List<ImageItem> imageList = new ArrayList<>();

    public List<ImageItem> getImageList() {
        return imageList;
    }

    public SimpleGame getSimpleGame() {
        return simpleGame;
    }

    public String getDynamicContent() {
        return dynamicContent;
    }

    public void setSimpleGame(SimpleGame simpleGame) {
        this.simpleGame = simpleGame;
        List<TagsInfo> tagsInfos = new ArrayList<>();
        if (simpleGame.getTagsInfos().size() > 3) {
            tagsInfos.add(simpleGame.getTagsInfos().get(0));
            tagsInfos.add(simpleGame.getTagsInfos().get(1));
            tagsInfos.add(simpleGame.getTagsInfos().get(2));
            simpleGame.getTagsInfos().clear();
            simpleGame.getTagsInfos().addAll(tagsInfos);
        }
    }

    public PostDynamicAdapter(Context mContext, String postType, String shareContent) {
        this.mContext = mContext;
        this.postType = postType;
        this.shareContent = shareContent;
        if (postType.equals("share")) {
            dynamicContent = shareContent;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_post_text, parent, false);
            return new TextViewHolder(itemView);
        } else if (viewType == TYPE_PICTURE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_post_picture, parent, false);
            return new PictureViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_post_game, parent, false);
            return new GameViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PictureViewHolder) {
            PictureViewHolder viewHolder = (PictureViewHolder) holder;
            viewHolder.itemView.setVisibility(postType.equals("post") ? View.VISIBLE : View.GONE);

            ImagePickerAdapter adapter = new ImagePickerAdapter(mContext, images, maxImgCount);
            int width = CommonUtil.getPhoneWidth(mContext) - CommonUtil.dip2px(mContext, 35);
            int spanCount = width / (CommonUtil.dip2px(mContext, 77));
            viewHolder.mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, spanCount));
            viewHolder.mRecyclerView.setHasFixedSize(true);
            viewHolder.mRecyclerView.setAdapter(adapter);
            adapter.setImages(images);
            imageList = adapter.getImages();

        } else if (holder instanceof GameViewHolder) {
            GameViewHolder viewHolder = (GameViewHolder) holder;
            viewHolder.itemView.setVisibility(postType.equals("post") ? View.VISIBLE : View.GONE);

            viewHolder.mGameTitle.setText(simpleGame.getGameNameCn());
            Glide.with(mContext).load(simpleGame.getGameIcon())
                    .placeholder(R.color.default_image_color).error(R.color.default_image_color)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(viewHolder.mGameIcon);
            viewHolder.mTagFlowLayout.setAdapter(new TagAdapter<TagsInfo>(simpleGame.getTagsInfos()) {
                @Override
                public View getView(FlowLayout parent, int position, TagsInfo tagsInfo) {
                    TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_dynamic_tag, parent, false);
                    textView.setText(tagsInfo.getName());
                    return textView;
                }
            });
            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, SelectGameActivity.class);
                mContext.startActivity(intent);
            });
        } else if (holder instanceof TextViewHolder) {
            TextViewHolder viewHolder = (TextViewHolder) holder;

            if (postType.equals("share")) {
                if (shareContent.contains("@#Forwarding#")) {
                    shareContent = shareContent.replace("@#Forwarding#", "");
                }
                viewHolder.mContent.setHint(" 来说点什么吧...");
                viewHolder.mContent.setText(Html.fromHtml(shareContent));
            } else {
                viewHolder.mContent.setHint(" 来聊聊这个游戏吧");
            }

            viewHolder.mContent.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    super.beforeTextChanged(s, start, count, after);

                }

                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    dynamicContent = postType.equals("share") ? s.toString().trim().replace(Html.fromHtml(shareContent), "") + shareContent : s.toString().trim();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TEXT;
        } else if (position == 1) {
            return TYPE_PICTURE;
        } else {
            return TYPE_GAME;
        }
    }

    public void setImages(ArrayList<ImageItem> images) {
        this.images = images;
    }

    class TextViewHolder extends BaseViewHolder {
        EditText mContent;

        public TextViewHolder(View itemView) {
            super(itemView, mContext, false);
            mContent = $(R.id.content);
        }
    }

    class PictureViewHolder extends BaseViewHolder {
        RecyclerView mRecyclerView;

        public PictureViewHolder(View itemView) {
            super(itemView, mContext, false);
            mRecyclerView = $(R.id.recycler_view);
        }
    }

    class GameViewHolder extends BaseViewHolder {
        ShapeImageView mGameIcon;
        TextView mGameTitle;
        TagFlowLayout mTagFlowLayout;

        public GameViewHolder(View itemView) {
            super(itemView, mContext, false);
            mGameIcon = $(R.id.game_icon);
            mGameTitle = $(R.id.game_title);
            mTagFlowLayout = $(R.id.tag_flow_layout);
        }
    }
}
