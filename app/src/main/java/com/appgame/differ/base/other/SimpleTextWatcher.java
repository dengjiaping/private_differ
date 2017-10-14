package com.appgame.differ.base.other;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by lzx on 2017/2/24.
 * 386707112@qq.com
 */

public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
