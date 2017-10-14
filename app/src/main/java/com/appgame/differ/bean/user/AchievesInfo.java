package com.appgame.differ.bean.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzx on 2017/5/15.
 * 386707112@qq.com
 */

public class AchievesInfo implements Parcelable {
    private String userId;
    private String acId;
    private String name;
    private String icon;
    private boolean status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAcId() {
        return acId;
    }

    public void setAcId(String acId) {
        this.acId = acId;
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


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.acId);
        dest.writeString(this.name);
        dest.writeString(this.icon);

        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
    }

    public AchievesInfo() {
    }

    protected AchievesInfo(Parcel in) {
        this.userId = in.readString();
        this.acId = in.readString();
        this.name = in.readString();
        this.icon = in.readString();

        this.status = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AchievesInfo> CREATOR = new Parcelable.Creator<AchievesInfo>() {
        @Override
        public AchievesInfo createFromParcel(Parcel source) {
            return new AchievesInfo(source);
        }

        @Override
        public AchievesInfo[] newArray(int size) {
            return new AchievesInfo[size];
        }
    };
}
