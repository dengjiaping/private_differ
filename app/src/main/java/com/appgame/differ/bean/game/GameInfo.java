package com.appgame.differ.bean.game;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.appgame.differ.R;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.UserInfoManager;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.appgame.differ.data.db.GameDownloadManager.gameIcon;

/**
 * 游戏信息
 * Created by lzx on 2017/3/15.
 * 386707112@qq.com
 */

public class GameInfo implements Parcelable {
    public String pagePosition; //当前页码

    private String gameId;
    @SerializedName("game_name_cn")
    private String gameNameCn;
    @SerializedName("game_name_en")
    private String gameNameEn;
    @SerializedName("avg_appraise_star")
    private String gameStar;
    private String intro;
    private String describe;
    private String publisher;
    private String status;
    @SerializedName("activity_label")
    private String activityLabel;
    @SerializedName("activity_url")
    private String activityUrl;
    @SerializedName("update_info")
    private String updateInfo;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("origin_updated_at")
    private String originUpdatedAt;
    @SerializedName("recommend_reason")
    private String recommendReason;
    @SerializedName("is_book")
    private String isBook;
    private String remark;
    private List<GameCategory> category;
    private String icon;
    @SerializedName("down_link")
    private List<DownLink> downLink;
    private String cover;
    private String video;
    private UserInfo user;
    private List<TagsInfo> tags;
    private List<String> pic;
    private AppraisePoints appraise_points;
    @SerializedName("relay_games")
    private List<RelatedGame> relayGames;
    @SerializedName("game_require")
    private List<GameRequire> gameRequires;
    @SerializedName("relate_id")
    private String relateId;
    @SerializedName("button_type")
    private int buttonType;

    @SerializedName("is_collected")
    private int isCollected;
    @SerializedName("game_name_alias")
    private String gameNameAlias;

    public int downloadStatus; //下载状态 0：手机没装该应用，1：手机有装该应用
    public DownloadInfo mDownloadInfo;  //保存下载信息

    public GameInfo resolveGameInfo(JSONObject object) {
        this.gameId = object.optString("id");
        JSONObject gameOb = object.optJSONObject("attributes");
        this.gameNameCn = gameOb.optString("game_name_cn");
        this.gameNameEn = gameOb.optString("game_name_en");
        this.gameStar = gameOb.optString("avg_appraise_star");
        this.intro = gameOb.optString("intro");
        this.describe = gameOb.optString("describe");
        this.publisher = gameOb.optString("publisher");
        this.status = gameOb.optString("status");
        this.activityLabel = gameOb.optString("activity_label");
        this.activityUrl = gameOb.optString("activity_url");
        this.updateInfo = gameOb.optString("update_info");
        this.updatedAt = gameOb.optString("updated_at");
        this.originUpdatedAt = gameOb.optString("origin_updated_at");
        this.recommendReason = gameOb.optString("recommend_reason");
        this.isBook = gameOb.optString("is_book");
        this.remark = gameOb.optString("remark");
        JSONArray categoryArray = gameOb.optJSONArray("category");
        GameCategory category = new GameCategory();
        this.category = category.resolveGameCategory(categoryArray);
        this.icon = gameOb.optString("icon");
        JSONArray downLinkArray = gameOb.optJSONArray("down_link");
        DownLink downLink = new DownLink();
        this.downLink = downLink.resolveDownLink(downLinkArray);
        this.cover = gameOb.optString("cover");
        this.video = gameOb.optString("video");
        JSONObject userOb = gameOb.optJSONObject("user");
        this.user = UserInfoManager.getImpl().resolveUserInfo(userOb);
        JSONArray tagsArray = gameOb.optJSONArray("tags");
        TagsInfo tagsInfo = new TagsInfo();
        this.tags = tagsInfo.resolveTagsInfo(tagsArray);
        JSONArray picArray = gameOb.optJSONArray("pic");
        List<String> pics = new ArrayList<>();
        for (int i = 0; i < picArray.length(); i++) {
            pics.add(picArray.optString(i));
        }
        this.pic = pics;
        AppraisePoints points = new AppraisePoints();
        points.resolveAppraisePoints(gameOb.optJSONObject("appraise_points"));
        this.appraise_points = points;

        JSONArray relatedGameArray = gameOb.optJSONArray("relay_games");
        List<RelatedGame> relatedGames = new ArrayList<>();
        for (int i = 0; i < relatedGameArray.length(); i++) {
            RelatedGame relatedGame = new Gson().fromJson(relatedGameArray.optJSONObject(i).toString(), RelatedGame.class);
            relatedGames.add(relatedGame);
        }
        this.relayGames = relatedGames;

        JSONArray gameRequireArray = gameOb.optJSONArray("game_require");
        List<GameRequire> gameRequires = new ArrayList<>();
        for (int i = 0; i < gameRequireArray.length(); i++) {
            GameRequire gameRequire = new Gson().fromJson(gameRequireArray.optJSONObject(i).toString(), GameRequire.class);
            gameRequires.add(gameRequire);
        }
        this.gameRequires = gameRequires;

        this.relateId = gameOb.optString("relate_id");
        this.buttonType = gameOb.optInt("button_type");

        this.isCollected = object.optInt("is_collected");

        for (DownLink link : this.downLink) {
            if (link.getPlatform().equals("android")) {
                String coverUrl = TextUtils.isEmpty(this.cover) ? this.icon : this.cover;
                String downloadUrl = TextUtils.isEmpty(link.getLink()) ? "" : link.getLink();
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.downloadUrl = downloadUrl;
                downloadInfo.downloadGameName = this.gameNameCn;
                downloadInfo.downloadGameCategory = this.category.get(0).getName();
                downloadInfo.downloadGameCover = coverUrl;
                downloadInfo.downloadGameIcon = gameIcon;
                downloadInfo.downloadPackageName = link.getPackageName();
                downloadInfo.downloadGameSize = link.getSize();
                downloadInfo.downloadGameId = this.gameId;
                downloadInfo.downloadLinkId = link.getId();
                this.mDownloadInfo = downloadInfo;
            }
        }

        for (GameRequire require : this.gameRequires) {
            if (require.getType().equals("online")) {
                require.setResId(R.drawable.ic_internet);
            } else if (require.getType().equals("chinese")) {
                require.setResId(R.drawable.ic_chinese);
            } else if (require.getType().equals("play")) {
                require.setResId(R.drawable.ic_googleplay);
            } else if (require.getType().equals("agent")) {
                require.setResId(R.drawable.ic_quicken);
            }
        }
        Iterator<GameRequire> iterator = this.gameRequires.iterator();
        while (iterator.hasNext()) {
            GameRequire info = iterator.next();
            if (info.getStatus().equals("0")) {
                iterator.remove();
            }
        }

        return this;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

    public String getActivityUrl() {
        return activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOriginUpdatedAt() {
        return originUpdatedAt;
    }

    public void setOriginUpdatedAt(String originUpdatedAt) {
        this.originUpdatedAt = originUpdatedAt;
    }

    public String getRecommendReason() {
        return recommendReason;
    }

    public void setRecommendReason(String recommendReason) {
        this.recommendReason = recommendReason;
    }

    public String getIsBook() {
        return isBook;
    }

    public void setIsBook(String isBook) {
        this.isBook = isBook;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public List<TagsInfo> getTags() {
        return tags;
    }

    public void setTags(List<TagsInfo> tags) {
        this.tags = tags;
    }

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public AppraisePoints getAppraise_points() {
        return appraise_points;
    }

    public void setAppraise_points(AppraisePoints appraise_points) {
        this.appraise_points = appraise_points;
    }

    public List<RelatedGame> getRelayGames() {
        return relayGames;
    }

    public void setRelayGames(List<RelatedGame> relayGames) {
        this.relayGames = relayGames;
    }

    public List<GameRequire> getGameRequires() {
        return gameRequires;
    }

    public void setGameRequires(List<GameRequire> gameRequires) {
        this.gameRequires = gameRequires;
    }

    public String getRelateId() {
        return relateId;
    }

    public void setRelateId(String relateId) {
        this.relateId = relateId;
    }

    public int getButtonType() {
        return buttonType;
    }

    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    public int getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(int isCollected) {
        this.isCollected = isCollected;
    }

    public String getGameNameAlias() {
        return gameNameAlias;
    }

    public void setGameNameAlias(String gameNameAlias) {
        this.gameNameAlias = gameNameAlias;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public DownloadInfo getDownloadInfo() {
        return mDownloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pagePosition);
        dest.writeString(this.gameId);
        dest.writeString(this.gameNameCn);
        dest.writeString(this.gameNameEn);
        dest.writeString(this.gameStar);
        dest.writeString(this.intro);
        dest.writeString(this.describe);
        dest.writeString(this.publisher);
        dest.writeString(this.status);
        dest.writeString(this.activityLabel);
        dest.writeString(this.activityUrl);
        dest.writeString(this.updateInfo);
        dest.writeString(this.updatedAt);
        dest.writeString(this.originUpdatedAt);
        dest.writeString(this.recommendReason);
        dest.writeString(this.isBook);
        dest.writeString(this.remark);
        dest.writeTypedList(this.category);
        dest.writeString(this.icon);
        dest.writeTypedList(this.downLink);
        dest.writeString(this.cover);
        dest.writeString(this.video);
        dest.writeParcelable(this.user, flags);
        dest.writeTypedList(this.tags);
        dest.writeStringList(this.pic);
        dest.writeList(this.relayGames);
        dest.writeTypedList(this.gameRequires);
        dest.writeString(this.relateId);
        dest.writeInt(this.buttonType);
        dest.writeInt(this.isCollected);
        dest.writeString(this.gameNameAlias);
        dest.writeInt(this.downloadStatus);
        dest.writeParcelable(this.mDownloadInfo, flags);
    }

    public GameInfo() {
    }

    protected GameInfo(Parcel in) {
        this.pagePosition = in.readString();
        this.gameId = in.readString();
        this.gameNameCn = in.readString();
        this.gameNameEn = in.readString();
        this.gameStar = in.readString();
        this.intro = in.readString();
        this.describe = in.readString();
        this.publisher = in.readString();
        this.status = in.readString();
        this.activityLabel = in.readString();
        this.activityUrl = in.readString();
        this.updateInfo = in.readString();
        this.updatedAt = in.readString();
        this.originUpdatedAt = in.readString();
        this.recommendReason = in.readString();
        this.isBook = in.readString();
        this.remark = in.readString();
        this.category = in.createTypedArrayList(GameCategory.CREATOR);
        this.icon = in.readString();
        this.downLink = in.createTypedArrayList(DownLink.CREATOR);
        this.cover = in.readString();
        this.video = in.readString();
        this.user = in.readParcelable(UserInfo.class.getClassLoader());
        this.tags = in.createTypedArrayList(TagsInfo.CREATOR);
        this.pic = in.createStringArrayList();
        this.relayGames = new ArrayList<RelatedGame>();
        in.readList(this.relayGames, RelatedGame.class.getClassLoader());
        this.gameRequires = in.createTypedArrayList(GameRequire.CREATOR);
        this.relateId = in.readString();
        this.buttonType = in.readInt();
        this.isCollected = in.readInt();
        this.gameNameAlias = in.readString();
        this.downloadStatus = in.readInt();
        this.mDownloadInfo = in.readParcelable(DownloadInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<GameInfo> CREATOR = new Parcelable.Creator<GameInfo>() {
        @Override
        public GameInfo createFromParcel(Parcel source) {
            return new GameInfo(source);
        }

        @Override
        public GameInfo[] newArray(int size) {
            return new GameInfo[size];
        }
    };
}
