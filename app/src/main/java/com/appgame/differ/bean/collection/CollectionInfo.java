package com.appgame.differ.bean.collection;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 我的收藏信息
 * Created by lzx on 2017/3/3.
 * 386707112@qq.com
 */

public class CollectionInfo implements Parcelable {

    private String type;
    private String id;
    private int is_appraised;
    private Attributes attributes;
    public String position;

    public int getIs_appraised() {
        return is_appraised;
    }

    public void setIs_appraised(int is_appraised) {
        this.is_appraised = is_appraised;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "CollectionInfo{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", attributes=" + attributes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeParcelable(this.attributes, flags);
    }

    public CollectionInfo() {
    }

    protected CollectionInfo(Parcel in) {
        this.type = in.readString();
        this.id = in.readString();
        this.attributes = in.readParcelable(Attributes.class.getClassLoader());
    }

    public static final Parcelable.Creator<CollectionInfo> CREATOR = new Parcelable.Creator<CollectionInfo>() {
        @Override
        public CollectionInfo createFromParcel(Parcel source) {
            return new CollectionInfo(source);
        }

        @Override
        public CollectionInfo[] newArray(int size) {
            return new CollectionInfo[size];
        }
    };
}
