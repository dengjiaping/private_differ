package com.appgame.differ.base.mvp;

/**
 * 基础契约类
 * Created by lzx on 2017/8/10.
 */

public interface BaseContract {
    interface BaseView {
        /**
         * 请求出错
         * @param msg  错误信息
         * @param isLoadMore 是否加载更多，用来区分刷新出错还是加载更多出错
         */
        void showError(String msg, boolean isLoadMore);

        /**
         * 请求完成
         */
        void complete();
    }

    interface BasePresenter<T> {
        /**
         * 绑定
         *
         * @param view view
         */
        void attachView(T view);

        /**
         * 解绑
         */
        void detachView();

        /**
         * 清除
         */
        void clearView();
    }
}
