package com.appgame.differ.bean.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzx on 2017/5/10.
 * 386707112@qq.com
 */

public class RankInfo   implements Parcelable {
    private String rankId;
    private String name;
    private String icon;

    public String getId() {
        return rankId;
    }

    public void setId(String id) {
        this.rankId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rankId);
        dest.writeString(this.name);
        dest.writeString(this.icon);
    }

    public RankInfo() {
    }

    protected RankInfo(Parcel in) {
        this.rankId = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
    }

    public static final Parcelable.Creator<RankInfo> CREATOR = new Parcelable.Creator<RankInfo>() {
        @Override
        public RankInfo createFromParcel(Parcel source) {
            return new RankInfo(source);
        }

        @Override
        public RankInfo[] newArray(int size) {
            return new RankInfo[size];
        }
    };
}
