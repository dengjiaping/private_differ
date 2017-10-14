package com.appgame.differ.module.find.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.NavigationInfo;
import com.appgame.differ.widget.banner.BannerEntity;

import java.util.List;

/**
 * Created by lzx on 2017/5/24.
 * 386707112@qq.com
 */

public interface FindContract {
    interface View extends BaseContract.BaseView {

        void onRequestBannerSuccess(List<BannerEntity> bannerEntities);

        void onRequestNavigationSuccess(List<NavigationInfo> navigationInfos);

        void onRequestConventionsSuccess(List<FindListInfo> findListInfos, int removeCount);

        void onLoadMoreSuccess(List<FindListInfo> listInfoList, int removeCount);

        void loadFinishAllData();

        void showProgressUI(boolean isShow);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void requestBanner(boolean isShowPro);

        void requestNavigation();

        void requestConventions(String target);

        void requestLoadMore(String target);
    }

}
