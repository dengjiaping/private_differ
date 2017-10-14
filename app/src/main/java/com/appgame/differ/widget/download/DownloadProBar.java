package com.appgame.differ.widget.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.utils.CommonUtil;


/**
 * 下载进度条
 * Created by lzx on 2017/3/2.
 * 386707112@qq.com
 */

public class DownloadProBar extends View {

    private int defaultHeight = CommonUtil.dip2px(getContext(), 70);
    private int defaultWidth = CommonUtil.dip2px(getContext(), 70);
    private int mStrokeWidth; //进度条宽度
    private float mTextSize = CommonUtil.dip2px(getContext(), 15); //百分数数字大小
    private int viewHeight, viewWidth;
    private Paint mProPaint; //进度
    private Paint mBgPaint;
    private Paint mTextPaint;

    //进度条当前角度
    private float sweepAngle = 0;

    private int currPro = 0;  //当前进度
    private String currText = "";

    private OnDownloadClickListener mOnClickListener;
    private boolean isNeedDrawText = true; //是否需要画字
    private int pro_color;
    private int pro_bg_color;


    public DownloadProBar(Context context) {
        this(context, null, 0);
    }

    public DownloadProBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownloadProBar, defStyleAttr, 0);
        isNeedDrawText = a.getBoolean(R.styleable.DownloadProBar_isNeedToDrawText, true);
        pro_color = a.getColor(R.styleable.DownloadProBar_proColor, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        pro_bg_color = a.getColor(R.styleable.DownloadProBar_proBgColor, ContextCompat.getColor(getContext(), R.color.line_color));
        mStrokeWidth = (int) a.getDimension(R.styleable.DownloadProBar_proLineWidth, CommonUtil.dip2px(getContext(), 2));
        a.recycle();

        mProPaint = new Paint();
        mProPaint.setAntiAlias(true);
        mProPaint.setStrokeWidth(mStrokeWidth);
        mProPaint.setStyle(Paint.Style.STROKE);
        mProPaint.setStrokeCap(Paint.Cap.ROUND);
        mProPaint.setColor(pro_color);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeWidth(mStrokeWidth);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setColor(pro_bg_color);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mTextPaint.setTextSize(mTextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, defaultHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, defaultHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int width = viewWidth - paddingLeft - paddingRight;
        int height = viewHeight - paddingTop - paddingBottom;

        //画底的圆
        float radius = Math.max(width / 2, height / 2) - mStrokeWidth;
        canvas.drawCircle(width / 2, height / 2, radius, mBgPaint);

        //画圆弧进度条
        @SuppressLint("DrawAllocation") RectF mOval = new RectF(mStrokeWidth, mStrokeWidth, width - mStrokeWidth, height - mStrokeWidth);
        canvas.drawArc(mOval, 270, sweepAngle, false, mProPaint);

        if (isNeedDrawText) {
            canvas.translate(width / 2, height / 2);
//            if (event != null) {
//                float textHeight, bigTextX;
//                int bigTextWidth;
//                int flag = event.getFlag();
//                if (flag == DownloadFlag.NORMAL || flag == DownloadFlag.WAITING) {
//                    //等待中
//                    currText = "等待中";
//                } else if (flag == DownloadFlag.STARTED) {
//                    //开始下载
//                    currText = currPro + "%";
//                } else if (flag == DownloadFlag.PAUSED) {
//                    //暂停下载
//                    currText = "暂停";
//                }
//                textHeight = mTextPaint.ascent() + mTextPaint.descent();
//                bigTextWidth = (int) mTextPaint.measureText(currText);
//                bigTextX = -bigTextWidth / 2;
//                canvas.drawText(currText, bigTextX, -textHeight / 2, mTextPaint);
//            } else {
//                float textHeight = mTextPaint.ascent() + mTextPaint.descent();
//                int bigTextWidth = (int) mTextPaint.measureText(currPro + "%");
//                float bigTextX = -bigTextWidth / 2;
//                canvas.drawText(currPro + "%", bigTextX, -textHeight / 2, mTextPaint);
//            }
        }
    }

    public void setData(float sweepAngle) {
        this.currPro = (int) ((sweepAngle / 360) * 100);
        this.sweepAngle = sweepAngle;
        postInvalidate();
    }

//    public void setData(float sweepAngle, DownloadEvent event) {
//        this.event = event;
//        this.currPro = (int) ((sweepAngle / 360) * 100);
//        this.sweepAngle = sweepAngle;
//        postInvalidate();
//    }

    public void setNeedDrawText(boolean needDrawText) {
        isNeedDrawText = needDrawText;
    }


    public void setOnDownloadClickListener(OnDownloadClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnDownloadClickListener {
        void onStart();

        void onPause();
    }

}
