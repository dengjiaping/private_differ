package com.appgame.differ.module.login.view;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.module.welcome.WelcomeActivity;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.statusbar.SystemBarHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by lzx on 2017/2/27.
 * 386707112@qq.com
 */

public class SplashActivity extends BaseActivity {

    private ImageView mBgImag;
    private AnimationDrawable anim;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        SystemBarHelper.hideStatusBar(getWindow(), true);
        mBgImag = (ImageView) findViewById(R.id.welcome_bg);
        mBgImag.postDelayed(() -> {
            mBgImag.setBackgroundResource(R.drawable.anim_splash);
            anim = (AnimationDrawable) mBgImag.getBackground();
            anim.start();
            Observable.just(anim).flatMap((Function<AnimationDrawable, ObservableSource<?>>) animationDrawable -> {
                int duration = 0;
                try {
                    for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
                        duration += anim.getDuration(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return Observable.just(duration).delay(duration, TimeUnit.MILLISECONDS);
            })
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {
                        boolean isFirstOpen = SpUtil.getInstance().getBoolean(AppConstants.IS_FIRST_OPEN, true);
                        if (isFirstOpen) {
                            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });
        }, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        anim = null;
    }
}
