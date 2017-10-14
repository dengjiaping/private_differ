package com.appgame.differ.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.appgame.differ.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by xian on 2017/5/20.
 */

public class DifferRefreshHeader extends FrameLayout implements PtrUIHandler {

    private ImageView mImageView;
    private AnimationDrawable anim;

    /**
     * 状态识别
     */
    private int mState;

    /**
     * 重置
     * 准备刷新
     * 开始刷新
     * 结束刷新
     */
    public static final int STATE_RESET = -1;
    public static final int STATE_PREPARE = 0;
    public static final int STATE_BEGIN = 1;
    public static final int STATE_FINISH = 2;
    public static final int MARGIN_RIGHT = 100;
    private AnimationDrawable mAnimation;

    public DifferRefreshHeader(@NonNull Context context) {
        this(context, null, 0);
        init();
    }

    public DifferRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public DifferRefreshHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_differ_refresh, this, true);
        mImageView = (ImageView) findViewById(R.id.image_refresh);


    }

    /**
     * 重置 View ，隐藏忙碌进度条，隐藏箭头 View ，更新最后刷新时间。
     * Content 重新回到顶部， Header 消失，整个下拉刷新过程完全结束以后，重置 View 。
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mState = STATE_RESET;
    }

    /**
     * 准备刷新，隐藏忙碌进度条，显示箭头 View ，显示文字，如果是下拉刷新，显示“下拉刷新”，如果是释放刷新，显示“下拉”。
     * 准备刷新，Header 将要出现时调用。
     */
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mState = STATE_PREPARE;
    }

    /**
     * 开始刷新，Header 进入刷新状态之前调用。
     * 开始刷新，隐藏箭头 View ，显示忙碌进度条，显示文字，显示“加载中...”，更新最后刷新时间。
     */
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mState = STATE_BEGIN;

        mImageView.setBackgroundResource(R.drawable.anim_refresh);
        mAnimation = (AnimationDrawable) mImageView.getBackground();
        if (!mAnimation.isRunning()) {
            mAnimation.start();
        }
    }

    /**
     * 刷新结束，隐藏箭头 View ，隐藏忙碌进度条，显示文字，显示“更新完成”，写入最后刷新时间。
     * 刷新结束，Header 开始向上移动之前调用。
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mState = STATE_FINISH;
        //停止动画
        if (mAnimation.isRunning()) {
            mAnimation.stop();
        }
        mImageView.setBackgroundResource(R.drawable.ic_reflash0);
    }

    /**
     * 下拉过程中位置变化回调。
     * <p>
     * 在拖动情况下，当下拉距离从 小于刷新高度到大于刷新高度 时，箭头 View 从向下，变成向上，同时改变文字显示。
     * 当下拉距离从 大于刷新高度到小于刷新高度 时，箭头 View 从向上，变为向下，同时改变文字显示。
     */
    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        float percent = Math.min(2f, ptrIndicator.getCurrentPercent());

        LogUtil.i("percent = "+percent);

//        if (percent > 1 && percent < 1.1f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash0);
//        } else if (percent > 1.1f && percent < 1.2f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash1);
//        } else if (percent > 1.2f && percent < 1.3f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash2);
//        } else if (percent > 1.3f && percent < 1.4f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash3);
//        } else if (percent > 1.4f && percent < 1.5f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash4);
//        } else if (percent > 1.5f && percent < 1.6f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash5);
//        } else if (percent > 1.6f && percent < 1.7f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash6);
//        } else if (percent > 1.8f && percent < 1.9f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash7);
//        } else if (percent > 1.9f && percent < 2f) {
//            mImageView.setBackgroundResource(R.drawable.ic_reflash8);
//        }
    }
}
