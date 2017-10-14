package com.appgame.differ.module.msg;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.module.msg.adapter.MsgCenterAdapter;
import com.appgame.differ.module.msg.presenter.MsgCenterContract;
import com.appgame.differ.module.msg.presenter.MsgCenterPresenter;

/**
 * 消息中心
 * Created by lzx on 2017/5/2.
 * 386707112@qq.com
 */

public class MsgCenterActivity extends BaseActivity<MsgCenterContract.Presenter, String> implements MsgCenterContract.View {

    private RecyclerView mRecyclerView;
    private MsgCenterAdapter mMsgCenterAdapter;
    private MsgCenterPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_center;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new MsgCenterPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMsgCenterAdapter = new MsgCenterAdapter(this);
        mRecyclerView.setAdapter(mMsgCenterAdapter);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPresenter.getUserMsg();
    }

    @Override
    public void requestUserMsgSuccess() {

    }

    @Override
    public void requestSystemMsgSuccess() {

    }

    @Override
    public void requestOfficialMsgSuccess() {

    }
}
