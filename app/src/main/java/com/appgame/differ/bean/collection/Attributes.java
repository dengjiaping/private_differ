package com.appgame.differ.bean.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.DownLink;
import com.appgame.differ.bean.game.GameCategory;
import com.appgame.differ.bean.game.TagsInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lzx on 2017/3/6.
 * 386707112@qq.com
 */

public class Attributes implements Parcelable {
    @SerializedName("game_name_cn")
    private String gameNameCn;
    @SerializedName("game_name_en")
    private String gameNameEn;
    @SerializedName("avg_appraise_star")
    private String gameStar;
    private String icon;
    private String cover;
    private List<GameCategory> category;
    @SerializedName("down_link")
    List<DownLink> downLink;
    private DownloadInfo downloadInfo;
    public int downloadStatus; //下载状态 0：手机没装该应用，1：手机有装该应用
    private List<TagsInfo> tags;

    public List<TagsInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagsInfo> tags) {
        this.tags = tags;
    }

    public DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    public List<DownLink> getDownLink() {
        return downLink;
    }

    public void setDownLink(List<DownLink> downLink) {
        this.downLink = downLink;
    }

    public List<GameCategory> getCategory() {
        return category;
    }

    public void setCategory(List<GameCategory> category) {
        this.category = category;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getGameNameCn() {
        return gameNameCn;
    }

    public void setGameNameCn(String gameNameCn) {
        this.gameNameCn = gameNameCn;
    }

    public String getGameNameEn() {
        return gameNameEn;
    }

    public void setGameNameEn(String gameNameEn) {
        this.gameNameEn = gameNameEn;
    }

    public String getGameStar() {
        return gameStar;
    }

    public void setGameStar(String gameStar) {
        this.gameStar = gameStar;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public Attributes() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gameNameCn);
        dest.writeString(this.gameNameEn);
        dest.writeString(this.gameStar);
        dest.writeString(this.icon);
        dest.writeString(this.cover);
        dest.writeTypedList(this.category);
        dest.writeTypedList(this.downLink);
        dest.writeParcelable(this.downloadInfo, flags);
        dest.writeInt(this.downloadStatus);
        dest.writeTypedList(this.tags);
    }

    protected Attributes(Parcel in) {
        this.gameNameCn = in.readString();
        this.gameNameEn = in.readString();
        this.gameStar = in.readString();
        this.icon = in.readString();
        this.cover = in.readString();
        this.category = in.createTypedArrayList(GameCategory.CREATOR);
        this.downLink = in.createTypedArrayList(DownLink.CREATOR);
        this.downloadInfo = in.readParcelable(DownloadInfo.class.getClassLoader());
        this.downloadStatus = in.readInt();
        this.tags = in.createTypedArrayList(TagsInfo.CREATOR);
    }

    public static final Creator<Attributes> CREATOR = new Creator<Attributes>() {
        @Override
        public Attributes createFromParcel(Parcel source) {
            return new Attributes(source);
        }

        @Override
        public Attributes[] newArray(int size) {
            return new Attributes[size];
        }
    };
}
