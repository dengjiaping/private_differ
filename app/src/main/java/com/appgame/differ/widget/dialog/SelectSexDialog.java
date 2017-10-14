package com.appgame.differ.widget.dialog;

import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;

/**
 * Created by lzx on 2017/4/20.
 * 386707112@qq.com
 */

public class SelectSexDialog extends BaseDialog implements View.OnClickListener {

    public SelectSexDialog(Context context) {
        super(context);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.dialog_select_sex;
    }

    private TextView mBtnMan, mBtnGirl;
    private OnSelectListener mListener;

    @Override
    protected void init() {
        mBtnMan = (TextView) findViewById(R.id.man);
        mBtnGirl = (TextView) findViewById(R.id.girl);
        mBtnMan.setOnClickListener(this);
        mBtnGirl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.man:
                if (mListener != null) {
                    mListener.onSelect("男");
                }
                break;
            case R.id.girl:
                if (mListener != null) {
                    mListener.onSelect("女");
                }
                break;
        }
        dismiss();
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mListener = listener;
    }

    public interface OnSelectListener {
        void onSelect(String sex);
    }

    @Override
    protected float setWidthScale() {
        return 0.8f;
    }

    @Override
    protected AnimatorSet setEnterAnim() {
        return null;
    }

    @Override
    protected AnimatorSet setExitAnim() {
        return null;
    }


}
