package com.appgame.differ.module.search.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.home.RecommedInfo;

import java.util.List;

/**
 * Created by lzx on 2017/8/10.
 */

public class GameClassContract {
    public interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getGameClassList(String typeId);

        void loadMoreGameClassList(String typeId);
    }

    public interface View extends BaseContract.BaseView {
        void requestGameClassListSuccess(List<RecommedInfo> list);

        void loadMoreGameClassListSuccess(List<RecommedInfo> list);

        void loadFinishAllData();
    }
}
