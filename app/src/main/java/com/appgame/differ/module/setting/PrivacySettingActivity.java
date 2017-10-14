package com.appgame.differ.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;

/**
 * 隐私设置
 * Created by lzx on 2017/4/12.
 * 386707112@qq.com
 */

public class PrivacySettingActivity extends BaseActivity {

    private SwitchCompat mSlideSwitchFollow, mSlideSwitchFans;
    private int public_follower = 1;
    private int public_following = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy_setting;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mSlideSwitchFollow = (SwitchCompat) findViewById(R.id.slide_switch_follow);
        mSlideSwitchFans = (SwitchCompat) findViewById(R.id.slide_switch_fans);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        public_follower = getIntent().getIntExtra("public_follower", 1);
        public_following = getIntent().getIntExtra("public_following", 1);

        mSlideSwitchFollow.setChecked(public_following == 1);
        mSlideSwitchFans.setChecked(public_follower == 1);

        mSlideSwitchFollow.setOnCheckedChangeListener((buttonView, isChecked) -> public_following = isChecked ? 1 : 0);

        mSlideSwitchFans.setOnCheckedChangeListener((buttonView, isChecked) -> public_follower = isChecked ? 1 : 0);

        setBarRightTextUI("确定", v -> {
            Intent intent = new Intent();
            intent.putExtra("public_following", public_following);
            intent.putExtra("public_follower", public_follower);
            setResult(RESULT_OK, intent);
            finish();
        });
    }



}
