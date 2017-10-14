package com.appgame.differ.module.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.bean.game.SimpleGame;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.module.dynamic.adapter.DynamicDetailAdapter;
import com.appgame.differ.module.dynamic.contract.DynamicDetailContract;
import com.appgame.differ.module.dynamic.presenter.DynamicDetailPresenter;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.KeyboardControlManager;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.VideoScrollListener;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.dialog.DynamicDialog;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.appgame.differ.widget.dialog.ReportDialog;
import com.appgame.differ.widget.popupwindow.DynamicPopWindow;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static com.appgame.differ.data.constants.AppConstants.page_size;

public class DynamicDetailActivity extends BaseActivity<DynamicDetailContract.Presenter, DailyComment> implements DynamicDetailContract.View,
        View.OnClickListener, DynamicDialog.OnReplayListener, DynamicDialog.OnDeleteSuccessListener {

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private LinearLayout mPostLayout;
    private RelativeLayout mCommentLayout;
    private TextView mAddComment;
    private TextView mBtnShare;
    private TextView mBtnSend;

    private DynamicDetailAdapter mAdapter;
    private String contentType = "";
    private String dynamicId;
    private String userName;
    private String reply_user_id;
    private String commentId;
    private String reply_id;
    private String userId;

    private DynamicInfo mDynamicInfo;

    private DynamicDialog dynamicDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dynamic_detail;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DynamicDetailPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEditText = (EditText) findViewById(R.id.edit_comm);
        mPostLayout = (LinearLayout) findViewById(R.id.post_layout);
        mCommentLayout = (RelativeLayout) findViewById(R.id.comment_layout);
        mAddComment = (TextView) findViewById(R.id.add_comment);
        mBtnShare = (TextView) findViewById(R.id.btn_share);
        mBtnSend = (TextView) findViewById(R.id.btn_send);
        mAddComment.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        dynamicId = getIntent().getStringExtra("dynamic_id");
        userId = getIntent().getStringExtra("userId");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DynamicDetailAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mPresenter.getDynamicDetail(dynamicId);

        KeyboardControlManager.observerKeyboardVisibleChange(this, (keyboardHeight, isVisible) -> {
            if (isVisible) {
                visible(mPostLayout);
                invisible(mCommentLayout);
                if (mAdapter != null)
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            } else {
                invisible(mPostLayout);
                visible(mCommentLayout);
            }
        });
        setBarRightImageUI(R.drawable.icon_more_def, v -> {
            DynamicPopWindow popWindow = new DynamicPopWindow(DynamicDetailActivity.this, userId);
            popWindow.showAsDropDown(mBarRightImage);
            popWindow.setOnClickListener(type -> {
                if (type.equals("delete")) {
                    OutLoginDialog dialog = new OutLoginDialog(DynamicDetailActivity.this);
                    dialog.show();
                    dialog.setDialogTitle("确定要删除该条动态");
                    dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                        @Override
                        public void onYes() {
                            mPresenter.deleteDynamic(dynamicId);
                        }

                        @Override
                        public void onNo() {
                        }
                    });
                } else if (type.equals("report")) {
                    if (!CommonUtil.isLogin()) {
                        LoginActivity.launch(mContext, "DynamicDetailActivity");
                    } else {
                        ReportDialog reportListFragment = ReportDialog.newInstance(dynamicId, "dynamic");
                        reportListFragment.show(getSupportFragmentManager(), "reportFragment");
                    }
                }
                popWindow.dismiss();
            });
        });

        mAdapter.setOnLikeClickListener((dynamic_id, isThumb, position, target) -> {
            mPresenter.thumbDynamic(dynamic_id, isThumb == 1 ? 0 : 1, position, target);
        });
        mAdapter.setOnCommentLikeListener((tagsId, type, position) -> {
            mPresenter.postDiscussThumb(tagsId, type == 1 ? 0 : 1, position);
        });
        mAdapter.setOnCommentClickListener((comment, position) -> {
            userName = CommonUtil.getNickName(comment.getRelationships().getNickName());
            mEditText.setHint("回复 " + userName + ":");
            contentType = "re_comment_one";
            commentId = comment.getComId();
            CommonUtil.showKeyboard(mEditText);
        });
        mAdapter.setOnReplayClickListener((replies, commentId1) -> {
            userName = CommonUtil.getNickName(replies.getRelationships().getNickName());
            mEditText.setHint("回复 " + userName + ":");
            DynamicDetailActivity.this.commentId = commentId1;
            reply_id = replies.getId();
            reply_user_id = replies.getRelationships().getUserId();
            contentType = "re_comment_two";
            CommonUtil.showKeyboard(mEditText);
        });
        mAdapter.setOnItemLongClickListener((comment, dynamicId, position) -> {
            dynamicDialog = DynamicDialog.newInstance(comment.getContent(), comment.getComId(), comment, position, "comment");
            dynamicDialog.show(getSupportFragmentManager(), "DynamicDialog");
        });
        mAdapter.setOnLoadMoreListener(() -> {
            if (mDynamicInfo == null)
                return;
            String target_id, target;
            if (mDynamicInfo.getTarget().equals("appraise")) {
                target_id = mDynamicInfo.getTargetId();
                target = "appraise";
            } else {
                target_id = dynamicId;
                target = "dynamic";
            }
            mPresenter.loadMoreDynamicComment(target, target_id);
        });

        mRecyclerView.addOnScrollListener(new VideoScrollListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_comment:
                if (SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN)) {
                    mEditText.setHint("请输入内容");
                    CommonUtil.showKeyboard(mEditText);
                    contentType = "comment";
                } else {
                    LoginActivity.launch(this,"EvaluationDetailActivity");
                }
                break;
            case R.id.btn_send:
                String content = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort("内容不能为空");
                    return;
                } else if (mDynamicInfo == null) {
                    return;
                } else {
                    if (contentType.equals("comment")) {
                        String target_id, target;
                        if (mDynamicInfo.getTarget().equals("appraise")) {
                            target_id = mDynamicInfo.getTargetId();
                            target = "appraise";
                        } else {
                            target_id = dynamicId;
                            target = "dynamic";
                        }
                        mPresenter.postComment(target, target_id, content);
                    } else if (contentType.equals("re_comment_one")) {
                        mPresenter.repliesComment(commentId, content, "0", "", "");
                    } else if (contentType.equals("re_comment_two")) {
                        mPresenter.repliesComment(commentId, "回复 " + userName + " " + content, "1", reply_id, reply_user_id);
                    }
                    mEditText.setText("");
                    CommonUtil.HideKeyboard(mEditText);
                }
                mPostLayout.setVisibility(View.INVISIBLE);
                mAddComment.setVisibility(View.VISIBLE);
                mBtnShare.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_share:
                if (CommonUtil.isLogin()) {
                    RecommedInfo game = mDynamicInfo.getGameInfo();
                    SimpleGame simpleGame = new SimpleGame();
                    simpleGame.setGameId(game != null ? game.getGameId() : mDynamicInfo.getArticle().getId());
                    simpleGame.setGameNameCn(game != null ? game.getGameNameCn() : "");
                    simpleGame.setGameIcon(game != null ? game.getIcon() : "");
                    simpleGame.setTagsInfos(game != null ? game.getTags() : new ArrayList<>());
                    Intent intent = new Intent(this, PostDynamicActivity.class);
                    intent.putExtra("dynamic_id", mDynamicInfo.getIsForward().equals("0") ? mDynamicInfo.getId() : mDynamicInfo.getForward().getId());
                    intent.putExtra("postType", "share");
                    String target, targetId;
                    if (mDynamicInfo.getTarget().equals("article")) {
                        target = "article";
                        targetId = mDynamicInfo.getTargetId();
                    } else if (mDynamicInfo.getTarget().equals("appraise")) {
                        target = "appraise";
                        targetId = mDynamicInfo.getTargetId();
                    } else {
                        target = "dynamic";
                        targetId = "0";
                    }
                    intent.putExtra("target", target);
                    intent.putExtra("targetId", targetId);
                    String shareContent;
                    if (mDynamicInfo.getIsForward().equals("0")) {
                        shareContent = "";
                    } else {
                        shareContent = "//@#Forwarding#<font color=\"#15B1B8\">@" + mDynamicInfo.getAuthor().getNickName() + "</font>:" + mDynamicInfo.getContent();
                    }
                    intent.putExtra("shareContent", shareContent);
                    intent.putExtra("game", simpleGame);
                    startActivity(intent);
                } else {
                    LoginActivity.launch(this,"DynamicDetailActivity");
                }
                break;
        }
    }

    @Override
    public void getDynamicDetailSuccess(DynamicInfo info) {
        if (info == null)
            return;
        mDynamicInfo = info;
        mAdapter.setDynamicInfo(mDynamicInfo);
        String target_id, target;
        if (info.getTarget().equals("appraise")) {
            target_id = info.getTargetId();
            target = "appraise";
        } else {
            target_id = dynamicId;
            target = "dynamic";
        }
        mPresenter.getDynamicComment(target, target_id);
    }

    @Override
    public void loadMoreSuccess(List<DailyComment> dailyComments) {
        mDataSource.addAll(dailyComments);
        BaseDiffUtilCallBack<DailyComment> callBack = new BaseDiffUtilCallBack<>(mAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getComId().equals(newData.getComId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - page_size - 2);
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mAdapter.setCanLoading(false);
            mAdapter.showLoadAllDataUI();
        } else {
            mAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showProgressUI() {

    }

    @Override
    public void postCommentSuccess() {
        if (mDynamicInfo == null)
            return;
        String target_id, target;
        if (mDynamicInfo.getTarget().equals("appraise")) {
            target_id = mDynamicInfo.getTargetId();
            target = "appraise";
        } else {
            target_id = dynamicId;
            target = "dynamic";
        }
        mPresenter.getDynamicComment(target, target_id);
        mRecyclerView.postDelayed(() -> {
            if (mAdapter != null)
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }, 500);
    }

    @Override
    public void postDiscussThumbSuccess(int type, int position) {
        mAdapter.updateCommentLike(type, position);
    }

    @Override
    public void thumbDynamicSuccess(int type, int position) {
        RxBus.getBus().send(EvenConstant.KEY_REFRESH_DYNAMIC_LIST);
        mAdapter.updateLikeUI(position, type);
    }

    @Override
    public void onDeleteDynamicSuccess() {
        RxBus.getBus().send(EvenConstant.KEY_DELETE_DYNAMIC_SUCCESS);
        finish();
    }

    @Override
    public void getDynamicCommentSuccess(List<DailyComment> info) {
        mDataSource = info;
        mAdapter.setDataList(mDataSource);
        mAdapter.notifyDataSetChanged();
        mAdapter.setShowLoadMore(mDataSource.size() >= page_size);
    }

    @Override
    public void replay(DailyComment comment, UserGuest userGuest) {
        if (!CommonUtil.isLogin()) {
            LoginActivity.launch(this,"DynamicDetailActivity");
        } else {
            mPostLayout.setVisibility(View.VISIBLE);
            mAddComment.setVisibility(View.INVISIBLE);
            mBtnShare.setVisibility(View.INVISIBLE);

            mEditText.setFocusable(true);
            userName = CommonUtil.getNickName(comment.getRelationships().getNickName());
            mEditText.setHint("回复 " + userName + ":");
            contentType = "re_comment_one";
            commentId = comment.getComId();
            CommonUtil.showKeyboard(mEditText);

            dynamicDialog.dismiss();
        }
    }

    @Override
    public void OnDeleteCommentSuccess(int position) {
        mAdapter.removeCommentUI(position);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore){
            mAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        }else {
            if (msg.equals(AppConstants.NOT_LOGIN)) {
                LoginActivity.launch(mContext,"EvaluationDetailActivity");
            } else {
                ToastUtil.showShort(msg);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardControlManager.clear();
    }
}

