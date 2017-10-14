package com.appgame.differ.module.msg;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.MsgInfo;
import com.appgame.differ.module.msg.adapter.MsgListAdapter;
import com.appgame.differ.module.msg.presenter.MsgCenterContract;
import com.appgame.differ.module.msg.presenter.MsgCenterPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */

public class MsgListActivity extends BaseActivity<MsgCenterContract.Presenter, String> implements MsgCenterContract.View {

    private RecyclerView mRecyclerView;
    private MsgListAdapter mMsgListAdapter;
    private String msgType;
    private String title;

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
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        msgType = getIntent().getStringExtra("msgType");
        title = getIntent().getStringExtra("title");

        setBarTitleUI(title);

        mMsgListAdapter = new MsgListAdapter(this, msgType);
        mRecyclerView.setAdapter(mMsgListAdapter);

        List<MsgInfo> list = new ArrayList<>();
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());
        list.add(new MsgInfo());

        mMsgListAdapter.setList(list);

        if (msgType.equals("system")) {
            mPresenter.getSystemMsg();
        } else if (msgType.equals("official")) {
            mPresenter.getOfficialMsg();
        }

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
