package com.appgame.differ.widget.download;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.liulishuo.filedownloader.model.FileDownloadStatus;

import java.text.DecimalFormat;

/**
 * Created by hiwhitley on 17-2-20.
 */

@SuppressLint("AppCompatCustomView")
public class DownloadProgressButton extends TextView {

    //背景画笔
    private Paint mBackgroundPaint;
    //按钮文字画笔
    private volatile Paint mTextPaint;
    //背景颜色
    private int mBackgroundColor;
    //下载中后半部分后面背景颜色
    private int mBackgroundSecondColor;
    //文字颜色
    private int mTextColor;
    //文字大小
    private float mTextSize;
    //覆盖后颜色
    private int mTextCoverColor;

    private float mButtonRadius;
    //边框宽度
    private float mBorderWidth;
    //点动画样式
    //private int mBallStyle = STYLE_BALL_JUMP;

    private float mProgress = -1;
    //private float mToProgress;
    private int mMaxProgress;
    private int mMinProgress;
    private float mProgressPercent;

    //是否显示边框，默认是true
    private boolean showBorder;
    private RectF mBackgroundBounds;
    private LinearGradient mProgressTextGradient;

    //下载平滑动画
    //private ValueAnimator mProgressAnimation;

    //记录当前文字
    private CharSequence mCurrentText = "";

    public static final int STATE_NORMAL = 0;//开始下载
    public static final int STATE_DOWNLOADING = 1;//下载之中
    public static final int STATE_PAUSE = 2;//暂停下载
    public static final int STATE_FINISH = 3;//下载完成

    private int mState;

    private PorterDuffXfermode porterDuffXfermode;
    private DecimalFormat format;

    public DownloadProgressButton(Context context) {
        this(context, null);
    }

    public DownloadProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
        format = new DecimalFormat("##0.0");
        if (!isInEditMode()) {
            initAttrs(context, attrs);
            init();
            // setupAnimations();
        }
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgressButton);
        try {
            mBackgroundColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_background_color, Color.parseColor("#3385FF"));
            mBackgroundSecondColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_background_second_color, Color.parseColor("#E8E8E8"));
            mButtonRadius = a.getDimension(R.styleable.DownloadProgressButton_progress_btn_radius, 0);
            mTextColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_text_color, mBackgroundColor);
            mTextCoverColor = a.getColor(R.styleable.DownloadProgressButton_progress_btn_text_cover_color, Color.WHITE);
            mBorderWidth = a.getDimension(R.styleable.DownloadProgressButton_progress_btn_border_width, dp2px(2));
            // mBallStyle = a.getInt(R.styleable.DownloadProgressButton_progress_btn_ball_style, STYLE_BALL_JUMP);
            mTextSize = a.getDimension(R.styleable.DownloadProgressButton_progress_btn_text_size, 10f);
        } finally {
            a.recycle();
        }
    }

    private void init() {
        mMaxProgress = 100;
        mMinProgress = 0;
        mProgress = 0;
        showBorder = true;

        //设置背景画笔
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        //设置文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //解决文字有时候画不出问题
            setLayerType(LAYER_TYPE_SOFTWARE, mTextPaint); //硬件加速
        }

        //初始化状态设为NORMAL
        mState = STATE_NORMAL;
        invalidate();
    }

//    private void setupAnimations() {
//        //ProgressBar的动画
//        mProgressAnimation = ValueAnimator.ofFloat(0, 1).setDuration(500);
//        mProgressAnimation.addUpdateListener(animation -> {
//            float timePercent = (float) animation.getAnimatedValue();
//            mProgress = ((mToProgress - mProgress) * timePercent + mProgress);
//            invalidate();
//        });
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInEditMode()) {
            drawing(canvas);
        }
    }

    private void drawing(Canvas canvas) {
        drawBackground(canvas);
        drawTextAbove(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mBackgroundBounds = new RectF();
        //根据Border宽度得到Button的显示区域
        mBackgroundBounds.left = showBorder ? mBorderWidth : 0;
        mBackgroundBounds.top = showBorder ? mBorderWidth : 0;
        mBackgroundBounds.right = getMeasuredWidth() - (showBorder ? mBorderWidth : 0);
        mBackgroundBounds.bottom = getMeasuredHeight() - (showBorder ? mBorderWidth : 0);

        if (showBorder) {
            mBackgroundPaint.setStyle(Paint.Style.STROKE);
            mBackgroundPaint.setColor(mBackgroundColor);
            mBackgroundPaint.setStrokeWidth(mBorderWidth);
            canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
        }
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        //color
        switch (mState) {
            case STATE_NORMAL:
                mBackgroundPaint.setColor(mBackgroundColor);
                canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                break;
            case STATE_PAUSE:
            case STATE_DOWNLOADING:
                //计算当前的进度
                mProgressPercent = mProgress / (mMaxProgress + 0f);
                mBackgroundPaint.setColor(mBackgroundSecondColor);
                canvas.save();
                //画出dst图层
                canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                //设置图层显示模式为 SRC_ATOP

                mBackgroundPaint.setColor(mBackgroundColor);
                mBackgroundPaint.setXfermode(porterDuffXfermode);
                //计算 src 矩形的右边界
                float right = mBackgroundBounds.right * mProgressPercent;
                //在dst画出src矩形
                canvas.drawRect(mBackgroundBounds.left, mBackgroundBounds.top, right, mBackgroundBounds.bottom, mBackgroundPaint);
                canvas.restore();
                mBackgroundPaint.setXfermode(null);
                break;
            case STATE_FINISH:
                mBackgroundPaint.setColor(mBackgroundColor);
                canvas.drawRoundRect(mBackgroundBounds, mButtonRadius, mButtonRadius, mBackgroundPaint);
                break;
        }
    }

    private void drawTextAbove(Canvas canvas) {
        //计算Baseline绘制的Y坐标
        final float y = canvas.getHeight() / 2 - (mTextPaint.descent() / 2 + mTextPaint.ascent() / 2);
        if (mCurrentText == null) {
            mCurrentText = "";
        }
        final float textWidth = mTextPaint.measureText(mCurrentText.toString());
        //color
        switch (mState) {
            case STATE_NORMAL:
                mTextPaint.setShader(null);
                mTextPaint.setColor(mTextCoverColor);
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
            case STATE_PAUSE:
            case STATE_DOWNLOADING:
                //进度条压过距离
                float coverLength = getMeasuredWidth() * mProgressPercent;
                //开始渐变指示器
                float indicator1 = getMeasuredWidth() / 2 - textWidth / 2;
                //结束渐变指示器
                float indicator2 = getMeasuredWidth() / 2 + textWidth / 2;
                //文字变色部分的距离
                float coverTextLength = textWidth / 2 - getMeasuredWidth() / 2 + coverLength;
                float textProgress = coverTextLength / textWidth;
                if (coverLength <= indicator1) {
                    mTextPaint.setShader(null);
                    mTextPaint.setColor(mTextColor);
                } else if (indicator1 < coverLength && coverLength <= indicator2) {
                    //设置变色效果
                    mProgressTextGradient = new LinearGradient((getMeasuredWidth() - textWidth) / 2, 0, (getMeasuredWidth() + textWidth) / 2, 0,
                            new int[]{mTextCoverColor, mTextColor},
                            new float[]{textProgress, textProgress + 0.001f},
                            Shader.TileMode.CLAMP);
                    mTextPaint.setColor(mTextColor);
                    mTextPaint.setShader(mProgressTextGradient);
                } else {
                    mTextPaint.setShader(null);
                    mTextPaint.setColor(mTextCoverColor);
                }
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
            case STATE_FINISH:
                mTextPaint.setColor(mTextCoverColor);
                canvas.drawText(mCurrentText.toString(), (getMeasuredWidth() - textWidth) / 2, y, mTextPaint);
                break;
        }
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        if (mState != state) {//状态确实有改变
            this.mState = state;
            invalidate();
        }
    }

    /**
     * 设置当前按钮文字
     */
    public void setCurrentText(CharSequence charSequence) {
        mCurrentText = charSequence;
        invalidate();
    }

    public String getCurrentText() {
        return mCurrentText.toString().trim();
    }

    /**
     * 设置带下载进度的文字
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setProgressText(String text, float progress, boolean isNeedPercent) {
        if (progress >= mMinProgress && progress <= mMaxProgress) {

            if (isNeedPercent) {
                mCurrentText = format.format(progress) + "%";
            } else {
                mCurrentText = text;
            }
            mProgress = progress;
        } else if (progress < mMinProgress) {
            mProgress = 0;
            mCurrentText = text;
        } else if (progress > mMaxProgress) {
            mProgress = 100;
            mCurrentText = text + progress + "%";
        } else {
            mCurrentText = text;
            mProgress = progress;
        }
        invalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mState = ss.state;
        mProgress = ss.progress;
        mCurrentText = ss.currentText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, (int) mProgress, mState, mCurrentText.toString());
    }

    private static class SavedState extends BaseSavedState {

        private int progress;
        private int state;
        private String currentText;

        SavedState(Parcelable parcel, int progress, int state, String currentText) {
            super(parcel);
            this.progress = progress;
            this.state = state;
            this.currentText = currentText;
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
            state = in.readInt();
            currentText = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
            out.writeInt(state);
            out.writeString(currentText);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    public void setProgressUI(float percent, int status) {
        if (status == FileDownloadStatus.pending) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("等待中", 0, false);
            setEnabled(false);
        } else if (status == FileDownloadStatus.started) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("等待中", 0, false);
            setEnabled(false);
        } else if (status == FileDownloadStatus.connected) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("已连接", 0, false);
            setEnabled(false);
        } else if (status == FileDownloadStatus.progress) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("暂停", percent * 100, true);
            setEnabled(true);
        } else if (status == DownLoadFlag.has_pkg) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("安装", 100, false);
            setEnabled(true);
        } else if (status == FileDownloadStatus.paused) {
            setState(DownloadProgressButton.STATE_PAUSE);
            setProgressText("继续", percent * 100, false);
            setEnabled(true);
        } else if (status == FileDownloadStatus.error) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("出错", 0, false);
            setEnabled(true);
        } else if (status == FileDownloadStatus.warn) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("警告", 0, false);
            setEnabled(true);
        } else if (status == DownLoadFlag.has_app) {
            setState(DownloadProgressButton.STATE_NORMAL);
            setProgressText("打开", 100, false);
            setEnabled(true);
        } else if (status == DownLoadFlag.no_download_url) {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("敬请期待", 100, false);
            setEnabled(false);
        } else {
            setState(DownloadProgressButton.STATE_DOWNLOADING);
            setProgressText("下载", 0, false);
        }
    }
    //打开 安装 等待中 开始 已连接 继续 警告 出错 下载中

    public static String getDownloadBtnString(int status) {
        if (status == FileDownloadStatus.pending) {
            return "等待中";

        } else if (status == FileDownloadStatus.started) {
            return "等待中";

        } else if (status == FileDownloadStatus.connected) {
            return "已连接";

        } else if (status == FileDownloadStatus.progress) {
            return "暂停";

        } else if (status == DownLoadFlag.has_pkg) {
            return "安装";

        } else if (status == FileDownloadStatus.paused) {
            return "继续";

        } else if (status == FileDownloadStatus.error) {
            return "出错";

        } else if (status == FileDownloadStatus.warn) {
            return "警告";

        } else if (status == DownLoadFlag.has_app) {
            return "打开";
        } else if (status == DownLoadFlag.no_download_url) {
            return "敬请期待";
        } else {
            return "下载";
        }
    }

}
