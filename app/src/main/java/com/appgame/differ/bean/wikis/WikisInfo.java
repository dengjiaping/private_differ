package com.appgame.differ.bean.wikis;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lzx on 2017/9/4.
 */

public class WikisInfo implements Parcelable {
    private String id;
    private String title;
    private String smallTitle;
    private String cover;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallTitle() {
        return smallTitle;
    }

    public void setSmallTitle(String smallTitle) {
        this.smallTitle = smallTitle;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.smallTitle);
        dest.writeString(this.cover);
        dest.writeString(this.url);
    }

    public WikisInfo() {
    }

    protected WikisInfo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.smallTitle = in.readString();
        this.cover = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<WikisInfo> CREATOR = new Parcelable.Creator<WikisInfo>() {
        @Override
        public WikisInfo createFromParcel(Parcel source) {
            return new WikisInfo(source);
        }

        @Override
        public WikisInfo[] newArray(int size) {
            return new WikisInfo[size];
        }
    };
}
