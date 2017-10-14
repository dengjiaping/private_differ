package com.appgame.differ.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.utils.DifferRefreshHeader;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by lzx on 2017/8/10.
 */

public abstract class BaseRefreshActivity<T extends BaseContract.BasePresenter, K> extends BaseActivity<T, K> {

    protected RecyclerView mRecyclerView;
    protected PtrClassicFrameLayout mPtrClassicFrameLayout;
    protected boolean mIsRefreshing = false;


    @Override
    protected void initWidget() {
        super.initWidget();
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_classic_frame_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        initRefreshLayout();
    }

    private void initRefreshLayout() {
        //初始化刷新控件
        if (mPtrClassicFrameLayout != null) {
            DifferRefreshHeader header = new DifferRefreshHeader(this);
//          mPtrClassicFrameLayout.setHeaderView(header);
//          mPtrClassicFrameLayout.addPtrUIHandler(header);
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
                        clearData();
                        refreshData();
                    }, 1800);
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }
            });
        }
    }

    protected void refreshData() {
    }

    protected void clearData() {
        mIsRefreshing = true;
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {

    }

    @Override
    public void complete() {
        if (mPtrClassicFrameLayout != null) {
            mPtrClassicFrameLayout.refreshComplete();
        }
        if (mIsRefreshing) {
            clear();
        }
        mIsRefreshing = false;
    }

    protected void clear() {

    }


}
