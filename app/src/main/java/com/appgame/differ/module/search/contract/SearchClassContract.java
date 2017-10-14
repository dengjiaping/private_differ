package com.appgame.differ.module.search.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.game.GameCategory;
import com.appgame.differ.bean.search.HotSearch;

import java.util.List;

/**
 * Created by lzx on 2017/5/24.
 * 386707112@qq.com
 */

public interface SearchClassContract {
    interface View extends BaseContract.BaseView {

        void onHotSearchSuccess(List<HotSearch> hotSearches);

        void onGameClassSuccess(List<GameCategory> gameCategories);

    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getHotSearch();

        void getGameClass();
    }
}
