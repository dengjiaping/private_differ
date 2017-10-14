package com.appgame.differ.bean.dynamic;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.UserInfoManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/5.
 * 386707112@qq.com
 */

public class DynamicInfo implements Parcelable {
    private String id;
    private String content;
    private String commented;
    private String thumbsUp;
    private String shared;
    private String createdAt;
    private String isChecked; //是否已经删除,1没删除，0已经删除
    private String updateAt;
    private String target;
    private String targetId;
    private List<String> images;
    private int isThumb;  //0没有点赞，1已点赞
    private String isForward; //1是转发
    private String star;
    private RecommedInfo gameInfo;
    private UserInfo author;
    private DynamicInfo forward;
    private List<UserInfo> latestThumbs;
    private DailyInfo article;
    public String postion;


    public void resolveJson(JSONObject info) {
        this.id = info.optString("id");
        JSONObject dynamicOb = info.optJSONObject("attributes");
        this.content = dynamicOb.optString("content");
        this.commented = dynamicOb.optString("commented");
        this.thumbsUp = dynamicOb.optString("thumbs_up");
        this.shared = dynamicOb.optString("shared");
        this.createdAt = dynamicOb.optString("created_at");
        this.isChecked = dynamicOb.optString("is_checked");
        this.updateAt = dynamicOb.optString("updated_at");
        this.target = dynamicOb.optString("target");
        this.targetId = dynamicOb.optString("target_id");
        JSONArray images = dynamicOb.optJSONArray("images");
        List<String> imageList = new ArrayList<>();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imageList.add(images.optString(i));
            }
        }
        this.images = imageList;
        this.isForward = dynamicOb.optString("is_forward");
        this.isThumb = dynamicOb.optInt("is_thumb", -1);

        if (target.equals("appraise")) {
            this.star = dynamicOb.optString("star");
        }

        if (target.equals("dynamic") || target.equals("appraise")) {
            JSONObject gameOb = info.optJSONObject("relationships").optJSONObject("game");
            RecommedInfo recommedInfo = new RecommedInfo();
            recommedInfo.resolveJson(gameOb);
            this.gameInfo = recommedInfo;
        } else if (target.equals("article")) {
            JSONObject articleOb = info.optJSONObject("relationships").optJSONObject("article");
            if (articleOb != null) {
                DailyInfo dailyInfo = new DailyInfo();
                dailyInfo.resolveJson(articleOb);
                this.article = dailyInfo;
            }
        }

        JSONObject authorOb = info.optJSONObject("relationships").optJSONObject("author");
        this.author = UserInfoManager.getImpl().resolveUserInfo(authorOb);

        JSONObject forwardOb = info.optJSONObject("relationships").optJSONObject("forward");
        if (forwardOb != null) {
            JSONObject attr = forwardOb.optJSONObject("attributes");
            if (attr != null) {
                DynamicInfo dynamicInfo = new DynamicInfo();
                dynamicInfo.resolveJson(forwardOb);
                this.forward = dynamicInfo;
            }
        }

        JSONArray latestThumbs = info.optJSONObject("relationships").optJSONArray("latestThumbs");
        List<UserInfo> userInfos = new ArrayList<>();
        if (latestThumbs != null) {
            for (int j = 0; j < latestThumbs.length(); j++) {
                UserInfo userInfo = UserInfoManager.getImpl().resolveUserInfo(latestThumbs.optJSONObject(j));
                userInfos.add(userInfo);
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

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommented() {
        return commented;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getIsThumb() {
        return isThumb;
    }

    public void setIsThumb(int isThumb) {
        this.isThumb = isThumb;
    }

    public String getIsForward() {
        return isForward;
    }

    public void setIsForward(String isForward) {
        this.isForward = isForward;
    }

    public RecommedInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(RecommedInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public UserInfo getAuthor() {
        if (author == null)
            return new UserInfo();
        return author;
    }

    public void setAuthor(UserInfo author) {
        this.author = author;
    }

    public DynamicInfo getForward() {
        return forward;
    }

    public void setForward(DynamicInfo forward) {
        this.forward = forward;
    }

    public List<UserInfo> getLatestThumbs() {
        return latestThumbs;
    }

    public void setLatestThumbs(List<UserInfo> latestThumbs) {
        this.latestThumbs = latestThumbs;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetId() {
        return targetId;
    }

    public DailyInfo getArticle() {
        return article;
    }

    public String getStar() {
        return TextUtils.isEmpty(star) ? "2" : star;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeString(this.commented);
        dest.writeString(this.thumbsUp);
        dest.writeString(this.shared);
        dest.writeString(this.createdAt);
        dest.writeString(this.isChecked);
        dest.writeString(this.updateAt);
        dest.writeString(this.target);
        dest.writeString(this.targetId);
        dest.writeStringList(this.images);
        dest.writeInt(this.isThumb);
        dest.writeString(this.isForward);
        dest.writeString(this.star);
        dest.writeParcelable(this.gameInfo, flags);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.forward, flags);
        dest.writeTypedList(this.latestThumbs);
        dest.writeParcelable(this.article, flags);
        dest.writeString(this.postion);
    }

    public DynamicInfo() {
    }

    protected DynamicInfo(Parcel in) {
        this.id = in.readString();
        this.content = in.readString();
        this.commented = in.readString();
        this.thumbsUp = in.readString();
        this.shared = in.readString();
        this.createdAt = in.readString();
        this.isChecked = in.readString();
        this.updateAt = in.readString();
        this.target = in.readString();
        this.targetId = in.readString();
        this.images = in.createStringArrayList();
        this.isThumb = in.readInt();
        this.isForward = in.readString();
        this.star = in.readString();
        this.gameInfo = in.readParcelable(RecommedInfo.class.getClassLoader());
        this.author = in.readParcelable(UserInfo.class.getClassLoader());
        this.forward = in.readParcelable(DynamicInfo.class.getClassLoader());
        this.latestThumbs = in.createTypedArrayList(UserInfo.CREATOR);
        this.article = in.readParcelable(DailyInfo.class.getClassLoader());
        this.postion = in.readString();
    }

    public static final Parcelable.Creator<DynamicInfo> CREATOR = new Parcelable.Creator<DynamicInfo>() {
        @Override
        public DynamicInfo createFromParcel(Parcel source) {
            return new DynamicInfo(source);
        }

        @Override
        public DynamicInfo[] newArray(int size) {
            return new DynamicInfo[size];
        }
    };
}
