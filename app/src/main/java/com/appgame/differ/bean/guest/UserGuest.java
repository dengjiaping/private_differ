package com.appgame.differ.bean.guest;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.user.UserInfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/10.
 * 386707112@qq.com
 */

public class UserGuest implements Parcelable {
    private String position;
    private String guested; //数量
    private String id;
    private String content;
    @SerializedName("parent_id")
    private String parentId;
    @SerializedName("created_at")
    private String createdAt;
    private UserInfo author;
    private String thumbsUp;
    private int isThumb;
    private List<UserGuest> childGuests;
    private List<UserGuest> twoChildGuests;
    public boolean isShowAll = false;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGuested() {
        return guested;
    }

    public void setGuested(String guested) {
        this.guested = guested;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public List<UserGuest> getChildGuests() {
        return childGuests;
    }

    public void setChildGuests(List<UserGuest> childGuests) {
        this.childGuests = childGuests;
    }

    public List<UserGuest> getTwoChildGuests() {
        return twoChildGuests;
    }

    public void setTwoChildGuests(List<UserGuest> twoChildGuests) {
        this.twoChildGuests = twoChildGuests;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public int getIsThumb() {
        return isThumb;
    }

    public void setIsThumb(int isThumb) {
        this.isThumb = isThumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.position);
        dest.writeString(this.guested);
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeString(this.parentId);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.author, flags);
        dest.writeString(this.thumbsUp);
        dest.writeInt(this.isThumb);
        dest.writeList(this.childGuests);
        dest.writeList(this.twoChildGuests);
        dest.writeByte(this.isShowAll ? (byte) 1 : (byte) 0);
    }

    public UserGuest() {
    }

    protected UserGuest(Parcel in) {
        this.position = in.readString();
        this.guested = in.readString();
        this.id = in.readString();
        this.content = in.readString();
        this.parentId = in.readString();
        this.createdAt = in.readString();
        this.author = in.readParcelable(UserInfo.class.getClassLoader());
        this.thumbsUp = in.readString();
        this.isThumb = in.readInt();
        this.childGuests = new ArrayList<UserGuest>();
        in.readList(this.childGuests, UserGuest.class.getClassLoader());
        this.twoChildGuests = new ArrayList<UserGuest>();
        in.readList(this.twoChildGuests, UserGuest.class.getClassLoader());
        this.isShowAll = in.readByte() != 0;
    }

    public static final Parcelable.Creator<UserGuest> CREATOR = new Parcelable.Creator<UserGuest>() {
        @Override
        public UserGuest createFromParcel(Parcel source) {
            return new UserGuest(source);
        }

        @Override
        public UserGuest[] newArray(int size) {
            return new UserGuest[size];
        }
    };
}
