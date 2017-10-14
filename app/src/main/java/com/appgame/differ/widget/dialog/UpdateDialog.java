package com.appgame.differ.widget.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.VersionInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;


/**
 * 版本更新dialog
 * Created by lzx on 2017/3/13.
 * 386707112@qq.com
 */

public class UpdateDialog extends RxBaseDialog implements View.OnClickListener {

    private ImageView mBtnDismiss;
    private TextView mUpdateProText;
    private ProgressBar mProgressBar;
    private TextView mUpdateVersion;
    private TextView mUpdateSize;

    private VersionInfo mVersionInfo;


    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_update;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mVersionInfo = getArguments().getParcelable("VersionInfo");
        setCancelable(false);
        mBtnDismiss = (ImageView) findViewById(R.id.btn_dismiss);
        mUpdateProText = (TextView) findViewById(R.id.update_pro_text);
        mProgressBar = (ProgressBar) findViewById(R.id.update_pro);
        mUpdateVersion = (TextView) findViewById(R.id.update_version);
        mUpdateSize = (TextView) findViewById(R.id.update_size);
        mUpdateVersion.setText("更新版本:" + mVersionInfo.version);
        mUpdateSize.setText(mVersionInfo.size + "M");
        mBtnDismiss.setOnClickListener(this);
        mBtnDismiss.setVisibility(View.GONE);
        mUpdateProText.setVisibility(View.GONE);

        startDownload();

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    startDownload();
                } else {
                    mProgressBar.postDelayed(() -> {
                        ToastUtil.showShort("网络断开，请重新打开更新版本");
                        dismiss();
                        RxBus.getBus().send(EvenConstant.KEY_EXIT_APP);
                    }, 10 * 1000);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dismiss:
                // dismiss();
                break;
        }
    }

    private void startDownload() {
        SpUtil.getInstance().putString(AppConstants.update_url, mVersionInfo.url);
        FileDownloader.getImpl().create(mVersionInfo.url)
                .setPath(FileUtil.createPath(mVersionInfo.url))
                .setListener(new UpdateFileDownloadListener()).start();
    }

    private class UpdateFileDownloadListener extends FileDownloadListener {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            mProgressBar.setIndeterminate(true);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setMax(totalBytes);
            mProgressBar.setProgress(soFarBytes);
            String pro = String.valueOf((float) (soFarBytes / totalBytes) * 100);
            mUpdateProText.setText(pro);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {

        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {

        }

        @Override
        protected void completed(BaseDownloadTask task) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(task.getSmallFileTotalBytes());
            if (FileUtil.isExistsInstallationPackage(mVersionInfo.url)) {
                try {
                    File file = FileUtil.getInstallationPackageFile(mVersionInfo.url);
                    CommonUtil.installApk(getActivity(), file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            VersionDialog dialog = new VersionDialog(getActivity(), mVersionInfo, new RxPermissions(getActivity()), getFragmentManager());
            dialog.show();
            dismiss();
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            e.printStackTrace();
            if (CommonUtil.isConnected(getActivity())) {
                startDownload();
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            if (CommonUtil.isConnected(getActivity())) {
                startDownload();
            }
        }
    }

}
