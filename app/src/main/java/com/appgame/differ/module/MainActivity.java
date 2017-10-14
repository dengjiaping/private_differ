package com.appgame.differ.module;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.VersionInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.daily.DailyFragment;
import com.appgame.differ.module.dynamic.DynamicFragment;
import com.appgame.differ.module.find.RecommendFragment;
import com.appgame.differ.module.login.model.LoginModel;
import com.appgame.differ.module.mine.MineFragment;
import com.appgame.differ.module.wikis.WikisFragment;
import com.appgame.differ.service.AppLoadService;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.dialog.VersionDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * FindActivity,DiscoverNavActivity,GameClassActivity,DailyDetailActivity
 * DifferFragment,GameDetailsActivity,PlayedFragment
 * Created by lzx on 2017/5/16.
 * 386707112@qq.com
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RadioButton mTabFind, mTabDaily, mTabDynamic, mTabMine, mTabWiki;
 //   private ImageView mTabDiffer;
    private Fragment[] fragments;
    private int currentTabIndex;
    private RadioButton[] mTabs;
    //private DifferFragment mDifferFragment;
    private WikisFragment mWikisFragment;
    private RecommendFragment mRecommendFragment;
    private DailyFragment mDailyFragment;
    private DynamicFragment mDynamicFragment;
    private MineFragment mMineFragment;
    private RxPermissions mRxPermissions;
    private long firstTime = 0;
    private LoginModel mLoginModel;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mRxPermissions = new RxPermissions(this);
        mLoginModel = new LoginModel();
        mTabFind = (RadioButton) findViewById(R.id.tab_find);
        mTabDaily = (RadioButton) findViewById(R.id.tab_daily);
        mTabDynamic = (RadioButton) findViewById(R.id.tab_dynamic);
        mTabMine = (RadioButton) findViewById(R.id.tab_mine);
    //    mTabDiffer = (ImageView) findViewById(R.id.tab_differ);
        mTabWiki = (RadioButton) findViewById(R.id.tab_wiki);
        mTabFind.setOnClickListener(this);
        mTabDaily.setOnClickListener(this);
        mTabDynamic.setOnClickListener(this);
        mTabMine.setOnClickListener(this);
        mTabWiki.setOnClickListener(this);
      //  mTabDiffer.setOnClickListener(this);


        initFragment();
        checkVersion();
        initService();


        //匿名登陆
        mLoginModel.incognitoLogin(bindToLifecycle());
        GameDownloadManager.getImpl().initApiInfo(bindToLifecycle());

        SpUtil.getInstance().putBoolean(AppConstants.IS_FIRST_OPEN, false);

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof String) {
                String event = (String) o;
                switch (event) {
                    case EvenConstant.KEY_EXIT_APP:
                        finish();
                        break;
                    case EvenConstant.KEY_RELOAD_APP_LIST:
                        GameDownloadManager.getImpl().uploadPhoneAppPackage(bindToLifecycle());
                        break;
                    case EvenConstant.KEY_LOGIN_OUT:
                        mLoginModel.incognitoLogin(bindToLifecycle());
                        break;
                    case EvenConstant.KEY_INCOGNITO_LOGIN_SUCCESS:
                        initService();
                        break;
                    case EvenConstant.KEY_SWITCH_FRAGMENT_MINE:
                        switchFragment(4);
                        break;
                }
            }
        });

        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe();
    }


    /**
     * 初始化服务
     */
    private void initService() {

        startService(new Intent(this, AppLoadService.class));
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        //mDifferFragment = DifferFragment.newInstance();
        mWikisFragment = WikisFragment.newInstance();
        mRecommendFragment = RecommendFragment.newInstance();
        mDailyFragment = DailyFragment.newInstance();
        mDynamicFragment = DynamicFragment.newInstance();
        mMineFragment = MineFragment.newInstance();

        fragments = new Fragment[]{
                mRecommendFragment,
                mDailyFragment,
                //mDifferFragment,
                mWikisFragment,
                mDynamicFragment,
                mMineFragment,
        };
        mTabs = new RadioButton[]{
                mTabFind, mTabDaily, mTabWiki, mTabDynamic, mTabMine
        };
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragments[0]).show(fragments[0]).commit();
        mTabs[0].setChecked(true);
    }

    /**
     * Fragment切换
     */
    private void switchFragment(int index) {
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        if (!fragments[index].isAdded()) {
            trx.add(R.id.container, fragments[index]);
        }
        for (Fragment fragment : fragments) {
            trx.hide(fragment);
        }
        trx.show(fragments[index]).commit();
        if (!mTabs[index].isChecked()) {
            mTabs[currentTabIndex].setChecked(false);
        }
        mTabs[index].setChecked(true);
        currentTabIndex = index;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_find:
                switchFragment(0);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_DYNAMIC_VIDEOS);
                break;
            case R.id.tab_daily:
                switchFragment(1);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_FIND_VIDEOS);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_DYNAMIC_VIDEOS);
                break;
            case R.id.tab_wiki:
                switchFragment(2);
                RxBus.getBus().send(EvenConstant.KEY_SHOW_EXPLORE_ANIM);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_FIND_VIDEOS);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_DYNAMIC_VIDEOS);
                break;
            case R.id.tab_dynamic:
                if (mDynamicFragment != null) {
                    mDynamicFragment.requestData();
                }
                switchFragment(3);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_FIND_VIDEOS);
                break;
            case R.id.tab_mine:
                if (mMineFragment != null) {
                    mMineFragment.requestData();
                }
                switchFragment(4);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_FIND_VIDEOS);
                RxBus.getBus().send(EvenConstant.KEY_RELEASE_DYNAMIC_VIDEOS);
                break;
            default:
                break;
        }
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonUtil.checkVersion(this, bindToLifecycle(), new CommonUtil.OnCheckVersionListener() {
            @Override
            public void onSuccess(VersionInfo mVersionInfo, boolean isNeedToUpdate) {
                if (isNeedToUpdate) {
                    VersionDialog mVersionDialog = new VersionDialog(MainActivity.this, mVersionInfo, mRxPermissions, getSupportFragmentManager());
                    mVersionDialog.show();
                } else {
                    String url = SpUtil.getInstance().getString(AppConstants.update_url);
                    if (!TextUtils.isEmpty(url)) {
                        FileUtil.deleteGamePkgFile(url);
                    }
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            ToastUtil.showShort("再按一次退出程序");
            firstTime = secondTime;
        } else {
            super.onBackPressed();
            //moveTaskToBack(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clear();
    }
}
