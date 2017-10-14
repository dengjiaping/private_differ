package com.appgame.differ.module.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.evaluation.EvaluationInfo;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.game.adapter.DiscussAdapter;
import com.appgame.differ.module.game.contract.EvaDetailContract;
import com.appgame.differ.module.game.presenter.EvaDetailPresenter;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.KeyboardControlManager;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.widget.ShapeImageView;
import com.appgame.differ.widget.dialog.DynamicDialog;
import com.appgame.differ.widget.dialog.OutLoginDialog;
import com.appgame.differ.widget.dialog.ReportDialog;
import com.appgame.differ.widget.dialog.ShareDialog;
import com.appgame.differ.widget.popupwindow.DynamicPopWindow;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.appgame.differ.data.constants.AppConstants.page_size;


public class EvaluationDetailActivity extends BaseActivity<EvaDetailContract.Presenter, String> implements EvaDetailContract.View, View.OnClickListener,
        DynamicDialog.OnReplayListener, DynamicDialog.OnDeleteSuccessListener {

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private LinearLayout mPostLayout, mTitleLayout;
    private TextView mAddTagBtn, mPostDiscuss, mGameTitle, mSendBtn, mBtnShare;

    private ShapeImageView mGameIcon;

    private DiscussAdapter mAdapter;

    private String position = "0";
    private String contentType;

    private GameInfo mGameInfo;
    private String comId;
    private String userId;

    private String reply_id;
    private String commentId;
    private String userName;
    private String reply_user_id;
    private String target = "appraise";
    private boolean isMore;

    private UserAppraise mUserAppraise;
    private DynamicDialog mDynamicDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_evaluation;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new EvaDetailPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mEditText = (EditText) findViewById(R.id.edit_comm);
        mPostLayout = (LinearLayout) findViewById(R.id.post_layout);
        mAddTagBtn = (TextView) findViewById(R.id.add_tag);
        mBtnShare = (TextView) findViewById(R.id.btn_share);
        mPostDiscuss = (TextView) findViewById(R.id.post_discuss);

        mGameIcon = (ShapeImageView) findViewById(R.id.game_icon);
        mGameTitle = (TextView) findViewById(R.id.game_title);
        mSendBtn = (TextView) findViewById(R.id.btn_send);
        mTitleLayout = (LinearLayout) findViewById(R.id.top_layout);

        mSendBtn.setOnClickListener(this);
        mPostDiscuss.setOnClickListener(this);
        mAddTagBtn.setOnClickListener(this);
        mGameIcon.setOnClickListener(this);
        mTitleLayout.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);

        // 避免notifyItemChanged时闪烁
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mGameInfo = getIntent().getParcelableExtra("game_info");
        comId = getIntent().getStringExtra("commendId");
        userId = getIntent().getStringExtra("userId");


        mAdapter = new DiscussAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.getDiscussList(target, comId, position, page_size, false);

        mGameTitle.setText(mGameInfo.getGameNameCn());
        Glide.with(this).load(mGameInfo.getIcon()).crossFade().diskCacheStrategy(DiskCacheStrategy.ALL).into(mGameIcon);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(mRecyclerView)) {
                    if (isMore) {
                        loading = true;
                        mPresenter.getDiscussList(target, comId, position, page_size, true);
                    }
                }
            }
        });

        KeyboardControlManager.observerKeyboardVisibleChange(this, (keyboardHeight, isVisible) -> {
            if (isVisible) {
                mPostLayout.setVisibility(View.VISIBLE);
                mPostDiscuss.setVisibility(View.INVISIBLE);
            } else {
                mPostLayout.setVisibility(View.INVISIBLE);
                mPostDiscuss.setVisibility(View.VISIBLE);
            }
        });
        //添加标签
        mAdapter.setOnAddTagClickListener((position, tagsInfo) -> {
            addTagAction();
        });
        //标签点赞、踩
        mAdapter.setOnTagClickListener((tagsId, type, position) -> mPresenter.postTagsThumb(tagsId, type, position));
        //回复
        mAdapter.setOnChildItemClickListener((position1, comment) -> {
            userName = CommonUtil.getNickName(comment.getRelationships().getNickName());
            mEditText.setHint("回复 " + userName + ":");
            commentId = comment.getComId();
            contentType = "re_comment_one";
            CommonUtil.showKeyboard(mEditText);
        });
        mAdapter.setOnReplayCommentListener((replies, commentId1) -> {
            userName = CommonUtil.getNickName(replies.getRelationships().getNickName());
            mEditText.setHint("回复 " + userName + ":");
            EvaluationDetailActivity.this.commentId = commentId1;
            reply_id = replies.getId();
            reply_user_id = replies.getRelationships().getUserId();
            contentType = "re_comment_two";
            CommonUtil.showKeyboard(mEditText);
        });
        //喜欢
        mAdapter.setOnLikeClickListener((tagsId, type, position) -> mPresenter.postDiscussThumb(tagsId, type == 1 ? 0 : 1, position));
        mAdapter.setOnFirstLikeClickListener((commend_id, isThumb, position) -> mPresenter.thumbEvaluation(commend_id, isThumb == 1 ? 0 : 1, position));
        //长按删除
        mAdapter.setOnItemLongClickListener((comment, dynamicId, position12) -> {
            mDynamicDialog = DynamicDialog.newInstance(comment.getContent(), comment.getComId(), comment, position12, "comment");
            mDynamicDialog.show(getSupportFragmentManager(), "DynamicDialog");
        });
        setBarRightImageUI(R.drawable.icon_more_def, v -> {
            DynamicPopWindow popWindow = new DynamicPopWindow(EvaluationDetailActivity.this, userId);
            popWindow.showAsDropDown(mBarRightImage);
            popWindow.setOnClickListener(type -> {
                if (type.equals("delete")) {
                    OutLoginDialog dialog = new OutLoginDialog(EvaluationDetailActivity.this);
                    dialog.show();
                    dialog.setDialogTitle("确定要删除该条评论");
                    dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                        @Override
                        public void onYes() {
                            // mPresenter.deleteDynamic(dynamicId, bindUntilEvent(ActivityEvent.STOP));
                            if (mUserAppraise != null) {
                                deleteComment(mUserAppraise.getId());
                            }
                        }

                        @Override
                        public void onNo() {
                        }
                    });
                } else if (type.equals("report")) {
                    if (!CommonUtil.isLogin()) {
                        LoginActivity.launch(this, "DynamicDetailActivity");
                    } else {
                        if (mUserAppraise != null) {
                            ReportDialog reportListFragment = ReportDialog.newInstance(mUserAppraise.getId(), "appraise");
                            reportListFragment.show(getSupportFragmentManager(), "reportFragment");
                        }
                    }
                }
                popWindow.dismiss();
            });
        });
    }

    private void deleteComment(String commendId) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        RetrofitHelper.getAppGameAPI().appraisesDelete(token, commendId, CommonUtil.getExtraParam())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    if (status == 200) {
                        finish();
                    } else {
                        ToastUtil.showShort("删除失败");
                    }
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        ToastUtil.showShort(error.getTitle());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_discuss:
                if (CommonUtil.isLogin()) {
                    mEditText.setHint("请输入内容");
                    CommonUtil.showKeyboard(mEditText);
                    contentType = "comment";
                } else {
                    LoginActivity.launch(this, "EvaluationDetailActivity");
                }
                break;
            case R.id.add_tag:
                addTagAction();
                break;
            case R.id.btn_send:
                String content = mEditText.getText().toString().trim();
                switch (contentType) {
                    case "comment":
                        mPresenter.postComment(target, comId, content);
                        break;
                    case "tag":
                        mPresenter.postTags(target, comId, content);
                        break;
                    case "re_comment_one":
                        mPresenter.repliesComment(commentId, content, "0", "", "");
                        break;
                    case "re_comment_two":
                        mPresenter.repliesComment(commentId, "回复 " + userName + " " + content, "1", reply_id, reply_user_id);
                        break;
                }
                mEditText.setText("");
                CommonUtil.HideKeyboard(mEditText);
                break;
            case R.id.top_layout:
                Intent intent = new Intent(this, GameDetailsActivity.class);
                intent.putExtra("game_id", mGameInfo.getGameId());
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.btn_share:
                if (mUserAppraise == null) {
                    ToastUtil.showShort("分享失败");
                    return;
                }
                ShareDialog dialog = new ShareDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShareToDynamic", false);
                bundle.putString("shareType", "comment");
                bundle.putParcelable("userAppraise", mUserAppraise);
                bundle.putParcelable("gameInfo", mGameInfo);
                bundle.putString("shareTitle", mUserAppraise.getCommented());
                bundle.putString("shareUrl", "http://differ.appgame.com/gameinfo.html?id=" + mUserAppraise.getGame().getGameId());
                dialog.setArguments(bundle);
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(dialog, "ShareDialog");
                ft.commitAllowingStateLoss();
                break;
        }
    }

    /**
     * 添加标签UI
     */
    private void addTagAction() {
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
        if (isLogin) {
            mEditText.setHint("请标签输入内容");
            CommonUtil.showKeyboard(mEditText);
            contentType = "tag";
        } else {
            LoginActivity.launch(this, "EvaluationDetailActivity");
        }
    }

    /**
     * 获取评论成功
     */
    @Override
    public void onCommentListSuccess(EvaluationInfo result, boolean isLoadMore) {
        isMore = result.getList().size() >= page_size;
        position = result.position;
        mUserAppraise = result.getUserAppraise();
        mAdapter.setCommentList(result.getCommentList(), result.getUserAppraise(), isLoadMore);
    }

    /**
     * 添加标签成功
     */
    @Override
    public void onPostTagsSuccess() {
        position = "0";
        mPresenter.getDiscussList(target, comId, position, page_size, false);
    }

    @Override
    public void onPostCommentSuccess() {
        position = "0";
        mPresenter.getDiscussList(target, comId, position, page_size, false);
    }

    /**
     * 喜欢取消喜欢成功
     */
    @Override
    public void postLikeClickSuccess(int type, int position) {
        mAdapter.updateLikeUI(type, position);
    }

    /**
     * 点赞踩成功
     */
    @Override
    public void postTagsThumbSuccess(int type, int position) {
        this.position = "0";
        mPresenter.getDiscussList(target, comId, this.position, page_size, false);
    }

    @Override
    public void repliesCommentSuccess() {
        position = "0";
        mPresenter.getDiscussList(target, comId, position, page_size, false);
    }

    @Override
    public void onRequestError(String msg) {
        if (msg.equals(AppConstants.NOT_LOGIN)) {
            LoginActivity.launch(this, "EvaluationDetailActivity");
        } else {
            ToastUtil.showShort(msg);
        }
    }

    @Override
    public void onTagListSuccess(List<TagsInfo> tagsInfos, int itemPosition) {

    }

    @Override
    public void thumbEvaluationSuccess(int type, int position) {
        mAdapter.updateFirstLikeUI(type, position);
    }

    @Override
    public void replay(DailyComment comment, UserGuest userGuest) {
        mDynamicDialog.dismiss();
        if (!CommonUtil.isLogin()) {
            LoginActivity.launch(this, "DynamicDetailActivity");
        } else {
            mPostLayout.setVisibility(View.VISIBLE);
            mPostDiscuss.setVisibility(View.INVISIBLE);

            userName = CommonUtil.getNickName(comment.getRelationships().getNickName());
            mEditText.setHint("回复 " + userName + ":");
            commentId = comment.getComId();
            contentType = "re_comment_one";
            CommonUtil.showKeyboard(mEditText);
        }
    }

    @Override
    public void OnDeleteCommentSuccess(int position) {
        mAdapter.removeCommentUI(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardControlManager.clear();
    }


}