package com.appgame.differ.module.dynamic.contract;

import com.appgame.differ.base.mvp.BasePresenter;
import com.appgame.differ.base.mvp.BaseView;

import java.util.List;

import io.reactivex.ObservableTransformer;

/**
 * Created by yukunlin on 17/5/11.
 */

public interface PostDynamicContract {
    interface View extends BaseView<PostDynamicContract.Presenter> {
        void onPostDynamicSuccess();

        void onPostDynamicError(String msg);

        void showProgressDialog(String msg, String type);

        <T> ObservableTransformer<T, T> bindToLifecycle();
    }

    interface Presenter extends BasePresenter {
        void postDynamic(String game_id, String content, List<String> imgPath, String dynamic_id, String postType, String type, String target_id);

    }
}
