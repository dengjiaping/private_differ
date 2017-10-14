package com.appgame.differ.module.mine;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.home.DownloadManagerActivity;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.adapter.MinePagerAdapter;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.rx.RxBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/5/21.
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_game;
    }

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    private MinePagerAdapter mMinePagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ImageView mDownloadTask, mBtnPersonal;
    private View mMsgDot;
    private String userId;
    private int clickCount = 0;
    private InstalledFragment installedFragment;
    private PlayedFragment playedFragment;
    private ReservationFragment reservationFragment;

    @Override
    public void initVariables() {
        super.initVariables();
        userId = UserInfoManager.getImpl().getUserId();
        installedFragment = InstalledFragment.newInstance(userId, "installed");
        playedFragment = PlayedFragment.newInstance(userId, "played");
        reservationFragment = ReservationFragment.newInstance();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mDownloadTask = (ImageView) $(R.id.btn_download_task);
        mTabLayout = (TabLayout) $(R.id.tab_layout);
        mViewPager = (ViewPager) $(R.id.view_pager);
        mBtnPersonal = (ImageView) $(R.id.btn_personal);
        mMsgDot = $(R.id.msg_dot);
        mDownloadTask.setOnClickListener(this);
        mBtnPersonal.setOnClickListener(this);
    }

    @Override
    protected void initDatas() {
        fragments.add(installedFragment);
        fragments.add(playedFragment);
        titles.add("在玩");
        titles.add("玩过");

        mMinePagerAdapter = new MinePagerAdapter(getChildFragmentManager(), fragments, titles);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mMinePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        checkIsShowRedDot();

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_DOWNLOAD_RED_DOT)) {
                checkIsShowRedDot();
            }
        });
    }

    public void requestData() {
        clickCount++;
        if (clickCount >= 3) {
            clickCount = 0;
            mViewPager.postDelayed(() -> {
                //  installedFragment.onRefreshData();
                //  playedFragment.onRefreshData();
            }, 1000);
        }
    }

    private void checkIsShowRedDot() {
        GameDownloadManager.getImpl().isShowRedDot(isShow -> {
            mMsgDot.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download_task:
                startActivity(new Intent(getActivity(), DownloadManagerActivity.class));
                break;
            case R.id.btn_personal:
                Intent intent;
                boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
                if (isLogin) {
                    intent = new Intent(getActivity(), PersonalActivity.class);
                    //String id = UserInfoManager.getImpl().getUserInfo().getUserId();
                    String id = UserInfoManager.getImpl().getUserId();
                    intent.putExtra("userId", id);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent);
                break;
        }
    }
}
