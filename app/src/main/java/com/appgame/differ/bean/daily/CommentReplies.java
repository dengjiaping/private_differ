package com.appgame.differ.bean.daily;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.user.UserInfo;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lzx on 2017/4/19.
 * 386707112@qq.com
 */

public class CommentReplies implements Parcelable {
    private String type;
    private String id;
    private String content;
    @SerializedName("is_replied")
    private String isReplied;
    @SerializedName("reply_id")
    private String replyId;
    @SerializedName("reply_user_id")
    private String replyUserId;
    @SerializedName("created_at")
    private String createdAt;
    private UserInfo relationships;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsReplied() {
        return isReplied;
    }

    public void setIsReplied(String isReplied) {
        this.isReplied = isReplied;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(String replyUserId) {
        this.replyUserId = replyUserId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeString(this.isReplied);
        dest.writeString(this.replyId);
        dest.writeString(this.replyUserId);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.relationships, flags);
    }

    public CommentReplies() {
    }

    protected CommentReplies(Parcel in) {
        this.type = in.readString();
        this.id = in.readString();
        this.content = in.readString();
        this.isReplied = in.readString();
        this.replyId = in.readString();
        this.replyUserId = in.readString();
        this.createdAt = in.readString();
        this.relationships = in.readParcelable(UserInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<CommentReplies> CREATOR = new Parcelable.Creator<CommentReplies>() {
        @Override
        public CommentReplies createFromParcel(Parcel source) {
            return new CommentReplies(source);
        }

        @Override
        public CommentReplies[] newArray(int size) {
            return new CommentReplies[size];
        }
    };
}
