package com.appgame.differ.module.search.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.bean.mine.MineFollows;

import java.util.List;

/**
 * Created by lzx on 2017/6/14.
 */

public interface SearchResultContract {

    interface View extends BaseContract.BaseView {
      //  void onRequestError(String msg);

        void onSearchGameSuccess(List<RecommedInfo> recommedInfos);

        void onSearchArticlesSuccess(List<DailyInfo> dailyInfos);

        void onSearchUserSuccess(List<MineFollows> userInfos);

        void loadMoreGameSuccess(List<RecommedInfo> recommedInfos);

        void loadMoreArticlesSuccess(List<DailyInfo> dailyInfos);

        void loadMoreUserSuccess(List<MineFollows> userInfos);

      //  void loadMoreFail(String msg);

        void loadGameFinishAllData();

        void loadArticlesFinishAllData();

        void loadUserFinishAllData();
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void searchGameByKeyWords(String keywords);

        void searchArticlesByKeyWords(String keywords);

        void searchUserByKeyWords(String keywords);

        void loadMoreGameByKeyWords(String keywords);

        void loadMoreArticlesByKeyWords(String keywords);

        void loadMoreUserByKeyWords(String keywords);
    }
}
