package com.appgame.differ.module.daily.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.daily.DailyListInfo;

/**
 * Created by lzx on 2017/4/14.
 * 386707112@qq.com
 */

public interface DailyContract {
    interface View extends BaseContract.BaseView {

        void onRequestSuccess(DailyListInfo dailyListInfo, boolean loadMore);

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestDailyList(String target,int position,boolean loadMore);
    }
}
