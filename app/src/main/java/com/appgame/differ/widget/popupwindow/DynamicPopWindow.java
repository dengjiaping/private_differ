package com.appgame.differ.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;

/**
 * Created by lzx on 2017/6/7.
 */

public class DynamicPopWindow extends PopupWindow implements View.OnClickListener {
    private View mContentView;
    private Context mContext;
    private TextView mBtnPop;
    private String userId, user_Id;
    private String type;
    private OnClickListener mOnClickListener;

    public DynamicPopWindow(Context context, String uId) {
        super(context);
        mContext = context;
        this.userId = uId;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.pop_dynamic, null);
        mBtnPop = (TextView) mContentView.findViewById(R.id.btn_pop);
        mBtnPop.setOnClickListener(this);
        setFocusable(true);
        this.setContentView(mContentView);
        this.setWidth(CommonUtil.dip2px(mContext, 100));
        this.setHeight(CommonUtil.dip2px(mContext, 64));
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        user_Id = CommonUtil.isLogin() ? UserInfoManager.getImpl().getUserId() : "";

        if (user_Id.equals(userId)) {
            mBtnPop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dy_delete, 0, 0, 0);
            mBtnPop.setText("删除");
            type = "delete";
        } else {
            mBtnPop.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_dy_report, 0, 0, 0);
            mBtnPop.setText("举报");
            type = "report";
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pop:
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(type);
                }
                break;
        }
    }

    public interface OnClickListener {
        void onClick(String type);
    }
}
