package com.appgame.differ.module.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.download.TasksManagerModel;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.GameDownloadManager;
import com.appgame.differ.module.home.adapter.DownloadManagerAdapter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2017/5/6.
 */

public class DownloadManagerActivity extends BaseActivity {

    private TextView mDownloadTip;
    private RecyclerView mRecyclerView;
    private DownloadManagerAdapter mManagerAdapter;
    private RxPermissions mRxPermissions;
    public FileDownloadConnectListener listener;
    private CircleProgressView mProgressView;
    private CustomEmptyView mEmptyView;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<TasksManagerModel> mTasksManagerModelList;
    private DownloadManager mDownloadManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download_manager;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mRxPermissions = new RxPermissions(this);
        mDownloadManager = new DownloadManager();
        mDownloadTip = (TextView) findViewById(R.id.download_tip);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressView = (CircleProgressView) findViewById(R.id.load_pro);
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTasksManagerModelList = new ArrayList<>();
        mManagerAdapter = new DownloadManagerAdapter(DownloadManagerActivity.this, mRxPermissions);
        mRecyclerView.setAdapter(mManagerAdapter);

        mEmptyView.initBigUI();

        mManagerAdapter.setOnDeleteListener(() -> {
            mEmptyView.setVisibility(mManagerAdapter.getItemCount() != 0 ? View.GONE : View.VISIBLE);
            RxBus.getBus().send(EvenConstant.KEY_DOWNLOAD_RED_DOT);
        });

        GameDownloadManager.getImpl().getDownloadTasksExceptFinishAnsy(tasks -> {
            mTasksManagerModelList = tasks;
            mManagerAdapter.setList(tasks);
            mProgressView.setVisibility(View.GONE);
            mEmptyView.setVisibility(tasks.size() != 0 ? View.GONE : View.VISIBLE);
        });

        mDownloadManager.initServiceConnectListener(() -> runOnUiThread(() -> {
            if (mManagerAdapter != null)
                mManagerAdapter.notifyDataSetChanged();
        }));

        if (!CommonUtil.isConnected(this)) {
            onErrorUI(getString(R.string.network_time_out));
        }

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        mManagerAdapter.startAllAutoTasks();
                        mDownloadTip.setVisibility(View.GONE);
                    } else {
                        FileDownloader.getImpl().pauseAll();
                        onErrorUI("当前处于移动网络，wifi自动下载");
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    onErrorUI(getString(R.string.network_time_out));
                }
                mHandler.post(() -> mManagerAdapter.notifyDataSetChanged());
            } else if (o.equals(EvenConstant.KEY_RELOAD_APP_LIST)) {
                AppInfoManager.getImpl().updateAppInfo();

                mHandler.post(() -> mManagerAdapter.setList(mTasksManagerModelList));
            }
        });

    }

    private void onErrorUI(String tip) {
        FileDownloader.getImpl().pauseAll();
        mDownloadTip.setText(tip);
        mDownloadTip.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        RxBus.getBus().send(EvenConstant.KEY_DOWNLOAD_RED_DOT);
        mDownloadManager.clearFileDownloadConnectListener();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
