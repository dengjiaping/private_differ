package com.appgame.differ.utils;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;

/**
 * Created by lzx on 2017/6/29.
 */

public class RemoveAnimator extends BaseItemAnimator {
    @Override
    protected ViewPropertyAnimatorCompat getRemoveAnimator(RecyclerView.ViewHolder viewHolder) {
        return ViewCompat.animate(viewHolder.itemView).setDuration(350).translationY(-viewHolder.itemView.getHeight());
    }
}
