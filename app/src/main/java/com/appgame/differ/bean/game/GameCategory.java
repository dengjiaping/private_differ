package com.appgame.differ.bean.game;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */

public class GameCategory implements Parcelable {
    private String id;
    private String name;
    private String icon;

    public GameCategory(String id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public List<GameCategory> resolveGameCategory(JSONArray jsonArray) {
        List<GameCategory> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            GameCategory category = new Gson().fromJson(jsonArray.optJSONObject(i).toString(), GameCategory.class);
            list.add(category);
        }
        return list;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
    }

    public GameCategory() {
    }

    protected GameCategory(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.icon = in.readString();
    }

    public static final Parcelable.Creator<GameCategory> CREATOR = new Parcelable.Creator<GameCategory>() {
        @Override
        public GameCategory createFromParcel(Parcel source) {
            return new GameCategory(source);
        }

        @Override
        public GameCategory[] newArray(int size) {
            return new GameCategory[size];
        }
    };
}
