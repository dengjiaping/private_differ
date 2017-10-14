package com.appgame.differ.module.dynamic;

import android.content.Intent;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseRefreshFragment;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.dynamic.adapter.DynamicAdapter;
import com.appgame.differ.module.dynamic.contract.DynamicContract;
import com.appgame.differ.module.dynamic.presenter.DynamicPresenter;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.VideoScrollListener;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.CircleProgressView;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.dialog.OutLoginDialog;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

import static com.appgame.differ.data.constants.AppConstants.page_size;

public class DynamicFragment extends BaseRefreshFragment<DynamicContract.Presenter, DynamicInfo> implements DynamicContract.View, View.OnClickListener {

    public static DynamicFragment newInstance() {
        return new DynamicFragment();
    }

    private DynamicAdapter mAdapter;
    private CustomEmptyView mEmptyView;
    private ImageView mBtnPersonal, mPostDynamic;
    private CircleProgressView mProgressView;
    private int clickCount = 0;
    private int mRemoveCount = 0;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DynamicPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
    }

    @Override
    public void initWidget() {
        super.initWidget();

        setHasOptionsMenu(true);
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);
        mBtnPersonal = (ImageView) $(R.id.btn_personal);
        mPostDynamic = (ImageView) $(R.id.post_dynamic);
        mProgressView = (CircleProgressView) $(R.id.load_pro);

        mEmptyView.initBigUI();

        mBtnPersonal.setOnClickListener(this);
        mPostDynamic.setOnClickListener(this);


        mAdapter = new DynamicAdapter(getActivity(), true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new VideoScrollListener());

        mPresenter.getDynamic("", true);

        mAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore(""));

        mAdapter.setOnMoreBtnClickListener((dynamic_id, position) -> {
            OutLoginDialog dialog = new OutLoginDialog(getActivity());
            dialog.show();
            dialog.setDialogTitle("确定要删除该条动态");
            dialog.setListener(new OutLoginDialog.OnOutLoginListener() {
                @Override
                public void onYes() {
                    mPresenter.deleteDynamic(dynamic_id, position);
                }

                @Override
                public void onNo() {
                }
            });
        });
        mAdapter.setOnLikeClickListener((dynamic_id, isThumb, position, target) -> {
            mPresenter.thumbDynamic(dynamic_id, isThumb == 1 ? 0 : 1, position, target);
        });
        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o instanceof String) {
                String event = (String) o;
                switch (event) {
                    case EvenConstant.KEY_LOGIN_SUCCESS:
                    case EvenConstant.KEY_DELETE_DYNAMIC_SUCCESS:
                    case EvenConstant.KEY_REFRESH_DYNAMIC_LIST:
                    case EvenConstant.KEY_LOGIN_OUT:
                        if (mAdapter != null) {
                            mAdapter.clearAllData();
                        }
                        mPresenter.getDynamic("", false);
                        break;
                    case EvenConstant.KEY_RELEASE_DYNAMIC_VIDEOS:
                        JCVideoPlayer.releaseAllVideos();
                        break;
                }
            }
        });
    }

    @Override
    protected void lazyLoadData() {
        mPresenter.getDynamic("", false);
    }

    public void requestData() {
        clickCount++;
        if (clickCount >= 3) {
            clickCount = 0;
            mPtrClassicFrameLayout.postDelayed(this::lazyLoadData, 1000);
        }
    }

    @Override
    public void onGetDynamicSuccess(List<DynamicInfo> dynamics, int removeCount) {
        mRemoveCount = removeCount;
        mDataSource = dynamics;
        mAdapter.setDataList(mDataSource);
        mAdapter.notifyDataSetChanged();
        mAdapter.setShowLoadMore(mDataSource.size() + removeCount >= page_size && dynamics.size() > 0);
        mEmptyView.setVisibility(mDataSource.size() == 0 && CommonUtil.isLogin() ? View.VISIBLE : View.GONE);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMoreSuccess(List<DynamicInfo> dynamics, int removeCount) {
        mRemoveCount = removeCount;
        mDataSource.addAll(dynamics);
        BaseDiffUtilCallBack<DynamicInfo> callBack = new BaseDiffUtilCallBack<>(mAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getId().equals(newData.getId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - page_size - 2 + removeCount);
        mAdapter.setShowLoadMore(mDataSource.size() + removeCount >= page_size);
        if (dynamics.size() == 0) {
            mAdapter.setCanLoading(false);
            mAdapter.showLoadAllDataUI();
        }
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore) {
            mAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        } else {
            if (msg.equals(AppConstants.NOT_LOGIN)) {
                LoginActivity.launch(getActivity(), "DynamicActivity");
            } else {
                mAdapter.clear();
                mEmptyView.initErrorUI();
                visible(mEmptyView);
            }
        }
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() + mRemoveCount >= page_size) {
            mAdapter.setCanLoading(false);
            mAdapter.showLoadAllDataUI();
        } else {
            mAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showProgressUI(boolean isShow) {
        mProgressView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDeleteDynamicSuccess(int position) {
        mAdapter.getDataList().remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());

        mEmptyView.setVisibility(mAdapter.getDataList().size() == 0 && CommonUtil.isLogin() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void thumbDynamicSuccess(int type, int position) {
        mAdapter.updateLikeUI(position, type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_personal:
                Intent intent;
                if (CommonUtil.isLogin()) {
                    intent = new Intent(getActivity(), PersonalActivity.class);
                    String id = UserInfoManager.getImpl().getUserId();
                    intent.putExtra("userId", id);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.post_dynamic:
                if (SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false)) {
                    intent = new Intent(getActivity(), SelectGameActivity.class);
                    startActivity(intent);
                } else {
                    LoginActivity.launch(getActivity(), "DynamicActivity");
                }
                break;
        }
    }
}
