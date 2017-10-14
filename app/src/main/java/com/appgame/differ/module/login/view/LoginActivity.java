package com.appgame.differ.module.login.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.base.other.SimpleTextWatcher;
import com.appgame.differ.base.other.WebActivity;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.login.contract.LoginContract;
import com.appgame.differ.module.login.presenter.LoginPresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.jakewharton.rxbinding2.view.RxView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 手机登录
 * Created by lzx on 2017/2/24.
 * 386707112@qq.com
 */
public class LoginActivity extends BaseActivity<LoginContract.Presenter, String> implements LoginContract.View, View.OnClickListener {

    private TextView mBtnLogin, mBtnGetVerCode;
    private EditText mEditPhone, mEditVerCode;
    private ImageView mBtnDelete;
    private ImageView mBtnWeibo, mBtnWx, mBtnQQ;
    private String action = "";


    private TextView mAgreement;

    public static void launch(Context context, String action) {
        Intent mIntent = new Intent(context, LoginActivity.class);
        mIntent.putExtra("login_action", action);
        context.startActivity(mIntent);
    }

    public static Intent launch(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone_login;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new LoginPresenter(this);
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnLogin = (TextView) findViewById(R.id.btn_login);
        mBtnGetVerCode = (TextView) findViewById(R.id.btn_get_ver_code);
        mAgreement = (TextView) findViewById(R.id.agreement);
        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        mEditVerCode = (EditText) findViewById(R.id.edit_ver_code);
        mBtnDelete = (ImageView) findViewById(R.id.img_delete);
        mBtnWeibo = (ImageView) findViewById(R.id.btn_weibo);
        mBtnWx = (ImageView) findViewById(R.id.btn_wx);
        mBtnQQ = (ImageView) findViewById(R.id.btn_qq);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        mEditPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                mBtnDelete.setVisibility(s.toString().length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        mBtnDelete.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mBtnWeibo.setOnClickListener(this);
        mBtnWx.setOnClickListener(this);
        mBtnQQ.setOnClickListener(this);
        mAgreement.setOnClickListener(this);

        RxView.clicks(mBtnGetVerCode).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(o -> {
            try {
                String phone = mEditPhone.getText().toString().trim();
                mPresenter.bindPhoneSendVerCode(phone);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        SystemBarHelper.immersiveStatusBar(this);
        action = getIntent().getStringExtra("login_action");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_delete:
                mEditPhone.setText("");
                break;
            case R.id.btn_login:
                String vercode = mEditVerCode.getText().toString().trim();
                String phone = mEditPhone.getText().toString().trim();
                showProgress();
                setIsCancelable();
                mPresenter.loginDiffer(phone, vercode);
                break;
            case R.id.btn_weibo:
                mPresenter.loginBySocial(this, SHARE_MEDIA.SINA);
                break;
            case R.id.btn_wx:
                mPresenter.loginBySocial(this, SHARE_MEDIA.WEIXIN);
                break;
            case R.id.btn_qq:
                mPresenter.loginBySocial(this, SHARE_MEDIA.QQ);
                break;
            case R.id.agreement:
                WebActivity.launch(this, "differ用户协议", AppConstants.USER_AGREEMENT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void loginSuccess(UserInfo userInfo) {
        GameDownloadManager.getImpl().uploadPhoneAppPackage(bindToLifecycle());
        hideProgress();
        if (TextUtils.isEmpty(action)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        if (!TextUtils.isEmpty(action) && action.equals("WebActivity")) {
            String url;
            if (CommonUtil.isLogin()) {
                url = AppConstants.ACTIVITY_URL + "?t=" + System.currentTimeMillis();
            } else {
                url = AppConstants.ACTIVITY_URL + "?t=" + System.currentTimeMillis() + "&type=app";
            }
            WebActivity.launch(LoginActivity.this, "", url, CommonUtil.isLogin(), true);
        }
        RxBus.getBus().send(EvenConstant.KEY_LOGIN_SUCCESS);
        finish();
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        hideProgress();
        ToastUtil.showShort(msg);
    }

    @Override
    public void sendVerCodeSuccess() {
        Observable.interval(0, 1, TimeUnit.SECONDS).takeWhile(aLong -> aLong < 61)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    mBtnGetVerCode.setText(String.valueOf(59 - aLong) + "秒");
                    if (mBtnGetVerCode.isEnabled()) {
                        mBtnGetVerCode.setEnabled(false);
                    }
                    if (aLong == 60) {
                        mBtnGetVerCode.setEnabled(true);
                        mBtnGetVerCode.setText("重新获取验证码");
                    }
                });
    }

    @Override
    public void isShowProgress(boolean isShow) {
        if (isShow) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.clearUMShareAPI();
        super.onDestroy();
    }
}
