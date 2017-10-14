package com.appgame.differ.bean.game;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzx on 2017/6/23.
 */

public class GameRequire implements Parcelable {

    private String type;
    private String status;
    private String title;
    private int resId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.status);
        dest.writeString(this.title);
        dest.writeInt(this.resId);
    }

    public GameRequire() {
    }

    protected GameRequire(Parcel in) {
        this.type = in.readString();
        this.status = in.readString();
        this.title = in.readString();
        this.resId = in.readInt();
    }

    public static final Parcelable.Creator<GameRequire> CREATOR = new Parcelable.Creator<GameRequire>() {
        @Override
        public GameRequire createFromParcel(Parcel source) {
            return new GameRequire(source);
        }

        @Override
        public GameRequire[] newArray(int size) {
            return new GameRequire[size];
        }
    };
}
