package com.appgame.differ.widget.dialog;

import android.os.Bundle;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.rx.RxBus;

/**
 * Created by lzx on 2017/6/28.
 */

public class ExploreEmptyDialog extends RxBaseDialog {
    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_explore_empty;
    }

    private TextView mBtnGame;

    @Override
    protected void init(Bundle savedInstanceState) {
        mBtnGame = (TextView) findViewById(R.id.btn_game);
        mBtnGame.setOnClickListener(v -> {
            RxBus.getBus().send(EvenConstant.KEY_SWITCH_FRAGMENT_MINE);
            dismiss();
        });
    }
}
