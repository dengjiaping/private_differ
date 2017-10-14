package com.appgame.differ.bean.evaluation;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.game.GameInfo;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/3/3.
 * 386707112@qq.com
 */

public class EvaluationInfo implements Parcelable {
    public String position;

    @SerializedName("is_appraise")
    private int isAppraise;
    @SerializedName("user_appraise")
    private UserAppraise userAppraise;
    private List<UserAppraise> list = new ArrayList<>();
    private GameInfo mGameInfo;
    private List<DailyComment> mCommentList;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getIsAppraise() {
        return isAppraise;
    }

    public void setIsAppraise(int isAppraise) {
        this.isAppraise = isAppraise;
    }

    public UserAppraise getUserAppraise() {
        return userAppraise;
    }

    public void setUserAppraise(UserAppraise userAppraise) {
        this.userAppraise = userAppraise;
    }

    public List<UserAppraise> getList() {
        return list;
    }

    public void setList(List<UserAppraise> list) {
        this.list = list;
    }

    public GameInfo getGameInfo() {
        return mGameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        mGameInfo = gameInfo;
    }

    public List<DailyComment> getCommentList() {
        return mCommentList;
    }

    public void setCommentList(List<DailyComment> commentList) {
        mCommentList = commentList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.position);
        dest.writeInt(this.isAppraise);
        dest.writeParcelable(this.userAppraise, flags);
        dest.writeTypedList(this.list);
        dest.writeParcelable(this.mGameInfo, flags);
        dest.writeList(this.mCommentList);
    }

    public EvaluationInfo() {
    }

    protected EvaluationInfo(Parcel in) {
        this.position = in.readString();
        this.isAppraise = in.readInt();
        this.userAppraise = in.readParcelable(UserAppraise.class.getClassLoader());
        this.list = in.createTypedArrayList(UserAppraise.CREATOR);
        this.mGameInfo = in.readParcelable(GameInfo.class.getClassLoader());
        this.mCommentList = new ArrayList<DailyComment>();
        in.readList(this.mCommentList, DailyComment.class.getClassLoader());
    }

    public static final Parcelable.Creator<EvaluationInfo> CREATOR = new Parcelable.Creator<EvaluationInfo>() {
        @Override
        public EvaluationInfo createFromParcel(Parcel source) {
            return new EvaluationInfo(source);
        }

        @Override
        public EvaluationInfo[] newArray(int size) {
            return new EvaluationInfo[size];
        }
    };
}
