package com.appgame.differ.widget.dialog;

import android.Manifest;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.VersionInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;



/**
 * Created by lzx on 2017/3/13.
 * 386707112@qq.com
 */

public class VersionDialog extends BaseDialog implements View.OnClickListener {

    private ImageView mBtnDismiss;
    private TextView mVersionDesc, mBtnCenter, mBtnEnter, mBtnInstall;
    private RelativeLayout mUpdateLayout;
    private LinearLayout mInstallLayout;
    private Context mContext;
    private UpdateDialog mUpdateDialog;
    private NetWorkDialog mNetWorkDialog;

    private VersionInfo mVersionInfo;
    private RxPermissions mRxPermissions;
    private FragmentManager fragmentManager;

    public VersionDialog(Context context, VersionInfo info, RxPermissions rxPermissions, FragmentManager fragmentManager) {
        super(context);
        mVersionInfo = info;
        mContext = context;
        this.fragmentManager = fragmentManager;
        mNetWorkDialog = new NetWorkDialog(context);
        mUpdateDialog = new UpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("VersionInfo", info);
        mUpdateDialog.setArguments(bundle);

        mRxPermissions = rxPermissions;
    }

    @Override
    protected float setWidthScale() {
        return 0.9f;
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
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mBtnDismiss = (ImageView) findViewById(R.id.btn_dismiss);
        mVersionDesc = (TextView) findViewById(R.id.version_desc);
        mBtnCenter = (TextView) findViewById(R.id.btn_center);
        mBtnEnter = (TextView) findViewById(R.id.btn_enter);
        mBtnInstall = (TextView) findViewById(R.id.btn_install);
        mUpdateLayout = (RelativeLayout) findViewById(R.id.update_layout);
        mInstallLayout = (LinearLayout) findViewById(R.id.install_layout);

        if (FileUtil.isExistsInstallationPackage(mVersionInfo.url)) {  //如果存在
            mInstallLayout.setVisibility(View.VISIBLE);
            mUpdateLayout.setVisibility(View.GONE);
        } else {
            mInstallLayout.setVisibility(View.GONE);
            mUpdateLayout.setVisibility(View.VISIBLE);
        }

        mVersionDesc.setText(mVersionInfo.description);
        mBtnCenter.setOnClickListener(this);
        mBtnEnter.setOnClickListener(this);
        mBtnInstall.setOnClickListener(this);
        mBtnDismiss.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_version;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_center:
                dismiss();
                RxBus.getBus().send(EvenConstant.KEY_EXIT_APP);
                break;
            case R.id.btn_enter:
                if (!CommonUtil.isConnected(mContext)) {
                    ToastUtil.showShort("更新失败，请检测你的网络");
                    return;
                }
                if (TextUtils.isEmpty(mVersionInfo.url)) {
                    ToastUtil.showShort("更新失败");
                    dismiss();
                    RxBus.getBus().send(EvenConstant.KEY_EXIT_APP);
                    return;
                }
                if (FileUtil.isExistsInstallationPackage(mVersionInfo.url)) {
                    try {
                        File file = FileUtil.getInstallationPackageFile(mVersionInfo.url);
                        CommonUtil.installApk(mContext, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showShort("文件不存在");
                    }
                    return;
                }
                mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (CommonUtil.isMobile(mContext)) {
                            mNetWorkDialog.show();
                            mNetWorkDialog.setOnClickListener(() -> mUpdateDialog.show(fragmentManager, "UpdateDialog"));
                        } else {
                            mUpdateDialog.show(fragmentManager, "UpdateDialog");
                            dismiss();
                        }
                    } else {
                        ToastUtil.showShort("权限拒绝，更新失败");
                        RxBus.getBus().send(EvenConstant.KEY_EXIT_APP);
                    }
                }, throwable -> {
                    ToastUtil.showShort("权限获取异常，更新失败");
                    RxBus.getBus().send(EvenConstant.KEY_EXIT_APP);
                });
                break;
            case R.id.btn_install:
                try {
                    File file = FileUtil.getInstallationPackageFile(mVersionInfo.url);
                    CommonUtil.installApk(mContext, file);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showShort("文件不存在");
                }
                break;
            case R.id.btn_dismiss:
                dismiss();
                RxBus.getBus().send(EvenConstant.KEY_EXIT_APP);
                break;
        }
    }




}
