package com.appgame.differ.module.game;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.download.GlideDownloadImageUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by lzx on 2017/5/9.
 * 386707112@qq.com
 */

public class GameImagePageFragment extends BaseFragment implements RequestListener<String, GlideDrawable> {

    private ImageView mImageView;
    private TextView mImageError;
    private static final String EXTRA_URL = "extra_url";
    private String url;
    private PhotoViewAttacher mPhotoViewAttacher;
    private RxPermissions mRxPermissions;

    public static GameImagePageFragment newInstance(String url) {
        GameImagePageFragment fragment = new GameImagePageFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(EXTRA_URL, url);
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_game_image_page;
    }

    @Override
    public void initVariables() {
        super.initVariables();
        mRxPermissions = new RxPermissions(getActivity());
        url = getArguments().getString(EXTRA_URL);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mImageView = (ImageView) $(R.id.game_image);
        mImageError = (TextView) $(R.id.tv_image_error);
    }

    @Override
    protected void initDatas() {
        Glide.with(this).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade(0)
                .listener(this)
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    private void saveImageToGallery() {
        Observable.just(R.string.app_name)
                .compose(this.bindToLifecycle())
                .compose(mRxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(aBoolean -> aBoolean)
                .flatMap(new Function<Boolean, ObservableSource<Uri>>() {

                    @Override
                    public ObservableSource<Uri> apply(@NonNull Boolean aBoolean) throws Exception {
                        return GlideDownloadImageUtil.saveImageToLocal(getActivity(), url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> {
                    File appDir = new File(Environment.getExternalStorageDirectory(), "differ");
                    String msg = String.format("图片已保存至 %s 文件夹", appDir.getAbsolutePath());
                    ToastUtil.showShort(msg);
                }, throwable -> ToastUtil.showShort("保存失败,请重试"));
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        mImageError.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        mImageView.setImageDrawable(resource);
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
        mImageError.setVisibility(View.GONE);
        setPhotoViewAttacher();
        return false;
    }

    private void setPhotoViewAttacher() {
        mPhotoViewAttacher.setOnLongClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setMessage("是否保存到本地?")
                    .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
                    .setPositiveButton("确定", (dialog, which) -> {
                        saveImageToGallery();
                        dialog.dismiss();
                    })
                    .show();
            return true;
        });

        mPhotoViewAttacher.setOnViewTapListener(
                (view, v, v1) -> RxBus.getBus().send(EvenConstant.KEY_IMAGE_PAGE_HIDE_APP_BAR));
        mImageError.setOnClickListener(v -> RxBus.getBus().send(EvenConstant.KEY_IMAGE_PAGE_HIDE_APP_BAR));
    }

    public View getSharedElement() {
        return mImageView;
    }
}
