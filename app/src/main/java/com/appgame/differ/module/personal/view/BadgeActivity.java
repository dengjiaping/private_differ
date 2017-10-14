package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.user.AchievesInfo;
import com.appgame.differ.bean.user.UserInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.adapter.BadgeAdapter;
import com.appgame.differ.module.personal.contract.BadgeContract;
import com.appgame.differ.module.personal.presenter.BadgePresenter;
import com.appgame.differ.utils.ToastUtil;

/**
 * 徽章
 * Created by lzx on 2017/5/4.
 * 386707112@qq.com
 */

public class BadgeActivity extends BaseActivity<BadgeContract.Presenter,AchievesInfo> implements BadgeContract.View {

    private RecyclerView mRecyclerView;
    private BadgeAdapter mBadgeAdapter;
    private String action;
    private boolean isOther;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_badge;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new BadgePresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        isOther = getIntent().getBooleanExtra("isOther", false);
        action = getIntent().getStringExtra("action");
        mDataSource = getIntent().getParcelableArrayListExtra("data");

        if (!TextUtils.isEmpty(action) && action.equals("all")) {
            setTitle("全部徽章");
        } else {
            setTitle("我的徽章");
        }

        mBadgeAdapter = new BadgeAdapter(this, action);
        if (isOther) {
            if (mDataSource != null) {
                mBadgeAdapter.setBadgeInfoList(mDataSource);
            }
        } else {
            UserInfo userInfo = UserInfoManager.getImpl().getUserInfo();
            if (userInfo.getAchieves() != null) {
                mBadgeAdapter.setBadgeInfoList(userInfo.getAchieves());
            }
        }
        mRecyclerView.setAdapter(mBadgeAdapter);
        mBadgeAdapter.setOnItemClickListener((position, achieve_id) -> mPresenter.changeIsShowBadge(position, achieve_id));
    }

    @Override
    public void onGetMineBadgeSuccess() {

    }

    @Override
    public void onGetAllBadgeSuccess() {

    }

    @Override
    public void changeIsShowBadgeSuccess(int position) {
        mBadgeAdapter.updateItemChange(position);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (msg.equals(AppConstants.NOT_LOGIN)) {
            LoginActivity.launch(this,"BadgeActivity");
        }else {
            ToastUtil.showShort(msg);
        }
    }
}
