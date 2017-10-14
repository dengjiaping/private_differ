package com.appgame.differ.module.setting;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.VersionInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LoginUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.appgame.differ.widget.dialog.ShareDialog;
import com.appgame.differ.widget.dialog.VersionDialog;
import com.meituan.android.walle.WalleChannelReader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.message.IUmengCallback;
import com.umeng.socialize.UMShareAPI;


/**
 * 设置
 * Created by lzx on 2017/3/1.
 * 386707112@qq.com
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private SwitchCompat mSlideSwitch, mSlideSwitchPull;
    private TextView mBtnOutLogin, mCurrVer, mNewVer, mBtnShare;
    public VersionDialog mVersionDialog;
    private RxPermissions mRxPermissions;
    private VersionInfo mVersionInfo;
    private boolean isNeedToUpdate = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mSlideSwitch = (SwitchCompat) findViewById(R.id.slide_switch_pkg);
        mSlideSwitchPull = (SwitchCompat) findViewById(R.id.slide_switch_pull);
        mBtnOutLogin = (TextView) findViewById(R.id.btn_outlogin);
        mCurrVer = (TextView) findViewById(R.id.curr_ver);
        mNewVer = (TextView) findViewById(R.id.new_ver);
        mBtnShare = (TextView) findViewById(R.id.btn_share);

        mCurrVer.setText("当前版本:" + CommonUtil.getVersionName(this));

        mBtnOutLogin.setVisibility(CommonUtil.isLogin() ? View.VISIBLE : View.GONE);
        mBtnOutLogin.setOnClickListener(this);
        mCurrVer.setOnClickListener(this);
        mNewVer.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);

        boolean isOpenPush = SpUtil.getInstance().getBoolean(AppConstants.IS_OPEN_PUSH, true);
        boolean isDeletePkg = SpUtil.getInstance().getBoolean(AppConstants.IS_DELETE_PKG, true);
        mSlideSwitchPull.setChecked(isOpenPush);
        mSlideSwitch.setChecked(isDeletePkg);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mRxPermissions = new RxPermissions(this);

        mSlideSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            if (aBoolean) {
                SpUtil.getInstance().putBoolean(AppConstants.IS_DELETE_PKG, isChecked);
            } else {
                ToastUtil.showShort("权限拒绝，操作失败");
            }
        }));
        mSlideSwitchPull.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mPushAgent.enable(new IUmengCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                    }
                });
            } else {
                mPushAgent.disable(new IUmengCallback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                    }
                });
            }
        });

        checkVersion();
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_outlogin:
                OutLoginDialog dialog = new OutLoginDialog(this);
                dialog.show();
                dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                    @Override
                    public void onYes() {
                        LoginUtil.getImpl().clearLoginInfo();
                        RxBus.getBus().send(EvenConstant.KEY_LOGIN_OUT);
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onNo() {

                    }
                });
                break;
            case R.id.curr_ver:
                if (mVersionInfo == null) {
                    ToastUtil.showShort("检查更新失败");
                } else {
                    if (isNeedToUpdate) {
                        mVersionDialog = new VersionDialog(SettingActivity.this, mVersionInfo, mRxPermissions, getSupportFragmentManager());
                        mVersionDialog.show();
                    } else {
                        ToastUtil.showShort("已是最新版本");
                    }
                }
                break;
            case R.id.btn_share:
                //String channel = BuildConfig.FLAVOR;
                String channel = WalleChannelReader.getChannel(this);
                ShareDialog shareDialog = new ShareDialog();
                Bundle bundle = new Bundle();
                bundle.putString("shareType", "setting");
                bundle.putString("shareImage", "");
                bundle.putBoolean("isShareToDynamic", false);
                bundle.putBoolean("isHideCardUI", true);
                bundle.putString("shareTitle", getString(R.string.share_title));

                String userId = UserInfoManager.getImpl().getUserId();

                if (!TextUtils.isEmpty(userId)) {
                    bundle.putString("shareUrl", AppConstants.SHARE_URL + "h5/download?user_id=" + userId + "&channel=" + channel);
                } else {
                    bundle.putString("shareUrl", AppConstants.SHARE_URL + "h5/download?channel=" + channel);
                }

                shareDialog.setArguments(bundle);
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(shareDialog, "ShareDialog");
                ft.commitAllowingStateLoss();
                break;
        }
    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        CommonUtil.checkVersion(this, this.bindToLifecycle(), new CommonUtil.OnCheckVersionListener() {
            @Override
            public void onSuccess(VersionInfo mVersionInfo, boolean isNeedToUpdate) {
                SettingActivity.this.mVersionInfo = mVersionInfo;
                SettingActivity.this.isNeedToUpdate = isNeedToUpdate;
            }

            @Override
            public void onFailure() {
                SettingActivity.this.isNeedToUpdate = false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }


}
