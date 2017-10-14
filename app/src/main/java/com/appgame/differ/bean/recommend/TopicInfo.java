package com.appgame.differ.bean.recommend;

import android.text.TextUtils;

import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.DownLink;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.UserInfoManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */

public class TopicInfo {

    private String topicId;
    private String title;
    private String userId;
    private String cover;
    private String content;
    private String layout;
    private String from;
    private String taged;
    private String commented;
    private String status;
    private TopicExtraData mExtraData;
    private List<TagsInfo> tags;
    private List<GameInfo> mGameInfos;
    private UserInfo user;
    private String createdAt;

    public void resolveJson(JSONObject info) {
        this.topicId = info.optString("id");

        JSONObject attr = info.optJSONObject("attributes");
        this.title = attr.optString("title");
        this.userId = attr.optString("user_id");
        this.cover = attr.optString("cover");
        this.content = attr.optString("content");
        this.layout = attr.optString("layout");
        this.from = attr.optString("from");
        this.taged = attr.optString("taged");
        this.commented = attr.optString("commented");
        this.status = attr.optString("status");

        this.createdAt = attr.optString("created_at");

        this.mExtraData = new Gson().fromJson(attr.optJSONObject("extra_data").toString(), TopicExtraData.class);

        List<TagsInfo> tagsInfos = new ArrayList<>();
        JSONArray tagsArray = attr.optJSONArray("tags");
        for (int i = 0; i < tagsArray.length(); i++) {
            JSONObject object = tagsArray.optJSONObject(i).optJSONObject("attributes");
            TagsInfo tagsInfo = new TagsInfo();
            tagsInfo.setId(object.optString("id"));
            tagsInfo.setName(object.optString("name"));
            tagsInfo.setThumbsUp(object.optString("thumbs_up"));
            tagsInfos.add(tagsInfo);
        }
        this.tags = tagsInfos;

        List<GameInfo> gameInfos = new ArrayList<>();
        JSONArray gameArray = attr.optJSONArray("games");
        if (gameArray != null) {
            for (int i = 0; i < gameArray.length(); i++) {
                JSONObject object = gameArray.optJSONObject(i);
                GameInfo gameInfo = new Gson().fromJson(object.optJSONObject("attributes").toString(), GameInfo.class);
                gameInfo.setGameId(object.optString("id"));

                List<DownLink> downLinks = gameInfo.getDownLink();
                for (DownLink downLink : downLinks) {
                    if (downLink.getPlatform().equals("android")) {
                        String gameCover = gameInfo.getCover();
                        String gameIcon = gameInfo.getIcon();
                        String coverUrl = TextUtils.isEmpty(gameCover) ? gameIcon : gameCover;
                        String downloadUrl = TextUtils.isEmpty(downLink.getLink()) ? "" : downLink.getLink();
                        DownloadInfo downloadInfo = new DownloadInfo();
                        downloadInfo.downloadUrl = downloadUrl;
                        downloadInfo.downloadGameName = gameInfo.getGameNameCn();
                        downloadInfo.downloadGameCategory = gameInfo.getCategory().get(0).getName();
                        downloadInfo.downloadGameCover = coverUrl;
                        downloadInfo.downloadGameIcon = gameIcon;
                        downloadInfo.downloadPackageName = downLink.getPackageName();
                        downloadInfo.downloadGameSize = downLink.getSize();
                        downloadInfo.downloadGameId = gameInfo.getGameId();
                        downloadInfo.downloadLinkId = downLink.getId();
                        gameInfo.setDownloadInfo(downloadInfo);
                    }
                }
                gameInfos.add(gameInfo);
            }
            this.mGameInfos = gameInfos;
        }
        JSONObject userOb = info.optJSONObject("relationships").optJSONObject("user");
        this.user = UserInfoManager.getImpl().resolveUserInfo(userOb);
    }

    public String getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public String getCover() {
        return cover;
    }

    public String getContent() {
        return content;
    }

    public String getLayout() {
        return layout;
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

    public String getStatus() {
        return status;
    }

    public TopicExtraData getExtraData() {
        return mExtraData;
    }

    public List<TagsInfo> getTags() {
        return tags;
    }

    public List<GameInfo> getGameInfos() {
        return mGameInfos;
    }

    public UserInfo getUser() {
        return user;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
