package com.appgame.differ.module.dynamic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.appgame.differ.R;
import com.appgame.differ.bean.game.SimpleGame;
import com.appgame.differ.utils.GlideImageLoader;
import com.appgame.differ.base.RxBaseActivity;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.dynamic.adapter.PostDynamicAdapter;
import com.appgame.differ.module.dynamic.contract.PostDynamicContract;
import com.appgame.differ.module.dynamic.presenter.PostDynamicPresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.compresshelper.CompressHelper;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lzy.imagepicker.ImagePicker.REQUEST_CODE_PREVIEW;

public class PostDynamicActivity extends RxBaseActivity implements PostDynamicContract.View {

    private RecyclerView mRecyclerView;
    private PostDynamicAdapter mAdapter;
    private PostDynamicPresenter mPresenter;
    private ArrayList<ImageItem> images = null;
    private ArrayList<ImageItem> selImageList = new ArrayList<>(); //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int IMAGE_ITEM_ADD = -1;
    private SimpleGame simpleGame;
    private String dynamicId;
    private String postType = ""; //发布还是转发
    private String target = "";
    private String targetId = "";
    private String shareContent = ""; //转发附加内容


    private OutLoginDialog dialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_post_dynamic;
    }

    @Override
    protected void init() {
        simpleGame = getIntent().getParcelableExtra("game");
        dynamicId = getIntent().getStringExtra("dynamic_id");
        postType = getIntent().getStringExtra("postType");
        target = getIntent().getStringExtra("target");
        targetId = getIntent().getStringExtra("targetId");
        shareContent = getIntent().getStringExtra("shareContent");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setBarTitleUI(postType.equals("share") ? "动态转发" : "发布动态");

        dialog = new OutLoginDialog(this);
        mAdapter = new PostDynamicAdapter(this, postType, shareContent);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.setSimpleGame(simpleGame);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new PostDynamicPresenter(this);

        setBarLeftImageUI(R.drawable.btn_back, v -> {
            dialog.show();
            dialog.setDialogTitle("你确定要取消发布吗");
            dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                @Override
                public void onYes() {
                    finish();
                }

                @Override
                public void onNo() {
                }
            });
        });

        initPicker();

        RxBus.getBus().toMainThreadObservable(this.bindToLifecycle()).subscribe(o -> {
            if (o instanceof Integer) {
                if ((int) o == IMAGE_ITEM_ADD) {
                    //打开选择,本次允许选择的数量
                    ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                    Intent intent = new Intent(PostDynamicActivity.this, ImageGridActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SELECT);
                } else {
                    //打开预览
                    Intent intentPreview = new Intent(PostDynamicActivity.this, ImagePreviewDelActivity.class);
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) mAdapter.getImageList());
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, (int) o);
                    intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }
            }
        });

        setBarRightTextUI("发布", v -> {
            if (postType.equals("share")) {
                mPresenter.postDynamic(simpleGame.getGameId(), mAdapter.getDynamicContent(), null, dynamicId, postType, target, targetId);
            } else {
                createProgressDialog(PostDynamicActivity.this, selImageList.size() == 0 ? "正在发布..." : "正在上传图片...", false);
                Observable.just(selImageList)
                        .compose(bindToLifecycle())
                        .map(imageItems -> {
                            List<String> stringList = new ArrayList<>();
                            for (int i = 0; i < imageItems.size(); i++) {
                                File oldFile = new File(imageItems.get(i).path);
                                File newFile = CompressHelper.getDefault(PostDynamicActivity.this).compressToFile(oldFile);
                                Uri uri = Uri.fromFile(newFile);
                                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                String quality = FileUtil.bitmapToBytes(bitmap);
                                stringList.add(quality);
                            }
                            return stringList;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(strings -> {
                            mPresenter.postDynamic(simpleGame.getGameId(), mAdapter.getDynamicContent(), strings, dynamicId, postType, target, "0");
                        }, throwable -> {
                            throwable.printStackTrace();
                            LogUtil.i("上传头像 = " + throwable.getMessage());
                        });
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        simpleGame = intent.getParcelableExtra("game");
        dynamicId = intent.getStringExtra("dynamic_id");
        postType = intent.getStringExtra("postType");
        shareContent = intent.getStringExtra("shareContent");
        setTitle(postType.equals("share") ? "动态转发" : "发布动态");
        mAdapter.setSimpleGame(simpleGame);
        mAdapter.notifyDataSetChanged();
    }

    private void initPicker() {
        ImagePicker.getInstance().setImageLoader(new GlideImageLoader());   //设置图片加载器
        ImagePicker.getInstance().setShowCamera(true);                      //显示拍照按钮
        ImagePicker.getInstance().setCrop(false);                            //允许裁剪（单选才有效）
        ImagePicker.getInstance().setSaveRectangle(true);                   //是否按矩形区域保存
        ImagePicker.getInstance().setSelectLimit(maxImgCount);              //选中数量限制
        ImagePicker.getInstance().setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        ImagePicker.getInstance().setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        ImagePicker.getInstance().setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        ImagePicker.getInstance().setOutPutX(1000);                         //保存文件的宽度。单位像素
        ImagePicker.getInstance().setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    mAdapter.setImages(selImageList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    mAdapter.setImages(selImageList);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }


    @Override
    public void onPostDynamicSuccess() {
        CommonUtil.HideKeyboard(mRecyclerView);
        ToastUtil.showShort("发布成功");
        RxBus.getBus().send(EvenConstant.KEY_REFRESH_DYNAMIC_LIST);
        finish();
    }

    @Override
    public void onPostDynamicError(String msg) {
        ToastUtil.showShort(msg);
    }

    @Override
    public void showProgressDialog(String msg, String type) {
        if (type.equals("show")) {
            createProgressDialog(this, msg, false);
        } else if (type.equals("update")) {
            updateProgressDialog(msg);
        } else if (type.equals("dismiss")) {
            dismissProgressDialog();
        }
    }

    @Override
    public void onBackPressed() {
        dialog.show();
        dialog.setDialogTitle("你确定要取消发布吗");
        dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
            @Override
            public void onYes() {
                finish();
            }

            @Override
            public void onNo() {
            }
        });
    }
}
