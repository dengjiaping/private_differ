package com.appgame.differ.module.welcome;

import android.os.Bundle;
import android.widget.ImageView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by lzx on 2017/6/28.
 */

public class GuideFragment extends BaseFragment  {

    private int showImageNum;

    public static GuideFragment newInstance(int showImageNum) {
        GuideFragment fragment = new GuideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("showImageNum", showImageNum);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_guide;
    }

    private ImageView mGuideImage;

    @Override
    public void initVariables() {
        super.initVariables();
        showImageNum = getArguments().getInt("showImageNum");
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mGuideImage = (ImageView) $(R.id.guide_image);
        if (showImageNum == 0) {
            Glide.with(getActivity()).load(R.drawable.welcome_one).diskCacheStrategy(DiskCacheStrategy.ALL).into(mGuideImage);
        } else if (showImageNum == 1) {
            Glide.with(getActivity()).load(R.drawable.welcome_two).diskCacheStrategy(DiskCacheStrategy.ALL).into(mGuideImage);
        } else {
            Glide.with(getActivity()).load(R.drawable.welcome_three).diskCacheStrategy(DiskCacheStrategy.ALL).into(mGuideImage);
        }
    }
}
