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
import com.appgame.differ.utils.LogUtil;

/**
 * Created by lzx on 2017/6/8.
 */

public class DynamicMorePopWindow extends PopupWindow implements View.OnClickListener {

    private View mContentView;
    private Context mContext;
    private String userId, user_Id;
    private TextView mBtnAction;
    private String type;
    private OnClickListener mOnClickListener;

    public DynamicMorePopWindow(Context context, String uId) {
        super(context);
        mContext = context;
        this.userId = uId;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = inflater.inflate(R.layout.popup_window_my_game, null);
        mBtnAction = (TextView) mContentView.findViewById(R.id.action);
        mBtnAction.setOnClickListener(this);
        setFocusable(true);
        this.setContentView(mContentView);
        this.setWidth(CommonUtil.dip2px(mContext, 85));
        this.setHeight(CommonUtil.dip2px(mContext, 50));
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());

        user_Id = CommonUtil.isLogin() ? UserInfoManager.getImpl().getUserId() : "";

        LogUtil.i("user_Id = " + user_Id + "  userId = " + userId);

        if (user_Id.equals(userId)) {
            mBtnAction.setText("删除");
            mBtnAction.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_delete_black_def, 0, 0, 0);
            type = "delete";
        } else {
            mBtnAction.setText("举报");
            mBtnAction.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_report_black_def, 0, 0, 0);
            type = "report";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(type);
                }
                break;
        }
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(String type);
    }
}
