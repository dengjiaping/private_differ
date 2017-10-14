package com.appgame.differ.bean.find;

import android.text.TextUtils;

import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.game.DownLink;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/6/14.
 */

public class FindTypeTopic {
    private String name;
    private String target;
    private String targetId;
    private List<String> pics;
    private String layout;
    private String url;
    private String createdAt;
    private String gameId;
    private TopicInfo mTopicInfo;
    private GameInfo mGameInfo;
    private int status;

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

        JSONObject topicOb = info.optJSONObject("relationships").optJSONObject("topic");
        TopicInfo topicInfo = new TopicInfo();
        topicInfo.resolveJson(topicOb);
        this.mTopicInfo = topicInfo;

        JSONObject gameOb = info.optJSONObject("relationships").optJSONObject("game");
        if (gameOb != null) {
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
            this.mGameInfo = gameInfo;
        }
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

    public String getGameId() {
        return gameId;
    }

    public TopicInfo getTopicInfo() {
        return mTopicInfo;
    }

    public GameInfo getGameInfo() {
        return mGameInfo;
    }

    public int getStatus() {
        return status;
    }
}
