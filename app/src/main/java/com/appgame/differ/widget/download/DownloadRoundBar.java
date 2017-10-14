package com.appgame.differ.widget.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.appgame.differ.utils.CommonUtil;


/**
 * Created by lzx on 2017/3/15.
 * 386707112@qq.com
 */

public class DownloadRoundBar extends View {

    private int defaultHeight = CommonUtil.dip2px(getContext(), 30);
    private int defaultWidth = CommonUtil.dip2px(getContext(), 30);
    private int mStrokeWidth = CommonUtil.dip2px(getContext(), 3); //进度条宽度
    private int viewHeight, viewWidth;
    private int offset = CommonUtil.dip2px(getContext(), 3);
    private Paint mPaint;
    private Paint mRectPaint;
    private Paint mProPaint; //进度
    private float sweepAngle = 0;
    private String viewColor = "#15B1B8";

    public DownloadRoundBar(Context context) {
        this(context, null, 0);
    }

    public DownloadRoundBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadRoundBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(CommonUtil.dip2px(getContext(), 1));


        mProPaint = new Paint();
        mProPaint.setAntiAlias(true);
        mProPaint.setStrokeWidth(mStrokeWidth);
        mProPaint.setStyle(Paint.Style.STROKE);
        mProPaint.setStrokeCap(Paint.Cap.ROUND);
        //ContextCompat.getColor(getContext(), viewColor)

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.FILL);

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
        mPaint.setColor(Color.parseColor(viewColor));
        mProPaint.setColor(Color.parseColor(viewColor));
        mRectPaint.setColor(Color.parseColor(viewColor));

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int width = viewWidth - paddingLeft - paddingRight;
        int height = viewHeight - paddingTop - paddingBottom;

        float radius = Math.max(width / 2, height / 2) - CommonUtil.dip2px(getContext(), 1);
        canvas.drawCircle(width / 2, height / 2, radius, mPaint);

        @SuppressLint("DrawAllocation") RectF mOval = new RectF(mStrokeWidth, mStrokeWidth, width - mStrokeWidth, height - mStrokeWidth);
        canvas.drawArc(mOval, 270, sweepAngle, false, mProPaint);

        int left = width / 2 - offset;
        int right = width / 2 + offset;
        int top = height / 2 - offset;
        int bottom = height / 2 + offset;
        canvas.drawRect(left, top, right, bottom, mRectPaint);
    }

    public void setData(float sweepAngle) {
        this.sweepAngle = sweepAngle;
        postInvalidate();
    }

    public void setViewColor(String viewColor) {
        this.viewColor = viewColor;
    }
}
