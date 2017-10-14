package com.appgame.differ.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by lzx on 2017/8/10.
 */

public abstract class BaseFragment<T extends BaseContract.BasePresenter, K> extends RxFragment implements BaseContract.BaseView {


    protected T mPresenter;
    protected View mRootView;
    protected Activity mActivity;
    protected LayoutInflater inflater;
    protected Context mContext;
    // 标志位 标志已经初始化完成。
    protected boolean isPrepared;
    //标志位 fragment是否可见
    protected boolean isVisible;

    public List<K> mDataSource;

    @Override
    public void onAttach(Context context) {
        mActivity = (Activity) context;
        mContext = context;
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        } else {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            mActivity = getSupportActivity();
            mContext = mActivity;
            this.inflater = inflater;
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initPresenter();
        initVariables();
        initWidget();
        finishCreateView(savedInstanceState);
        initDatas();
    }

    protected void lazyLoadData() {

    }

    protected void initDatas() {
        loadData();
    }

    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getActivity().getLocalClassName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    public void onStop() {
        super.onStop();
        // if (mPresenter != null) mPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) mPresenter.clearView();
    }

    /**
     * 分离
     */
    @Override
    public void onDetach() {
        this.mActivity = null;
        super.onDetach();
    }

    /**
     * 初始化RV
     */
    protected void initRecyclerView() {

    }

    /**
     * 初始化刷新
     */
    @SuppressLint("CheckResult")
    protected void initRefreshLayout() {

    }

    /**
     * 清除数据
     */
    protected void clearData() {

    }

    /**
     * 初始化Presenter
     */
    protected void initPresenter() {
        if (mPresenter != null) mPresenter.attachView(this);
    }


    /**
     * 初始化变量
     */
    public void initVariables() {
    }

    /**
     * 懒加载
     */
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) return;
        lazyLoadData();
        isPrepared = false;
    }

    protected void onInvisible() {

    }

    /**
     * 加载数据
     */
    protected void loadData() {
    }


    /**
     * 显示错误信息
     *
     * @param msg msg
     */
    @Override
    public void showError(String msg, boolean isLoadMore) {

    }

    /**
     * 完成加载
     */
    @Override
    public void complete() {

    }

    protected void finishTask() {

    }

    /**
     * 布局
     *
     * @return int
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * 对各种控件进行设置、适配、填充数据
     */
    public void initWidget() {

    }

    protected void onVisible() {
        lazyLoad();
    }

    /**
     * Fragment数据的懒加载
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.i("isVisibleToUser = " + isVisibleToUser + "  getUserVisibleHint = " + getUserVisibleHint());
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 获取Activity
     *
     * @return FragmentActivity
     */
    public FragmentActivity getSupportActivity() {
        return super.getActivity();
    }

    /**
     * 获取ApplicationContext 信息
     *
     * @return Context
     */
    public Context getApplicationContext() {
        return this.mContext == null ? (getActivity() == null ? null : getActivity()
                .getApplicationContext()) : this.mContext.getApplicationContext();
    }


    /**
     * 隐藏View
     *
     * @param views view数组
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
     * @param views view数组
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

    public View $(@IdRes int id) {
        View view;
        if (mRootView != null) {
            view = mRootView.findViewById(id);
            return view;
        }
        return null;
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
