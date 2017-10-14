package com.appgame.differ.module.game;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.ImmersiveUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.download.GlideDownloadImageUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.viewpager.DepthTransFormes;
import com.jakewharton.rxbinding2.view.RxMenuItem;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lzx on 2017/5/9.
 * 386707112@qq.com
 */

public class GameImagePageActivity extends BaseActivity {

    private String url;
    private int currenIndex;
    private List<String> imgUrls;
    private String gameName;
    private boolean isHide = false;
    private ViewPager mViewPager;
    private GameImagePagerAdapter mPagerAdapter;
    private RxPermissions mRxPermissions;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_image_page;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mRxPermissions = new RxPermissions(this);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        imgUrls = new ArrayList<>();
        imgUrls = getIntent().getStringArrayListExtra("imgUrl");
        currenIndex = getIntent().getIntExtra("position", -1);
        gameName = getIntent().getStringExtra("name");

        setTitle(gameName);
        mAppBarLayout.setAlpha(0.5f);
        mToolbar.setBackgroundResource(R.color.black_alpha_90);
        mAppBarLayout.setBackgroundResource(R.color.black_alpha_90);

        mPagerAdapter = new GameImagePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new DepthTransFormes());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currenIndex = position;
                url = imgUrls.get(currenIndex);
            }
        });

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_IMAGE_PAGE_HIDE_APP_BAR)) {
                hideOrShowToolbar();
            }
        });

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                String url = imgUrls.get(mViewPager.getCurrentItem());
                GameImagePageFragment fragment = (GameImagePageFragment) mPagerAdapter.instantiateItem(mViewPager, currenIndex);
                sharedElements.clear();
                sharedElements.put(url, fragment.getSharedElement());
            }
        });
    }


    public static Intent luanch(Context context, String gameName, int position, List<String> imgUrl) {
        Intent mIntent = new Intent(context, GameImagePageActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra("name", gameName);
        mIntent.putExtra("position", position);
        mIntent.putStringArrayListExtra("imgUrl", (ArrayList<String>) imgUrl);
        return mIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.meun_game_image, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_download);
        saveImage(menuItem);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(currenIndex);
        url = imgUrls.get(currenIndex);
    }

    protected void hideOrShowToolbar() {
        if (isHide) {
            //显示
            ImmersiveUtil.exit(this);
            mAppBarLayout.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            isHide = false;
        } else {
            //隐藏
            ImmersiveUtil.enter(this);
            mAppBarLayout.animate()
                    .translationY(-mAppBarLayout.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            isHide = true;
        }
    }

    /**
     * 保存图片到本地
     */
    private void saveImage(MenuItem menuItem) {
        RxMenuItem.clicks(menuItem)
                .compose(bindToLifecycle())
                .compose(mRxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(aBoolean -> aBoolean)
                .flatMap(new Function<Boolean, ObservableSource<Uri>>() {
                    @Override
                    public ObservableSource<Uri> apply(@NonNull Boolean aBoolean) throws Exception {
                        return GlideDownloadImageUtil.saveImageToLocal(GameImagePageActivity.this, url);
                    }
                })
                .map(uri -> {
                    String msg = String.format("图片已保存至 %s 文件夹",
                            new File(Environment.getExternalStorageDirectory(), "differ").getAbsolutePath());
                    return msg;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(ToastUtil::showShort, throwable -> ToastUtil.showShort("保存失败,请重试"));
    }

    @Override
    public void supportFinishAfterTransition() {
        Intent data = new Intent();
        data.putExtra("index", currenIndex);
        RxBus.getBus().send(data);
        super.supportFinishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private class GameImagePagerAdapter extends FragmentStatePagerAdapter {

        public GameImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return GameImagePageFragment.newInstance(imgUrls.get(position));
        }

        @Override
        public int getCount() {
            return imgUrls.size();
        }
    }
}
