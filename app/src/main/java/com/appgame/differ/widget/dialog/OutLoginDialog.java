package com.appgame.differ.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.utils.CommonUtil;

/**
 * Created by lzx on 2017/4/11.
 * 386707112@qq.com
 */

public class OutLoginDialog extends BaseDialog implements View.OnClickListener {

    private TextView mBtnNo, mBtnYes, mDialogTitle;
    private OnOutLoginListener mListener;
    private Context mContext;

    public OutLoginDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_out_login;
    }

    @Override
    protected void init() {
        mBtnNo = (TextView) findViewById(R.id.btn_no);
        mBtnYes = (TextView) findViewById(R.id.btn_yes);
        mDialogTitle = (TextView) findViewById(R.id.dialog_title);
        mBtnNo.setOnClickListener(this);
        mBtnYes.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                if (mListener != null) {
                    mListener.onNo();
                }
                break;
            case R.id.btn_yes:
                dismiss();
                if (mListener != null) {
                    mListener.onYes();
                }
                break;
        }
    }

    public void setDialogTitle(String dialogTitle) {
        mDialogTitle.setText(dialogTitle);
    }

    public void setDialogBtnText(String textLeft, String textRight) {
        mBtnNo.setText(textLeft);
        mBtnYes.setText(textRight);
    }

    public void setDialogBtnTextSize(int textLeftSize, int textRightSize) {
        mBtnNo.setTextSize(CommonUtil.dip2px(mContext, textLeftSize));
        mBtnYes.setTextSize(CommonUtil.dip2px(mContext, textRightSize));
    }

    public void setListener(OnOutLoginListener listener) {
        mListener = listener;
    }

    public interface OnOutLoginListener {
        void onYes();

        void onNo();
    }
}
