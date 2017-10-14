package com.appgame.differ.bean.recommend;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */

public class TopicExtraData implements Parcelable {
    @SerializedName("bg_color")
    private String bgColor;
    @SerializedName("font_color")
    private String fontColor;

    public TopicExtraData(String bgColor, String fontColor) {
        this.bgColor = bgColor;
        this.fontColor = fontColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bgColor);
        dest.writeString(this.fontColor);
    }

    public TopicExtraData() {
    }

    protected TopicExtraData(Parcel in) {
        this.bgColor = in.readString();
        this.fontColor = in.readString();
    }

    public static final Parcelable.Creator<TopicExtraData> CREATOR = new Parcelable.Creator<TopicExtraData>() {
        @Override
        public TopicExtraData createFromParcel(Parcel source) {
            return new TopicExtraData(source);
        }

        @Override
        public TopicExtraData[] newArray(int size) {
            return new TopicExtraData[size];
        }
    };
}
