package com.appgame.differ.bean.find;

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
 * Created by lzx on 2017/5/24.
 * 386707112@qq.com
 */

public class FindTypeArticle {
    private String name;
    private String target;
    private String targetId;
    private List<String> pics;
    private String layout;
    private String url;
    private String createdAt;
    private String gameId;
    private int status;

    //文章信息
    private String artId;
    private String title;
    private String cover;
    private String description;
    private String from;
    private String taged;
    private String commented;
    private String created_at;
    private String updated_at;
    private String type;
    private List<TagsInfo> tags;
    private UserInfo user;
    private String artUrl;

    //游戏信息
    private GameInfo game;

    public void resolveJson(JSONObject info) {
        JSONObject object = info.optJSONObject("attributes");
        this.name = object.optString("name");
        this.target = object.optString("target");
        this.targetId = object.optString("target_id");
        JSONArray picArray = object.optJSONArray("pics");
        List<String> pics = new ArrayList<>();
        for (int i = 0; i < picArray.length(); i++) {
            pics.add(picArray.optString(i));
        }
        this.pics = pics;
        this.layout = object.optString("layout");
        this.url = object.optString("url");
        this.createdAt = object.optString("created_at");
        this.gameId = object.optString("game_id");
        this.status = object.optInt("status");

        JSONObject artOb = info.optJSONObject("relationships").optJSONObject("article");
        this.artId = artOb.optString("id");
        JSONObject artArrt = artOb.optJSONObject("attributes");
        this.title = artArrt.optString("title");
        this.cover = artArrt.optString("cover");
        this.description = artArrt.optString("description");
        this.from = artArrt.optString("from");
        this.taged = artArrt.optString("taged");
        this.commented = artArrt.optString("commented");
        this.created_at = artArrt.optString("created_at");
        this.updated_at = artArrt.optString("updated_at");
        this.type = artArrt.optString("type");

        List<TagsInfo> infoList = new ArrayList<>();
        JSONArray tagsArray = artArrt.optJSONArray("tags");
        for (int i = 0; i < tagsArray.length(); i++) {
            JSONObject attr = tagsArray.optJSONObject(i).optJSONObject("attributes");
            TagsInfo tagsInfo = new TagsInfo();
            tagsInfo.setId(attr.optString("id"));
            tagsInfo.setName(attr.optString("name"));
            tagsInfo.setThumbsUp(attr.optString("thumbs_up"));
            infoList.add(tagsInfo);
        }
        this.tags = infoList;

        this.user = UserInfoManager.getImpl().resolveUserInfo(artArrt.optJSONObject("user"));

        this.artUrl = artArrt.optString("url");

        JSONObject gameOb = info.optJSONObject("relationships").optJSONObject("game");
        GameInfo gameInfo = new Gson().fromJson(gameOb.optJSONObject("attributes").toString(), GameInfo.class);
        gameInfo.setGameId(gameOb.optString("id"));

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
        this.game = gameInfo;
    }

    public String getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }

    public String getTargetId() {
        return targetId;
    }

    public List<String> getPics() {
        return pics;
    }

    public String getLayout() {
        return layout;
    }

    public String getUrl() {
        return url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getArtId() {
        return artId;
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

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
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

    public String getArtUrl() {
        return artUrl;
    }

    public String getGameId() {
        return gameId;
    }

    public GameInfo getGame() {
        return game;
    }

    public int getStatus() {
        return status;
    }
}
