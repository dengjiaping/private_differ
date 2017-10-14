package com.appgame.differ.module.msg;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.module.msg.adapter.MsgReplyAdapter;

/**
 * 消息回复
 * Created by lzx on 2017/5/9.
 * 386707112@qq.com
 */

public class MsgReplyActivity extends BaseActivity implements View.OnClickListener {

    private TextView mBtnSend;
    private RecyclerView mRecyclerView;
    private EditText mEditComm;
    private MsgReplyAdapter mReplyAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_msg_replay;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnSend = (TextView) findViewById(R.id.btn_send);
        mEditComm = (EditText) findViewById(R.id.edit_comm);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mBtnSend.setOnClickListener(this);

        mReplyAdapter = new MsgReplyAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mReplyAdapter);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                break;
        }
    }
}
