package com.appgame.differ.bean.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 用户信息
 * Created by lzx on 2017/2/23.
 * 386707112@qq.com
 */

public class UserInfo implements Parcelable {
    public String position;
    private String userId;
    @SerializedName("username")
    private String userName = "";
    @SerializedName("nickname")
    private String nickName = "";
    private String avatar = "";
    private RankInfo rank;
    private List<AchievesInfo> achieves;
    private String follower = "";
    private String following = "";
    private String sex = "";
    private String birthday = "";
    private String remark = "";
    @SerializedName("public_follower")
    private String publicFollower = "";
    @SerializedName("public_following")
    private String publicFollowing = "";
    private String type = "";
    @SerializedName("is_followed")
    private boolean isFollowed;
    private String cover;
    private String last_appraise;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public RankInfo getRank() {
        return rank;
    }

    public void setRank(RankInfo rank) {
        this.rank = rank;
    }

    public List<AchievesInfo> getAchieves() {
        return achieves;
    }

    public void setAchieves(List<AchievesInfo> achieves) {
        this.achieves = achieves;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPublicFollower() {
        return publicFollower;
    }

    public void setPublicFollower(String publicFollower) {
        this.publicFollower = publicFollower;
    }

    public String getPublicFollowing() {
        return publicFollowing;
    }

    public void setPublicFollowing(String publicFollowing) {
        this.publicFollowing = publicFollowing;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLast_appraise() {
        return last_appraise;
    }

    public void setLast_appraise(String last_appraise) {
        this.last_appraise = last_appraise;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.position);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.nickName);
        dest.writeString(this.avatar);
        dest.writeParcelable(this.rank, flags);
        dest.writeTypedList(this.achieves);
        dest.writeString(this.follower);
        dest.writeString(this.following);
        dest.writeString(this.sex);
        dest.writeString(this.birthday);
        dest.writeString(this.remark);
        dest.writeString(this.publicFollower);
        dest.writeString(this.publicFollowing);
        dest.writeString(this.type);
        dest.writeByte(this.isFollowed ? (byte) 1 : (byte) 0);
        dest.writeString(this.cover);
        dest.writeString(this.last_appraise);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.position = in.readString();
        this.userId = in.readString();
        this.userName = in.readString();
        this.nickName = in.readString();
        this.avatar = in.readString();
        this.rank = in.readParcelable(RankInfo.class.getClassLoader());
        this.achieves = in.createTypedArrayList(AchievesInfo.CREATOR);
        this.follower = in.readString();
        this.following = in.readString();
        this.sex = in.readString();
        this.birthday = in.readString();
        this.remark = in.readString();
        this.publicFollower = in.readString();
        this.publicFollowing = in.readString();
        this.type = in.readString();
        this.isFollowed = in.readByte() != 0;
        this.cover = in.readString();
        this.last_appraise = in.readString();
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
