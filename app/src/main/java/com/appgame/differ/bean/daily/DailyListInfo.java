package com.appgame.differ.bean.daily;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/25.
 * 386707112@qq.com
 */

public class DailyListInfo {
    public String position;
    private String date;
    private String begin_at;
    private String end_at;
    private int count;

    private List<DailyList> list;

    public void resolveJson(JSONObject data) {
        this.date = data.optString("date");
        this.begin_at = data.optString("begin_at");
        this.end_at = data.optString("end_at");
        this.count = data.optInt("count");

        List<DailyList> list = new ArrayList<>();
        JSONArray jsonArray = data.optJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.optJSONObject(i);
            DailyList dailyList = new DailyList();
            dailyList.resolveJson(object.optJSONObject("attributes"));
            dailyList.setDate(this.date);
            dailyList.setBeginAt(this.begin_at);
            dailyList.setEndAt(this.end_at);
            dailyList.setCount(this.count);
            if (object.optInt("status") == 1) {
                list.add(dailyList);
            }
        }
        this.list = list;
    }

    public String getDate() {
        return date;
    }

    public String getBegin_at() {
        return begin_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public int getCount() {
        return count;
    }

    public List<DailyList> getList() {
        return list;
    }
}
