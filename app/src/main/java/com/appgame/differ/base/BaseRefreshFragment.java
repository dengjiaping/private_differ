package com.appgame.differ.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.utils.DifferRefreshHeader;
import com.appgame.differ.widget.FixPtrFrameLayout;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by lzx on 2017/8/10.
 */

public abstract class BaseRefreshFragment<T extends BaseContract.BasePresenter, K> extends BaseFragment<T, K> {
    protected RecyclerView mRecyclerView;
    protected FixPtrFrameLayout mPtrClassicFrameLayout;
    protected boolean mIsRefreshing = false;


    @Override
    protected void initRefreshLayout() {
        //初始化刷新控件
        if (mPtrClassicFrameLayout != null) {
            DifferRefreshHeader header = new DifferRefreshHeader(getActivity());
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
                        lazyLoadData();
                    }, 1800);
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }
            });
        }
    }

    @Override
    protected void clearData() {
        mIsRefreshing = true;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) return;
        initRefreshLayout();
        initRecyclerView();
        if (mPtrClassicFrameLayout != null) {
            lazyLoadData();
        }
        isPrepared = false;
    }

    protected void loadData() {

    }

    @Override
    public void complete() {
        super.complete();
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

    @Override
    public void initWidget() {
        mPtrClassicFrameLayout = (FixPtrFrameLayout) $(R.id.ptr_classic_frame_layout);
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);
        if (!isPrepared || !isVisible) {
            initRefreshLayout();
        }
    }
}
