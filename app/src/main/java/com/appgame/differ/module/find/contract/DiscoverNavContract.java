package com.appgame.differ.module.find.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.recommend.TopicInfo;

import java.util.List;

/**
 * Created by lzx on 2017/5/27.
 * 386707112@qq.com
 */

public interface DiscoverNavContract {
    interface View extends BaseContract.BaseView {

        void onRequestGame(List<RecommedInfo> infoList);

        void onRequestArticles(List<DailyInfo> dailyInfos);

        void onRequestTopicList(List<TopicInfo> topicInfos);

        void loadMoreGameSuccess(List<RecommedInfo> infoList);

        void loadMoreArticlesSuccess(List<DailyInfo> dailyInfos);

        void loadMoreTopicList(List<TopicInfo> topicInfos);

        void loadFinishAllData();

        void showProgressUI();

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestDiscoverNavInfo(String id, String type );

        void loadMore(String id, String type );
    }

}
