package com.appgame.differ.module.differ;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.AppInfoManager;
import com.appgame.differ.data.db.DownloadManager;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.differ.adapter.DifferAdapter;
import com.appgame.differ.module.differ.contract.DifferContract;
import com.appgame.differ.module.differ.presenter.DifferPresenter;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.module.search.SearchClassActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.RemoveAnimator;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.utils.rx.even.NetWorkEvent;
import com.appgame.differ.widget.dialog.ExploreAnimDialog;
import com.appgame.differ.widget.dialog.ExploreEmptyDialog;
import com.appgame.differ.widget.nineoldandroids.animation.ObjectAnimator;
import com.appgame.differ.widget.recycle.ScrollYLinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

/**
 * Created by lzx on 2017/5/22.
 * 386707112@qq.com
 */
public class DifferFragment extends BaseFragment<DifferContract.Presenter, RecommedInfo> implements DifferContract.View, View.OnClickListener {

    public static DifferFragment newInstance() {
        return new DifferFragment();
    }

    private ImageView mBgImage, mBtnSearch, mBtnPersonal;
    private TextView mDifferTip;
    private RecyclerView mRecyclerView;

    private DifferAdapter mDifferAdapter;

    private ObjectAnimator animator;
    private RxPermissions mRxPermissions;
    public FileDownloadConnectListener listener;
    private ItemTouchHelper.Callback callback;
    private ExploreAnimDialog mAnimDialog;
    private DownloadManager mDownloadManager;
    private boolean isShowDialog = false;
    private ScrollYLinearLayoutManager manager;
    private PagerSnapHelper mLinearSnapHelper;
    private RemoveAnimator mItemAnimator;
    private ItemTouchHelper touchHelper;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_differ;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DifferPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        mDownloadManager = new DownloadManager();
        mRxPermissions = new RxPermissions(getActivity());
        mAnimDialog = new ExploreAnimDialog();

        manager = new ScrollYLinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLinearSnapHelper = new PagerSnapHelper();

        mItemAnimator = new RemoveAnimator();

        mDifferAdapter = new DifferAdapter(getActivity(), mRxPermissions);

        //上滑回调
        callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                if (position != mDifferAdapter.getItemCount() - 1) {
                    String gameId = mDataSource.get(position).getGameId();
                    mPresenter.unLikeGame(gameId, viewHolder.itemView, viewHolder.getAdapterPosition(), true);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                dY = viewHolder.getLayoutPosition() == mDifferAdapter.getItemCount() - 1 ? 0 : dY;
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        touchHelper = new ItemTouchHelper(callback);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBgImage = (ImageView) $(R.id.bg_image);
        mBtnSearch = (ImageView) $(R.id.btn_search);
        mBtnPersonal = (ImageView) $(R.id.btn_personal);
        mDifferTip = (TextView) $(R.id.differ_tip);
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);

        mBtnSearch.setOnClickListener(this);
        mBtnPersonal.setOnClickListener(this);

        mDifferTip.setText(Html.fromHtml("根据你玩过的<font color=\"#15B1B8\">" + "天命传说" + "</font>推荐"));
        mDifferTip.setVisibility(View.INVISIBLE);

        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);
        touchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(mItemAnimator);
        mRecyclerView.setAdapter(mDifferAdapter);

        //点击不喜欢
        mDifferAdapter.setOnDisLikeClickListener((child, recommedInfo, position) -> {
            if (child != null) {
                mPresenter.unLikeGame(recommedInfo.getGameId(), child, position, false);
            }
        });
        //探索按钮
        mDifferAdapter.setOnShowAnimDialogListener(() -> {
            if (!isShowDialog) {
                showDialog();
            }
        });
        mDifferAdapter.setOnSwitchListener(imageUrl -> {
            if (TextUtils.isEmpty(imageUrl)) {
                mBgImage.setImageResource(R.color.white_alpha_80);
            } else {
                Glide.with(getActivity()).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(mBgImage);
            }
        });

        boolean isFirstOpen = SpUtil.getInstance().getBoolean(AppConstants.IS_FIRST_OPEN, true);
        if (isFirstOpen) {
            mAnimDialog.show(getChildFragmentManager(), "ExploreAnimDialog");
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        //请求数据
        mPresenter.requestRecommendList();
        //链接下载服务
        mDownloadManager.initServiceConnectListener(() -> getActivity().runOnUiThread(() -> {
            if (mDifferAdapter != null) mDifferAdapter.notifyDataSetChanged();
        }));

        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof NetWorkEvent) {
                NetWorkEvent event = (NetWorkEvent) o;
                if (event.isNetWorkConnected) {
                    if (event.isWifi) {
                        mDifferAdapter.startAllAutoTasks();
                    } else {
                        FileDownloader.getImpl().pauseAll();
                    }
                } else {
                    FileDownloader.getImpl().pauseAll();
                    ToastUtil.showShort(getString(R.string.network_time_out));
                }
                mDifferAdapter.notifyDataSetChanged();
            } else if (o instanceof String) {
                String event = (String) o;
                switch (event) {
                    case EvenConstant.KEY_RELOAD_APP_LIST:
                        AppInfoManager.getImpl().updateAppInfo();
                        mDifferAdapter.notifyDataSetChanged();
                        break;
                    case EvenConstant.KEY_SHOW_EXPLORE_ANIM:
                        doExploreAnim();
                        break;
                }
            }
        });
    }

    private void doExploreAnim() {
        long exploreTime = SpUtil.getInstance().getLong(AppConstants.EXPLORE_TIME);
        if (System.currentTimeMillis() - exploreTime > 1000 * 60 * 15) {
            if (!isShowDialog) {
                showDialog();
                SpUtil.getInstance().putLong(AppConstants.EXPLORE_TIME, System.currentTimeMillis());
            }
        }
    }

    private void showDialog() {
        mPresenter.requestRecommendList();
        if (mAnimDialog == null)
            mAnimDialog = new ExploreAnimDialog();
        mAnimDialog.show(getChildFragmentManager(), "ExploreAnimDialog");
        mRecyclerView.setVisibility(View.INVISIBLE);
        isShowDialog = true;
        mRecyclerView.postDelayed(() -> {
            mAnimDialog.dismiss();
            isShowDialog = false;
            mRecyclerView.scrollToPosition(0);
            mRecyclerView.setVisibility(View.VISIBLE);
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDifferAdapter != null) {
            mDifferAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                startActivity(new Intent(getActivity(), SearchClassActivity.class));
                break;
            case R.id.btn_personal:
                Intent intent;
                if (CommonUtil.isLogin()) {
                    String id = UserInfoManager.getImpl().getUserId();
                    intent = new Intent(getActivity(), PersonalActivity.class);
                    intent.putExtra("userId", id);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    public void unLikeSuccess(View view, int position, boolean isSwiped) {
        mDifferAdapter.removedItemUI(position);
        mDataSource.remove(position);
    }

    @Override
    public void onRequestSuccess(List<RecommedInfo> gameInfos) {
        if (mAnimDialog.getDialog() != null && mAnimDialog.getDialog().isShowing()) {
            mRecyclerView.postDelayed(() -> {
                mAnimDialog.dismiss();
                mRecyclerView.scrollToPosition(0);
                mRecyclerView.setVisibility(View.VISIBLE);
                if (gameInfos.size() > 0) {
                    String url = getBgCover(gameInfos.get(0).getCover(), gameInfos.get(0).getIcon());
                    Glide.with(getActivity()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(mBgImage);
                }
            }, 2000);
        }
        mDataSource = gameInfos;
        mDifferAdapter.setInfoList(mDataSource);
        mRecyclerView.postDelayed(() -> mRecyclerView.scrollToPosition(0), 500);
        if (mDataSource.size() == 0) {
            if (mAnimDialog != null && mAnimDialog.getDialog() != null && mAnimDialog.getDialog().isShowing()) {
                mAnimDialog.dismiss();
            }
            ExploreEmptyDialog dialog = new ExploreEmptyDialog();
            dialog.show(getChildFragmentManager(), "ExploreEmptyDialog");
        }
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        ToastUtil.showShort(msg);
    }

    private String getBgCover(String cover, String icon) {
        if (TextUtils.isEmpty(cover)) {
            return icon;
        } else {
            return cover;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.clearFileDownloadConnectListener();
    }
}
