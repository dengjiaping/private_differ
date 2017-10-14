package com.appgame.differ.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.appgame.differ.R;
import com.appgame.differ.widget.dialog.BaseDialog;

/**
 * 加载中弹窗工具类
 * Created by lzx on 2017/2/24.
 * 386707112@qq.com
 */

public class LoadingUtil extends BaseDialog implements DialogInterface.OnDismissListener {

    private View mDotLeft;
    private View mDotCenter;
    private View mDotRight;

    private ObjectAnimator animator1, animator2;
    private AnimatorSet mAnimatorSet, mSetOne, mSetTwo, mSetThree, mSet;

    private static final int DURATION = 500;

    public LoadingUtil(Context context) {
        super(context, R.style.DialogTransparent);
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    protected float setWidthScale() {
        return 0;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }

    @Override
    protected void init() {

        setOnDismissListener(this);
        mDotLeft = findViewById(R.id.dot_left);
        mDotCenter = findViewById(R.id.dot_center);
        mDotRight = findViewById(R.id.dot_right);

        mDotLeft.setScaleX(0f);
        mDotLeft.setScaleY(0f);
        mDotCenter.setScaleX(0f);
        mDotCenter.setScaleY(0f);
        mDotRight.setScaleX(0f);
        mDotRight.setScaleY(0f);

        mSetOne = createDotAnim(mDotLeft);
        mSetTwo = createDotAnim(mDotCenter);
        mSetTwo.setStartDelay(DURATION / 2);
        mSetThree = createDotAnim(mDotRight);
        mSetThree.setStartDelay(DURATION);

        mSet = new AnimatorSet();
        mSet.playTogether(mSetOne, mSetTwo, mSetThree);

        mSet.start();
    }

    public AnimatorSet createDotAnim(View dot) {
        mAnimatorSet = new AnimatorSet();
        animator1 = ObjectAnimator.ofFloat(dot, "scaleX", 0f, 1f);
        animator2 = ObjectAnimator.ofFloat(dot, "scaleY", 0f, 1f);
        animator1.setDuration(DURATION);
        animator2.setDuration(DURATION);

        animator1.setInterpolator(new LinearInterpolator());
        animator2.setInterpolator(new LinearInterpolator());

        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.REVERSE);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.REVERSE);

        mAnimatorSet.playTogether(animator1, animator2);

        return mAnimatorSet;
    }

    public void setIsCancelable(boolean flag) {
        setCancelable(flag);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.layout_loading;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        animator1 = null;
        animator2 = null;
        mAnimatorSet = null;
        mSetOne = null;
        mSetTwo = null;
        mSetThree = null;
        mSet = null;
    }
}
