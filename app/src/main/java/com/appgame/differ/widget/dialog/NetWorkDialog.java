package com.appgame.differ.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;

/**
 * 网络状态dialig
 * Created by lzx on 2017/3/13.
 * 386707112@qq.com
 */

public class NetWorkDialog extends BaseDialog implements View.OnClickListener {

    private ImageView mBtnDismiss;
    private TextView mBtnCenter, mBtnEnter, mNetworkTitle, mNetworkDesc;
    private OnClickListener mOnClickListener;
    private boolean isShowQuitStyle = false;

    public NetWorkDialog(Context context) {
        super(context);
    }

    public NetWorkDialog(Context context, boolean isShowQuitStyle) {
        super(context);
        this.isShowQuitStyle = isShowQuitStyle;
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
        mBtnDismiss = (ImageView) findViewById(R.id.btn_dismiss);
        mBtnCenter = (TextView) findViewById(R.id.btn_center);
        mBtnEnter = (TextView) findViewById(R.id.btn_enter);
        mNetworkTitle = (TextView) findViewById(R.id.network_title);
        mNetworkDesc = (TextView) findViewById(R.id.network_desc);
        if (isShowQuitStyle) {
            mNetworkTitle.setText(R.string.dialog_quit_title);
            mNetworkDesc.setText(R.string.dialog_quit_desc);
            mBtnCenter.setText(R.string.dialog_quit_center);
            mBtnEnter.setText(R.string.dialog_quit_enter);
        }
        mBtnCenter.setOnClickListener(this);
        mBtnEnter.setOnClickListener(this);
        mBtnDismiss.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_network;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_center:
                dismiss();
                break;
            case R.id.btn_enter:
                if (mOnClickListener != null) {
                    dismiss();
                    mOnClickListener.OnClick();
                }
                break;
            case R.id.btn_dismiss:
                dismiss();
                break;
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void OnClick();
    }
}
