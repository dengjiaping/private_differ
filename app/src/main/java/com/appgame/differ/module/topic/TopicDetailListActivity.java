package com.appgame.differ.module.topic;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.appgame.differ.module.topic.adapter.TopicDetailLisAdapter;
import com.appgame.differ.module.topic.mvp.TopicDetailContract;
import com.appgame.differ.module.topic.mvp.TopicDetailPresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * 专题详情列表式
 * Created by lzx on 2017/3/14.
 * 386707112@qq.com
 */
public class TopicDetailListActivity extends BaseActivity<TopicDetailContract.Presenter, TopicInfo> implements View.OnClickListener, TopicDetailContract.View {

    private RelativeLayout mRootLayout;
    private ImageView mBtnBack;
    private TextView mBarTitle;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private TopicDetailLisAdapter mLisAdapter;
    private RelativeLayout mToolbarLayout, mToolbarLayoutTemp;
    private int mDistanceY;
    private TopicInfo mTopicInfo;
    private String topicId;
    private RxPermissions mRxPermissions;
    private ImageView mBtnBackTemp;

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
        mBtnBackTemp = (ImageView) findViewById(R.id.btn_back_temp);

        mAppBarLayout.setAlpha(0);

        mToolbarLayout.post(() -> {
            int height = mToolbarLayout.getHeight() + CommonUtil.getStatusBarHeight(TopicDetailListActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            mToolbarLayout.setLayoutParams(params);
            mToolbarLayoutTemp.setLayoutParams(params);
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mDistanceY += dy;
                int toolbarHeight = CommonUtil.dip2px(TopicDetailListActivity.this, 250) - mToolbarLayout.getMeasuredHeight();
                if (mDistanceY <= toolbarHeight) {
                    float scale = (float) mDistanceY / toolbarHeight;
                    mAppBarLayout.setAlpha(scale);
                    SystemBarHelper.immersiveStatusBar(TopicDetailListActivity.this, scale * SystemBarHelper.DEFAULT_ALPHA);
                } else {
                    mAppBarLayout.setAlpha(1f);
                    SystemBarHelper.immersiveStatusBar(TopicDetailListActivity.this);
                }
            }
        });

        mBtnBack.setOnClickListener(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        SystemBarHelper.immersiveStatusBar(this, 0);
        mTopicInfo = getIntent().getParcelableExtra("topicInfo");
        topicId = getIntent().getStringExtra("topicId");

        mRxPermissions = new RxPermissions(this);

        DownloadManager.getImpl().initServiceConnectListener(() -> {
            runOnUiThread(() -> {
                if (mLisAdapter != null) {
                    mLisAdapter.notifyDataSetChanged();
                }
            });
        });

        if (mTopicInfo != null) {
            mBarTitle.setText(mTopicInfo.getTitle());
            mToolbarLayout.setAlpha(0);

            mGridLayoutManager = new GridLayoutManager(this, 12);
            mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    return mLisAdapter.getSpanSize(position);
                }
            });
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mLisAdapter = new TopicDetailLisAdapter(this, mRxPermissions);
            mRecyclerView.setAdapter(mLisAdapter);

            mPresenter.requestDetail(mTopicInfo.getTopicId());
        } else {
            mPresenter.requestDetail(topicId);
        }

        RxBus.getBus().toMainThreadObservable(this.bindToLifecycle()).subscribe(o -> {
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
                        if (mLisAdapter != null) {
                            mLisAdapter.startAllAutoTasks();
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
                if (mLisAdapter != null) {
                    mLisAdapter.notifyDataSetChanged();
                }
            }
        });
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

            mGridLayoutManager = new GridLayoutManager(this, 12);
            mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                @Override
                public int getSpanSize(int position) {
                    return mLisAdapter.getSpanSize(position);
                }
            });
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mLisAdapter = new TopicDetailLisAdapter(this, mRxPermissions);
            mRecyclerView.setAdapter(mLisAdapter);
        }
        mLisAdapter.setData(mTopicInfo, topicInfo.getGameInfos());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().clearFileDownloadConnectListener();
    }
}