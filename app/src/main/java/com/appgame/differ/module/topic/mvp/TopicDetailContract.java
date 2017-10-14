package com.appgame.differ.module.topic.mvp;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.recommend.TopicInfo;

/**
 * Created by lzx on 2017/3/15.
 * 386707112@qq.com
 */

public interface TopicDetailContract {
    interface View extends BaseContract.BaseView {
        void onRequestDetailSuccess(TopicInfo topicInfo);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestDetail(String topic_id);
    }
}
