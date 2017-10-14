package com.appgame.differ.bean.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.game.GameInfo;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public class OtherInfo implements Parcelable {
    private String id;
    private String type;
    private String title;
    private String cover;
    private String target;
    @SerializedName("target_id")
    private String targetId;

    private String targetInfoId;
    private String targetInfoType;
    private String targetInfoTitle;
    private String targetInfoCover;
    private String targetInfoDescription;
    private String targetInfoCreatedAt;
    private String targetInfoUpdatedAt;
    private String targetInfoUrl;

    private GameInfo gameInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetInfoId() {
        return targetInfoId;
    }

    public void setTargetInfoId(String targetInfoId) {
        this.targetInfoId = targetInfoId;
    }

    public String getTargetInfoType() {
        return targetInfoType;
    }

    public void setTargetInfoType(String targetInfoType) {
        this.targetInfoType = targetInfoType;
    }

    public String getTargetInfoTitle() {
        return targetInfoTitle;
    }

    public void setTargetInfoTitle(String targetInfoTitle) {
        this.targetInfoTitle = targetInfoTitle;
    }

    public String getTargetInfoCover() {
        return targetInfoCover;
    }

    public void setTargetInfoCover(String targetInfoCover) {
        this.targetInfoCover = targetInfoCover;
    }

    public String getTargetInfoDescription() {
        return targetInfoDescription;
    }

    public void setTargetInfoDescription(String targetInfoDescription) {
        this.targetInfoDescription = targetInfoDescription;
    }

    public String getTargetInfoCreatedAt() {
        return targetInfoCreatedAt;
    }

    public void setTargetInfoCreatedAt(String targetInfoCreatedAt) {
        this.targetInfoCreatedAt = targetInfoCreatedAt;
    }

    public String getTargetInfoUpdatedAt() {
        return targetInfoUpdatedAt;
    }

    public void setTargetInfoUpdatedAt(String targetInfoUpdatedAt) {
        this.targetInfoUpdatedAt = targetInfoUpdatedAt;
    }

    public String getTargetInfoUrl() {
        return targetInfoUrl;
    }

    public void setTargetInfoUrl(String targetInfoUrl) {
        this.targetInfoUrl = targetInfoUrl;
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public void resolveJson(JSONObject info) {
        try {
            this.type = info.optString("type", "");
            this.id = info.optString("id", "");
            JSONObject infoAttr = info.getJSONObject("attributes");
            this.title = infoAttr.optString("title", "");
            this.cover = infoAttr.optString("cover", "");
            this.target = infoAttr.optString("target", "");
            this.targetId = infoAttr.optString("targetId", "");
            JSONObject targetAttr = infoAttr.getJSONObject("target_info");
            this.targetInfoId = targetAttr.optString("id");
            this.targetInfoType = targetAttr.optString("type");
            if (this.targetInfoType.equals("articles")) {
                this.targetInfoTitle = targetAttr.optString("title");
                this.targetInfoCover = targetAttr.optString("cover");
                this.targetInfoDescription = targetAttr.optString("description");
                this.targetInfoCreatedAt = targetAttr.optString("created_at");
                this.targetInfoUpdatedAt = targetAttr.optString("updated_at");
                this.targetInfoUrl = targetAttr.optString("url");
            } else if (this.targetInfoType.equals("game")) {
                this.gameInfo = new Gson().fromJson(targetAttr.getJSONObject("attributes").toString(), GameInfo.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.target);
        dest.writeString(this.targetId);
        dest.writeString(this.targetInfoId);
        dest.writeString(this.targetInfoType);
        dest.writeString(this.targetInfoTitle);
        dest.writeString(this.targetInfoCover);
        dest.writeString(this.targetInfoDescription);
        dest.writeString(this.targetInfoCreatedAt);
        dest.writeString(this.targetInfoUpdatedAt);
        dest.writeString(this.targetInfoUrl);
        dest.writeParcelable(this.gameInfo, flags);
    }

    public OtherInfo() {
    }

    protected OtherInfo(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.target = in.readString();
        this.targetId = in.readString();
        this.targetInfoId = in.readString();
        this.targetInfoType = in.readString();
        this.targetInfoTitle = in.readString();
        this.targetInfoCover = in.readString();
        this.targetInfoDescription = in.readString();
        this.targetInfoCreatedAt = in.readString();
        this.targetInfoUpdatedAt = in.readString();
        this.targetInfoUrl = in.readString();
        this.gameInfo = in.readParcelable(GameInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<OtherInfo> CREATOR = new Parcelable.Creator<OtherInfo>() {
        @Override
        public OtherInfo createFromParcel(Parcel source) {
            return new OtherInfo(source);
        }

        @Override
        public OtherInfo[] newArray(int size) {
            return new OtherInfo[size];
        }
    };
}
