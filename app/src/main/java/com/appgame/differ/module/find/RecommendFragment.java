package com.appgame.differ.module.find;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.module.search.SearchClassActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.rx.RxBus;

/**
 * Created by lzx on 2017/6/22.
 */

public class RecommendFragment extends BaseFragment implements View.OnClickListener {

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    private TextView mBtnRecommend, mBtnVideo;
    private ImageView mBtnSearch, mBtnPersonal;
    private FindFragment mFindFragment;
    private VideoFragment mVideoFragment;

    private Fragment[] fragments;


    @Override
    public void initVariables() {
        super.initVariables();
        mFindFragment = FindFragment.newInstance();
        mVideoFragment = VideoFragment.newInstance();
        fragments = new Fragment[]{
                mFindFragment,
                mVideoFragment
        };

        getChildFragmentManager().beginTransaction()
                .add(R.id.container, fragments[0]).show(fragments[0]).commit();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBtnRecommend = (TextView) $(R.id.btn_recommend);
        mBtnVideo = (TextView) $(R.id.btn_video);
        mBtnSearch = (ImageView) $(R.id.btn_search);
        mBtnPersonal = (ImageView) $(R.id.btn_personal);
        mBtnRecommend.setOnClickListener(this);
        mBtnVideo.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mBtnPersonal.setOnClickListener(this);
    }

    private void switchFragment(int index) {
        FragmentTransaction trx = getChildFragmentManager().beginTransaction();
        if (!fragments[index].isAdded()) {
            trx.add(R.id.container, fragments[index]);
        }
        for (Fragment fragment : fragments) {
            trx.hide(fragment);
        }
        trx.show(fragments[index]).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                startActivity(new Intent(getActivity(), SearchClassActivity.class));
                break;
            case R.id.btn_personal:
                Intent intent;
                if (CommonUtil.isLogin()) {
                    intent = new Intent(getActivity(), PersonalActivity.class);
                    String id = UserInfoManager.getImpl().getUserId();
                    intent.putExtra("userId", id);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.btn_recommend:
                switchFragment(0);
                mBtnRecommend.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_alpha_80));
                mBtnVideo.setTextColor(Color.parseColor("#FF9B9B9B"));
                mBtnRecommend.setTextSize(15);
                mBtnVideo.setTextSize(14);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_FIND_VIDEOS);
                break;
            case R.id.btn_video:
                switchFragment(1);
                mBtnRecommend.setTextColor(Color.parseColor("#FF9B9B9B"));
                mBtnVideo.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_alpha_80));
                mBtnRecommend.setTextSize(14);
                mBtnVideo.setTextSize(15);
                break;
        }
    }
}
