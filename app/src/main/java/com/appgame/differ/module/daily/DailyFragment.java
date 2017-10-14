package com.appgame.differ.module.daily;

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
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.utils.CommonUtil;

/**
 * Created by lzx on 2017/6/22.
 */

public class DailyFragment extends BaseFragment implements View.OnClickListener {

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_recommend;
    }

    private TextView mBtnInfo, mBtnTopic;
    private ImageView mBtnPersonal, mBtnSearch;

    private DailyListFragment mInfoFragment, mTopicFragment;
    private Fragment[] fragments;

    @Override
    public void initVariables() {
        super.initVariables();
        mInfoFragment = DailyListFragment.newInstance("info");
        mTopicFragment = DailyListFragment.newInstance("topic");
        fragments = new Fragment[]{
                mInfoFragment,
                mTopicFragment
        };

        getChildFragmentManager().beginTransaction()
                .add(R.id.container, fragments[0]).show(fragments[0]).commit();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBtnInfo = (TextView) $(R.id.btn_recommend);
        mBtnTopic = (TextView) $(R.id.btn_video);
        mBtnPersonal = (ImageView) $(R.id.btn_personal);
        mBtnSearch = (ImageView) $(R.id.btn_search);
        gone(mBtnSearch);
        mBtnInfo.setText("资讯");
        mBtnTopic.setText("话题");
        mBtnInfo.setOnClickListener(this);
        mBtnTopic.setOnClickListener(this);
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
                mBtnInfo.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_alpha_80));
                mBtnTopic.setTextColor(Color.parseColor("#FF9B9B9B"));
                mBtnInfo.setTextSize(15);
                mBtnTopic.setTextSize(14);
                break;
            case R.id.btn_video:
                switchFragment(1);
                mBtnInfo.setTextColor(Color.parseColor("#FF9B9B9B"));
                mBtnTopic.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_alpha_80));
                mBtnInfo.setTextSize(14);
                mBtnTopic.setTextSize(15);
                break;
        }
    }
}
