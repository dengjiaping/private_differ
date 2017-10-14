package com.appgame.differ.bean.home;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.appgame.differ.bean.AppInfo;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.DownLink;
import com.appgame.differ.bean.game.GameCategory;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.UserInfoManager;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by lzx on 2017/4/15.
 * 386707112@qq.com
 */

public class RecommedInfo implements Parcelable {
    public String position;
    private String type;
    private String infoId;
    private String gameId;
    private RecomColumn mRecomColumn;
    @SerializedName("game_name_cn")
    private String gameNameCn;
    @SerializedName("game_name_en")
    private String gameNameEn;
    @SerializedName("avg_appraise_star")
    private String gameStar;
    @SerializedName("recommend_reason")
    private String recommendReason;
    private String status;
    private List<GameCategory> category;
    private String icon;
    @SerializedName("down_link")
    List<DownLink> downLink;
    private String cover;
    private String video;
    private String userId;
    private UserInfo userInfo;
    private DownloadInfo mDownloadInfo;

    private List<TagsInfo> tags;


    public List<TagsInfo> getTags() {
        return tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTags(List<TagsInfo> tags) {
        this.tags = tags;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
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

    public String getRecommendReason() {
        return recommendReason;
    }

    public void setRecommendReason(String recommendReason) {
        this.recommendReason = recommendReason;
    }

    public List<GameCategory> getCategory() {
        return category;
    }

    public void setCategory(List<GameCategory> category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<DownLink> getDownLink() {
        return downLink;
    }

    public void setDownLink(List<DownLink> downLink) {
        this.downLink = downLink;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public RecomColumn getRecomColumn() {
        return mRecomColumn;
    }

    public void setRecomColumn(RecomColumn recomColumn) {
        mRecomColumn = recomColumn;
    }

    public DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
    }

    public String getVideo() {
        return video;
    }

    /**
     * json解析
     *
     * @param info
     */
    public void resolveJson(JSONObject info) {
        try {

            //  List<AppInfo> mAppList = DataSupport.findAll(AppInfo.class);
            List<AppInfo> mAppList = AppInfoManager.getImpl().findAppInfoAll();
            List<String> mAppPkgList = new ArrayList<>();
            for (AppInfo appInfo : mAppList) {
                mAppPkgList.add(appInfo.getPackageName());
            }

            this.infoId = info.optString("id");
            this.gameId = info.optString("id");
            JSONObject gameOb = info.getJSONObject("attributes");
            this.gameNameCn = gameOb.optString("game_name_cn");
            this.gameNameEn = gameOb.optString("game_name_en");
            this.gameStar = gameOb.optString("avg_appraise_star");
            this.recommendReason = gameOb.optString("recommend_reason");
            this.status = gameOb.optString("status");
            JSONArray categoryArray = gameOb.getJSONArray("category");
            List<GameCategory> gameCategories = new ArrayList<>();
            for (int j = 0; j < categoryArray.length(); j++) {
                GameCategory category = new GameCategory();
                category.setId(categoryArray.getJSONObject(j).getString("id"));
                category.setName(categoryArray.getJSONObject(j).getString("name"));
                category.setIcon(categoryArray.getJSONObject(j).getString("icon"));
                gameCategories.add(category);
            }
            this.category = gameCategories;
            this.icon = gameOb.getString("icon");
            JSONArray downlinkArray = gameOb.getJSONArray("down_link");
            List<DownLink> downLinks = new ArrayList<>();
            String downloadUrl = "";
            String packageName = "";
            String size = "";
            String downloadLinkId = "";
            for (int k = 0; k < downlinkArray.length(); k++) {
                JSONObject downlinkOb = downlinkArray.getJSONObject(k);
                if (downlinkOb.getString("platform").equals("android")) {
                    DownLink downLink = new DownLink();
                    downLink.setId(downlinkOb.getString("id"));
                    downLink.setPlatform(downlinkOb.getString("platform"));
                    downLink.setLang(downlinkOb.getString("lang"));
                    downLink.setSize(downlinkOb.getString("size"));
                    downLink.setVersion(downlinkOb.getString("version"));
                    downLink.setLink(downlinkOb.getString("link"));
                    downLink.setPackageName(downlinkOb.getString("package_name"));
                    downloadUrl = downLink.getLink();
                    packageName = downLink.getPackageName();
                    size = downLink.getSize();
                    downloadLinkId = downLink.getId();
                    downLinks.add(downLink);
                }
            }
            this.downLink = downLinks;

            String coverUrl = TextUtils.isEmpty(this.cover) ? this.icon : this.cover;
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.downloadUrl = downloadUrl;
            downloadInfo.downloadGameName = this.gameNameCn;
            downloadInfo.downloadGameCategory = this.category.get(0).getName();
            downloadInfo.downloadGameCover = coverUrl;
            downloadInfo.downloadGameIcon = this.icon;
            downloadInfo.downloadPackageName = packageName;
            downloadInfo.downloadGameSize = size;
            downloadInfo.downloadGameId = this.gameId;
            downloadInfo.downloadLinkId = downloadLinkId;
            this.mDownloadInfo = downloadInfo;

            this.cover = gameOb.getString("cover");
            this.video = gameOb.getString("video");

            JSONObject userOb = gameOb.getJSONObject("user");
            JSONObject attributes = userOb.optJSONObject("attributes");
            if (attributes != null) {
                this.userInfo = UserInfoManager.getImpl().resolveUserInfo(userOb);
            }

            JSONArray tagsArray = gameOb.optJSONArray("tags");
            List<TagsInfo> tagsInfos = new ArrayList<>();
            if (tagsArray != null) {
                for (int q = 0; q < tagsArray.length(); q++) {
                    TagsInfo tagsInfo = new TagsInfo();
                    tagsInfo.setId(tagsArray.getJSONObject(q).getString("id"));
                    tagsInfo.setName(tagsArray.getJSONObject(q).getString("name"));
                    if (q < 3) {
                        tagsInfos.add(tagsInfo);
                    }
                }
            }
            this.tags = tagsInfos;
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
        dest.writeString(this.type);
        dest.writeString(this.infoId);
        dest.writeString(this.gameId);
        dest.writeParcelable(this.mRecomColumn, flags);
        dest.writeString(this.gameNameCn);
        dest.writeString(this.gameNameEn);
        dest.writeString(this.gameStar);
        dest.writeString(this.recommendReason);
        dest.writeTypedList(this.category);
        dest.writeString(this.icon);
        dest.writeString(this.status);
        dest.writeTypedList(this.downLink);
        dest.writeString(this.cover);
        dest.writeString(this.video);
        dest.writeString(this.userId);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeParcelable(this.mDownloadInfo, flags);
        dest.writeTypedList(this.tags);
    }

    public RecommedInfo() {
    }

    protected RecommedInfo(Parcel in) {
        this.type = in.readString();
        this.infoId = in.readString();
        this.gameId = in.readString();
        this.mRecomColumn = in.readParcelable(RecomColumn.class.getClassLoader());
        this.gameNameCn = in.readString();
        this.gameNameEn = in.readString();
        this.gameStar = in.readString();
        this.status = in.readString();
        this.recommendReason = in.readString();
        this.category = in.createTypedArrayList(GameCategory.CREATOR);
        this.icon = in.readString();
        this.downLink = in.createTypedArrayList(DownLink.CREATOR);
        this.cover = in.readString();
        this.video = in.readString();
        this.userId = in.readString();
        this.userInfo = in.readParcelable(UserInfo.class.getClassLoader());
        this.mDownloadInfo = in.readParcelable(DownloadInfo.class.getClassLoader());
        this.tags = in.createTypedArrayList(TagsInfo.CREATOR);
    }

    public static final Parcelable.Creator<RecommedInfo> CREATOR = new Parcelable.Creator<RecommedInfo>() {
        @Override
        public RecommedInfo createFromParcel(Parcel source) {
            return new RecommedInfo(source);
        }

        @Override
        public RecommedInfo[] newArray(int size) {
            return new RecommedInfo[size];
        }
    };
}
