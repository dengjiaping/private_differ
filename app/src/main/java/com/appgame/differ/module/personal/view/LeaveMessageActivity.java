package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.adapter.LeaveMessageAdapter;
import com.appgame.differ.module.personal.contract.LeaveMessageContract;
import com.appgame.differ.module.personal.presenter.LeaveMessagePresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.KeyboardControlManager;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.dialog.DynamicDialog;

import java.util.List;

/**
 * 全部留言
 * Created by lzx on 2017/5/4.
 * 386707112@qq.com
 */

public class LeaveMessageActivity extends BaseActivity<LeaveMessageContract.Presenter,String> implements View.OnClickListener, LeaveMessageContract.View,
        DynamicDialog.OnReplayListener, DynamicDialog.OnDeleteSuccessListener {

    private LeaveMessageAdapter mMessageAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private TextView mBtnSend;
    private String action;
    private String userId;
    private String position = "0";
    private boolean isMore;

    private LinearLayoutManager mLinearLayoutManager;
    private CustomEmptyView mEmptyView;
    private boolean isReplay = false;
    private String guestId, replayNickName;
    private DynamicDialog mDynamicDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_leave_message;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new LeaveMessagePresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mEditText = (EditText) findViewById(R.id.edit_comm);
        mBtnSend = (TextView) findViewById(R.id.btn_send);

        mEmptyView.initBigUI();

        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageAdapter = new LeaveMessageAdapter(this);
        mRecyclerView.setAdapter(mMessageAdapter);

        mBtnSend.setOnClickListener(this);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int lastCompletelyVisiableItemPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if ((visibleItemCount > 0) && (lastCompletelyVisiableItemPosition >= totalItemCount - 1)) {
                    if (isMore) {
                        mPresenter.requestUserGuest(userId, position, action, true);
                    }
                }
            }
        });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        action = getIntent().getStringExtra("action");
        userId = getIntent().getStringExtra("userId");

        mPresenter.requestUserGuest(userId, position, action, false);
        mMessageAdapter.setOnItemClickListener(this::replayMsg);
        mMessageAdapter.setOnChildItemClickListener((position1, guest, childGuest) -> {
            CommonUtil.showKeyboard(mEditText);
            guestId = guest.getId();
            replayNickName = "回复#Replay#" + CommonUtil.getNickName(childGuest.getAuthor().getNickName());
            isReplay = true;
            mEditText.setHint("回复:" + CommonUtil.getNickName(childGuest.getAuthor().getNickName()));
            mEditText.setText("");
        });
        mMessageAdapter.setOnLikeClickListener(this::onLikeMsg);
        mMessageAdapter.setOnItemLongClickListener((guest, guestId, position) -> {
            mDynamicDialog = DynamicDialog.newInstance(guest.getContent(), guest, position,"guest");
            mDynamicDialog.show(getSupportFragmentManager(), "DynamicDialog");
        });
        KeyboardControlManager.observerKeyboardVisibleChange(this, (keyboardHeight, isVisible) -> {
            if (!isVisible) {
                mEditText.setText("");
                mEditText.setHint("我来说两句");
                guestId = "";
                isReplay = false;
            }
        });
    }

    private void replayMsg(int position, UserGuest guest) {
        mEditText.setFocusable(true);
        CommonUtil.showKeyboard(mEditText);
        guestId = guest.getId();
        replayNickName = "";
        isReplay = true;
        mEditText.setHint("回复:" + CommonUtil.getNickName(guest.getAuthor().getNickName()));
        mEditText.setText("");
    }

    private void onLikeMsg(UserGuest guest, int position) {
        mPresenter.sumbitGuestThumb(guest.getId(), guest.getIsThumb() == 1 ? 0 : 1, position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                String editContent = mEditText.getText().toString().trim();
                if (!isReplay) {
                    mPresenter.sumbitMsg(userId, editContent, "");
                } else {
                    if (!TextUtils.isEmpty(guestId)) {
                        mPresenter.sumbitMsg(userId, replayNickName + " " + editContent, guestId);
                    } else {
                        ToastUtil.showShort("操作失败");
                    }
                }
                break;
        }
    }

    @Override
    public void sumbitMsgSuccess() {
        mEditText.setText("");
        this.position = "0";
        isReplay = false;
        mPresenter.requestUserGuest(userId, position, action, false);
        CommonUtil.HideKeyboard(mEditText);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        isReplay = false;
        mEditText.setText("");
        if (msg.equals(AppConstants.NOT_LOGIN)) {
            LoginActivity.launch(this,"LeaveMessageActivity");
        } else {
            ToastUtil.showShort(msg);
        }
    }

    @Override
    public void onRequestUserGuest(List<UserGuest> list, boolean isLoadMore) {
        if (list.size() > 0)
            this.position = list.get(0).getPosition();
        isMore = list.size() >= 20;
        mMessageAdapter.setList(list, isLoadMore);
        RxBus.getBus().send(list);
    }

    @Override
    public void sumbitGuestThumbSuccess(int position, int type) {
        mMessageAdapter.updateLikeUI(position, type);
    }

    @Override
    public void deleteGuestSuccess(int position) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardControlManager.clear();
    }

    @Override
    public void replay(DailyComment comment, UserGuest userGuest) {
        mDynamicDialog.dismiss();
        replayMsg(0, userGuest);
    }

    @Override
    public void OnDeleteCommentSuccess(int position) {
        mMessageAdapter.removeCommentUI(position);
    }
}
