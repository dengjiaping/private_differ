package com.appgame.differ.module.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.CommentReplies;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.utils.LogUtil;

import java.util.List;

/**
 * Created by lzx on 2017/7/17.
 */

public class CommentReplayLayout extends LinearLayout {

    private TextView mBtnSeeMore;
    private CommentLayout mCommentLayout;
    private OnCommentReplayListener mListener;
    private onUserGuestClickListener mOnUserGuestClickListener;
    private onClickListener mOnClickListener;

    public CommentReplayLayout(Context context) {
        this(context, null, 0);
    }

    public CommentReplayLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setOnCommentReplayListener(OnCommentReplayListener listener) {
        mListener = listener;
    }


    public void setOnUserGuestClickListener(onUserGuestClickListener onUserGuestClickListener) {
        mOnUserGuestClickListener = onUserGuestClickListener;
    }

    public void setOnClickListener(onClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public CommentReplayLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_comment_reply, this);
        mBtnSeeMore = (TextView) findViewById(R.id.btn_see_more);
        mCommentLayout = (CommentLayout) findViewById(R.id.comment_layout);

        mBtnSeeMore.setOnClickListener(v -> {
            LogUtil.i("isShowAll = " + isShowAll);
            if (isShowAll) {
                //    mBtnSeeMore.setText("收起");
                mCommentLayout.changeHeightParams(false);
                mListener.checkIsShowAll(false);
            } else {
                //  mBtnSeeMore.setText("查看全部" + lave + "条回复");
                mCommentLayout.changeHeightParams(true);
                mListener.checkIsShowAll(true);
            }
        });

        mCommentLayout.setOnUserGuestClickListener((position, userGuest) -> {
            if (mOnUserGuestClickListener != null) {
                mOnUserGuestClickListener.onClick(position, userGuest);
            }
        });
    }

    private boolean isShowAll = false;
    private int lave = 0;

    public void init(boolean isShowAll, int lave) {
        this.isShowAll = isShowAll;
        this.lave = lave;
        mBtnSeeMore.setVisibility(lave <= 3 ? GONE : VISIBLE);

        mBtnSeeMore.setText(isShowAll ? "收起" : "查看全部" + lave + "条回复");
        mCommentLayout.initParamsStatus(isShowAll);
    }

    public void addCommentReplies(List<CommentReplies> repliesList) {
        mCommentLayout.setVisibility(repliesList.size() == 0 ? GONE : VISIBLE);
        mCommentLayout.addCommentReplies(repliesList);
    }

    public void addUserGuests(List<UserGuest> userGuestList) {
        mCommentLayout.setVisibility(userGuestList.size() == 0 ? GONE : VISIBLE);
        mCommentLayout.addUserGuests(userGuestList);
    }

    public void setIsShowCommentLayout(boolean isShow) {
        mCommentLayout.setVisibility(isShow ? VISIBLE : GONE);
    }

    public interface OnCommentReplayListener {
        void checkIsShowAll(boolean isShowAll);
    }

    public interface onClickListener {
        void onClick(int position);
    }

    public interface onUserGuestClickListener {
        void onClick(int position, UserGuest userGuest);
    }

}
