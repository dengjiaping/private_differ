package com.appgame.differ.bean.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/3/15.
 * 386707112@qq.com
 */

public class DownLink implements Parcelable {
    private String id;
    private String platform;
    private String lang;
    private String size;
    private String version;
    private String link;
    @SerializedName("package_name")
    private String packageName;

    public List<DownLink> resolveDownLink(JSONArray jsonArray) {
        List<DownLink> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            DownLink downLink = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), DownLink.class);
            list.add(downLink);
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.platform);
        dest.writeString(this.lang);
        dest.writeString(this.size);
        dest.writeString(this.version);
        dest.writeString(this.link);
        dest.writeString(this.packageName);
    }

    public DownLink() {
    }

    protected DownLink(Parcel in) {
        this.id = in.readString();
        this.platform = in.readString();
        this.lang = in.readString();
        this.size = in.readString();
        this.version = in.readString();
        this.link = in.readString();
        this.packageName = in.readString();
    }

    public static final Parcelable.Creator<DownLink> CREATOR = new Parcelable.Creator<DownLink>() {
        @Override
        public DownLink createFromParcel(Parcel source) {
            return new DownLink(source);
        }

        @Override
        public DownLink[] newArray(int size) {
            return new DownLink[size];
        }
    };
}
