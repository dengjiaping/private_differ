package com.appgame.differ.module.game;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.download.DownLoadFlag;
import com.appgame.differ.bean.download.DownloadInfo;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.GameRequire;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.game.adapter.GameDetailAdapter;
import com.appgame.differ.module.game.contract.GamesDetailContract;
import com.appgame.differ.module.game.presenter.GamesDetailPresenter;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.dialog.ShareDialog;
import com.appgame.differ.widget.download.DownloadProgressButton;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.socialize.UMShareAPI;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static com.appgame.differ.data.constants.AppConstants.page_size;


/**
 * Created by lzx on 2017/5/23.
 * 386707112@qq.com
 */
public class GameDetailsActivity extends BaseActivity<GamesDetailContract.Presenter, String> implements View.OnClickListener, GamesDetailContract.View {

    private ImageView mBarLeftImage, mBarLeftImage2, mBtnFeedback, mBtnFeedback2;
    private TextView mBtnShare, mBtnEvaluate, mBarTitle;
    private DownloadProgressButton mDownloadBar;
    private RecyclerView mRecyclerView;
    private CircleProgressView mProgressView;
    private GameDetailAdapter mDetailAdapter;
    private GameInfo mGameInfo;
    private UserAppraise mUserAppraise;
    private List<UserAppraise> mUserAppraises;
    private String gameId;
    //   private GamesDetailPresenter mPresenter;
    private DownloadManager mDownloadManager;
    private RxPermissions mRxPermissions;

    private RelativeLayout mTopBar;
    private float mDistanceY = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_details;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new GamesDetailPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (RelativeLayout) findViewById(R.id.top_bar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mBtnShare = (TextView) findViewById(R.id.btn_share);
        mBtnEvaluate = (TextView) findViewById(R.id.go_evaluate);
        mBtnFeedback = (ImageView) findViewById(R.id.btn_feedback);
        mBtnFeedback2 = (ImageView) findViewById(R.id.btn_feedback2);
        mBarLeftImage = (ImageView) findViewById(R.id.bar_left_image);
        mBarLeftImage2 = (ImageView) findViewById(R.id.bar_left_image2);
        mBarTitle = (TextView) findViewById(R.id.bar_title);
        mProgressView = (CircleProgressView) findViewById(R.id.load_pro);
        mDownloadBar = (DownloadProgressButton) findViewById(R.id.btn_download);

        SystemBarHelper.immersiveStatusBar(this);

        mTopBar.setPadding(0, CommonUtil.getStatusBarHeight(GameDetailsActivity.this), 0, 0);
        mBtnFeedback2.setPadding(0, CommonUtil.getStatusBarHeight(GameDetailsActivity.this) + CommonUtil.dp2px(GameDetailsActivity.this, 10), 0, 0);
        mBarLeftImage2.setPadding(0, CommonUtil.getStatusBarHeight(GameDetailsActivity.this) + CommonUtil.dp2px(GameDetailsActivity.this, 10), 0, 0);
        mTopBar.setAlpha(0);

        mBtnShare.setOnClickListener(this);
        mBtnEvaluate.setOnClickListener(this);
        mDownloadBar.setOnClickListener(this);
        mBtnFeedback.setOnClickListener(this);
        mBtnFeedback2.setOnClickListener(this);
        mBarLeftImage.setOnClickListener(this);
        mBarLeftImage2.setOnClickListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mDetailAdapter = new GameDetailAdapter(this);
        mRecyclerView.setAdapter(mDetailAdapter);

        int height = CommonUtil.dip2px(GameDetailsActivity.this, 250);
        int bottom_height = CommonUtil.dip2px(GameDetailsActivity.this, 50);
        int toolbarHeight = height - mTopBar.getMeasuredHeight() - bottom_height;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mDistanceY += dy;
                if (mDistanceY <= toolbarHeight) {
                    if (mDistanceY < 0) mDistanceY = 0;
                    float scale = mDistanceY / toolbarHeight;
                    mTopBar.setAlpha(scale);
                } else {
                    JCVideoPlayer.releaseAllVideos();
                    mDistanceY = toolbarHeight;
                    mTopBar.setAlpha(1f);
                }
            }
        });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        gameId = getIntent().getStringExtra("game_id");
        mRxPermissions = new RxPermissions(this);
        mDownloadManager = new DownloadManager();


        mPresenter.getGameData(gameId);

        mDetailAdapter.setOnLoadMoreListener(() -> {
            if (mGameInfo != null)
                mPresenter.loadMoreEvaluationList(mGameInfo.getGameId(), "created_at");
        });
        mDetailAdapter.setOnLikeClickListener((commend_id, isThumb, position, isUserComment) -> {
            mPresenter.thumbEvaluation(commend_id, isThumb == 1 ? 0 : 1, position, isUserComment);
        });

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        if (mGameInfo != null) {
                            DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
                            mDownloadManager.startAllAutoTasks(downloadInfo);
                        }
                    } else {
                        if (mGameInfo != null) {
                            DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
                            int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl);
                            FileDownloader.getImpl().pause(downloadId);
                        }
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
            } else if (o instanceof String) {
                String event = (String) o;
                switch (event) {
                    case EvenConstant.KEY_RELOAD_APP_LIST:
                        AppInfoManager.getImpl().updateAppInfo();
                        if (mGameInfo != null) {
                            DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
                            boolean hasApp = AppInfoManager.getImpl().hasPkg(downloadInfo.downloadPackageName);
                            if (hasApp) {
                                mDownloadBar.setProgressUI(100, DownLoadFlag.has_app);
                            }
                        }
                        break;
                    case EvenConstant.KEY_REFRESH_GAMES_COMMENTS:
                    case EvenConstant.KEY_LOGIN_SUCCESS:
                        mPresenter.getUserAppraise(gameId, "hot");
                        mRecyclerView.postDelayed(() -> {
                            if (mGameInfo != null) {
                                List<GameRequire> requires = mGameInfo.getGameRequires();
                                int itemChanged = requires.size() > 0 ? 4 : 3;
                                mDetailAdapter.notifyItemChanged(itemChanged);
                            }
                        }, 500);
                        break;
                }
            }
        });
    }

    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_share:
                if (mGameInfo != null) {
                    ShareDialog dialog = new ShareDialog();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("gameInfo", mGameInfo);
                    bundle.putParcelable("userAppraise", mUserAppraise);
                    bundle.putBoolean("isShareToDynamic", true);
                    bundle.putString("shareType", "game");
                    bundle.putString("shareTitle", mGameInfo.getGameNameCn());
                    bundle.putString("shareUrl",
                            AppConstants.IS_DEBUG ? "http://games-planet.test.appgame.com/m/gameinfo.html?id=" + mGameInfo.getGameId()
                                    : "http://differ.appgame.com/gameinfo.html?id=" + mGameInfo.getGameId());
                    dialog.setArguments(bundle);
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.add(dialog, "ShareDialog");
                    ft.commitAllowingStateLoss();
                }
                break;
            case R.id.go_evaluate:
                if (CommonUtil.isLogin()) {
                    intent = new Intent(this, PostEvaluationActivity.class);
                    if (mUserAppraise != null) {
                        intent.putExtra("content", mUserAppraise.getContent());
                        intent.putExtra("star", mUserAppraise.getStar());
                        intent.putExtra("id", mUserAppraise.getId());
                    }
                    intent.putExtra("game_id", gameId);
                    startActivity(intent);

                } else {
                    LoginActivity.launch(this, "GameDetailActivity");
                }
                break;
            case R.id.btn_download:
                downloadGame();
                break;
            case R.id.btn_feedback2:
            case R.id.btn_feedback:
                if (!SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN)) {
                    LoginActivity.launch(this, "GameDetailActivity");
                } else {
                    intent = new Intent(this, GameFeedbackActivity.class);
                    intent.putExtra("game_id", gameId);
                    startActivity(intent);
                }
                break;
            case R.id.bar_left_image:
            case R.id.bar_left_image2:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    private void downloadGame() {
        if (mGameInfo == null) {
            ToastUtil.showShort("下载失败");
            return;
        }
        DownloadInfo downloadInfo = mGameInfo.getDownloadInfo();
        if (downloadInfo == null) {
            ToastUtil.showShort("下载失败");
            return;
        }
        String proText = mDownloadBar.getCurrentText();
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl);
        String path = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl);
        mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(aBoolean -> {
            mDownloadManager.downloadClick(this, aBoolean, proText, downloadInfo, downloadId, path);
        });
    }

    private void initDownload(DownloadInfo downloadInfo) {
        if (downloadInfo == null) {
            mDownloadBar.setProgressUI(100, DownLoadFlag.no_download_url);
            mDownloadBar.setEnabled(false);
            return;
        }
        String downloadUrl = downloadInfo.downloadUrl; //下载地址
        String downloadPath = GameDownloadManager.getImpl().getDownloadPath(downloadInfo.downloadUrl);
        int downloadId = GameDownloadManager.getImpl().getDownloadId(downloadInfo.downloadUrl); //下载id

        mDownloadManager.updateDownloadTask(downloadInfo.downloadUrl, downloadPath);
        mDownloadManager.initDownloadStatus(downloadUrl, downloadInfo.downloadPackageName, downloadPath, downloadId, (percent, status) -> {
            mDownloadBar.setProgressUI(percent, status);
        });
        mDownloadManager.addUpdater(downloadUrl, (task, percent, status) -> mDownloadBar.setProgressUI(percent, status));
    }

    @Override
    public void onGetGameDetailSuccess(GameInfo result) {
        mGameInfo = result;
        mBarTitle.setText(mGameInfo.getGameNameCn());
        mDetailAdapter.setGameInfo(result);
        mPresenter.getEvaluationList(result.getGameId(), "created_at"); //全部评论
        mPresenter.getUserAppraise(result.getGameId(), "hot"); //用户评论
        initDownload(mGameInfo.getDownloadInfo());
    }

    @Override
    public void onGetEvaluationListSuccess(List<UserAppraise> list) {
        mUserAppraises = list;
        if (mUserAppraises.size() == 0) {
            mDetailAdapter.notifyDataSetChanged();
        } else {
            mPresenter.getHotEvaluationList(mGameInfo.getGameId(), "hot"); //热门评价
        }
    }

    @Override
    public void onGetHotEvaluationListSuccess(List<UserAppraise> list) {
        if (list.size() >= page_size) {
            mUserAppraises.add(0, list.get(0));
            mUserAppraises.add(1, list.get(1));
            mUserAppraises.add(2, list.get(2));
        }
        boolean hasHot = list.size() >= page_size;
        mDetailAdapter.setHasHotAppraise(hasHot);
        mDetailAdapter.setHotAppraiseCount(hasHot ? 3 : 0);
        mDetailAdapter.setDataList(mUserAppraises);
        mDetailAdapter.notifyDataSetChanged();

        mDetailAdapter.setShowLoadMore(mUserAppraises.size() >= page_size);
    }

    @Override
    public void onGetUserAppraiseSuccess(UserAppraise userAppraise) {
        mUserAppraise = userAppraise;
        mDetailAdapter.setUserAppraise(mUserAppraise);

        if (CommonUtil.isLogin()) {
            if (mUserAppraise != null) {
                mBtnEvaluate.setText("已评价");
            }
        }
    }

    @Override
    public void loadMoreEvaluationListSuccess(List<UserAppraise> list) {
        mUserAppraises.addAll(list);

        BaseDiffUtilCallBack<UserAppraise> callBack = new BaseDiffUtilCallBack<>(mDetailAdapter.getDataList(), mUserAppraises);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getId().equals(newData.getId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mDetailAdapter.setDataList(mUserAppraises);
        diffResult.dispatchUpdatesTo(mDetailAdapter);

        mRecyclerView.scrollToPosition(mDetailAdapter.getItemCount() - page_size - 2);
    }


    @Override
    public void showProgressUI(boolean isShowPro) {
        mProgressView.setVisibility(isShowPro ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore) {
            mDetailAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        } else {
            if (msg.equals(AppConstants.NOT_LOGIN)) {
                LoginActivity.launch(this, "GameDetailsActivity");
            } else {
                ToastUtil.showShort(msg);
            }
        }
    }

    @Override
    public void loadFinishAllData() {
        if (mUserAppraises.size() >= page_size) {
            mDetailAdapter.setCanLoading(false);
            mDetailAdapter.showLoadAllDataUI();
        } else {
            mDetailAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void thumbEvaluationSuccess(int type, int position, boolean isUserComment) {
        mDetailAdapter.updateLikeUI(position, type, isUserComment);
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress())
            return;
        super.onBackPressed();
    }
}

