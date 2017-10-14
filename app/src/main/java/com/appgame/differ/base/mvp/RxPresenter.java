package com.appgame.differ.base.mvp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by lzx on 2017/8/10.
 * 基础Presenter
 */

public class RxPresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    private final CompositeDisposable mDisposable = new CompositeDisposable();
    protected T mView;

    private void unSubscribe() {
        mDisposable.dispose();
    }

    /**
     * 删除
     *
     * @param disposable disposable
     */
    protected boolean remove(Disposable disposable) {
        return mDisposable.remove(disposable);
    }

    protected void addSubscribe(Disposable disposable) {
        mDisposable.add(disposable);
    }

    protected void clearSubscribe() {
        mDisposable.clear();
    }

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {
        unSubscribe();
    }

    @Override
    public void clearView() {
        clearSubscribe();
        this.mView = null;
    }
}