package com.appgame.differ.bean.home;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public class RecomColumn implements Parcelable {
    private RecommedInfo colum;

    public RecommedInfo getColum() {
        return colum;
    }

    public void setColum(RecommedInfo colum) {
        this.colum = colum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.colum, flags);
    }

    public RecomColumn() {
    }

    protected RecomColumn(Parcel in) {
        this.colum = in.readParcelable(RecommedInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<RecomColumn> CREATOR = new Parcelable.Creator<RecomColumn>() {
        @Override
        public RecomColumn createFromParcel(Parcel source) {
            return new RecomColumn(source);
        }

        @Override
        public RecomColumn[] newArray(int size) {
            return new RecomColumn[size];
        }
    };
}
