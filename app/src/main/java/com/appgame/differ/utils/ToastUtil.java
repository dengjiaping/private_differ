package com.appgame.differ.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appgame.differ.R;
import com.appgame.differ.base.app.DifferApplication;

/**
 * description: Toast工具类
 * author: xiaodifu
 * date: 2016/7/24.
 */
public class ToastUtil {

    private static Toast mToast;

        public static void showLong(String string) {
        if (string == null || string.trim().equals("")) return;
        View layout = LayoutInflater.from(DifferApplication.getContext()).inflate(R.layout.layout_toast, null);
        layout.getBackground().setAlpha(200);

        /*设置布局*/
        TextView textView = (TextView) layout.findViewById(R.id.text_content);
        textView.setText(string);

        if (mToast == null) {
            mToast = new Toast(DifferApplication.getContext());
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.BOTTOM, 0, CommonUtil.dip2px(DifferApplication.getContext(),60f));
        mToast.setView(layout);
        mToast.show();
    }

    public static void showShort(String string) {
        if (string == null || string.trim().equals("")) return;
        View layout = LayoutInflater.from(DifferApplication.getContext()).inflate(R.layout.layout_toast, null);
        layout.getBackground().setAlpha(200);

        /*设置布局*/
        TextView textView = (TextView) layout.findViewById(R.id.text_content);
        textView.setText(string);

        if (mToast == null) {
            mToast = new Toast(DifferApplication.getContext());
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM, 0, CommonUtil.dip2px(DifferApplication.getContext(),60f));
        mToast.setView(layout);
        mToast.show();
    }


}
