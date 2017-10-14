package com.appgame.differ.module.personal.view;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.KtSettingActivity;
import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.msg.MsgCenterActivity;
import com.appgame.differ.module.personal.adapter.MinePagerAdapter;
import com.appgame.differ.module.personal.contract.PersonalContract;
import com.appgame.differ.module.personal.presenter.PersonalPresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.StickyNavLayout;
import com.appgame.differ.widget.dialog.UnFollowDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 我的TAB
 */
public class PersonalActivity extends BaseActivity<PersonalContract.Presenter, String> implements View.OnClickListener, PersonalContract.View {


    private Toolbar mToolbarTemp;
    private AppBarLayout mAppBarLayout;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mBtnProfile, mBgCover, mBtnSetting, mMsgNews;
    private TextView mFollow, mFans, mFollowBtn;
    private CircleImageView mCircleImageView;
    private RelativeLayout mUserHeaderLayout;
    private TextView mUserName;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private MinePagerAdapter mMinePagerAdapter;

    private MineGameFragment mMineGameFragment;
    private MineDynamicFragment mMineDynamicFragment;
    private AboutFragment mAboutFragment;

    private String action = "";
    private String userId = "";

    private UserInfo mLocalUserInfo;

    private UnFollowDialog dialog;
    private ImageView mIconBadge;
    private StickyNavLayout mStickyNavLayout;

    private RelativeLayout.LayoutParams mParams;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new PersonalPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        SystemBarHelper.immersiveStatusBar(PersonalActivity.this);
        mToolbarTemp = (Toolbar) findViewById(R.id.toolbar_temp);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.toolbar_layout);
        mStickyNavLayout = (StickyNavLayout) findViewById(R.id.stick_nav_layout);
        mTabLayout = (TabLayout) findViewById(R.id.id_stickynavlayout_indicator);
        mViewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
        mBtnProfile = (ImageView) findViewById(R.id.btn_profile);
        mIconBadge = (ImageView) findViewById(R.id.icon_badge);
        mBgCover = (ImageView) findViewById(R.id.bg_cover);
        mBtnSetting = (ImageView) findViewById(R.id.btn_setting);
        mMsgNews = (ImageView) findViewById(R.id.msg_news);
        mFollow = (TextView) findViewById(R.id.btn_follow);
        mFans = (TextView) findViewById(R.id.btn_fans);
        mCircleImageView = (CircleImageView) findViewById(R.id.user_header);
        mUserName = (TextView) findViewById(R.id.user_name);
        mFollowBtn = (TextView) findViewById(R.id.follow_btn);
        mUserHeaderLayout = (RelativeLayout) findViewById(R.id.user_header_layout);
        SystemBarHelper.setHeightAndPadding(this, mToolbarTemp);
        mAppBarLayout.setAlpha(0);
        mToolbar.post(() -> {
            mStickyNavLayout.setToolbarHeight(mToolbar.getMeasuredHeight());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.bottomMargin = mToolbar.getMeasuredHeight();
            mStickyNavLayout.setLayoutParams(params);
        });

        mCircleImageView.setOnClickListener(this);
        mUserName.setOnClickListener(this);
        mFollowBtn.setOnClickListener(this);
        mFollow.setOnClickListener(this);
        mFans.setOnClickListener(this);
        mBtnProfile.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);
        mMsgNews.setOnClickListener(this);
        mToolbarTemp.setNavigationOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                finish();
            } else {
                supportFinishAfterTransition();
            }
        });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        dialog = new UnFollowDialog(this);
        action = getIntent().getStringExtra("action");
        userId = getIntent().getStringExtra("userId");

        initFragment();

        mStickyNavLayout.setOnScrollListener(alpha -> mAppBarLayout.setAlpha(alpha));

        RxBus.getBus().toMainThreadObservable(this.bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_REFRESH_USER_INFO)) {
                mPresenter.requestUserInfoByToken();
            } else if (o.equals(EvenConstant.KEY_LOGIN_OUT)) {
                finish();
            }
        });

        initUserInfo();
    }

    public void initFragment() {
        fragments.clear();
        titles.clear();

        action = TextUtils.isEmpty(action) ? "" : action;
        userId = TextUtils.isEmpty(userId) ? "" : userId;

        mMineGameFragment = MineGameFragment.newInstance(action, userId);
        mMineDynamicFragment = MineDynamicFragment.newInstance(action, userId);
        mAboutFragment = AboutFragment.newInstance(action, userId);

        fragments.add(mMineGameFragment);
        fragments.add(mMineDynamicFragment);
        fragments.add(mAboutFragment);

        titles = Arrays.asList(getResources().getStringArray(R.array.mine_tab));
        mMinePagerAdapter = new MinePagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mMinePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initUserInfo() {
        if (!TextUtils.isEmpty(action) && action.equals("other")) {
            mBtnProfile.setVisibility(View.GONE);
            mBtnSetting.setVisibility(View.GONE);
            mMsgNews.setVisibility(View.GONE);
            mPresenter.requestUserInfoById(userId, true);
        } else {
            if (mParams == null) {
                mParams = new RelativeLayout.LayoutParams(CommonUtil.dip2px(this, 50), CommonUtil.dip2px(this, 43));
                mParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                mParams.topMargin = CommonUtil.dip2px(this, 73);
            }
            mUserHeaderLayout.setLayoutParams(mParams);
            mPresenter.requestUserInfoByToken();
        }
    }

    private void initUserInfoUI(UserInfo userInfo, boolean isOther) {
        RxBus.getBus().send(userInfo);

        if (userInfo.getRank() != null) {
            mIconBadge.setVisibility(View.VISIBLE);
            Glide.with(PersonalActivity.this).load(userInfo.getRank().getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mIconBadge);
        } else {
            mIconBadge.setVisibility(View.GONE);
        }
        mLocalUserInfo = userInfo;
        setTitle(CommonUtil.getNickName(userInfo.getNickName()));
        mUserName.setText(CommonUtil.getNickName(userInfo.getNickName()));
        mFollow.setText("关注 " + userInfo.getFollowing());
        mFans.setText("粉丝 " + userInfo.getFollower());
        Glide.with(PersonalActivity.this).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_back)
                .crossFade().into(mCircleImageView);
        Glide.with(PersonalActivity.this).load(userInfo.getCover())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .override(720, CommonUtil.dp2px(this, 195))
                .error(R.color.default_bg_color)
                .placeholder(R.color.default_bg_color)
                .into(mBgCover);
        if (isOther) {
            boolean isFollow = userInfo.isFollowed();
            mFollowBtn.setVisibility(View.VISIBLE);
            mFollowBtn.setText(isFollow ? "已关注" : "+关注");
        } else {
            mFollowBtn.setVisibility(View.GONE);
        }
    }

    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.follow_btn:
                if (mLocalUserInfo.isFollowed()) {
                    dialog.show();
                    dialog.setListener(() -> mPresenter.followUser(mLocalUserInfo.getUserId(), "cancel"));
                } else {
                    boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
                    if (isLogin) {
                        mPresenter.followUser(mLocalUserInfo.getUserId(), "");
                    } else {
                        LoginActivity.launch(this, "PersonalActivity");
                    }
                }
                break;
            case R.id.btn_follow:
                intent = new Intent(this, FollowsActivity.class);
                if (!TextUtils.isEmpty(action) && action.equals("other")) {
                    intent.putExtra("action", "other");
                }
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
            case R.id.btn_fans:
                intent = new Intent(this, FansActivity.class);
                if (!TextUtils.isEmpty(action) && action.equals("other")) {
                    intent.putExtra("action", "other");
                }
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
            case R.id.btn_profile:
                intent = new Intent(PersonalActivity.this, EditUserInfoActivity.class);
                intent.putExtra("userInfo", mLocalUserInfo);
                startActivity(intent);
                break;
            case R.id.btn_setting:
                intent = new Intent(this, KtSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.msg_news:
                intent = new Intent(PersonalActivity.this, MsgCenterActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onUserInfoSuccess(UserInfo userInfo, boolean isOther) {
        initUserInfoUI(userInfo, isOther);
    }

    @Override
    public void onFollowSuccess(String action) {
        if (action.equals("cancel")) {
            mLocalUserInfo.setFollowed(false);
            mFollowBtn.setText("+关注");
        } else {
            mLocalUserInfo.setFollowed(true);
            mFollowBtn.setText("已关注");
        }
        RxBus.getBus().send(EvenConstant.KEY_REFRESH_FANS_LIST);
    }

    @Override
    public void onRequestUserGuest(List<UserGuest> list, String position) {

    }

    @Override
    public void onBackPressed() {
        finishActivity(0);
    }

    @Override
    public void finishActivity(int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            finishAfterTransition();
        }
    }
}
