package com.appgame.differ.bean.daily;

import com.appgame.differ.bean.recommend.TopicInfo;

import org.json.JSONObject;

/**
 * Created by lzx on 2017/5/25.
 * 386707112@qq.com
 */

public class DailyList {
    private String date;
    private String begin_at;
    private String end_at;
    private int count;

    private String target;
    private String target_id;
    private String published_at;
    private DailyInfo dailyInfo;
    private TopicInfo topicInfo;

    public void resolveJson(JSONObject info) {
        this.target = info.optString("target");
        this.target_id = info.optString("target_id");
        this.published_at = info.optString("published_at");
        if (target.equals("topic")) {
            TopicInfo topicInfo = new TopicInfo();
            topicInfo.resolveJson(info.optJSONObject("info"));
            this.topicInfo = topicInfo;
        } else if (target.equals("article")) {
            DailyInfo dailyInfo = new DailyInfo();
            dailyInfo.resolveJson(info.optJSONObject("info"));
            this.dailyInfo = dailyInfo;
        }
    }

    public String getTargetId() {
        return target_id;
    }

    public String getPublishedAt() {
        return published_at;
    }

    public String getTarget() {
        return target;
    }

    public String getPublished_at() {
        return published_at;
    }

    public DailyInfo getDailyInfo() {
        return dailyInfo;
    }

    public TopicInfo getTopicInfo() {
        return topicInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeginAt() {
        return begin_at;
    }

    public void setBeginAt(String begin_at) {
        this.begin_at = begin_at;
    }

    public String getEndAt() {
        return end_at;
    }

    public void setEndAt(String end_at) {
        this.end_at = end_at;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
