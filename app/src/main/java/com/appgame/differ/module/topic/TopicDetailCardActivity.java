package com.appgame.differ.module.topic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.recommend.TopicInfo;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.module.topic.adapter.TopicDetailCardAdapter;
import com.appgame.differ.module.topic.mvp.TopicDetailContract;
import com.appgame.differ.module.topic.mvp.TopicDetailPresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * 专题详情卡片式
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */
public class TopicDetailCardActivity extends BaseActivity<TopicDetailContract.Presenter, TopicInfo> implements View.OnClickListener, TopicDetailContract.View {

    private RelativeLayout mRootLayout;
    private ImageView mBtnBack;
    private TextView mBarTitle;
    private RecyclerView mRecyclerView;
    private RelativeLayout mToolbarLayout, mToolbarLayoutTemp;
    private int mDistanceY;
    private RxPermissions mRxPermissions;
    private TopicInfo mTopicInfo;
    private String topicId;
    private TopicDetailCardAdapter mDetailCardAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_detail;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new TopicDetailPresenter(this);
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBarTitle = (TextView) findViewById(R.id.bar_title);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mToolbarLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        mToolbarLayoutTemp = (RelativeLayout) findViewById(R.id.toolbar_layout_temp);

        mAppBarLayout.setAlpha(0);

        mToolbarLayout.post(() -> {
            int height = mToolbarLayout.getHeight() + CommonUtil.getStatusBarHeight(TopicDetailCardActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            mToolbarLayout.setLayoutParams(params);
            mToolbarLayoutTemp.setLayoutParams(params);
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mDistanceY += dy;
                int toolbarHeight = CommonUtil.dip2px(TopicDetailCardActivity.this, 250) - mToolbarLayout.getMeasuredHeight();
                if (mDistanceY <= toolbarHeight) {
                    float scale = (float) mDistanceY / toolbarHeight;
                    mAppBarLayout.setAlpha(scale);
                } else {
                    mAppBarLayout.setAlpha(1f);
                }
            }
        });
        mBtnBack.setOnClickListener(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mRxPermissions = new RxPermissions(this);
        mTopicInfo = getIntent().getParcelableExtra("topicInfo");
        topicId = getIntent().getStringExtra("topicId");

        SystemBarHelper.immersiveStatusBar(this);

        DownloadManager.getImpl().initServiceConnectListener(() -> {
            runOnUiThread(() -> {
                if (mDetailCardAdapter != null) {
                    mDetailCardAdapter.notifyDataSetChanged();
                }
            });
        });

        if (mTopicInfo != null) {
            mBarTitle.setText(mTopicInfo.getTitle());
            mToolbarLayout.setAlpha(0);
            if (!TextUtils.isEmpty(mTopicInfo.getExtraData().getBgColor()))
                mRootLayout.setBackgroundColor(Color.parseColor(mTopicInfo.getExtraData().getBgColor()));

            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mDetailCardAdapter = new TopicDetailCardAdapter(mRxPermissions, this);
            mRecyclerView.setAdapter(mDetailCardAdapter);

            mPresenter.requestDetail(mTopicInfo.getTopicId());
        } else {
            mPresenter.requestDetail(topicId);
        }

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_REFRESH_TOPIC_LIST)) {
                if (mTopicInfo != null) {
                    mPresenter.requestDetail(mTopicInfo.getTopicId());
                } else {
                    mPresenter.requestDetail(topicId);
                }
            } else if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        if (mDetailCardAdapter != null) {
                            mDetailCardAdapter.startAllAutoTasks();
                        }
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
            } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                AppInfoManager.getImpl().updateAppInfo();
                if (mDetailCardAdapter != null) {
                    mDetailCardAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onRequestDetailSuccess(TopicInfo topicInfo) {
        if (mTopicInfo == null) {
            mTopicInfo = topicInfo;
            mBarTitle.setText(mTopicInfo.getTitle());
            setTitle(mTopicInfo.getTitle());
            mToolbarLayout.setAlpha(0);
            if (!TextUtils.isEmpty(mTopicInfo.getExtraData().getBgColor())) {
                int bgColor = Color.parseColor(mTopicInfo.getExtraData().getBgColor());
                mRootLayout.setBackgroundColor(bgColor);

            }
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mDetailCardAdapter = new TopicDetailCardAdapter(mRxPermissions, this);
            mRecyclerView.setAdapter(mDetailCardAdapter);
        }
        mDetailCardAdapter.setData(mTopicInfo, topicInfo.getGameInfos());
    }
}