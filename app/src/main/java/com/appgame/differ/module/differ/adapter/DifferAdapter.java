package com.appgame.differ.module.differ.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.MainActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.adapter.BaseViewHolder;
import com.appgame.differ.utils.helper.IntentHelper;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.appgame.differ.widget.nineoldandroids.animation.Animator;
import com.appgame.differ.widget.nineoldandroids.animation.AnimatorListenerAdapter;
import com.appgame.differ.widget.nineoldandroids.animation.ObjectAnimator;
import com.appgame.differ.widget.nineoldandroids.animation.PropertyValuesHolder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding2.view.RxView;
import com.liulishuo.filedownloader.FileDownloadServiceProxy;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by lzx on 2017/5/22.
 * 386707112@qq.com
 */

public class DifferAdapter extends RecyclerView.Adapter<DifferAdapter.DifferHolder> {

    private Context mContext;
    private List<RecommedInfo> mInfoList;
    private LayoutInflater mInflater;
    private OutLoginDialog dialog;
    private RxPermissions mRxPermissions;
    private OnDisLikeClickListener mDisLikeClickListener;
    private OnSwitchListener mOnSwitchListener;
    private int cardHeight, cardWidth;
    private int coverHeight;
    private OnShowAnimDialogListener mOnShowAnimDialogListener;
    private RelativeLayout.LayoutParams mParams;
    private DecimalFormat format;

    public DifferAdapter(Context context, RxPermissions rxPermissions) {
        mContext = context;
        mRxPermissions = rxPermissions;
        mInfoList = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
        dialog = new OutLoginDialog(mContext);
        format = new DecimalFormat("##0.0");

        cardHeight = CommonUtil.getPhoneHeight(mContext) * 896 / 1280;
        cardWidth = CommonUtil.getPhoneWidth(mContext) * 624 / 720;
        coverHeight = cardHeight / 2 - 50;

        mParams = new RelativeLayout.LayoutParams(cardWidth, cardHeight);
    }

    public void setInfoList(List<RecommedInfo> infoList) {
        mInfoList.clear();
        mInfoList.addAll(infoList);
        notifyDataSetChanged();
    }

    public void clear() {
        mInfoList.clear();
        notifyDataSetChanged();
    }

    public List<RecommedInfo> getInfoList() {
        return mInfoList;
    }

    public void setOnDisLikeClickListener(OnDisLikeClickListener disLikeClickListener) {
        mDisLikeClickListener = disLikeClickListener;
    }

    public void setOnShowAnimDialogListener(OnShowAnimDialogListener onShowAnimDialogListener) {
        mOnShowAnimDialogListener = onShowAnimDialogListener;
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        mOnSwitchListener = onSwitchListener;
    }
    // ExploreManager.getImpl().addExploreId(mInfoList.get(position).getGameId());

    public void removedItemUI(int position) {
        mInfoList.remove(position);
        notifyItemRemoved(position);
        if (position != mInfoList.size()) {
            notifyItemRangeChanged(position, mInfoList.size() - position);
        }
    }

    @Override
    public DifferHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_differ_card, parent, false);
        return new DifferHolder(view);
    }

    @Override
    public void onBindViewHolder(DifferHolder holder, int position) {
        if (getItemCount() == 1) {
            mParams.leftMargin = CommonUtil.dip2px(mContext, 20);
        } else if (position == 0) {
            mParams.leftMargin = CommonUtil.dip2px(mContext, 20);
        } else if (position == getItemCount() - 1) {
            mParams.rightMargin = CommonUtil.dip2px(mContext, 20);
        } else {
            mParams.leftMargin = CommonUtil.dip2px(mContext, 0);
            mParams.rightMargin = CommonUtil.dip2px(mContext, 0);
        }
        mParams.topMargin = CommonUtil.dip2px(mContext, 100);
        mParams.bottomMargin = CommonUtil.dip2px(mContext, 10);
        holder.mCardView.setLayoutParams(mParams);

        if (position == getItemCount() - 1) {
            holder.mDifferLastCard.setVisibility(View.VISIBLE);
            holder.mDifferCard.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
            holder.mBtnRound.setOnClickListener(v -> doAnim(holder));
        } else {
            holder.mDifferLastCard.setVisibility(View.GONE);
            holder.mDifferCard.setVisibility(View.VISIBLE);

            final RecommedInfo info = mInfoList.get(position);

            if (mOnSwitchListener != null) {
                mOnSwitchListener.onSwitch(TextUtils.isEmpty(info.getCover()) ? info.getIcon() : info.getCover());
            }

            //封面图标
            Glide.with(mContext).load(info.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameIcon);
            if (TextUtils.isEmpty(info.getCover())) {
                Glide.with(mContext).load(info.getIcon()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
            } else {
                Glide.with(mContext).load(info.getCover()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.mGameCover);
            }
            holder.mGameName.setText(info.getGameNameCn());
            holder.mGameDesc.setText(info.getRecommendReason());
            //评分
            @SuppressLint("DefaultLocale") String start = String.format("%.1f", Float.parseFloat(info.getGameStar()));
            if (start.endsWith("0")) {
                start = start.split("\\.")[0];
            }
            if (start.length() == 1) {
                start = start + ".0";
            }
            holder.mGameScore.setText(start);
            //标签
            holder.mFlowLayout.setAdapter(new TagAdapter<TagsInfo>(info.getTags()) {
                @Override
                public View getView(FlowLayout parent, int position, TagsInfo tagsInfo) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.item_recom_tab, holder.mFlowLayout, false);
                    tv.setText(tagsInfo.getName());
                    tv.setBackgroundResource(R.drawable.bg_tag_checked_click);
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
                    return tv;
                }
            });
            //点击事件
            holder.itemView.setOnClickListener(v -> {
                IntentHelper.startGameDetailActivity((MainActivity) mContext, holder.mGameCover, info.getGameId());
            });

            initDownload(holder, info, position);
        }
    }

    private void doAnim(DifferHolder holder) {
        holder.mBtnRound.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancleHeartAnim();
                    pressHeartAnim(holder.mBtnRound.getScaleX(), holder.mBtnRound.getScaleY(), holder.mBtnRound);
                    holder.mBtnRound.setScaleX(0.9f);
                    holder.mBtnRound.setScaleY(0.9f);
                    break;
                case MotionEvent.ACTION_UP:
                    releaseHeartAnim(holder.mBtnRound);
                    holder.mBtnRound.setScaleX(1.0f);
                    holder.mBtnRound.setScaleY(1.0f);
                    break;
            }
            return false;
        });
    }

    //取消心跳圆动画
    private void cancleHeartAnim() {
//        if (animator1 != null && animator1.isRunning()) animator1.cancel();
//        if (animator2 != null && animator2.isRunning()) animator2.cancel();
//        if (animator3 != null && animator3.isRunning()) animator3.cancel();
//        if (animator5 != null && animator5.isRunning()) animator5.cancel();
//        if (animator6 != null && animator6.isRunning()) animator6.cancel();
    }

    //按压压下效果
    private void pressHeartAnim(float scaleX, float scaleY, View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", scaleX, 0.95f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", scaleY, 0.95f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.start();
    }


    //按压抬起效果
    private void releaseHeartAnim(View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.95f, 1.0f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.95f, 1.0f);
        ObjectAnimator scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY);
        scaleAnimator.setDuration(200);
        scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleAnimator.start();
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnShowAnimDialogListener != null) {
                    mOnShowAnimDialogListener.showDialog();
                }
            }
        });
    }

    private void initDownload(DifferHolder holder, RecommedInfo recommedInfo, int position) {
        DownloadInfo downloadInfo = recommedInfo.getDownloadInfo();
        if (downloadInfo == null) {
            return;
        }

        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl); //下载路径
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id
        String downloadUrl = downloadInfo.downloadUrl; //下载地址
        DownloadManager downloadManager = new DownloadManager();
        downloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        downloadManager.initDownloadStatus(downloadInfo.downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, (percent, status) -> {
            if (status == DownLoadFlag.has_app || status == DownLoadFlag.has_pkg) {
                initDownloadCompleted(holder.mBtnLike, holder.mBtnDisLike, DownloadProgressButton.getDownloadBtnString(status));
            } else if (status == FileDownloadStatus.paused) {
                initPauseOrProgress(holder.mBtnLike, holder.mBtnDisLike, "继续", percent);
            } else {
                initReady(holder.mBtnLike, holder.mBtnDisLike);
            }
        });

        downloadManager.addUpdater(downloadUrl, (task, percent, status) -> {
            if (status == DownLoadFlag.has_pkg) {
                initDownloadCompleted(holder.mBtnLike, holder.mBtnDisLike, "安装");
            } else if (status == FileDownloadStatus.error) {
                initReady(holder.mBtnLike, holder.mBtnDisLike);
            } else {
                initPauseOrProgress(holder.mBtnLike, holder.mBtnDisLike, DownloadProgressButton.getDownloadBtnString(status), percent);
                if (!FileUtil.isExistsInstallationPackageTemp(downloadUrl) && !FileUtil.isExistsInstallationPackage(downloadUrl)) {
                    initReady(holder.mBtnLike, holder.mBtnDisLike);
                }
            }
        });

        RxView.clicks(holder.mBtnLike).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(o -> mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            downloadManager.downloadClick(mContext, aBoolean, holder.mBtnLike.getText().toString().trim(), downloadInfo, downloadId, downloadPath);
        }));

        RxView.clicks(holder.mBtnDisLike).throttleFirst(500, TimeUnit.MICROSECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                String dislikeText = holder.mBtnDisLike.getText().toString().trim();
                if (dislikeText.equals("取消")) {
                    dialog.show();
                    dialog.setDialogTitle("确定删除下载任务及安装包？");
                    dialog.setDialogBtnText("取消", "确定");
                    dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                        @Override
                        public void onYes() {
                            FileDownloader.getImpl().pause(downloadId);
                            FileDownloadServiceProxy.getImpl().clearTaskData(downloadId);
                            if (FileUtil.isExistsInstallationPackageTemp(downloadUrl)) {
                                String path = FileUtil.getTempDownloadPath(downloadUrl);
                                new File(path).delete();
                            }
                            FileUtil.deleteGamePkgFile(downloadUrl);
                            GameDownloadManager.getImpl().deleteDownloadTasks(downloadUrl, downloadInfo.downloadPackageName);
                            initReady(holder.mBtnLike, holder.mBtnDisLike);
                        }

                        @Override
                        public void onNo() {
                        }
                    });
                } else if (dislikeText.equals("不喜欢")) {
                    if (mDisLikeClickListener != null) {
                        mDisLikeClickListener.OnClick(holder.itemView, recommedInfo, holder.getLayoutPosition());
                    }
                } else if (dislikeText.equals("打开")) {
                    if (!TextUtils.isEmpty(downloadInfo.downloadPackageName))
                        CommonUtil.startApp(mContext, downloadInfo.downloadPackageName);
                    else {
                        ToastUtil.showShort("运行失败");
                    }
                } else if (dislikeText.equals("安装")) {
                    if (FileUtil.isExistsInstallationPackage(downloadInfo.downloadUrl)) {
                        CommonUtil.installApk(mContext, FileUtil.getInstallationPackageFile(downloadInfo.downloadUrl));
                    } else {
                        ToastUtil.showShort("找不到安装包");
                    }
                }
            }
        });
    }

    public void startAllAutoTasks() {
        for (RecommedInfo info : mInfoList) {
            DownloadInfo downloadInfo = info.getDownloadInfo();
            DownloadManager.getImpl().startAllAutoTasks(downloadInfo);
        }
    }

    /**
     * 下载完成
     */
    private void initDownloadCompleted(TextView mBtnLike, TextView mBtnDisLike, String btnLikeText) {
        mBtnLike.setVisibility(View.INVISIBLE);
        mBtnDisLike.setText(btnLikeText);
        mBtnDisLike.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
        mBtnLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mBtnDisLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void initReady(TextView mBtnLike, TextView mBtnDisLike) {
        mBtnLike.setVisibility(View.VISIBLE);
        mBtnLike.setText("好想玩");
        mBtnDisLike.setText("不喜欢");

        mBtnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_def, 0, 0, 0);
        mBtnDisLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dislike_def, 0, 0, 0);

        mBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.black_alpha_50));
        mBtnDisLike.setTextColor(ContextCompat.getColor(mContext, R.color.black_alpha_50));
    }

    @SuppressLint("SetTextI18n")
    private void initPauseOrProgress(TextView mBtnLike, TextView mBtnDisLike, String btnLikeText, float percent) {
        if (btnLikeText.equals("继续")) {
            mBtnLike.setText(btnLikeText);
        } else {

            mBtnLike.setText(format.format(percent * 100) + "%");
        }
        mBtnDisLike.setText("取消");
        mBtnLike.setTextColor(ContextCompat.getColor(mContext, R.color.btn_normal));
        mBtnDisLike.setTextColor(ContextCompat.getColor(mContext, R.color.black_alpha_50));
        mBtnLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        mBtnDisLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return mInfoList.size() + 1;
    }

    class DifferHolder extends BaseViewHolder {
        private RelativeLayout mCardView;
        private RelativeLayout mDifferLastCard;
        private ImageView mGameCover;
        private ShapeImageView mGameIcon;
        private TextView mGameName, mGameDesc, mGameScore, mBtnLike, mBtnDisLike;
        private TagFlowLayout mFlowLayout;
        private CardView mDifferCard;
        private ImageView mBtnRound;

        DifferHolder(View itemView) {
            super(itemView, mContext, false);
            mCardView = $(R.id.cardView);
            mDifferCard = $(R.id.differ_card);
            mDifferLastCard = $(R.id.differ_last_card);
            mGameCover = $(R.id.game_cover);
            mGameIcon = $(R.id.game_icon);
            mGameName = $(R.id.game_name);
            mGameDesc = $(R.id.game_desc);
            mGameScore = $(R.id.game_score);
            mBtnLike = $(R.id.btn_like);
            mBtnDisLike = $(R.id.btn_dislike);
            mFlowLayout = $(R.id.tab_layout);

            mBtnRound = $(R.id.btn_round);

//            mCardView.getLayoutParams().width = cardWidth;
//            mCardView.getLayoutParams().height = cardHeight;
//            mCardView.requestLayout();
            //   mCardView.setLayoutParams(mParams);

            mGameCover.getLayoutParams().height = coverHeight;
            mGameCover.requestLayout();

        }
    }

    public interface OnDisLikeClickListener {
        void OnClick(View view, RecommedInfo recommedInfo, int position);
    }

    public interface OnSwitchListener {
        void onSwitch(String imageUrl);
    }

    public interface OnShowAnimDialogListener {
        void showDialog();
    }
}
