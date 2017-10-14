package com.appgame.differ.bean.find;

import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.recommend.TopicInfo;

import java.util.List;

/**
 * Created by lzx on 2017/5/27.
 * 386707112@qq.com
 */

public class DiscoverNavInfo {
    public String position;
    private List<RecommedInfo> mRecommedInfos;
    private List<DailyInfo> mDailyInfos;
    private List<TopicInfo> mTopicInfos;

    public List<RecommedInfo> getRecommedInfos() {
        return mRecommedInfos;
    }

    public void setRecommedInfos(List<RecommedInfo> recommedInfos) {
        mRecommedInfos = recommedInfos;
    }

    public List<DailyInfo> getDailyInfos() {
        return mDailyInfos;
    }

    public void setDailyInfos(List<DailyInfo> dailyInfos) {
        mDailyInfos = dailyInfos;
    }

    public List<TopicInfo> getTopicInfos() {
        return mTopicInfos;
    }

    public void setTopicInfos(List<TopicInfo> topicInfos) {
        mTopicInfos = topicInfos;
    }
}
