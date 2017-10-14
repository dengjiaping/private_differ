package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.home.presenter.MyGameContract;
import com.appgame.differ.module.home.presenter.MyGamePresenter;
import com.appgame.differ.module.personal.adapter.MineGameAdapter;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.widget.CustomEmptyView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

/**
 * 游戏
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */

public class MineGameFragment extends BaseFragment<MyGameContract.Presenter, String> implements MyGameContract.View {

    private String action;
    private String userId;

    public static MineGameFragment newInstance(String action, String userId) {
        MineGameFragment fragment = new MineGameFragment();
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecyclerView mRecyclerView;
    private MineGameAdapter mMineGameAdapter;
    private RxPermissions mRxPermissions;
    private String position = "0";
    private CustomEmptyView mEmptyView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MyGamePresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        action = getArguments().getString("action");
        userId = getArguments().getString("userId");
        mRxPermissions = new RxPermissions(getActivity());
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);
        mEmptyView.initSmallUI();
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setHasFixedSize(true);
        mMineGameAdapter = new MineGameAdapter(getActivity(), mRxPermissions);
        mRecyclerView.setAdapter(mMineGameAdapter);

    }

    @Override
    protected void initDatas() {
        mPresenter.getGameList("played", position, userId, false);
        DownloadManager.getImpl().initServiceConnectListener(() -> getActivity().runOnUiThread(() -> {
            if (mMineGameAdapter != null)
                mMineGameAdapter.notifyDataSetChanged();
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        gone(mEmptyView);
    }

    @Override
    public void onGameListSuccess(List<RecommedInfo> recommedInfos, boolean isLoadMore) {
        mMineGameAdapter.setInfoList(recommedInfos, isLoadMore, false);
        mEmptyView.setVisibility(recommedInfos.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onGetRecommendListSuccess(List<RecommedInfo> recommedInfos) {

    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        ToastUtil.showShort(msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }
}
