package com.appgame.differ.widget.dialog;


import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;

import com.appgame.differ.R;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.nineoldandroids.animation.AnimatorSet;
import com.appgame.differ.widget.nineoldandroids.animation.ObjectAnimator;
import com.appgame.differ.widget.nineoldandroids.animation.PropertyValuesHolder;

/**
 * Created by lzx on 2017/4/12.
 * 386707112@qq.com
 */

public class ExploreAnimDialog extends RxBaseDialog {

    private CircleImageView mViewOne, mViewTwo, mViewThree;

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_explore_anim;
    }

    private ObjectAnimator animator1, animator2, animator3;
    private AnimatorSet mAnimatorSet;
    private PropertyValuesHolder pvhScaleX, pvhScaleY, pvhAlpha;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
        attributes.width = CommonUtil.dip2px(getActivity(), 200);
        attributes.height = CommonUtil.dip2px(getActivity(), 150);
        getDialog().getWindow().setAttributes(attributes);
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        setCancelable(false);

        mViewOne = (CircleImageView) findViewById(R.id.bg_white_one);
        mViewTwo = (CircleImageView) findViewById(R.id.bg_white_two);
        mViewThree = (CircleImageView) findViewById(R.id.bg_white_three);

        pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.5f);
        pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.5f);
        pvhAlpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f, 0f);

        animator1 = ObjectAnimator.ofPropertyValuesHolder(mViewOne, pvhScaleX, pvhScaleY, pvhAlpha);
        animator1.setDuration(1000);
        animator1.setInterpolator(new LinearInterpolator());
        animator1.setRepeatCount(Integer.MAX_VALUE);

        animator2 = ObjectAnimator.ofPropertyValuesHolder(mViewTwo, pvhScaleX, pvhScaleY, pvhAlpha);
        animator2.setDuration(1000);
        animator2.setInterpolator(new LinearInterpolator());
        animator2.setRepeatCount(Integer.MAX_VALUE);


        animator3 = ObjectAnimator.ofPropertyValuesHolder(mViewThree, pvhScaleX, pvhScaleY, pvhAlpha);
        animator3.setDuration(1000);
        animator3.setInterpolator(new LinearInterpolator());
        animator3.setRepeatCount(Integer.MAX_VALUE);


        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(1500);
        mAnimatorSet.playTogether(animator1, animator2, animator3);
        mAnimatorSet.start();

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        animator1 = null;
        animator2 = null;
        animator3 = null;
        mAnimatorSet = null;
        pvhScaleX = null;
        pvhScaleY = null;
        pvhAlpha = null;
    }
}
