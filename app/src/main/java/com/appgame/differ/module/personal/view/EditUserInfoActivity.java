package com.appgame.differ.module.personal.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.base.other.SimpleTextWatcher;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.personal.contract.EditUserInfoContract;
import com.appgame.differ.module.personal.presenter.EditUserInfoPresenter;
import com.appgame.differ.module.setting.PrivacySettingActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.GlideImageLoader;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.compresshelper.CompressHelper;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CircleImageView;
import com.appgame.differ.widget.dialog.DatePickerDialog;
import com.appgame.differ.widget.dialog.SelectSexDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appgame.differ.module.dynamic.PostDynamicActivity.REQUEST_CODE_SELECT;

/**
 * 修改个人信息
 * Created by lzx on 2017/3/1.
 * 386707112@qq.com
 */

public class EditUserInfoActivity extends BaseActivity<EditUserInfoContract.Presenter,String> implements OnClickListener, EditUserInfoContract.View {

    private TextView mTextHeader, mTextBirthday, mUserBirthday, mTextPrivacy, mUserSex, mTextSex, mNetworkLayout, mUserSummary, mBtnUploadCover;
    private CircleImageView mCircleImageView;
    private EditText mEditUserName;
    private ImageView mBtnDelete, mUserCover;
    private RelativeLayout mSummaryLayout;

    private Map<String, String> photoPath;

    private SelectSexDialog sexDialog;
    private int public_follower = 1;
    private int public_following = 1;
    private String selectType;
    private UserInfo mUserInfo;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_userinfo;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new EditUserInfoPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTextHeader = (TextView) findViewById(R.id.text_header);
        mCircleImageView = (CircleImageView) findViewById(R.id.user_header);
        mEditUserName = (EditText) findViewById(R.id.edit_username);
        mBtnDelete = (ImageView) findViewById(R.id.btn_delete);
        mUserCover = (ImageView) findViewById(R.id.user_cover);
        mTextBirthday = (TextView) findViewById(R.id.text_birthday);
        mUserBirthday = (TextView) findViewById(R.id.user_birthday);
        mTextPrivacy = (TextView) findViewById(R.id.text_privacy);
        mUserSex = (TextView) findViewById(R.id.user_sex);
        mBtnUploadCover = (TextView) findViewById(R.id.btn_upload_cover);

        mTextSex = (TextView) findViewById(R.id.text_sex);
        mNetworkLayout = (TextView) findViewById(R.id.network_layout);
        mUserSummary = (TextView) findViewById(R.id.user_summary);
        mSummaryLayout = (RelativeLayout) findViewById(R.id.summary_layout);

        mBtnDelete.setOnClickListener(this);
        mTextHeader.setOnClickListener(this);
        mCircleImageView.setOnClickListener(this);
        mTextBirthday.setOnClickListener(this);
        mTextPrivacy.setOnClickListener(this);
        mTextSex.setOnClickListener(this);
        mSummaryLayout.setOnClickListener(this);
        mBtnUploadCover.setOnClickListener(this);

        mEditUserName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                mBtnDelete.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });

        setBarRightTextUI("保存", v -> {
            createProgressDialog(EditUserInfoActivity.this, "正在修改,请稍后...", false);
            String nickname = mEditUserName.getText().toString().trim();
            String sex = mUserSex.getText().toString().trim();
            String birthday = mUserBirthday.getText().toString().trim();
            String signature = mUserSummary.getText().toString().trim();
            mPresenter.updateUserInfo(photoPath, nickname, sex, birthday, signature, public_follower, public_following);
        });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        photoPath = new HashMap<>();
        sexDialog = new SelectSexDialog(this);
        mUserInfo = getIntent().getParcelableExtra("userInfo");

        if (mUserInfo != null) {
            mEditUserName.setText(CommonUtil.getNickName(mUserInfo.getNickName()));
            Glide.with(this).load(mUserInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy.NONE).into(mCircleImageView);
            Glide.with(this).load(mUserInfo.getCover()).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).into(mUserCover);
            public_follower = Integer.parseInt(mUserInfo.getPublicFollower());
            public_following = Integer.parseInt(mUserInfo.getPublicFollowing());
            mUserSex.setText(Integer.parseInt(mUserInfo.getSex()) == 1 ? "男" : "女");
            mUserBirthday.setText(TextUtils.isEmpty(mUserInfo.getBirthday()) ? "未设置" : mUserInfo.getBirthday());
            mUserSummary.setText(TextUtils.isEmpty(mUserInfo.getRemark()) ? "" : mUserInfo.getRemark());
        }

        RxBus.getBus().toMainThreadObservable(this.bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                mNetworkLayout.setVisibility(event.isNetWorkConnected ? View.GONE : View.VISIBLE);
            }
        });

        ImagePicker.getInstance().setCrop(true);
        ImagePicker.getInstance().setImageLoader(new GlideImageLoader());   //设置图片加载器
        ImagePicker.getInstance().setShowCamera(true);                      //显示拍照按钮
        ImagePicker.getInstance().setSaveRectangle(true);                   //是否按矩形区域保存
        ImagePicker.getInstance().setSelectLimit(1);              //选中数量限制
    }

    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_header:
            case R.id.user_header:
                selectType = "user_header";
                ImagePicker.getInstance().setFocusWidth(CommonUtil.dip2px(this, 150));                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
                ImagePicker.getInstance().setFocusHeight(CommonUtil.dip2px(this, 150));                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
                ImagePicker.getInstance().setOutPutX(CommonUtil.dip2px(this, 128));                         //保存文件的宽度。单位像素
                ImagePicker.getInstance().setOutPutY(CommonUtil.dip2px(this, 128));                         //保存文件的高度。单位像素
                ImagePicker.getInstance().setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
                intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
            case R.id.btn_delete:
                mEditUserName.setText("");
                break;
            case R.id.text_birthday:
                DatePickerDialog pickerDialog = new DatePickerDialog(this, mUserBirthday.getText().toString());
                pickerDialog.show();
                pickerDialog.setOnSelectListener(text -> mUserBirthday.setText(text));
                break;
            case R.id.text_privacy:
                intent = new Intent(this, PrivacySettingActivity.class);
                intent.putExtra("public_follower", public_follower);
                intent.putExtra("public_following", public_following);
                startActivityForResult(intent, 1001);
                break;
            case R.id.text_sex:
                sexDialog.show();
                sexDialog.setOnSelectListener(sex -> mUserSex.setText(sex));
                break;
            case R.id.summary_layout:
                intent = new Intent(this, EditSummaryActivity.class);
                intent.putExtra("type", "user");
                intent.putExtra("content", mUserSummary.getText().toString().trim());
                startActivityForResult(intent, 1002);
                break;
            case R.id.user_cover:
            case R.id.btn_upload_cover:
                selectType = "user_cover";
                ImagePicker.getInstance().setFocusWidth(CommonUtil.dip2px(this, 720)); //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
                ImagePicker.getInstance().setFocusHeight(CommonUtil.dip2px(this, 300));  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
                ImagePicker.getInstance().setOutPutX(CommonUtil.dip2px(this, 720));     //保存文件的宽度。单位像素
                ImagePicker.getInstance().setOutPutY(CommonUtil.dip2px(this, 300));     //保存文件的高度。单位像素
                ImagePicker.getInstance().setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
                intent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT);
                break;
        }
    }

    @Override
    public void onUpdateSuccess() {
        dismissProgressDialog();
        ToastUtil.showShort("修改成功");
        RxBus.getBus().send(EvenConstant.KEY_REFRESH_USER_INFO);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        dismissProgressDialog();
        ToastUtil.showShort(msg);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                try {
                    List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    if (images != null) {
                        ImageItem imageItem = images.get(0);
                        File oldFile = new File(imageItem.path);
                        File newFile = CompressHelper.getDefault(this).compressToFile(oldFile);
                        Uri uri = Uri.fromFile(newFile);
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        String quality = FileUtil.bitmapToBytes(bitmap);
                        photoPath.put(selectType, quality);
                        if (selectType.equals("user_header")) {
                            Glide.with(EditUserInfoActivity.this).load(imageItem.path).into(mCircleImageView);
                        } else if (selectType.equals("user_cover")) {
                            Glide.with(EditUserInfoActivity.this).load(imageItem.path).centerCrop().into(mUserCover);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001:
                    public_following = data.getIntExtra("public_following", 1);
                    public_follower = data.getIntExtra("public_follower", 1);
                    break;
                case 1002:
                    mUserSummary.setText(data.getStringExtra("summary"));
                    break;
            }
        }
    }
}
