package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.personal.adapter.AboutAdapter;
import com.appgame.differ.module.personal.contract.PersonalContract;
import com.appgame.differ.module.personal.presenter.PersonalPresenter;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.HeaderScrollHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于TA
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */

public class AboutFragment extends BaseFragment<PersonalContract.Presenter,String> implements PersonalContract.View, HeaderScrollHelper.ScrollableContainer {

    private String action;
    private String userId;
    private String position = "0";

    public static AboutFragment newInstance(String action, String userId) {
        AboutFragment fragment = new AboutFragment();
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private AboutAdapter mAboutAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new PersonalPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        action = getArguments().getString("action");
        userId = getArguments().getString("userId");
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAboutAdapter = new AboutAdapter(getActivity(), action, userId);
        mRecyclerView.setAdapter(mAboutAdapter);

        mPresenter.requestUserGuest(userId, position, action);

        if (!TextUtils.isEmpty(action) && action.equals("other")) {
            mPresenter.requestUserInfoById(userId, true);
        } else {
            UserInfo userInfo = UserInfoManager.getImpl().getUserInfo();
            mAboutAdapter.setUserInfo(userInfo);
        }

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof ArrayList<?>) {
                List<UserGuest> list = (List<UserGuest>) o;
                mAboutAdapter.setList(list);
            } else if (o instanceof UserInfo) {
                UserInfo userInfo = (UserInfo) o;
                mAboutAdapter.setUserInfo(userInfo);
            }
        });
    }

    @Override
    public View getScrollableView() {
        return mRecyclerView;
    }

    @Override
    public void onUserInfoSuccess(UserInfo userInfo, boolean isOther) {
        //do nothing
    }

    @Override
    public void onFollowSuccess(String action) {
        //do nothing
    }

    @Override
    public void onRequestUserGuest(List<UserGuest> list, String position) {
        mAboutAdapter.setList(list);
    }
}
