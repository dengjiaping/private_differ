package com.appgame.differ.module.personal.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.base.other.SimpleTextWatcher;
import com.appgame.differ.utils.CommonUtil;

/**
 * 编辑个性签名，公司简介
 * Created by lzx on 2017/5/5.
 * 386707112@qq.com
 */

public class EditSummaryActivity extends BaseActivity {

    private String type, content;
    private EditText mEditSummary;
    private TextView mMaxNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_summary;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEditSummary = (EditText) findViewById(R.id.edit_summary);
        mMaxNum = (TextView) findViewById(R.id.max_num);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        type = getIntent().getStringExtra("type");
        content = getIntent().getStringExtra("content");

        mEditSummary.setText(content);

        LinearLayout.LayoutParams params = null;
        if (type.equals("user")) {
            setBarTitleUI("个性签名");
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.dp2px(EditSummaryActivity.this, 70));
            InputFilter[] filters = {new InputFilter.LengthFilter(50)};
            mEditSummary.setFilters(filters);
            mMaxNum.setText(content.length() + "/50");
        } else if (type.equals("organization")) {
            setBarTitleUI("企业简介");
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.dp2px(EditSummaryActivity.this, 165));
            InputFilter[] filters = {new InputFilter.LengthFilter(150)};
            mEditSummary.setFilters(filters);
            mMaxNum.setText(content.length() +"/150");
        }
        params.topMargin = CommonUtil.dip2px(this, 20);
        mEditSummary.setLayoutParams(params);

        mEditSummary.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (type.equals("user")) {
                    mMaxNum.setText(s.length() + "/50");
                } else if (type.equals("organization")) {
                    mMaxNum.setText(s.length() + "/150");
                }
            }
        });

        setBarRightTextUI("保存", v -> {
            Intent intent = new Intent();
            intent.putExtra("summary", mEditSummary.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        });
    }




}
