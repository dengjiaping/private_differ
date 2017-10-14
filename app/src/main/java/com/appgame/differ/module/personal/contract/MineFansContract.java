package com.appgame.differ.module.personal.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.user.UserInfo;

import java.util.List;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public interface MineFansContract {
    interface View extends BaseContract.BaseView {

        void requestFansSuccess(List<UserInfo> userInfos);

        void loadMoreSuccess(List<UserInfo> userInfos);

        void loadFinishAllData();

        void showProgressUI();

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestFans(String action, String userId);

        void loadMore(String action, String userId);
    }
}
