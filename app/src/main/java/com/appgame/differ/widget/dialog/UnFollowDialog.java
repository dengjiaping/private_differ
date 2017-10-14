package com.appgame.differ.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;

/**
 * Created by lzx on 2017/4/24.
 * 386707112@qq.com
 */

public class UnFollowDialog extends BaseDialog implements View.OnClickListener {
    public UnFollowDialog(Context context) {
        super(context);
    }

    private TextView mDialogTitle, mBtnNo, mBtnYes;
    private onDalogClickListener mListener;

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_out_login;
    }

    @Override
    protected void init() {
        mDialogTitle = (TextView) findViewById(R.id.dialog_title);
        mBtnNo = (TextView) findViewById(R.id.btn_no);
        mBtnYes = (TextView) findViewById(R.id.btn_yes);
        mDialogTitle.setText("确定不再关注此人？");
        mBtnNo.setOnClickListener(this);
        mBtnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                break;
            case R.id.btn_yes:
                if (mListener != null) {
                    dismiss();
                    mListener.onClick();
                }
                break;
        }
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

    public void setListener(onDalogClickListener listener) {
        mListener = listener;
    }

    public interface onDalogClickListener {
        void onClick();
    }


}
