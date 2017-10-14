package com.appgame.differ.module.topic.mvp;

import android.content.Context;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONObject;

/**
 * Created by lzx on 2017/3/15.
 * 386707112@qq.com
 */

public class TopicDetailPresenter extends RxPresenter<TopicDetailContract.View> implements TopicDetailContract.Presenter<TopicDetailContract.View> {


    private Context mContext;

    public TopicDetailPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void requestDetail(String topic_id) {
        RetrofitHelper.getAppGameAPI().getTopicDetail(topic_id, CommonUtil.getPhoneWidthString(mContext), CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONObject data = jsonObject.getJSONObject("data");
                    TopicInfo topicInfo = new TopicInfo();
                    topicInfo.resolveJson(data);
                    return topicInfo;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<TopicInfo>(mView,false) {
                    @Override
                    public void onSuccess(TopicInfo topicInfo) {
                        mView.onRequestDetailSuccess(topicInfo);
                    }
                });
    }


}
