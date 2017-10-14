package com.appgame.differ.callback;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by lzx on 2017/6/12.
 */

public class BaseDiffUtilCallBack<T> extends DiffUtil.Callback {

    private List<T> oldDataEntitiesList;
    private List<T> newDataEntitiesList;
    private OnAreItemsTheSameListener mOnAreItemsTheSameListener;

    public BaseDiffUtilCallBack(List<T> oldDataEntitiesList, List<T> newDataEntitiesList) {
        this.oldDataEntitiesList = oldDataEntitiesList;
        this.newDataEntitiesList = newDataEntitiesList;
    }

    @Override
    public int getOldListSize() {
        return oldDataEntitiesList != null ? oldDataEntitiesList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newDataEntitiesList != null ? newDataEntitiesList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        if (mOnAreItemsTheSameListener != null) {
            return mOnAreItemsTheSameListener.itemsTheSame(oldDataEntitiesList.get(oldItemPosition), newDataEntitiesList.get(newItemPosition));
        }else {
            return false;
        }
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldDataEntitiesList.get(oldItemPosition).
                equals(newDataEntitiesList.get(newItemPosition));
    }

    public void setOnAreItemsTheSameListener(OnAreItemsTheSameListener<T> onAreItemsTheSameListener) {
        mOnAreItemsTheSameListener = onAreItemsTheSameListener;
    }

    public interface OnAreItemsTheSameListener<T> {
        boolean itemsTheSame(T oldData, T newData);
    }
}
