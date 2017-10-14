package com.appgame.differ.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;

/**
 * Created by lzx on 2017/5/11.
 * 386707112@qq.com
 */

public class KeyboardControlManager {
    private OnKeyboardStateChangeListener onKeyboardStateChangeListener;
    private WeakReference<Activity> act;
    private View decorView;
    private static KeyboardControlManager mKeyboardControlManager;

    private KeyboardControlManager(Activity act, OnKeyboardStateChangeListener onKeyboardStateChangeListener) {
        this.act = new WeakReference<>(act);
        this.onKeyboardStateChangeListener = onKeyboardStateChangeListener;
    }

    public void observerKeyboardVisibleChangeInternal() {
        if (onKeyboardStateChangeListener == null) return;
        Activity activity = act.get();
        if (activity == null) return;
        setOnKeyboardStateChangeListener(onKeyboardStateChangeListener);

        decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        int preKeyboardHeight = -1;
        Rect rect = new Rect();
        boolean preVisible = false;

        @Override
        public void onGlobalLayout() {
            rect.setEmpty();
            if (decorView != null) {
                decorView.getWindowVisibleDisplayFrame(rect);
                int displayHeight = rect.height();
                int windowHeight = decorView.getHeight();
                int keyboardHeight = windowHeight - displayHeight;
                if (preKeyboardHeight != keyboardHeight) {
                    //判定可见区域与原来的window区域占比是否小于0.75,小于意味着键盘弹出来了。
                    boolean isVisible = (displayHeight * 1.0f / windowHeight * 1.0f) < 0.75f;
                    if (isVisible != preVisible) {
                        onKeyboardStateChangeListener.onKeyboardChange(keyboardHeight, isVisible);
                        preVisible = isVisible;
                    }
                }
                preKeyboardHeight = keyboardHeight;
            }
        }
    };

    public static void clear() {
        if (mKeyboardControlManager != null) {
            if (mKeyboardControlManager.decorView != null) {
                mKeyboardControlManager.decorView.getViewTreeObserver().removeOnGlobalLayoutListener(mKeyboardControlManager.mOnGlobalLayoutListener);
            }
            mKeyboardControlManager.decorView = null;
            mKeyboardControlManager = null;
        }
    }

    public static void observerKeyboardVisibleChange(Activity act, OnKeyboardStateChangeListener onKeyboardStateChangeListener) {
        mKeyboardControlManager = new KeyboardControlManager(act, onKeyboardStateChangeListener);
        mKeyboardControlManager.observerKeyboardVisibleChangeInternal();
    }

    public OnKeyboardStateChangeListener getOnKeyboardStateChangeListener() {
        return onKeyboardStateChangeListener;
    }

    public void setOnKeyboardStateChangeListener(OnKeyboardStateChangeListener onKeyboardStateChangeListener) {
        this.onKeyboardStateChangeListener = onKeyboardStateChangeListener;
    }

    //=============================================================interface
    public interface OnKeyboardStateChangeListener {
        void onKeyboardChange(int keyboardHeight, boolean isVisible);
    }
}
