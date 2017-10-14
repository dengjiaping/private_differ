package com.appgame.differ.bean.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yukunlin on 17/5/11.
 */

public class SimpleGame implements Parcelable {
    private String gameNameCn;
    private String gameIcon;
    private String gameId;
    private List<TagsInfo> tagsInfos;

    public List<TagsInfo> getTagsInfos() {
        return tagsInfos;
    }

    public void setTagsInfos(List<TagsInfo> tagsInfos) {
        this.tagsInfos = tagsInfos;
    }

    public String getGameNameCn() {
        return gameNameCn;
    }

    public void setGameNameCn(String gameNameCn) {
        this.gameNameCn = gameNameCn;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


    public SimpleGame() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.gameNameCn);
        dest.writeString(this.gameIcon);
        dest.writeString(this.gameId);
        dest.writeTypedList(this.tagsInfos);
    }

    protected SimpleGame(Parcel in) {
        this.gameNameCn = in.readString();
        this.gameIcon = in.readString();
        this.gameId = in.readString();
        this.tagsInfos = in.createTypedArrayList(TagsInfo.CREATOR);
    }

    public static final Creator<SimpleGame> CREATOR = new Creator<SimpleGame>() {
        @Override
        public SimpleGame createFromParcel(Parcel source) {
            return new SimpleGame(source);
        }

        @Override
        public SimpleGame[] newArray(int size) {
            return new SimpleGame[size];
        }
    };
}
