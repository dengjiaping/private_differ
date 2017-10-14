package com.appgame.differ.bean.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/3/15.
 * 386707112@qq.com
 */

public class TagsInfo implements Parcelable {
    private String id;
    private String name;
    @SerializedName("thumbs_up")
    private String thumbsUp;
    @SerializedName("is_thumb")
    private int isThumb;
    private String cover;

    public List<TagsInfo> resolveTagsInfo(JSONArray jsonArray) {
        List<TagsInfo> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            TagsInfo tagsInfo = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), TagsInfo.class);
            list.add(tagsInfo);
        }
        return list;
    }

    public TagsInfo() {
    }

    public TagsInfo(String id, String name, String thumbsUp) {
        this.id = id;
        this.name = name;
        this.thumbsUp = thumbsUp;
    }

    public TagsInfo(String id, String name, String thumbsUp, int isThumb) {
        this.id = id;
        this.name = name;
        this.thumbsUp = thumbsUp;
        this.isThumb = isThumb;
    }

    protected TagsInfo(Parcel in) {
        id = in.readString();
        name = in.readString();
        thumbsUp = in.readString();
        cover = in.readString();
        isThumb = in.readInt();
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public static final Creator<TagsInfo> CREATOR = new Creator<TagsInfo>() {
        @Override
        public TagsInfo createFromParcel(Parcel in) {
            return new TagsInfo(in);
        }

        @Override
        public TagsInfo[] newArray(int size) {
            return new TagsInfo[size];
        }
    };

    public String getThumbsUp() {
        if (TextUtils.isEmpty(thumbsUp))
            return "0";
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(thumbsUp);
        dest.writeString(cover);
        dest.writeInt(isThumb);
    }
}
