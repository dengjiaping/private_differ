package com.appgame.differ.bean.daily;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.user.UserInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lzx on 2017/4/17.
 * 386707112@qq.com
 */

public class DailyComment implements Parcelable {
    private String comId;
    private String content;
    private String replied;
    @SerializedName("thumbs_up")
    private String thumbsUp;
    @SerializedName("created_at")
    private String createdAt;
    private List<CommentReplies> replies;
    private int is_thumb;
    private UserInfo relationships;
    private boolean isShowAll = false;
    public String position;

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReplied() {
        return replied;
    }

    public void setReplied(String replied) {
        this.replied = replied;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public UserInfo getRelationships() {
        return relationships;
    }

    public void setRelationships(UserInfo relationships) {
        this.relationships = relationships;
    }

    public List<CommentReplies> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentReplies> replies) {
        this.replies = replies;
    }

    public int getIs_thumb() {
        return is_thumb;
    }

    public void setIs_thumb(int is_thumb) {
        this.is_thumb = is_thumb;
    }

    public boolean isShowAll() {
        return isShowAll;
    }

    public void setShowAll(boolean showAll) {
        isShowAll = showAll;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.comId);
        dest.writeString(this.content);
        dest.writeString(this.replied);
        dest.writeString(this.thumbsUp);
        dest.writeString(this.createdAt);
        dest.writeTypedList(this.replies);
        dest.writeInt(this.is_thumb);
        dest.writeParcelable(this.relationships, flags);
        dest.writeByte(this.isShowAll ? (byte) 1 : (byte) 0);
    }

    public DailyComment() {
    }

    protected DailyComment(Parcel in) {
        this.comId = in.readString();
        this.content = in.readString();
        this.replied = in.readString();
        this.thumbsUp = in.readString();
        this.createdAt = in.readString();
        this.replies = in.createTypedArrayList(CommentReplies.CREATOR);
        this.is_thumb = in.readInt();
        this.relationships = in.readParcelable(UserInfo.class.getClassLoader());
        this.isShowAll = in.readByte() != 0;
    }

    public static final Parcelable.Creator<DailyComment> CREATOR = new Parcelable.Creator<DailyComment>() {
        @Override
        public DailyComment createFromParcel(Parcel source) {
            return new DailyComment(source);
        }

        @Override
        public DailyComment[] newArray(int size) {
            return new DailyComment[size];
        }
    };
}
