package com.appgame.differ.module.welcome;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.login.model.LoginModel;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.rx.RxBus;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by lzx on 2017/5/25.
 * 386707112@qq.com
 */

public class WelcomeActivity extends BaseActivity<BaseContract.BasePresenter,String> implements View.OnClickListener {

    private ViewPager mViewPager;
    private FragmentAdapter mAdapter;
    private SparseArray<int[]> mLayoutViewIdsMap = new SparseArray<>();
    private LinearLayout mDotLayout;
    private LinearLayout.LayoutParams mParams;

    //选中显示Indicator
    private int selectRes = R.drawable.bg_frame_green_round2;
    //非选中显示Indicator
    private int unSelcetRes = R.drawable.bg_frame_gray_round2;

    private RelativeLayout mBtnLogin;
    private RelativeLayout mBtnExperience;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mDotLayout = (LinearLayout) findViewById(R.id.dot_layout);
        mBtnLogin = (RelativeLayout) findViewById(R.id.btn_login);
        mBtnExperience = (RelativeLayout) findViewById(R.id.btn_experience);

        mBtnLogin.setOnClickListener(this);
        mBtnExperience.setOnClickListener(this);
        mAdapter = new FragmentAdapter(getSupportFragmentManager());

//        addGuide(0, new FirstGuideFragment());
//        addGuide(1, new SecondGuideFragment());
//        addGuide(2, new ThreeGuideFragment());
//        addGuide(3, new FourGuideFragment());

        mAdapter.addItem(GuideFragment.newInstance(0));
        mAdapter.addItem(GuideFragment.newInstance(1));
        mAdapter.addItem(GuideFragment.newInstance(2));

        mViewPager.setAdapter(mAdapter);
        //mViewPager.setPageTransformer(true, new ParallaxTransformer(1.2f, 0.5f));

//        mParams = new LinearLayout.LayoutParams(CommonUtil.dip2px(this, 8), CommonUtil.dip2px(this, 8));
//        mParams.leftMargin = CommonUtil.dip2px(this, 2.5f);
//        mParams.rightMargin = CommonUtil.dip2px(this, 2.5f);
//        if (mDotLayout.getChildCount() != 0) {
//            mDotLayout.removeAllViewsInLayout();
//        }
//        for (int i = 0; i < 4; i++) {
//            ImageView dot = new ImageView(this);
//            dot.setBackgroundResource(unSelcetRes);
//            dot.setLayoutParams(mParams);
//            dot.setEnabled(false);
//            mDotLayout.addView(dot);
//        }
//        mDotLayout.getChildAt(0).setBackgroundResource(selectRes);
//        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                position = position % 4;
//                for (int i = 0; i < mDotLayout.getChildCount(); i++) {
//                    mDotLayout.getChildAt(i).setBackgroundResource(unSelcetRes);
//                }
//                mDotLayout.getChildAt(position).setBackgroundResource(selectRes);
//            }
//        });

        new LoginModel().incognitoLogin(bindToLifecycle());

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_LOGIN_SUCCESS)) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_experience:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.btn_login:
                startActivity(LoginActivity.launch(this));
                break;
        }
    }


    private class ParallaxTransformer implements ViewPager.PageTransformer {

        float parallaxCoefficient;
        float distanceCoefficient;

        ParallaxTransformer(float parallaxCoefficient, float distanceCoefficient) {
            this.parallaxCoefficient = parallaxCoefficient;
            this.distanceCoefficient = distanceCoefficient;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void transformPage(View page, float position) {
            float scrollXOffset = page.getWidth() * parallaxCoefficient;

            ViewGroup pageViewWrapper = (ViewGroup) page;

            @SuppressWarnings("SuspiciousMethodCalls")
            int[] layer = mLayoutViewIdsMap.get(mViewPager.getCurrentItem());
            for (int id : layer) {
                View view = page.findViewById(id);
                if (view != null) {
                    view.setTranslationX(scrollXOffset * position);
                }
                scrollXOffset *= distanceCoefficient;
            }
        }
    }

    private class FragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<Fragment>();

        FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        void addItem(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
    }

}
