package com.appgame.differ.bean.download;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xian on 2017/4/22.
 */

public class DownloadInfo implements Parcelable {
    public String downloadUrl = "";
    public String downloadGameName = "";
    public String downloadGameCategory = "";
    public String downloadGameCover = "";
    public String downloadGameIcon = "";
    public String downloadPackageName = "";
    public String downloadGameSize = "";
    public String downloadGameId = "";
    public String downloadLinkId = "";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.downloadUrl);
        dest.writeString(this.downloadGameName);
        dest.writeString(this.downloadGameCategory);
        dest.writeString(this.downloadGameCover);
        dest.writeString(this.downloadGameIcon);
        dest.writeString(this.downloadPackageName);
        dest.writeString(this.downloadGameSize);
        dest.writeString(this.downloadGameId);
        dest.writeString(this.downloadLinkId);
    }

    public DownloadInfo() {
    }

    protected DownloadInfo(Parcel in) {
        this.downloadUrl = in.readString();
        this.downloadGameName = in.readString();
        this.downloadGameCategory = in.readString();
        this.downloadGameCover = in.readString();
        this.downloadGameIcon = in.readString();
        this.downloadPackageName = in.readString();
        this.downloadGameSize = in.readString();
        this.downloadGameId = in.readString();
        this.downloadLinkId = in.readString();
    }

    public static final Parcelable.Creator<DownloadInfo> CREATOR = new Parcelable.Creator<DownloadInfo>() {
        @Override
        public DownloadInfo createFromParcel(Parcel source) {
            return new DownloadInfo(source);
        }

        @Override
        public DownloadInfo[] newArray(int size) {
            return new DownloadInfo[size];
        }
    };
}
