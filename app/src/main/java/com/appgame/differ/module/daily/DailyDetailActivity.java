package com.appgame.differ.module.daily;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.base.other.WebActivity;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.daily.adapter.DailyDetailCommentAdapter;
import com.appgame.differ.module.daily.contract.DailyDetailContract;
import com.appgame.differ.module.daily.presenter.DailyDetailPresenter;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.KeyboardControlManager;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.UserNameView;
import com.appgame.differ.widget.dialog.DynamicDialog;
import com.appgame.differ.widget.dialog.ShareDialog;
import com.appgame.differ.widget.dialog.UnFollowDialog;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.appgame.differ.widget.nineoldandroids.animation.Animator;
import com.appgame.differ.widget.nineoldandroids.animation.AnimatorListenerAdapter;
import com.appgame.differ.widget.nineoldandroids.animation.ObjectAnimator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.UMShareAPI;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 日报详情
 * Created by lzx on 2017/4/13.
 * 386707112@qq.com
 */
public class DailyDetailActivity extends BaseActivity<DailyDetailContract.Presenter, DailyComment> implements DailyDetailContract.View,
        View.OnClickListener, DynamicDialog.OnReplayListener, DynamicDialog.OnDeleteSuccessListener {


    private TextView mShareBtn;
    private ImageView mDailyCover;
    private ProgressBar mProgressBar;
    private CircleImageView mUserHeader;
    private TextView mBtnFollow, mDetailTitle, mDiscussNum;
    private WebView mWebView;
    private TagFlowLayout mTagFlowLayout;
    private RecyclerView mRecyclerView;
    private EditText mEditComm;
    private NestedScrollView mNestedScrollView;
    private LinearLayout mPostLayout;
    private TextView mBtnSend;
    private RelativeLayout mBottomLayout;
    private TextView mBtnComment;
    private ImageView mBtnBack;
    private UserNameView mUserNameView;

    private RelativeLayout mGameLayout;
    private ShapeImageView mGameIcon;
    private TextView mGameName;
    private TagFlowLayout mFlowLayout;
    private DownloadProgressButton mDownloadBar;
    private View mLineBar;

    private ObjectAnimator animatorProgress, animatorDismiss;
    private WebSettings webSettings;
    private boolean isAnimStart = false;
    private int currentProgress;
    private String sendType = "", commentId;
    public String reply_id;
    public String reply_user_id;
    public String userName;

    private DailyInfo mDailyInfo;
    private String dailyId;
    private DailyDetailCommentAdapter mCommentAdapter;

    private UnFollowDialog dialog;

    public String from;
    private GameInfo mGameInfo;
    private RxPermissions mRxPermissions;

    private DynamicDialog dynamicDialog;
    boolean blockLoadingNetworkImage = true;
    private DownloadManager mDownloadManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_daily_detail;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DailyDetailPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mLineBar = findViewById(R.id.line4);
        mDailyCover = (ImageView) findViewById(R.id.daily_cover);
        mShareBtn = (TextView) findViewById(R.id.share_btn);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollview);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mUserHeader = (CircleImageView) findViewById(R.id.user_header);
        mUserNameView = (UserNameView) findViewById(R.id.user_name_layout);
        mBtnFollow = (TextView) findViewById(R.id.btn_follow);
        mDetailTitle = (TextView) findViewById(R.id.detail_title);
        mDiscussNum = (TextView) findViewById(R.id.discuss_num);
        mWebView = (WebView) findViewById(R.id.web_view);
        mTagFlowLayout = (TagFlowLayout) findViewById(R.id.flow_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEditComm = (EditText) findViewById(R.id.edit_comm);
        mPostLayout = (LinearLayout) findViewById(R.id.post_layout);
        mBtnSend = (TextView) findViewById(R.id.btn_send);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mBtnComment = (TextView) findViewById(R.id.btn_comment);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);

        mGameLayout = (RelativeLayout) findViewById(R.id.game_layout);
        mGameIcon = (ShapeImageView) findViewById(R.id.game_icon);
        mGameName = (TextView) findViewById(R.id.game_name);
        mFlowLayout = (TagFlowLayout) findViewById(R.id.tag_flow_layout);
        mDownloadBar = (DownloadProgressButton) findViewById(R.id.pro_bar);

        mLineBar.setAlpha(0);

        mWebView.setFocusable(false);
        mBtnBack.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        SystemBarHelper.immersiveStatusBar(this);
        dialog = new UnFollowDialog(this);
        mRxPermissions = new RxPermissions(this);
        mDownloadManager = new DownloadManager();

        mAppBarLayout.setAlpha(0);

        dailyId = getIntent().getStringExtra("dailyId");
        from = getIntent().getStringExtra("from");
        mGameInfo = getIntent().getParcelableExtra("gameInfo");

        initRecyclerView();

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            int alpha = 0;
            float scale = 0;
            int height = CommonUtil.getPhoneHeight(DailyDetailActivity.this);

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= height) {
                    scale = (float) scrollY / height;
                    alpha = (int) (255 * scale);
                    mAppBarLayout.setAlpha(scale * 1.5f);
                    mLineBar.setAlpha(scale * 1.5f);
                    mBtnBack.setAlpha(1 - scale * 1.5f);
                } else {
                    if (alpha < 255) {
                        alpha = 255;
                    }
                }
            }
        });

        //游戏文章
        if (!TextUtils.isEmpty(from) && from.equals("find")) {
            mTagFlowLayout.setVisibility(View.GONE);
            mDiscussNum.setVisibility(View.GONE);
            mBtnComment.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mGameLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.line3).setVisibility(View.GONE);
            findViewById(R.id.line2).setVisibility(View.GONE);
            Glide.with(this).load(mGameInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mGameIcon);
            mGameName.setText(mGameInfo.getGameNameCn());
            List<TagsInfo> tagsInfos = new ArrayList<>();
            if (mGameInfo.getTags().size() > 3) {
                for (int i = 0; i < 3; i++) {
                    tagsInfos.add(mGameInfo.getTags().get(i));
                }
            } else {
                tagsInfos.addAll(mGameInfo.getTags());
            }
            mFlowLayout.setAdapter(new TagAdapter<TagsInfo>(tagsInfos) {
                @Override
                public View getView(FlowLayout parent, int position, TagsInfo tagsInfo) {
                    TextView textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dynamic_tag, parent, false);
                    textView.setText(tagsInfo.getName());
                    return textView;
                }
            });
            initDownload(mGameInfo.getDownloadInfo());
            mDownloadBar.setOnClickListener(this);
            mGameIcon.setOnClickListener(this);
            mGameName.setOnClickListener(this);
            mGameLayout.setOnClickListener(this);
        }

        mCommentAdapter.setOnItemLongClickListener((comment, dynamicId, position) -> {
            dynamicDialog = DynamicDialog.newInstance(comment.getContent(), comment.getComId(), comment, position, "comment");
            dynamicDialog.show(getSupportFragmentManager(), "DynamicDialog");
        });

        mPresenter.requestDailyDetail(dailyId);

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        if (mGameInfo != null) {
                            DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
                            mDownloadManager.startAllAutoTasks(downloadInfo);
                        }
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
            } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                AppInfoManager.getImpl().updateAppInfo();
                if (mGameInfo != null) {
                    DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
                    boolean hasApp = AppInfoManager.getImpl().hasPkg(downloadInfo.downloadPackageName);
                    if (hasApp) {
                        mDownloadBar.setProgressUI(100, DownLoadFlag.has_app);
                    }
                }
            }
        });
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mCommentAdapter = new DailyDetailCommentAdapter(this);
        mRecyclerView.setAdapter(mCommentAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_layout:
                if (mGameLayout.getVisibility() != View.VISIBLE) {
                    sendType = "comment";
                    CommonUtil.showKeyboard(mEditComm);
                }
                break;
            case R.id.user_header:
            case R.id.user_name_layout:
                IntentHelper.startPersionActivity(DailyDetailActivity.this, mUserHeader, mDailyInfo.getUser().getUserId());
                break;
            case R.id.btn_follow:
                if (mDailyInfo.getUser().isFollowed()) {
                    dialog.show();
                    dialog.setListener(() -> mPresenter.followUser(mDailyInfo.getUser().getUserId(), "cancel"));
                } else {
                    mPresenter.followUser(mDailyInfo.getUser().getUserId(), "");
                }
                break;
            case R.id.pro_bar:
                downloadGame();
                break;
            case R.id.game_name:
            case R.id.game_icon:
            case R.id.game_layout:
                if (mGameInfo != null) {
                    Intent intent = new Intent(this, GameDetailsActivity.class);
                    intent.putExtra("game_id", mGameInfo.getGameId());
                    startActivity(intent);
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.share_btn:
                ShareDialog dialog = new ShareDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelable("dailyInfo", mDailyInfo);
                bundle.putBoolean("isShareToDynamic", false);
                bundle.putString("shareType", "article");
                bundle.putString("shareTitle", mDailyInfo.getTitle());
                bundle.putString("shareUrl", "http://differ.appgame.com/article.html?id=" + mDailyInfo.getId());
                dialog.setArguments(bundle);
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(dialog, "ShareDialog");
                ft.commitAllowingStateLoss();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        CommonUtil.HideKeyboard(mEditComm);
        if (msg.equals(AppConstants.NOT_LOGIN)) {
            LoginActivity.launch(this, "DailyDetailActivity");
        } else {
            ToastUtil.showShort(msg);
        }
    }

    @Override
    public void onDailyDetailSuccess(DailyInfo dailyInfo) {
        mDailyInfo = dailyInfo;

        initDailyInfo(dailyInfo);
        initWebView(dailyInfo.getUrl());
        initFlowLayout(dailyInfo);
        initComment();

        mPresenter.requestCommentList("article", dailyInfo.getId(), 0, false);
    }

    @Override
    public void onSubmitCommentSuccess() {
        CommonUtil.HideKeyboard(mEditComm);
        mPresenter.requestCommentList("article", mDailyInfo.getId(), 0, false);
        mNestedScrollView.postDelayed(() -> mNestedScrollView.scrollTo(0, (int) mRecyclerView.getY() + mRecyclerView.getMeasuredHeight()), 500);
    }

    @Override
    public void onRequestCommentSuccess(List<DailyComment> dailyComments) {
        mCommentAdapter.setList(dailyComments);
        mDiscussNum.setText("玩家讨论（" + dailyComments.size() + "）");
    }

    @Override
    public void onRequestTagList(List<TagsInfo> tagsInfos) {
        TagsInfo info = new TagsInfo();
        info.setName("+");
        tagsInfos.add(info);
        mTagFlowLayout.setAdapter(new TagAdapter<TagsInfo>(tagsInfos) {
            @Override
            public View getView(FlowLayout parent, int position, TagsInfo tagsInfo) {
                TextView tv = (TextView) LayoutInflater.from(DailyDetailActivity.this).inflate(R.layout.item_recom_tab, mTagFlowLayout, false);
                if (tagsInfo.getName().equals("+")) {
                    tv.setText("+");
                    tv.setBackgroundResource(R.drawable.item_tag_eva);
                    tv.setTextColor(ContextCompat.getColor(DailyDetailActivity.this, R.color.black_alpha_60));
                    tv.setPadding(CommonUtil.dip2px(DailyDetailActivity.this, 25), CommonUtil.dip2px(DailyDetailActivity.this, 2),
                            CommonUtil.dip2px(DailyDetailActivity.this, 25), CommonUtil.dip2px(DailyDetailActivity.this, 2));

                } else {
                    int num = Integer.parseInt(tagsInfo.getThumbsUp());
                    String tagNum = num > 99 ? "99+" : tagsInfo.getThumbsUp();
                    tv.setText(tagsInfo.getName() + " | " + tagNum);
                    if (tagsInfo.getIsThumb() == 1) {
                        tv.setBackgroundResource(R.drawable.bg_tag_checked_click);
                        tv.setTextColor(ContextCompat.getColor(DailyDetailActivity.this, R.color.btn_normal));
                    } else {
                        tv.setBackgroundResource(R.drawable.bg_tag_normal_click);
                        tv.setTextColor(Color.parseColor("#979797"));
                    }
                }
                return tv;
            }
        });
    }

    @Override
    public void onPutTagsSuccess() {
        CommonUtil.HideKeyboard(mEditComm);
        mNestedScrollView.scrollTo(0, (int) mTagFlowLayout.getY());
        mPresenter.requestTagList("article", mDailyInfo.getId(), 0);
    }

    @Override
    public void putTagsThumbSuccess(int type) {
        mPresenter.requestTagList("article", mDailyInfo.getId(), 0);
    }

    @Override
    public void repliesCommentSuccess() {
        CommonUtil.HideKeyboard(mEditComm);
        mPresenter.requestCommentList("article", mDailyInfo.getId(), 0, false);
    }

    @Override
    public void commentsThumbSuccess(DailyComment dailyComment, int position) {
        if (dailyComment.getIs_thumb() == 0) { //没点
            dailyComment.setIs_thumb(1);
            int num = Integer.parseInt(dailyComment.getThumbsUp());
            num = num + 1;
            dailyComment.setThumbsUp(String.valueOf(num));
            mCommentAdapter.notifyItemChanged(position, dailyComment);
        } else {
            dailyComment.setIs_thumb(0);
            int num = Integer.parseInt(dailyComment.getThumbsUp());
            num = num - 1;
            dailyComment.setThumbsUp(String.valueOf(num));

            mCommentAdapter.notifyItemChanged(position, dailyComment);
        }
    }

    @Override
    public void onFollowSuccess(String action) {
        if (action.equals("cancel")) {
            mDailyInfo.getUser().setFollowed(false);
            mBtnFollow.setText("+关注");
        } else {
            mDailyInfo.getUser().setFollowed(true);
            mBtnFollow.setText("已关注");
        }
        RxBus.getBus().send(EvenConstant.KEY_DAILY_LIST_REFRESH);
    }

    @Override
    public void replay(DailyComment comment, UserGuest userGuest) {
        if (!CommonUtil.isLogin()) {
            LoginActivity.launch(this, "DynamicDetailActivity");
        } else {
            CommonUtil.showKeyboard(mEditComm);
            String userName = CommonUtil.getNickName(comment.getRelationships().getNickName());
            mEditComm.setHint("回复 " + userName + ":");
            DailyDetailActivity.this.userName = userName;
            commentId = comment.getComId();
            sendType = "re_comment_one";

            mBottomLayout.setVisibility(View.INVISIBLE);
            mPostLayout.setVisibility(View.VISIBLE);
            mEditComm.setText("");

        }
        dynamicDialog.dismiss();
    }

    @Override
    public void OnDeleteCommentSuccess(int position) {
        mCommentAdapter.removeCommentUI(position);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mEditComm.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
        animatorProgress = null;
        animatorDismiss = null;
        KeyboardControlManager.clear();
        UMShareAPI.get(this).release();
    }

    private void downloadGame() {
        if (mGameInfo == null) {
            ToastUtil.showShort("下载失败");
            return;
        }
        DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
        String proText = mDownloadBar.getCurrentText();
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl);
        String path = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl);
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            mDownloadManager.downloadClick(this, aBoolean, proText, downloadInfo, downloadId, path);
        });
    }

    private void initDailyInfo(DailyInfo dailyInfo) {

        LogUtil.i("dailyInfo.getTitle() = " + dailyInfo.getTitle());

        Glide.with(this).load(dailyInfo.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mDailyCover);
        mDetailTitle.setText(dailyInfo.getTitle());
        setTitle(dailyInfo.getTitle());
        Glide.with(this).load(dailyInfo.getUser().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserHeader);
        mBtnFollow.setText(dailyInfo.getUser().isFollowed() ? "已关注" : "+关注");

        mUserNameView.setUserName(dailyInfo.getUser().getNickName()).setRankInfo(dailyInfo.getUser().getRank());
        mUserHeader.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mUserNameView.setOnClickListener(this);
    }

    private void initWebView(String url) {
        webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);

        // 设置可以支持缩放
        webSettings.setSupportZoom(false);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(false);
        //扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        //H5缓存
        String cacheDirPath = getCacheDir().getAbsolutePath() + "/differ/webViewCache";
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        //设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
        //开启Application H5 Caches 功能
        webSettings.setAppCacheEnabled(true);
        //设置Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        webSettings.setAppCacheMaxSize(100 * 1204 * 1024);

        //加快html网页加载完成速度
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                currentProgress = mProgressBar.getProgress();
                if (newProgress >= 100 && !isAnimStart) {
                    // 防止调用多次动画
                    isAnimStart = true;
                    mProgressBar.setProgress(newProgress);
                    // 开启属性动画让进度条平滑消失
                    startDismissAnimation(mProgressBar.getProgress());
                } else {
                    // 开启属性动画让进度条平滑递增
                    startProgressAnimation(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setAlpha(1.0f);
                if (!blockLoadingNetworkImage) {
                    webSettings.setBlockNetworkImage(true);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!webSettings.getLoadsImagesAutomatically()) {
                    webSettings.setLoadsImagesAutomatically(true);
                }
                if (blockLoadingNetworkImage) {
                    webSettings.setBlockNetworkImage(false);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(DailyDetailActivity.this, WebActivity.class);
                intent.putExtra("url", url);
                //intent.putExtra("title",url);
                startActivity(intent);
                return true;
            }
        });
        LogUtil.i("url = " + url);
        mWebView.loadUrl(url);
    }

    private void initFlowLayout(DailyInfo dailyInfo) {
        mPresenter.requestTagList("article", dailyInfo.getId(), 0);

        mTagFlowLayout.setOnTagClickListener((view, position, parent) -> {
            TagsInfo tagsInfo = (TagsInfo) mTagFlowLayout.getAdapter().getItem(position);
            if (tagsInfo.getName().equals("+")) {
                mEditComm.setHint("请输入标签内容");
                sendType = "tag";
                CommonUtil.showKeyboard(mEditComm);
            } else {
                if (!(tagsInfo.getIsThumb() == 1 && tagsInfo.getThumbsUp().equals("1"))) {
                    mPresenter.putTagsThumb(tagsInfo.getId(), tagsInfo.getIsThumb() == 1 ? 0 : 1); //1:顶，0: 取消顶
                }
            }
            return false;
        });
    }

    private void initComment() {
        mBottomLayout.setOnClickListener(this);

        RxView.clicks(mBtnSend).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> {
            String content = mEditComm.getText().toString().trim();
            switch (sendType) {
                case "comment":
                    mPresenter.submitComment("article", mDailyInfo.getId(), content);
                    break;
                case "re_comment_one":
                    mPresenter.repliesComment(commentId, content, "0", "", "");
                    break;
                case "re_comment_two":
                    mPresenter.repliesComment(commentId, "回复#Replay#" + userName + " " + content, "1", reply_id, reply_user_id);
                    break;
                case "tag":
                    mPresenter.putTags("article", mDailyInfo.getId(), content);
                    break;
            }
        });

        mCommentAdapter.setOnLikeClickListener((dailyComment, position) -> mPresenter.submitCommentsThumb(dailyComment, position));
        mCommentAdapter.setOnCommentListener(dailyComment -> {
            CommonUtil.showKeyboard(mEditComm);
            String userName = CommonUtil.getNickName(dailyComment.getRelationships().getNickName());
            mEditComm.setHint("回复 " + userName + ":");
            DailyDetailActivity.this.userName = userName;
            commentId = dailyComment.getComId();
            sendType = "re_comment_one";
        });
        mCommentAdapter.setOnReplayCommentListener((replies, commentId1) -> {
            CommonUtil.showKeyboard(mEditComm);
            String userName1 = CommonUtil.getNickName(replies.getRelationships().getNickName());
            mEditComm.setHint("回复 " + userName1 + ":");
            DailyDetailActivity.this.userName = userName1;
            DailyDetailActivity.this.commentId = commentId1;
            reply_id = replies.getId();
            reply_user_id = replies.getRelationships().getUserId();
            sendType = "re_comment_two";
        });

        KeyboardControlManager.observerKeyboardVisibleChange(this, (keyboardHeight, isVisible) -> {
            if (!isVisible) {
                mBottomLayout.setVisibility(View.VISIBLE);
                mPostLayout.setVisibility(View.GONE);
                mEditComm.setHint("说点什么吧");
            } else {
                mBottomLayout.setVisibility(View.INVISIBLE);
                mPostLayout.setVisibility(View.VISIBLE);
                mEditComm.setText("");
            }
            if (sendType.equals("comment")) {
                mNestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void startDismissAnimation(final int progress) {
        animatorDismiss = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        animatorDismiss.setDuration(1500);  // 动画时长
        animatorDismiss.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        animatorDismiss.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
            int offset = 100 - progress;
            mProgressBar.setProgress((int) (progress + offset * fraction));
        });
        animatorDismiss.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
                // mNestedScrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        });
        animatorDismiss.start();
    }

    private void startProgressAnimation(int newProgress) {
        animatorProgress = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animatorProgress.setDuration(500);
        animatorProgress.setInterpolator(new DecelerateInterpolator());
        animatorProgress.start();
    }

    private void initDownload(DownloadInfo downloadInfo) {
        if (downloadInfo == null)
            return;
        String downloadUrl = downloadInfo.downloadUrl; //下载地址
        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl);
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id

        mDownloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        mDownloadManager.initDownloadStatus(downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, (percent, status) -> {
            mDownloadBar.setProgressUI(percent, status);
        });
        mDownloadManager.addUpdater(downloadUrl, (task, percent, status) -> mDownloadBar.setProgressUI(percent, status));
    }
}
