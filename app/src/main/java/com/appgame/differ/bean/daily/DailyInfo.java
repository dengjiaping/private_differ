package com.appgame.differ.bean.daily;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public class DailyInfo implements Parcelable {
    private String id;
    private String title;
    private String cover;
    private String description;
    private String from;
    private String taged;
    private String commented;
    private String created_at;
    private String updated_at;
    private String type;
    private String status;
    private List<TagsInfo> tags;
    private UserInfo user;
    private String url;
    public String position;

    public void resolveJson(JSONObject info) {
        this.id = info.optString("id");
        JSONObject attr = info.optJSONObject("attributes");
        this.title = attr.optString("title");
        this.cover = attr.optString("cover");
        this.description = attr.optString("description");
        this.from = attr.optString("from");
        this.taged = attr.optString("taged");
        this.commented = attr.optString("commented");
        this.created_at = attr.optString("created_at");
        this.updated_at = attr.optString("updated_at");
        this.type = attr.optString("type");
        this.status = attr.optString("status");

        JSONArray tagsArray = attr.optJSONArray("tags");
        List<TagsInfo> tagsInfos = new ArrayList<>();
        for (int i = 0; i < tagsArray.length(); i++) {
            JSONObject object = tagsArray.optJSONObject(i).optJSONObject("attributes");
            TagsInfo tagsInfo = new TagsInfo();
            tagsInfo.setId(object.optString("id"));
            tagsInfo.setName(object.optString("name"));
            tagsInfo.setThumbsUp(object.optString("thumbs_up"));
            tagsInfos.add(tagsInfo);
        }
        this.tags = tagsInfos;

        UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(attr.optJSONObject("user"));

        this.user = userInfo;

        this.url = attr.optString("url");
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getDescription() {
        return description;
    }

    public String getFrom() {
        return from;
    }

    public String getTaged() {
        return taged;
    }

    public String getCommented() {
        return commented;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public String getType() {
        return type;
    }

    public List<TagsInfo> getTags() {
        return tags;
    }

    public UserInfo getUser() {
        return user;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.description);
        dest.writeString(this.from);
        dest.writeString(this.taged);
        dest.writeString(this.commented);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeString(this.type);
        dest.writeString(this.status);
        dest.writeTypedList(this.tags);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.url);
    }

    public DailyInfo() {
    }

    protected DailyInfo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.description = in.readString();
        this.from = in.readString();
        this.taged = in.readString();
        this.commented = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.type = in.readString();
        this.status = in.readString();
        this.tags = in.createTypedArrayList(TagsInfo.CREATOR);
        this.user = in.readParcelable(UserInfo.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Parcelable.Creator<DailyInfo> CREATOR = new Parcelable.Creator<DailyInfo>() {
        @Override
        public DailyInfo createFromParcel(Parcel source) {
            return new DailyInfo(source);
        }

        @Override
        public DailyInfo[] newArray(int size) {
            return new DailyInfo[size];
        }
    };
}
