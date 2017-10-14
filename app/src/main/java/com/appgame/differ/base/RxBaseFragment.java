package com.appgame.differ.base;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appgame.differ.R;
import com.appgame.differ.utils.DifferRefreshHeader;
import com.appgame.differ.utils.LoadingUtil;
import com.appgame.differ.widget.FixPtrFrameLayout;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by lzx on 2017/2/21.
 * 386707112@qq.com
 * * 弃用了，看 {@link com.appgame.differ.base.BaseFragment}
 */

@Deprecated
public abstract class RxBaseFragment extends RxFragment {

    private View parentView;
    private LayoutInflater inflater;
    private LoadingUtil loadingUtil;
    // protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected FixPtrFrameLayout mPtrClassicFrameLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        this.inflater = inflater;
        parentView = inflater.inflate(getLayoutResId(), container, false);

        return parentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLoading();
        initPtrFrameLayout();
        init(savedInstanceState);
    }

    private void initLoading() {
        loadingUtil = new LoadingUtil(getActivity());
    }

    public void showProgress() {
        loadingUtil.show();
    }

    public void hideProgress() {
        loadingUtil.dismiss();
    }

    /**
     * 得到布局id
     *
     * @return
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    protected abstract void init(Bundle savedInstanceState);


    /**
     * 下拉刷新
     */
    protected void initPtrFrameLayout() {
        mPtrClassicFrameLayout = (FixPtrFrameLayout) findViewById(R.id.ptr_classic_frame_layout);
        if (mPtrClassicFrameLayout != null) {
            DifferRefreshHeader header = new DifferRefreshHeader(getActivity());
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

    protected View findViewById(int id) {
        return parentView.findViewById(id);
    }

    public boolean isSlideToBottom(RecyclerView recyclerView, int count) {
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
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = DifferApplication.getRefWatcher();
//        refWatcher.watch(this);
    }
}
