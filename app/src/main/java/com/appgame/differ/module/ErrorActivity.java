package com.appgame.differ.module;

import android.content.Intent;
import android.os.Bundle;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.widget.CustomEmptyView;

/**
 * Created by lzx on 2017/6/21.
 */

public class ErrorActivity extends BaseActivity {

    private CustomEmptyView mEmptyView;
    private String barTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_error;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        barTitle = getIntent().getStringExtra("barTitle");
        setBarTitleUI(barTitle);

        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mEmptyView.initErrorUI().setErrorTitle("该游戏已被吸入宇宙黑洞，无法查看");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        barTitle = intent.getStringExtra("barTitle");
        setBarTitleUI(barTitle);
    }
}
