package com.appgame.differ.widget.dialog;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.zxing.CodeUtils;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.RoundCornerImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lzx on 2017/6/26.
 */

public class ShareCardDialog extends RxBaseDialog implements View.OnClickListener {
    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_share_card;
    }

    private String shareType;
    private UserAppraise mUserAppraise;
    private GameInfo mGameInfo;
    private DailyInfo mDailyInfo;
    private DynamicInfo mDynamicInfo;
    private boolean isShareToDynamic;

    private RoundCornerImageView mRoundCornerImageView;
    private TextView mShareContent, mShareUserName, mShareTitle, mShareGameName, mShareGameTag, mBtnSave, mBtnShare;
    private CircleImageView mUserHeader, mShareHeader;
    private RelativeLayout mShareRootLayout, mShareLayout, mQrLayout, mLineLayout, mShareContentLayout, mGameUserLayout;
    private LinearLayout mOperatingLayout;
    private ImageView mQrImage;

    private String shareTitle, shareText;
    private String qrText;
    private int qrSize;
    private RxPermissions mRxPermissions;

    private String tempImageName;

    @Override
    protected void init(Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mShareLayout = (RelativeLayout) findViewById(R.id.share_layout);
        mRoundCornerImageView = (RoundCornerImageView) findViewById(R.id.share_cover);
        mShareContent = (TextView) findViewById(R.id.share_content);
        mShareUserName = (TextView) findViewById(R.id.share_user_name);
        mShareTitle = (TextView) findViewById(R.id.share_title);
        mShareGameName = (TextView) findViewById(R.id.share_game_name);
        mShareGameTag = (TextView) findViewById(R.id.share_game_tag);
        mBtnSave = (TextView) findViewById(R.id.btn_save);
        mBtnShare = (TextView) findViewById(R.id.btn_share);
        mUserHeader = (CircleImageView) findViewById(R.id.share_user_header);
        mShareHeader = (CircleImageView) findViewById(R.id.share_header);
        mQrLayout = (RelativeLayout) findViewById(R.id.qr_layout);
        mLineLayout = (RelativeLayout) findViewById(R.id.line_layout);
        mShareContentLayout = (RelativeLayout) findViewById(R.id.share_content_layout);
        mGameUserLayout = (RelativeLayout) findViewById(R.id.game_user_layout);
        mShareRootLayout = (RelativeLayout) findViewById(R.id.share_root_layout);
        mOperatingLayout = (LinearLayout) findViewById(R.id.operating_layout);
        mQrImage = (ImageView) findViewById(R.id.qr_code);

        mRxPermissions = new RxPermissions(getActivity());
        shareType = getArguments().getString("shareType");
        mUserAppraise = getArguments().getParcelable("userAppraise");
        mGameInfo = getArguments().getParcelable("gameInfo");
        mDailyInfo = getArguments().getParcelable("dailyInfo");
        mDynamicInfo = getArguments().getParcelable("dynamicInfo");
        isShareToDynamic = getArguments().getBoolean("isShareToDynamic");
        qrSize = CommonUtil.dip2px(getActivity(), 50);
        if (shareType.equals("game") || shareType.equals("comment")) {
            if (mGameInfo == null) {
                dismiss();
                return;
            }
            if (AppConstants.IS_DEBUG) {
                qrText = "http://games-planet.test.appgame.com/m/gameinfo.html?id=" + mGameInfo.getGameId();
            } else {
                qrText = "http://differ.appgame.com/gameinfo.html?id=" + mGameInfo.getGameId();
            }

            String cover = TextUtils.isEmpty(mGameInfo.getCover()) ? mGameInfo.getIcon() : mGameInfo.getCover();
            Glide.with(getActivity()).load(cover).centerCrop().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).into(mRoundCornerImageView);
            mShareGameName.setText(mGameInfo.getGameNameCn());
            List<TagsInfo> tagsInfos = CommonUtil.getTagsInfoWithNum(mGameInfo.getTags(), 3);
            StringBuilder builder = new StringBuilder();
            for (TagsInfo info : tagsInfos) {
                builder.append(info.getName()).append(" | ");
            }
            String tag = builder.toString();
            if (!TextUtils.isEmpty(tag))
                tag = tag.substring(0, tag.length() - 3);
            mShareGameTag.setText(tag);

            String shareContent;
            UserInfo userInfo;
            if (CommonUtil.isLogin()) {
                shareContent = mUserAppraise != null ? mUserAppraise.getContent() : mGameInfo.getIntro();
                userInfo = mUserAppraise == null ? mGameInfo.getUser() : UserInfoManager.getImpl().getUserInfo();
            } else {
                shareContent = mGameInfo.getIntro();
                userInfo = mGameInfo.getUser();
            }

            String shareAvatar, shareNickName;
            if (shareType.equals("comment")) {
                shareAvatar = mUserAppraise.getAuthor().getAvatar();
                shareNickName = mUserAppraise.getAuthor().getNickName();
                shareContent = mUserAppraise != null ? mUserAppraise.getContent() : mGameInfo.getIntro();
            } else {
                shareAvatar = userInfo.getAvatar();
                shareNickName = userInfo.getNickName();
            }

            mShareContent.setText(shareContent);
            Glide.with(getActivity()).load(shareAvatar).diskCacheStrategy(DiskCacheStrategy.ALL).into(mUserHeader);
            mShareUserName.setText(shareNickName);
            shareTitle = mGameInfo.getGameNameCn();
            shareText = mGameInfo.getIntro();

        } else if (shareType.equals("article")) {
            if (mDailyInfo == null) {
                dismiss();
                return;
            }
            if (AppConstants.IS_DEBUG) {
                qrText = "http://games-planet.test.appgame.com/m/article.html?id=" + mDailyInfo.getId();
            } else {
                qrText = "http://differ.appgame.com/article.html?id=" + mDailyInfo.getId();
            }

            Glide.with(getActivity()).load(mDailyInfo.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mRoundCornerImageView);
            mShareHeader.setVisibility(View.VISIBLE);
            mShareTitle.setVisibility(View.VISIBLE);
            mShareGameName.setVisibility(View.GONE);
            mShareGameTag.setVisibility(View.GONE);
            mGameUserLayout.setVisibility(View.GONE);

            mShareContent.setBackgroundResource(R.color.white);
            UserInfo userInfo = mDailyInfo.getUser();
            Glide.with(getActivity()).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mShareHeader);
            mShareTitle.setText(userInfo.getNickName());
            mShareContent.setText(mDailyInfo.getTitle());
            mShareContent.getPaint().setFakeBoldText(true);
            mShareContent.setTextSize(18);

            shareTitle = mDailyInfo.getTitle();
            shareText = "来自" + mDailyInfo.getUser().getNickName() + "的文章";
        } else if (shareType.equals("dynamic")) {
            if (mDynamicInfo == null) {
                dismiss();
                return;
            }
            mShareHeader.setVisibility(View.VISIBLE);
            mShareTitle.setVisibility(View.VISIBLE);
            mShareGameName.setVisibility(View.GONE);
            mShareGameTag.setVisibility(View.GONE);
            mGameUserLayout.setVisibility(View.GONE);

            qrText = "http://differ.appgame.com";

            String cover;
            if (mDynamicInfo.getImages().size() == 0) {
                if (mDynamicInfo.getGameInfo() != null) {
                    cover = TextUtils.isEmpty(mDynamicInfo.getGameInfo().getCover()) ? mDynamicInfo.getGameInfo().getIcon() : mDynamicInfo.getGameInfo().getCover();
                } else {
                    cover = mDynamicInfo.getArticle().getCover();
                }
            } else {
                cover = mDynamicInfo.getImages().get(0);
            }
            Glide.with(getActivity()).load(cover).diskCacheStrategy(DiskCacheStrategy.ALL).into(mRoundCornerImageView);
            Glide.with(getActivity()).load(mDynamicInfo.getAuthor().getAvatar()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mShareHeader);
            mShareTitle.setText(mDynamicInfo.getAuthor().getNickName());
            mShareContent.setText(Html.fromHtml(mDynamicInfo.getContent()));
            shareTitle = Html.fromHtml(mDynamicInfo.getContent()).toString();
            shareText = "来自" + mDynamicInfo.getAuthor().getNickName() + "的动态";
        }

        mLineLayout.setVisibility(View.VISIBLE);
        mQrLayout.setVisibility(View.VISIBLE);
        mShareContentLayout.setBackgroundResource(R.color.white);

        tempImageName = "/share_temp_" + System.currentTimeMillis() + ".png";
        Bitmap qrBitmap = CodeUtils.createImage(qrText, qrSize, qrSize, null);
        mQrImage.setImageBitmap(qrBitmap);

        RxView.clicks(mBtnSave).throttleFirst(500, TimeUnit.SECONDS)
                .compose(bindToLifecycle())
                .compose(mRxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(aBoolean -> aBoolean)
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(@NonNull Boolean aBoolean) throws Exception {
                        return saveImageToLocal(mShareLayout, "share_" + System.currentTimeMillis() + ".png");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(isSuccess -> {
                    ToastUtil.showShort(isSuccess ? "保存成功" : "保存失败");
                    dismiss();
                }, throwable -> {
                    ToastUtil.showShort("保存失败");
                    dismiss();
                });

        RxView.clicks(mBtnShare).throttleFirst(500, TimeUnit.SECONDS)
                .compose(bindToLifecycle())
                .compose(mRxPermissions.ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(aBoolean -> aBoolean)
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(@NonNull Boolean aBoolean) throws Exception {
                        return saveImageToLocal(mShareLayout, tempImageName);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isSuccess -> {
                    if (isSuccess) {
                        ShareDialog dialog = new ShareDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("shareImage", FileUtil.getImagePath() + tempImageName);
                        bundle.putString("shareTitle", shareTitle);
                        bundle.putString("shareText", shareText);
                        bundle.putString("shareUrl", qrText);
                        bundle.putString("shareType", shareType);
                        bundle.putParcelable("gameInfo", mGameInfo);
                        bundle.putParcelable("dailyInfo", mDailyInfo);
                        bundle.putParcelable("dynamicInfo", mDynamicInfo);
                        bundle.putBoolean("isShareToDynamic", false);
                        bundle.putBoolean("isHideCardUI", true);
                        bundle.putBoolean("isHideCopyLineUI", true);
                        bundle.putBoolean("isHideSina", true);
                        dialog.setArguments(bundle);
                        android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.add(dialog, "ShareDialog");
                        ft.commitAllowingStateLoss();
                    } else {
                        ToastUtil.showShort("分享失败");
                    }
                    dismiss();
                }, throwable -> {
                    ToastUtil.showShort("分享失败");
                    dismiss();
                });
    }

    @Override
    public void onClick(View v) {

    }

    public Observable<Boolean> saveImageToLocal(View shareView, String shareFileName) {
        return Observable.create((ObservableOnSubscribe<Bitmap>) e -> {
            Bitmap bmp = Bitmap.createBitmap(shareView.getMeasuredWidth(), shareView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            shareView.draw(new Canvas(bmp));
            e.onNext(bmp);
        })
                .map(bitmap -> {
                    File file = FileUtil.createImageFile(shareFileName);
                    boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                    if (isSuccess) {
//                        Uri uri = Uri.fromFile(file);
//                        Intent mIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
//                        getActivity().sendBroadcast(mIntent);
                    }
                    return isSuccess;
                })
                .subscribeOn(Schedulers.io());
    }


}
