package com.appgame.differ.module.dynamic.contract;

import com.appgame.differ.base.mvp.BaseContract;
import com.appgame.differ.bean.dynamic.DynamicInfo;

import java.util.List;

/**
 * Created by lzx on 17/5/12.
 */

public interface DynamicContract {


    interface View extends BaseContract.BaseView {
        void onGetDynamicSuccess(List<DynamicInfo> dynamics, int removeCount);

        void loadMoreSuccess(List<DynamicInfo> dynamics,int removeCount);

        void loadFinishAllData();

        void showProgressUI(boolean isShow);

        void onDeleteDynamicSuccess(int position);

        void thumbDynamicSuccess(int type, int position);
    }

    interface Presenter<T> extends BaseContract.BasePresenter<T> {
        void getDynamic(String user_id, boolean isShowPro);

        void loadMore(String user_id);

        void deleteDynamic(String dynamic_id, int position);

        void thumbDynamic(String dynamic_id, int type, int position, String thumbType);
    }

}
