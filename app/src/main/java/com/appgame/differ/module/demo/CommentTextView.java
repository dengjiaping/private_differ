package com.appgame.differ.module.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.utils.ToastUtil;

/**
 * Created by lzx on 2017/7/14.
 */

public class CommentTextView extends AppCompatTextView {


    @Override
    public void setMaxLines(int maxlines) {
        super.setMaxLines(maxlines);
    }

    public CommentTextView(Context context) {
        super(context, null, 0);
    }

    public CommentTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CommentTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredWidth() > 0) {
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void setCommentText(String nickName, String content) {

        ForegroundColorSpan colorNickNameSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black));
        ForegroundColorSpan colorRePlayNickNameSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.black));

        int nickNameLength = nickName.length() + 1;

        //  PraiseClick praiseClick = new PraiseClick(getContext(), nickName);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (content.contains("回复#Replay#")) {
            content = content.replace("#Replay#", "");
            int replayNickNameLength = nickNameLength + content.indexOf("回复") + 2;
            builder.append(nickName).append(":").append(content);
            builder.setSpan(colorNickNameSpan, 0, nickNameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //builder.setSpan(colorRePlayNickNameSpan, replayNickNameLength, nickNameLength + content.indexOf(" "), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //builder.setSpan(praiseClick, replayNickNameLength, nickNameLength + content.indexOf(" "), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            builder.append(nickName).append(":").append(content);
            builder.setSpan(colorNickNameSpan, 0, nickNameLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        setText(builder);
    }

    private class PraiseClick extends ClickableSpan {

        private Context mContext;
        private String nickName;

        PraiseClick(Context context, String nickName) {
            mContext = context;
            this.nickName = nickName;
        }

        @Override
        public void onClick(View widget) {
            ToastUtil.showShort(nickName);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(ContextCompat.getColor(mContext, R.color.btn_normal));
            ds.setUnderlineText(false);
        }
    }

}
