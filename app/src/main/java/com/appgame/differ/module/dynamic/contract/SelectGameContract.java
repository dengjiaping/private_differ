package com.appgame.differ.module.dynamic.contract;


import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.collection.CollectionInfo;

import java.util.List;

/**
 * Created by lzx on 17/5/11.
 */

public interface SelectGameContract {

    interface View extends BaseContract.BaseView {
        void onPlayingGameSuccess(List<CollectionInfo> collectionInfos);

        void onSearchGameSuccess(List<CollectionInfo> collectionInfos);


    }


    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getPlayingGame(String type, String position, String userId);

        void getGameBySearch(String keyWord);
    }
}
