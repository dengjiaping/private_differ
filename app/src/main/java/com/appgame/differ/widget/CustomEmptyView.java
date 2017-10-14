package com.appgame.differ.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;

/**
 * 自定义EmptyView
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */

public class CustomEmptyView extends FrameLayout {

    private TextView mSmallErrorTitle, mBigErrorTitle,mEmptyErrTitle;
    private ImageView mSmallEmptyImage, mBigEmptyImage, mEmptyErrImage;

    private RelativeLayout mSmallLayout, mBigLayout, mErrorLayout;


    public CustomEmptyView(Context context) {
        this(context, null);
    }

    public CustomEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_empty, this);

        mSmallErrorTitle = (TextView) findViewById(R.id.small_error_title);
        mBigErrorTitle = (TextView) findViewById(R.id.big_error_title);
        mEmptyErrTitle = (TextView) findViewById(R.id.empty_err_title);
        mSmallEmptyImage = (ImageView) findViewById(R.id.small_empty_image);
        mBigEmptyImage = (ImageView) findViewById(R.id.big_empty_image);
        mEmptyErrImage = (ImageView) findViewById(R.id.empty_err_image);
        mSmallLayout = (RelativeLayout) findViewById(R.id.small_layout);
        mBigLayout = (RelativeLayout) findViewById(R.id.big_layout);
        mErrorLayout = (RelativeLayout) findViewById(R.id.error_layout);
    }

    public void initSmallUI() {
        mSmallLayout.setVisibility(VISIBLE);
        mBigLayout.setVisibility(GONE);
        mErrorLayout.setVisibility(GONE);
    }

    public CustomEmptyView initBigUI() {
        mSmallLayout.setVisibility(GONE);
        mBigLayout.setVisibility(VISIBLE);
        mErrorLayout.setVisibility(GONE);
        return this;
    }

    public void setBigUI(int resId, String title) {
        mBigEmptyImage.setImageResource(resId);
        mBigErrorTitle.setText(title);
    }

    public CustomEmptyView initErrorUI(){
        mSmallLayout.setVisibility(GONE);
        mBigLayout.setVisibility(GONE);
        mErrorLayout.setVisibility(VISIBLE);
        return this;
    }

    public void setErrorTitle(String title){
        mEmptyErrTitle.setText(title);
    }


}
