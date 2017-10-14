package com.appgame.differ.widget.banner;

import org.json.JSONObject;

/**
 * <p>
 * Banner模型类
 */
public class BannerEntity {

    public String id;
    public String cover;
    public String target;
    public String targetId;
    public String createdAt;
    public String url;
    public String title;
    public String smallTitle;

    public void resolveJson(JSONObject info) {
        id = info.optString("id");
        JSONObject attr = info.optJSONObject("attributes");
        cover = attr.optString("cover");
        target = attr.optString("target");
        targetId = attr.optString("target_id");
        createdAt = attr.optString("created_at");
        url = attr.optString("url");
        title = attr.optString("title");
        smallTitle = attr.optString("small_title");
    }

}
