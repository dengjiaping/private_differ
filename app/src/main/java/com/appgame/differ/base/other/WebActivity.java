package com.appgame.differ.base.other;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.topic.TopicDetailCardActivity;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;

import static com.appgame.differ.R.id.webview;


/**
 * description: WebView Activity
 */
public class WebActivity extends BaseActivity {

    private final String TAG = WebActivity.class.getName();

    private WebView mWebView;

    private WebSettings webSettings;

    private ProgressBar mProgressBar;

    private String mTitle;

    private boolean isAnimStart = false;
    private int currentProgress;
    private boolean isNeedToken = true;
    private boolean isHideBar = false;
    private boolean isNeedTofinish = false;

    public static void launch(Context context, String title, String url, boolean isNeedToken, boolean isHideBar) {
        Intent mIntent = new Intent(context, WebActivity.class);
        mIntent.putExtra("title", title);
        mIntent.putExtra("url", url);
        mIntent.putExtra("isNeedToken", isNeedToken);
        mIntent.putExtra("isHideBar", isHideBar);
        context.startActivity(mIntent);
    }

    public static void launch(Context context, String title, String url) {
        Intent mIntent = new Intent(context, WebActivity.class);
        mIntent.putExtra("title", title);
        mIntent.putExtra("url", url);
        context.startActivity(mIntent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String url = intent.getStringExtra("url");
        if (intent.hasExtra("title")) {
            mTitle = intent.getStringExtra("title");
            setBarTitleUI(mTitle);
        }
        isNeedToken = intent.getBooleanExtra("isNeedToken", true);
        isHideBar = intent.getBooleanExtra("isHideBar", false);
        if (isNeedToken) {
            String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
            if (url.contains("?")) {
                url = url + "&access_token=" + token;
            } else {
                url = url + "?access_token=" + token;
            }
        }
        LogUtil.i("url = " + url);
        mWebView.loadUrl(url);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        String url = getIntent().getStringExtra("url");
        if (getIntent().hasExtra("title")) {
            mTitle = getIntent().getStringExtra("title");
            setBarTitleUI(mTitle);
        }
        isNeedToken = getIntent().getBooleanExtra("isNeedToken", true);
        isHideBar = getIntent().getBooleanExtra("isHideBar", false);
        if (isNeedToken) {
            String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
            if (url.contains("?")) {
                url = url + "&access_token=" + token;
            } else {
                url = url + "?access_token=" + token;
            }
        }
        LogUtil.i("url = " + url);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView = (WebView) findViewById(webview);

        // 设置参数
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
        //Activity开启了硬件加速，但是有副作用：容易会出现页面加载白块同时界面闪烁现象。下面为解决办法：暂时关闭
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            mWebView.setBackgroundColor(Color.TRANSPARENT);
//        }
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (TextUtils.isEmpty(mTitle)) {
                    setBarTitleUI(mTitle);
                }
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
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
                if (!webSettings.getLoadsImagesAutomatically()) {
                    webSettings.setLoadsImagesAutomatically(true);
                }

            }

            /**
             * 监听点击链接
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals("http://differ.appgame.com/act/login")) { //跳登陆页面
                    isNeedTofinish = true;
                    LoginActivity.launch(WebActivity.this, "WebActivity");
                } else if (url.equals("http://differ.appgame.com/act/zhuanti")) {  //专题
                    Intent intent = new Intent(WebActivity.this, TopicDetailCardActivity.class);
                    intent.putExtra("topicId", "7");
                    startActivity(intent);
                } else if (url.equals("http://differ.appgame.com/act/game")) { //游戏
                    Intent intent = new Intent(WebActivity.this, GameDetailsActivity.class);
                    intent.putExtra("game_id", "1358597500010840");
                    startActivity(intent);
                } else if (url.contains("http://differ.appgame.com/m/activity.html")) { //活动页面
                    url = url + "&is_hide_head=1&t=" + System.currentTimeMillis();
                    view.loadUrl(url);
                } else {
                    view.loadUrl(url);
                }
                LogUtil.i("shouldOverrideUrlLoading = " + url);
                return true;
            }
        });
        mWebView.loadUrl(url);
        if (isHideBar) {
            mBarLayout.setVisibility(View.GONE);
        }
    }

    /**
     * progressBar递增动画
     */
    private void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(mProgressBar, "progress", currentProgress, newProgress);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    private void startDismissAnimation(final int progress) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(mProgressBar, "alpha", 1.0f, 0.0f);
        anim.setDuration(1500);  // 动画时长
        anim.setInterpolator(new DecelerateInterpolator());     // 减速
        // 关键, 添加动画进度监听器
        anim.addUpdateListener(valueAnimator -> {
            float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
            int offset = 100 - progress;
            mProgressBar.setProgress((int) (progress + offset * fraction));
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }

    @Override
    public void onBackPressed() {
        if (isNeedTofinish) {
            isNeedTofinish = false;
            finish();
        } else {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
    }
}
