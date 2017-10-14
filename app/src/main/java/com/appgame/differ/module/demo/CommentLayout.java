package com.appgame.differ.module.demo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.CommentReplies;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.utils.CommonUtil;

import java.util.List;

/**
 * Created by lzx on 2017/7/14.
 */

public class CommentLayout extends LinearLayout {

    private LinearLayout.LayoutParams textViewParams;
    private int textColor;
    private int currLayoutHeight = 0;
    private onUserGuestClickListener mOnUserGuestClickListener;
    private onClickListener mOnClickListener;

    public CommentLayout(Context context) {
        this(context, null, 0);
    }

    public CommentLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //   textColor = ContextCompat.getColor(getContext(), R.color.btn_normal);
        textViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.leftMargin = CommonUtil.dip2px(context, 5);
        textViewParams.rightMargin = CommonUtil.dip2px(context, 5);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#F6F6F6"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (currLayoutHeight == 0)
            currLayoutHeight = getMeasuredHeight();
    }

    public void setOnUserGuestClickListener(onUserGuestClickListener onUserGuestClickListener) {
        mOnUserGuestClickListener = onUserGuestClickListener;
    }

    public void setOnClickListener(onClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void addCommentReplies(List<CommentReplies> replies) {
        if (replies == null || replies.size() == 0) return;
        final int childCount = getChildCount();
//        if (childCount < replies.size()) {
//            //当前的view少于list的长度，则补充相差的view
//            int subCount = replies.size() - childCount;
//            for (int i = 0; i < subCount; i++) {
//                CommentTextView commentWidget = new CommentTextView(getContext());
//                commentWidget.setLayoutParams(textViewParams);
//                commentWidget.setTextColor(ContextCompat.getColor(getContext(), R.color.black_alpha_50));
//                addView(commentWidget);
//            }
//        } else if (childCount > replies.size()) {
//            //当前的view的数目比list的长度大，则减去对应的view
//            removeViews(replies.size(), childCount - replies.size());
//        }

        removeAllViews();
        for (int i = 0; i < replies.size(); i++) {
            CommentTextView commentWidget = new CommentTextView(getContext());
            commentWidget.setLayoutParams(textViewParams);
            commentWidget.setTextColor(ContextCompat.getColor(getContext(), R.color.black_alpha_50));
            addView(commentWidget);
        }

        //绑定数据
        for (int i = 0; i < replies.size(); i++) {
            CommentTextView commentWidget = (CommentTextView) getChildAt(i);
            CommentReplies commentReplies = replies.get(i);
            if (commentWidget != null) {
                commentWidget.setCommentText(CommonUtil.getNickName(commentReplies.getRelationships().getNickName()), commentReplies.getContent());
            }
        }
    }

    public void addUserGuests(List<UserGuest> userGuestList) {
        if (userGuestList == null || userGuestList.size() == 0) return;
        final int childCount = getChildCount();
//        if (childCount < userGuestList.size()) {
//            //当前的view少于list的长度，则补充相差的view
//            int subCount = userGuestList.size() - childCount;
//            for (int i = 0; i < subCount; i++) {
//                CommentTextView commentWidget = new CommentTextView(getContext());
//                commentWidget.setLayoutParams(textViewParams);
//                commentWidget.setTextColor(ContextCompat.getColor(getContext(), R.color.black_alpha_50));
//                addView(commentWidget);
//            }
//        } else if (childCount > userGuestList.size()) {
//            //当前的view的数目比list的长度大，则减去对应的view
//            removeViews(userGuestList.size(), childCount - userGuestList.size());
//        }
        removeAllViews();

        for (int i = 0; i < userGuestList.size(); i++) {
            CommentTextView commentWidget = new CommentTextView(getContext());
            commentWidget.setLayoutParams(textViewParams);
            commentWidget.setTextColor(ContextCompat.getColor(getContext(), R.color.black_alpha_50));
            addView(commentWidget);
        }

        //绑定数据
        for (int i = 0; i < userGuestList.size(); i++) {
            CommentTextView commentWidget = (CommentTextView) getChildAt(i);
            UserGuest userGuest = userGuestList.get(i);
            if (commentWidget != null) {
                commentWidget.setCommentText(CommonUtil.getNickName(userGuest.getAuthor().getNickName()), userGuest.getContent());
                int finalI = i;
                commentWidget.setOnClickListener(v -> {
                    if (mOnUserGuestClickListener != null) {
                        mOnUserGuestClickListener.onClick(finalI, userGuest);
                    }
                });
            }
        }
    }

    /**
     * 得到前三行的总高度
     */
    public int getTopThreeHeight() {
        int childCount = getChildCount();
        if (childCount > 3) {
            int totalHeight = 0;
            for (int i = 0; i < 3; i++) {
                CommentTextView textView = (CommentTextView) getChildAt(i);
                totalHeight += textView.getMeasuredHeight();
            }
            return totalHeight + CommonUtil.dip2px(getContext(), 10);
        }
        return 0;
    }


    public void changeHeightParams(boolean isShowAll) {
        post(() -> {
            if (getChildCount() <= 3)
                return;
            getLayoutParams().height = isShowAll ? currLayoutHeight : getTopThreeHeight();
            requestLayout();
        });
    }

    public void initParamsStatus(boolean isShowAll) {
        changeHeightParams(isShowAll);
    }

    public interface onClickListener {
        void onClick(int position);
    }

    public interface onUserGuestClickListener {
        void onClick(int position, UserGuest userGuest);
    }
}
