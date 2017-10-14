package com.appgame.differ.widget.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appgame.differ.R;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;

/**
 * Created by lzx on 2017/6/7.
 */

public abstract class RxBaseDialog extends RxDialogFragment {


    private View parentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(getLayoutResId(), container, false);
        init(savedInstanceState);
        return parentView;
    }

    protected abstract int getLayoutResId();

    protected abstract void init(Bundle savedInstanceState);

    protected View findViewById(int id) {
        return parentView.findViewById(id);
    }




}
