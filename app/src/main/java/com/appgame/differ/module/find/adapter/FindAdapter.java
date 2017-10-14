package com.appgame.differ.module.find.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.FindTypeArticle;
import com.appgame.differ.bean.find.FindTypeExternal;
import com.appgame.differ.bean.find.FindTypeGame;
import com.appgame.differ.bean.find.FindTypeTopic;
import com.appgame.differ.bean.find.FindTypeVideo;
import com.appgame.differ.bean.find.NavigationInfo;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.base.other.WebActivity;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.daily.DailyDetailActivity;
import com.appgame.differ.module.find.DiscoverNavActivity;
import com.appgame.differ.module.find.VideoPlayActivity;
import com.appgame.differ.module.topic.TopicDetailCardActivity;
import com.appgame.differ.module.topic.TopicDetailListActivity;
import com.appgame.differ.module.topic.TopicListActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.adapter.LoadMoreAdapter;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.FixPtrFrameLayout;
import com.appgame.differ.widget.JCVideoView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.banner.BannerEntity;
import com.appgame.differ.widget.banner.BannerView;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;




/**
 * Created by lzx on 2017/5/16.
 * 386707112@qq.com
 */

public class FindAdapter extends LoadMoreAdapter<FindListInfo> {

    private static final int TYPE_BANNER = 0;
    private static final int TYPE_ONE_IMAGE = 1;
    private static final int TYPE_THREE_IMAGE = 2;
    private static final int TYPE_VIDEO = 3;
    private static final int TYPE_RESERVE = 4;
    private static final int TYPE_BIG_IMAGE = 5;
    private static final int TYPE_SMALL_VIDEO = 6;
    //private FindInfo findInfo;
    private LinearLayout.LayoutParams params;
    private RxPermissions mRxPermissions;
    private FixPtrFrameLayout mPtrClassicFrameLayout;

    private List<BannerEntity> mBannerEntities;
    private List<NavigationInfo> mNavigationInfos;

    public FindAdapter(Context context, RxPermissions permissions, FixPtrFrameLayout mPtrClassicFrameLayout) {
        super(context);
        this.mPtrClassicFrameLayout = mPtrClassicFrameLayout;
        mRxPermissions = permissions;
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        params.topMargin = CommonUtil.dip2px(mContext, 9);
    }

    public void setBannerEntities(List<BannerEntity> bannerEntities) {
        mBannerEntities = bannerEntities;
    }

    public void setNavigationInfos(List<NavigationInfo> navigationInfos) {
        mNavigationInfos = navigationInfos;
    }

    private boolean isNull() {
        return mBannerEntities == null || mNavigationInfos == null;
    }

    public void clear() {
        if (mBannerEntities != null)
            mBannerEntities.clear();
        if (mNavigationInfos != null)
            mNavigationInfos.clear();
        mDataList.clear();
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_BANNER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_banner, parent, false);
            return new BannerHolder(view);
        } else if (viewType == TYPE_ONE_IMAGE || viewType == TYPE_SMALL_VIDEO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_one_image, parent, false);
            return new GameOneHolder(view);
        } else if (viewType == TYPE_THREE_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_three_image, parent, false);
            return new GameThreeHolder(view);
        } else if (viewType == TYPE_VIDEO || viewType == TYPE_BIG_IMAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_video, parent, false);
            return new GameVideoHolder(view);
        } else if (viewType == TYPE_RESERVE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_reserve, parent, false);
            return new ReserveHolder(view);
        }
        return null;
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        if (viewHolder instanceof BannerHolder) {
            BannerHolder holder = (BannerHolder) viewHolder;
            if (!isNull()) {
                mPtrClassicFrameLayout.setViewPager(holder.mBannerView.getViewPager());
                holder.mBannerView.build(mBannerEntities);
                holder.mBannerView.setOnItemClickListener((position1, entity) -> {
                    Intent intent;
                    switch (entity.target) {
                        case "game":
                            IntentHelper.startGameDetailActivity((MainActivity) mContext, null, entity.targetId);
                            break;
                        case "external":
                            if (entity.url.equals(AppConstants.ACTIVITY_URL)) {
                                String url;
                                if (CommonUtil.isLogin()) {
                                    url = entity.url + "?t=" + System.currentTimeMillis() + "&type=app";
                                } else {
                                    url = entity.url + "?t=" + System.currentTimeMillis() + "&type=app";
                                }
                                WebActivity.launch(mContext, "", url, CommonUtil.isLogin(), true);
                            } else {
                                WebActivity.launch(mContext, entity.title, entity.url);
                            }
                            break;
                        case "article":
                            intent = new Intent(mContext, DailyDetailActivity.class);
                            intent.putExtra("dailyId", entity.targetId);
                            mContext.startActivity(intent);
                            break;
                        case "topic":
                            intent = new Intent(mContext, TopicDetailCardActivity.class);
                            intent.putExtra("topicId", entity.targetId);
                            mContext.startActivity(intent);
                            break;
                    }
                });
                holder.mNavigationLayout.setVisibility(mNavigationInfos.size() == 0 ? View.GONE : View.VISIBLE);
                holder.mNavigationLayout.removeAllViews();
                for (NavigationInfo info : mNavigationInfos) {
                    View view = View.inflate(mContext, R.layout.layout_find_nav, null);
                    RelativeLayout tabLayout = (RelativeLayout) view.findViewById(R.id.tab_layout);
                    tabLayout.setLayoutParams(params);
                    ImageView icon = (ImageView) view.findViewById(R.id.tab_image);
                    TextView text = (TextView) view.findViewById(R.id.tab_text);
                    Glide.with(mContext).load(info.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(icon);
                    text.setText(info.getTitle());
                    tabLayout.setOnClickListener(v -> {
                        switch (info.getType()) {
                            case "article":
                                mContext.startActivity(DiscoverNavActivity.launch(mContext, info.getId(), info.getTitle(), "article"));
                                break;
                            case "game":
                                mContext.startActivity(DiscoverNavActivity.launch(mContext, info.getId(), info.getTitle(), "game"));
                                break;
                            case "topic":
                                String layout = info.getLayout();
                                Intent intent;
                                if (layout.equals("table")) {
                                    intent = new Intent(mContext, TopicDetailListActivity.class);
                                } else {
                                    intent = new Intent(mContext, TopicDetailCardActivity.class);
                                }
                                intent.putExtra("topicId", info.getTopicId());
                                mContext.startActivity(intent);
                                break;
                            case "external":
                                WebActivity.launch(mContext, info.getTitle(), info.getUrl());
                                break;
                            case "list":
                                intent = new Intent(mContext, TopicListActivity.class);
                                intent.putExtra("id", info.getId());
                                intent.putExtra("type", "list");
                                mContext.startActivity(intent);
                                break;
                        }
                    });
                    holder.mNavigationLayout.addView(view);
                }
            }
        } else if (viewHolder instanceof GameOneHolder) {
            GameOneHolder holder = (GameOneHolder) viewHolder;
            if (!isNull()) {
                List<FindListInfo> findListInfos = mDataList;
                position = position - 1;
                FindListInfo findListInfo = findListInfos.get(position);
                String target = findListInfo.target;
                String type;
                if (target.equals("article") || target.equals("external") || target.equals("game")) {
                    type = "one_small";
                } else {
                    type = "one";
                }
                initAllItemUI(findListInfo, holder, null, type);
                OnItemClick(holder.mDescLayout, target, findListInfo, holder.mGameLayout, target.equals("video") ? "one" : "");
            }
        } else if (viewHolder instanceof GameThreeHolder) {
            GameThreeHolder holder = (GameThreeHolder) viewHolder;
            if (!isNull()) {
                List<FindListInfo> findListInfos = mDataList;
                position = position - 1;
                FindListInfo findListInfo = findListInfos.get(position);
                String target = findListInfo.target;
                initAllItemUI(findListInfo, null, holder, "more");  // target.equals("external") ? "more" : "three"
                OnItemClick(holder.mDescLayout, target, findListInfo, holder.mGameLayout, "");
            }
        } else if (viewHolder instanceof GameVideoHolder) {
            GameVideoHolder holder = (GameVideoHolder) viewHolder;
            List<FindListInfo> findListInfos = mDataList;
            position = position - 1;
            FindListInfo findListInfo = findListInfos.get(position);
            String target = findListInfo.target;
            GameInfo gameInfo = null;
            String gameTitle = "";
            String videoUrl = "";
            String cover = "";
            if (target.equals("video")) {
                FindTypeVideo video = findListInfo.typeVideo;
                gameTitle = video.getName();
                videoUrl = video.getUrl();
                cover = video.getPics().size() > 0 ? video.getPics().get(0) : "";
                gameInfo = video.getGame();
            } else if (target.equals("article")) {
                FindTypeArticle article = findListInfo.typeArticle;
                gameTitle = article.getName();
                cover = article.getPics().size() > 0 ? article.getPics().get(0) : "";
                gameInfo = article.getGame();
            } else if (target.equals("game")) {
                FindTypeGame typeGame = findListInfo.typeGame;
                gameTitle = typeGame.getName();
                cover = typeGame.getPics().size() > 0 ? typeGame.getPics().get(0) : "";
                gameInfo = typeGame.getGameInfo();
            } else if (target.equals("external")) {
                FindTypeExternal external = findListInfo.typeExternal;
                gameTitle = external.getName();
                cover = external.getPics().size() > 0 ? external.getPics().get(0) : "";
                gameInfo = external.getGame();
            } else if (target.equals("topic")) {
                FindTypeTopic topic = findListInfo.typeTopic;

                if (topic != null) {
                    gameTitle = topic.getName();
                    cover = topic.getPics().size() > 0 ? topic.getPics().get(0) : "";
                    gameInfo = topic.getGameInfo();
                }
            }

            initVideoItemUI(holder, target, gameTitle, videoUrl, cover, gameInfo);
            OnItemClick(holder.mDescLayout, target, findListInfo, holder.mGameLayout, "");

        } else if (viewHolder instanceof ReserveHolder) {
//            ReserveHolder holder = (ReserveHolder) viewHolder;
//            ReserveAdapter reserveAdapter = new ReserveAdapter(mContext);
//            holder.mRecyclerView.setAdapter(reserveAdapter);
        }
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        super.onViewRecycled(holder);

    }

    /**
     * 视频item UI
     */
    private void initVideoItemUI(GameVideoHolder holder, String target, String gameTitle, String videoUrl, String cover, GameInfo gameInfo) {
        if (target.equals("video")) {
            holder.mGameCover.setVisibility(View.GONE);
            holder.mJCVideoPlayer.setVisibility(View.VISIBLE);
            holder.mJCVideoPlayer.setUp(videoUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
            Glide.with(mContext).load(cover).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mJCVideoPlayer.thumbImageView);
        } else {
            holder.mGameCover.setVisibility(View.VISIBLE);
            holder.mJCVideoPlayer.setVisibility(View.GONE);
        }
        holder.mGameTitle.setText(gameTitle);
        Glide.with(mContext).load(cover).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
        if (gameInfo != null) {
            String gameId = gameInfo.getGameId();
            holder.mGameLayout.setVisibility(target.equals("topic") ? View.GONE : View.VISIBLE);
            Glide.with(mContext).load(gameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
            holder.mGameName.setText(gameInfo.getGameNameCn());
            String gameDesc = gameInfo.getCategory().size() > 0 ? gameInfo.getCategory().get(0).getName() : "";
            String gameSize = gameInfo.getDownloadInfo() != null ? gameInfo.getDownloadInfo().downloadGameSize + "M" : "";
            holder.mGameSize.setText(gameDesc + " | " + gameSize);
            holder.mGameIcon.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameId));
            holder.mGameName.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameId));
            if (gameInfo.getDownloadInfo() != null) {
                initDownloadGame(gameInfo.getDownloadInfo(), holder.mProgressBar);
            } else {
                holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            }
        } else {
            holder.mGameLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化item UI
     */
    private void initAllItemUI(FindListInfo findListInfo, GameOneHolder oneHolder, GameThreeHolder threeHolder, String type) {
        String target = findListInfo.target;
        switch (target) {
            case "game":
                FindTypeGame typeGame = findListInfo.typeGame;
                if (type.equals("one_small")) {
                    oneHolder.mIconPlay.setVisibility(View.GONE);
                    oneHolder.mGameLayout.setVisibility(View.VISIBLE);
                    initOneImageUI(oneHolder, typeGame.getPics().size() > 0 ? typeGame.getPics().get(0) : "", typeGame.getName(), typeGame.getGameInfo());
                } else {
                    initThreeImageUI(threeHolder, typeGame.getPics(), typeGame.getName(), typeGame.getGameInfo());
                }
                break;
            case "video":
                FindTypeVideo typeVideo = findListInfo.typeVideo;
                oneHolder.mIconPlay.setVisibility(View.VISIBLE);
                oneHolder.mGameLayout.setVisibility(View.VISIBLE);
                initOneImageUI(oneHolder, typeVideo.getPics().size() > 0 ? typeVideo.getPics().get(0) : "", typeVideo.getName(), typeVideo.getGame());
                break;
            case "article":
                FindTypeArticle article = findListInfo.typeArticle;
                if (type.equals("one_small")) {
                    oneHolder.mIconPlay.setVisibility(View.GONE);
                    oneHolder.mGameLayout.setVisibility(View.VISIBLE);
                    initOneImageUI(oneHolder, article.getPics().size() > 0 ? article.getPics().get(0) : "", article.getName(), article.getGame());
                } else if (type.equals("more")) {
                    initThreeImageUI(threeHolder, article.getPics(), article.getName(), article.getGame());
                }
                break;
            case "external":  //外链
                FindTypeExternal external = findListInfo.typeExternal;
                if (type.equals("one_small")) {
                    oneHolder.mIconPlay.setVisibility(View.GONE);
                    oneHolder.mGameLayout.setVisibility(View.VISIBLE);
                    initOneImageUI(oneHolder, external.getPics().size() > 0 ? external.getPics().get(0) : "", external.getName(), external.getGame());
                } else if (type.equals("more")) {
                    initThreeImageUI(threeHolder, external.getPics(), external.getName(), external.getGame());
                }
                break;
            case "topic":
                FindTypeTopic topic = findListInfo.typeTopic;
                oneHolder.mGameLayout.setVisibility(View.GONE);
                LogUtil.i("pic = " + topic.getPics().get(0) + " topic.getName() = " + topic.getName());
                initOneImageUI(oneHolder, topic.getPics().size() > 0 ? topic.getPics().get(0) : "", topic.getName(), topic.getGameInfo());
                break;
        }
    }

    /**
     * item 点击处理
     */
    private void OnItemClick(View view, String target, FindListInfo findListInfo, RelativeLayout mGameLayout, String type) {
        mGameLayout.setOnClickListener(v -> {
            String gameId = "";
            if (target.equals("game")) {
                gameId = findListInfo.typeGame.getGameId();
            } else if (target.equals("article")) {
                gameId = findListInfo.typeArticle.getGameId();
            } else if (target.equals("external")) {
                gameId = findListInfo.typeExternal.getGameId();
            } else if (target.equals("video")) {
                gameId = findListInfo.typeVideo.getGameId();
            }
            if (!TextUtils.isEmpty(gameId)) {
                IntentHelper.startGameDetailActivity((MainActivity) mContext, null, gameId);
            }
        });
        view.setOnClickListener(v -> {
            Intent intent;
            switch (target) {
                case "game":
                    FindTypeGame typeGame = findListInfo.typeGame;
                    IntentHelper.startGameDetailActivity((MainActivity) mContext, null, typeGame.getGameId());
                    break;
                case "article":
                    FindTypeArticle article = findListInfo.typeArticle;
                    intent = new Intent(mContext, DailyDetailActivity.class);
                    intent.putExtra("dailyId", article.getArtId());
                    intent.putExtra("from", "find");
                    intent.putExtra("gameInfo", article.getGame());
                    mContext.startActivity(intent);
                    break;
                case "external":  //外链
                    FindTypeExternal external = findListInfo.typeExternal;
                    WebActivity.launch(mContext, external.getName(), external.getUrl());
                    break;
                case "video":
                    if (type.equals("one")) {
                        FindTypeVideo video = findListInfo.typeVideo;
                        if (!TextUtils.isEmpty(video.getUrl()) && !TextUtils.isEmpty(video.getName())) {
                            intent = new Intent(mContext, VideoPlayActivity.class);
                            intent.putExtra("url", video.getUrl());
                            intent.putExtra("title", video.getName());
                            intent.putExtra("cover", video.getPics().size() > 0 ? video.getPics().get(0) : "");
                            mContext.startActivity(intent);
                        }
                    }
                    break;
                case "topic":
                    FindTypeTopic topic = findListInfo.typeTopic;
                    if (topic != null) {
                        String layout = topic.getTopicInfo().getLayout();
                        if (layout.equals("table")) {
                            intent = new Intent(mContext, TopicDetailListActivity.class);
                        } else {
                            intent = new Intent(mContext, TopicDetailCardActivity.class);
                        }
                        intent.putExtra("topicId", topic.getTopicInfo().getTopicId());
                        mContext.startActivity(intent);
                    }
                    break;
            }
        });
    }

    /**
     * 1个图片
     */
    private void initOneImageUI(GameOneHolder holder, String pic, String gameTitle, GameInfo gameInfo) {
        Glide.with(mContext).load(pic).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
        holder.mGameTitle.setText(gameTitle);
        if (gameInfo != null) {
            holder.mGameLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(gameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
            holder.mGameName.setText(gameInfo.getGameNameCn());
            String gameDesc = gameInfo.getCategory().size() > 0 ? gameInfo.getCategory().get(0).getName() : "";
            String gameSize = gameInfo.getDownloadInfo() != null ? gameInfo.getDownloadInfo().downloadGameSize + "M" : "";
            holder.mGameSize.setText(gameDesc + " | " + gameSize);
            holder.mGameIcon.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameInfo.getGameId()));
            holder.mGameName.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameInfo.getGameId()));
            if (gameInfo.getDownloadInfo() != null) {
                initDownloadGame(gameInfo.getDownloadInfo(), holder.mProgressBar);
            } else {
                holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            }
        } else {
            holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            holder.mGameLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 3个图片
     */
    private void initThreeImageUI(GameThreeHolder holder, List<String> pics, String gameTitle, GameInfo gameInfo) {
        ImageView[] imageViews = new ImageView[]{holder.mImageOne, holder.mImageTwo, holder.mImageThree};
        for (int i = 0; i < pics.size(); i++) {
            if (i < 3) {
                Glide.with(mContext).load(pics.get(i)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViews[i]);
            }
        }
        holder.mGameTitle.setText(gameTitle);
        if (gameInfo != null) {
            holder.mGameLayout.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(gameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
            holder.mGameName.setText(gameInfo.getGameNameCn());
            String gameDesc = gameInfo.getCategory().size() > 0 ? gameInfo.getCategory().get(0).getName() : "";
            holder.mGameSize.setText(gameDesc + " | " + gameInfo.getDownloadInfo().downloadGameSize + "M");
            holder.mGameIcon.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameInfo.getGameId()));
            holder.mGameName.setOnClickListener(v -> IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameIcon, gameInfo.getGameId()));
            if (gameInfo.getDownloadInfo() != null) {
                initDownloadGame(gameInfo.getDownloadInfo(), holder.mProgressBar);
            } else {
                holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            }
        } else {
            holder.mProgressBar.setProgressUI(0, DownLoadFlag.download_normal);
            holder.mGameLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 刷新adapter(除去banner)
     */
    public void refreshAdapter() {
        for (int i = 0; i < mDataList.size(); i++) {
            FindListInfo info = mDataList.get(i);
            if (!info.target.equals("topic")) {
                notifyItemChanged(i);
            }
        }
    }

    /**
     * 下载
     */
    private void initDownloadGame(DownloadInfo downloadInfo, DownloadProgressButton mProgressBar) {

        mProgressBar.setTag(downloadInfo.downloadUrl);

        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl); //下载路径
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        downloadManager.initDownloadStatus(downloadInfo.downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, mProgressBar::setProgressUI);
        downloadManager.addUpdater(downloadInfo.downloadUrl, (task, percent, status) -> {
            if (mProgressBar.getTag().equals(downloadInfo.downloadUrl)) {
                mProgressBar.setProgressUI(percent, status);
            }
        });

        RxView.clicks(mProgressBar).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            downloadManager.downloadClick(mContext, aBoolean, mProgressBar.getCurrentText(), downloadInfo, downloadId, downloadPath);
        }));

    }

    /**
     * 自动下载
     */
    public void startAllAutoTasks() {
        if (!isNull()) {
            //  List<FindListInfo> findListInfos = findInfo.mFindListInfos;
            for (FindListInfo info : mDataList) {
                String target = info.target;
                GameInfo gameInfo = null;
                if (target.equals("game")) {
                    FindTypeGame typeGame = info.typeGame;
                    gameInfo = typeGame.getGameInfo();
                } else if (target.equals("article")) {
                    FindTypeArticle typeArticle = info.typeArticle;
                    gameInfo = typeArticle.getGame();
                } else if (target.equals("external")) {
                    FindTypeExternal typeExternal = info.typeExternal;
                    gameInfo = typeExternal.getGame();
                } else if (target.equals("video")) {
                    FindTypeVideo typeVideo = info.typeVideo;
                    gameInfo = typeVideo.getGame();
                }
                if (gameInfo != null) {
                    startAllAutoTasksImpl(gameInfo);
                }
            }
        }
    }

    private void startAllAutoTasksImpl(GameInfo gameInfo) {
        if (gameInfo != null) {
            DownloadInfo downloadInfo = gameInfo.getDownloadInfo();
            DownloadManager.getImpl().startAllAutoTasks(downloadInfo);
        }
    }

    @Override
    protected int getViewType(int position) {
        if (!isNull()) {
            if (position == 0) {
                return TYPE_BANNER;
            } else {
                position = position - 1;
                FindListInfo listInfo = mDataList.get(position);
                String target = listInfo.target;

                String layout = "";
                if (target.equals("game")) {
                    layout = listInfo.typeGame.getLayout();
                } else if (target.equals("article")) {
                    layout = listInfo.typeArticle.getLayout();
                } else if (target.equals("video")) {
                    layout = listInfo.typeVideo.getLayout();
                } else if (target.equals("external")) {
                    layout = listInfo.typeExternal.getLayout();
                }

                if (target.equals("game") || target.equals("article")) {
                    if (layout.equals("one_small")) {
                        return TYPE_ONE_IMAGE;
                    } else if (layout.equals("one")) {
                        return TYPE_BIG_IMAGE;
                    } else {
                        return TYPE_THREE_IMAGE;
                    }
                }
                if (target.equals("video")) {
                    if (layout.equals("one")) {
                        return TYPE_ONE_IMAGE;
                    } else {
                        return TYPE_VIDEO;
                    }
                }
                if (target.equals("external")) {
                    if (layout.equals("one_small")) {
                        return TYPE_ONE_IMAGE;
                    } else if (layout.equals("one")) {
                        return TYPE_VIDEO;
                    } else if (layout.equals("more")) {
                        return TYPE_THREE_IMAGE;
                    }
                }
                if (target.equals("topic")) {
                    return TYPE_BIG_IMAGE;
                }
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (showLoadMore) {
            return !isNull() ? mDataList.size() + 2 : 0;
        } else {
            return !isNull() ? mDataList.size() + 1 : 0;
        }
    }

    private class BannerHolder extends BaseViewHolder {
        BannerView mBannerView;
        LinearLayout mNavigationLayout;

        public BannerHolder(View itemView) {
            super(itemView, mContext, false);
            mBannerView = $(R.id.banner_view);
            mNavigationLayout = $(R.id.navigation_layout);
        }
    }

    private class GameOneHolder extends BaseViewHolder {
        ImageView mGameCover;
        TextView mGameTitle, mGameName, mGameSize;
        ShapeImageView mGameIcon;
        DownloadProgressButton mProgressBar;
        RelativeLayout mGameLayout, mDescLayout;
        ImageView mIconPlay;

        public GameOneHolder(View itemView) {
            super(itemView, mContext, false);
            mGameCover = $(R.id.game_cover);
            mGameTitle = $(R.id.game_title);
            mGameName = $(R.id.game_name);
            mGameSize = $(R.id.game_size);
            mGameIcon = $(R.id.game_icon);
            mProgressBar = $(R.id.pro_bar);
            mGameLayout = $(R.id.game_layout);
            mDescLayout = $(R.id.desc_layout);
            mIconPlay = $(R.id.icon_play);
        }
    }

    private class GameThreeHolder extends BaseViewHolder {
        TextView mGameTitle, mGameName, mGameSize;
        ImageView mImageOne, mImageTwo, mImageThree;
        ShapeImageView mGameIcon;
        DownloadProgressButton mProgressBar;
        RelativeLayout mGameLayout, mDescLayout;

        public GameThreeHolder(View itemView) {
            super(itemView, mContext, false);
            mImageOne = $(R.id.image_one);
            mImageTwo = $(R.id.image_two);
            mImageThree = $(R.id.image_three);
            mGameTitle = $(R.id.game_title);
            mGameName = $(R.id.game_name);
            mGameSize = $(R.id.game_size);
            mGameIcon = $(R.id.game_icon);
            mProgressBar = $(R.id.pro_bar);
            mGameLayout = $(R.id.game_layout);
            mDescLayout = $(R.id.desc_layout);
        }
    }

    private class GameVideoHolder extends BaseViewHolder {
        TextView mGameTitle, mGameName, mGameSize;
        JCVideoView mJCVideoPlayer;
        ShapeImageView mGameIcon;
        DownloadProgressButton mProgressBar;
        RelativeLayout mGameLayout;
        ImageView mGameCover;
        LinearLayout mDescLayout;

        public GameVideoHolder(View itemView) {
            super(itemView, mContext, false);
            mJCVideoPlayer = $(R.id.video_player);
            mGameTitle = $(R.id.game_title);
            mGameName = $(R.id.game_name);
            mGameSize = $(R.id.game_size);
            mGameIcon = $(R.id.game_icon);
            mProgressBar = $(R.id.pro_bar);
            mGameLayout = $(R.id.game_layout);
            mGameCover = $(R.id.game_cover);
            mDescLayout = $(R.id.desc_layout);
        }
    }

    private class ReserveHolder extends BaseViewHolder {
        RecyclerView mRecyclerView;

        public ReserveHolder(View itemView) {
            super(itemView, mContext, false);
            mRecyclerView = $(R.id.recycler_view);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setFocusable(false);
        }
    }


}
