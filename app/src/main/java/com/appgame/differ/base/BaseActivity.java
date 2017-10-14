package com.appgame.differ.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
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
import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.utils.LoadingUtil;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.List;

/**
 * Created by lzx on 2017/2/21.
 * 386707112@qq.com
 */

public abstract class BaseActivity<T extends BaseContract.BasePresenter, K> extends RxAppCompatActivity implements BaseContract.BaseView {


    protected T mPresenter;
    protected Context mContext;//上下文环境

    protected boolean mBack = true;
    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected TextView mCustomTitleTextView;
    protected ImageView mBarLeftImage, mBarRightImage;
    protected TextView mBarLeftText, mBarRightText, mToolbarTitle;
    protected RelativeLayout mBarLayout;

    public ProgressDialog progressDialog;

    private LoadingUtil loadingUtil;

    public PushAgent mPushAgent;

   public List<K> mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        initStatusBar();
        initToolbar();
        initPresenter();
        initLoading();
        initWidget();
        init(savedInstanceState);

        mPushAgent = PushAgent.getInstance(this);  //友盟推送
        mPushAgent.onAppStart();
    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    protected void init(Bundle savedInstanceState) {

    }

    protected void initRecyclerView() {

    }

    /**
     * 完成请求
     */
    protected void finishTask() {
    }

    /**
     * 初始化StatusBar
     */
    protected void initStatusBar() {

    }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //如果用以下这种做法则不保存状态，再次进来的话会显示默认tab
        //总是执行这句代码来调用父类去保存视图层的状态
        //super.onSaveInstanceState(outState);
    }

    /**
     * 初始化Presenter
     */
    protected void initPresenter() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }


    @Override
    public void showError(String msg, boolean isLoadMore) {

    }

    @Override
    public void complete() {

    }

    /**
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
       // if (mPresenter != null) mPresenter.detachView();

    }

    /**
     * 友盟统计
     */
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        if (mPresenter != null) mPresenter.clearView();
        if (mDataSource != null) {
            mDataSource.clear();
            mDataSource = null;
        }
        super.onDestroy();
    }

    /**
     * 布局文件
     *
     * @return 布局文件
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget() {
        initBarLayout();
    }

    /**
     * 初始化自定义布局的顶部bar
     */
    protected void initBarLayout() {
        mBarLayout = (RelativeLayout) findViewById(R.id.bar_layout);
        mBarLeftImage = (ImageView) findViewById(R.id.bar_left_image);
        mBarRightImage = (ImageView) findViewById(R.id.bar_right_image);
        mBarLeftText = (TextView) findViewById(R.id.bar_left_text);
        mBarRightText = (TextView) findViewById(R.id.bar_right_text);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        if (mBarLeftImage != null && mBarRightImage != null && mBarLeftText != null && mBarRightText != null && mToolbarTitle != null) {
            if (getTitle() != null) {
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

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        if (mCustomTitleTextView != null && title != null) {
            mCustomTitleTextView.setText(title);
        }
    }


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


    /**
     * 隐藏View
     *
     * @param views 视图
     */
    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示View
     *
     * @param views 视图
     */
    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void invisible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 隐藏View
     *
     * @param id
     */
    protected void gone(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    gone(view);
            }
        }
    }

    protected void invisible(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    invisible(view);
            }
        }
    }

    /**
     * 显示View
     *
     * @param id
     */
    protected void visible(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    visible(view);
            }
        }
    }

    private View $(@IdRes int id) {
        View view;
        view = this.findViewById(id);
        return view;
    }


    private int previousTotal = 0;
    protected boolean loading = true;

    public boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = manager.getItemCount();
            int lastCompletelyVisiableItemPosition = manager.findLastCompletelyVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            return !loading && (visibleItemCount > 0) && (lastCompletelyVisiableItemPosition >= totalItemCount - 1);
        }
        return false;
    }

}
