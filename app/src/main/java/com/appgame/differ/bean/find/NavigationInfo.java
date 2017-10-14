package com.appgame.differ.bean.find;

import org.json.JSONObject;

/**
 * Created by lzx on 2017/5/24.
 * 386707112@qq.com
 */

public class NavigationInfo {
    private String id;
    private String title;
    private String cover;
    private String type;
    private String targetId;
    private String url;
    private String content;
    private String topicId;
    private String layout;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getCover() {
        return cover;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public String getTopicId() {
        return topicId;
    }

    public String getLayout() {
        return layout;
    }

    public void resolveJson(JSONObject info) {
        id = info.optString("id");
        JSONObject attr = info.optJSONObject("attributes");
        title = attr.optString("title");
        cover = attr.optString("cover");
        type = attr.optString("type");
        targetId = attr.optString("target_id");
        url = attr.optString("url");
        content = attr.optString("content");
        if (type.equals("topic")){
            topicId = attr.optString("topic_id");
            layout = attr.optString("layout");
        }
    }
}
