package com.appgame.differ.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 版本信息
 * Created by xian on 2017/4/23.
 */

public class VersionInfo implements Parcelable {
    public String platform = "";
    public String version = "";
    public String type = "";
    public String description = "";
    public String size = "";
    public String status = "";
    public String published_at = "";
    public String created_at = "";
    public String extra_data = "";
    public String url = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.platform);
        dest.writeString(this.version);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeString(this.size);
        dest.writeString(this.status);
        dest.writeString(this.published_at);
        dest.writeString(this.created_at);
        dest.writeString(this.extra_data);
        dest.writeString(this.url);
    }

    public VersionInfo() {
    }

    protected VersionInfo(Parcel in) {
        this.platform = in.readString();
        this.version = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.size = in.readString();
        this.status = in.readString();
        this.published_at = in.readString();
        this.created_at = in.readString();
        this.extra_data = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<VersionInfo> CREATOR = new Parcelable.Creator<VersionInfo>() {
        @Override
        public VersionInfo createFromParcel(Parcel source) {
            return new VersionInfo(source);
        }

        @Override
        public VersionInfo[] newArray(int size) {
            return new VersionInfo[size];
        }
    };
}
