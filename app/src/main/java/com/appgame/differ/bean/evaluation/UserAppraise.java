package com.appgame.differ.bean.evaluation;

import android.os.Parcel;
import android.os.Parcelable;

import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/23.
 * 386707112@qq.com
 */

public class UserAppraise implements Parcelable {
    public String position;
    public String count;
    private String id;
    private String star;
    private String taged;
    private String commented;
    private int isThumb;
    private String thumbsUp;
    private String content;
    private List<TagsInfo> tags;
    private RecommedInfo game;
    private UserInfo author;
    private List<UserInfo> latestThumbs;

    public void resolveJson(JSONObject info) {
        this.id = info.optString("id");
        JSONObject attr = info.optJSONObject("attributes");
        this.star = attr.optString("star");
        this.taged = attr.optString("taged");
        this.commented = attr.optString("commented");
        this.content = attr.optString("content");
        this.isThumb = attr.optInt("is_thumb");
        this.thumbsUp = attr.optString("thumbs_up");
        JSONArray tagsArray = attr.optJSONArray("tags");
        List<TagsInfo> tagsInfos = new ArrayList<>();
        if (tagsArray != null) {
            for (int i = 0; i < tagsArray.length(); i++) {
                TagsInfo tagsInfo = new TagsInfo();
                JSONObject tagOb = tagsArray.optJSONObject(i);
                tagsInfo.setId(tagOb.optString("id"));
                tagsInfo.setName(tagOb.optJSONObject("attributes").optString("name"));
                tagsInfo.setThumbsUp(tagOb.optJSONObject("attributes").optString("thumbs_up"));
                tagsInfo.setIsThumb(tagOb.optJSONObject("attributes").optInt("is_thumb"));
                tagsInfos.add(tagsInfo);
            }
        }
        this.tags = tagsInfos;

        JSONObject gameOb = info.optJSONObject("relationships").optJSONObject("game");
        RecommedInfo recommedInfo = new RecommedInfo();
        recommedInfo.resolveJson(gameOb);
        this.game = recommedInfo;

        JSONObject userOb = info.optJSONObject("relationships").optJSONObject("author");
        UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(userOb);
        this.author = userInfo;

        JSONArray latestThumbs = info.optJSONObject("relationships").optJSONArray("latestThumbs");
        List<UserInfo> userInfos = new ArrayList<>();
        if (latestThumbs != null) {
            for (int j = 0; j < latestThumbs.length(); j++) {
                userInfos.add(UserInfoManager.getImpl().resolveUserInfo(latestThumbs.optJSONObject(j)));
            }
        }
        this.latestThumbs = userInfos;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getTaged() {
        return taged;
    }

    public void setTaged(String taged) {
        this.taged = taged;
    }

    public String getCommented() {
        return commented;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public int getIsThumb() {
        return isThumb;
    }

    public void setIsThumb(int isThumb) {
        this.isThumb = isThumb;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<TagsInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagsInfo> tags) {
        this.tags = tags;
    }

    public RecommedInfo getGame() {
        return game;
    }

    public void setGame(RecommedInfo game) {
        this.game = game;
    }

    public UserInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public List<UserInfo> getLatestThumbs() {
        return latestThumbs;
    }

    public void setLatestThumbs(List<UserInfo> latestThumbs) {
        this.latestThumbs = latestThumbs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.position);
        dest.writeString(this.count);
        dest.writeString(this.id);
        dest.writeString(this.star);
        dest.writeString(this.taged);
        dest.writeString(this.commented);
        dest.writeInt(this.isThumb);
        dest.writeString(this.thumbsUp);
        dest.writeString(this.content);
        dest.writeTypedList(this.tags);
        dest.writeParcelable(this.game, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeTypedList(this.latestThumbs);
    }

    public UserAppraise() {
    }

    protected UserAppraise(Parcel in) {
        this.position = in.readString();
        this.count = in.readString();
        this.id = in.readString();
        this.star = in.readString();
        this.taged = in.readString();
        this.commented = in.readString();
        this.isThumb = in.readInt();
        this.thumbsUp = in.readString();
        this.content = in.readString();
        this.tags = in.createTypedArrayList(TagsInfo.CREATOR);
        this.game = in.readParcelable(RecommedInfo.class.getClassLoader());
        this.author = in.readParcelable(UserInfo.class.getClassLoader());
        this.latestThumbs = in.createTypedArrayList(UserInfo.CREATOR);
    }

    public static final Parcelable.Creator<UserAppraise> CREATOR = new Parcelable.Creator<UserAppraise>() {
        @Override
        public UserAppraise createFromParcel(Parcel source) {
            return new UserAppraise(source);
        }

        @Override
        public UserAppraise[] newArray(int size) {
            return new UserAppraise[size];
        }
    };
}
