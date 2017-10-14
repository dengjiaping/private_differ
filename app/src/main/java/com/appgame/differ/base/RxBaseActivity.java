package com.appgame.differ.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.utils.DifferRefreshHeader;
import com.appgame.differ.utils.LoadingUtil;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;


import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by lzx on 2017/2/21.
 * 386707112@qq.com
 *
 * 弃用了，看 {@link com.appgame.differ.base.BaseActivity}
 */
@Deprecated
public abstract class RxBaseActivity extends RxAppCompatActivity {

    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected TextView mCustomTitleTextView;
    protected PtrClassicFrameLayout mPtrClassicFrameLayout;
    private LoadingUtil loadingUtil;

    public PushAgent mPushAgent;

    public ProgressDialog progressDialog;

    protected ImageView mBarLeftImage, mBarRightImage;
    protected TextView mBarLeftText, mBarRightText, mToolbarTitle;
    protected RelativeLayout mBarLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getContentViewId() <= 0) {
            throw new RuntimeException("layout resId undefind");
        }
        initBefore(savedInstanceState);
        setContentView(getContentViewId());

        initBundle(savedInstanceState);
        initToolbar();
        initBarLayout();
        initLoading();
        setStatusBar();
        initPtrFrameLayout();
        init();

        mPushAgent = PushAgent.getInstance(this);  //友盟推送
        mPushAgent.onAppStart();
    }

    /**
     * 当前布局文件资源
     */
    protected abstract int getContentViewId();

    /**
     * 初始化
     */
    protected abstract void init();

    private void initLoading() {
        loadingUtil = new LoadingUtil(this);
    }

    public void showProgress() {
        loadingUtil.show();
    }

    public void hideProgress() {
        loadingUtil.dismiss();
    }

    public ProgressDialog createProgressDialog(Context mContext, String message, boolean cancelable) {
        dismissProgressDialog();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        return progressDialog;
    }

    public void updateProgressDialog(String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            progressDialog = null;
        }
    }

    public void setIsCancelable() {
        loadingUtil.setCancelable(false);
    }

    protected void setStatusBar() {

    }

    protected void initBundle(Bundle savedInstanceState) {

    }

    // 初始化之前
    protected void initBefore(Bundle savedInstanceState) {

    }

    /**
     * 下拉刷新
     */
    protected void initPtrFrameLayout() {
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_classic_frame_layout);
        if (mPtrClassicFrameLayout != null) {
            DifferRefreshHeader header = new DifferRefreshHeader(this);
//            mPtrClassicFrameLayout.setHeaderView(header);
//            mPtrClassicFrameLayout.addPtrUIHandler(header);
            mPtrClassicFrameLayout.setResistance(1.7f);
            mPtrClassicFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
            mPtrClassicFrameLayout.setDurationToClose(200);
            mPtrClassicFrameLayout.setDurationToCloseHeader(1000);
            mPtrClassicFrameLayout.setPullToRefresh(false);
            mPtrClassicFrameLayout.setKeepHeaderWhenRefresh(true);
            mPtrClassicFrameLayout.disableWhenHorizontalMove(true);
            mPtrClassicFrameLayout.setPtrHandler(new PtrHandler() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    frame.postDelayed(() -> {
                        onRefreshData();
                        mPtrClassicFrameLayout.refreshComplete();
                    }, 1800);
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }
            });
        }
    }

    /**
     * 刷新
     */
    protected void onRefreshData() {
    }

    /**
     * 初始化工具栏
     */
    public void initToolbar() {
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            SystemBarHelper.immersiveStatusBar(this, 0);
            SystemBarHelper.setHeightAndPadding(this, mToolbar);

            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            mToolbar.setTitleMarginStart(0);
            mToolbar.setContentInsetStartWithNavigation(0);
            mToolbar.setNavigationOnClickListener(v -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    finish();
                } else {
                    supportFinishAfterTransition();
                }
            });

            resetToolbar();
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setElevation(0);

            // SystemBarHelper.immersiveStatusBar(this);
            //  SystemBarHelper.tintStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimary), 1);
        }
    }

    public void resetToolbar() {
        if (mCustomTitleTextView == null) {
            mCustomTitleTextView = (TextView) getLayoutInflater().inflate(R.layout.layout_toolbar_title, null);
        }
        getSupportActionBar().setCustomView(mCustomTitleTextView, new ActionBar.LayoutParams(Gravity.LEFT | Gravity.CENTER_VERTICAL));
        if (getTitle() != null) {
            mCustomTitleTextView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
            mCustomTitleTextView.setText(getTitle());
        }
    }

    public void hiddenActionBar() {
        getSupportActionBar().hide();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        if (mCustomTitleTextView != null && title != null) {
            mCustomTitleTextView.setText(title);
        }
    }

    /**
     * 是否滑倒底部
     */
    protected boolean isSlideToBottom(RecyclerView recyclerView,int count) {
        if (recyclerView == null) return false;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = manager.getItemCount();
            int lastCompletelyVisiableItemPosition = manager.findLastCompletelyVisibleItemPosition();
            if ((visibleItemCount > 0) && (lastCompletelyVisiableItemPosition >= totalItemCount - count)) {  //totalItemCount - 1
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    protected void initBarLayout() {
        mBarLayout = (RelativeLayout) findViewById(R.id.bar_layout);
        mBarLeftImage = (ImageView) findViewById(R.id.bar_left_image);
        mBarRightImage = (ImageView) findViewById(R.id.bar_right_image);
        mBarLeftText = (TextView) findViewById(R.id.bar_left_text);
        mBarRightText = (TextView) findViewById(R.id.bar_right_text);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        if (mBarLeftImage != null && mBarRightImage != null && mBarLeftText != null && mBarRightText != null && mToolbarTitle != null) {
           if (getTitle()!=null) {
               mToolbarTitle.setText(getTitle());
           }
            mBarLeftImage.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    finish();
                } else {
                    supportFinishAfterTransition();
                }
            });
        }
    }

    protected void setBarLeftImageUI(int res, View.OnClickListener listener) {
        if (mBarLeftImage != null) {
            mBarLeftImage.setImageResource(res);
            if (listener != null)
                mBarLeftImage.setOnClickListener(listener);
        }
    }

    protected void setBarRightImageUI(int res, View.OnClickListener listener) {
        if (mBarRightImage != null) {
            mBarRightImage.setImageResource(res);
            if (listener != null)
                mBarRightImage.setOnClickListener(listener);
        }
    }

    protected void setBarLeftTextUI(String leftText, View.OnClickListener listener) {
        if (mBarLeftText != null) {
            mBarLeftText.setText(leftText);
            if (listener != null)
                mBarLeftText.setOnClickListener(listener);
        }
    }

    protected void setBarRightTextUI(String rightText, View.OnClickListener listener) {
        if (mBarRightText != null) {
            mBarRightText.setText(rightText);
            if (listener != null)
                mBarRightText.setOnClickListener(listener);
        }
    }

    protected void setBarTitleUI(String title) {
        if (mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        }
    }

    /**
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 友盟统计
     */
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
