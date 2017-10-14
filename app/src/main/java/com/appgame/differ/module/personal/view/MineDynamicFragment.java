package com.appgame.differ.module.personal.view;

import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.callback.BaseDiffUtilCallBack;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.dynamic.adapter.DynamicAdapter;
import com.appgame.differ.module.dynamic.contract.DynamicContract;
import com.appgame.differ.module.dynamic.presenter.DynamicPresenter;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.CustomEmptyView;
import com.appgame.differ.widget.dialog.OutLoginDialog;

import java.util.List;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * 动态
 * Created by lzx on 2017/5/3.
 * 386707112@qq.com
 */
public class MineDynamicFragment extends BaseFragment<DynamicContract.Presenter, DynamicInfo> implements DynamicContract.View {

    public static MineDynamicFragment newInstance(String action, String userId) {
        MineDynamicFragment fragment = new MineDynamicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        bundle.putString("userId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String action;
    private String userId;

    private RecyclerView mRecyclerView;
    private DynamicAdapter mDynamicAdapter;
    private CustomEmptyView mEmptyView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new DynamicPresenter();
        super.initPresenter();
    }

    @Override
    public void initVariables() {
        super.initVariables();
        action = getArguments().getString("action");
        userId = getArguments().getString("userId");
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);
        mEmptyView = (CustomEmptyView) $(R.id.empty_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDynamicAdapter = new DynamicAdapter(getActivity(), false);
        mRecyclerView.setAdapter(mDynamicAdapter);

        mEmptyView.initBigUI();

        if (!action.equals("other")) {
            userId = UserInfoManager.getImpl().getUserId();
        }

        mDynamicAdapter.setOnLoadMoreListener(() -> mPresenter.loadMore(userId));

        mDynamicAdapter.setOnLikeClickListener((dynamic_id, isThumb, position, target) -> mPresenter.thumbDynamic(dynamic_id, isThumb == 1 ? 0 : 1, position, target));
        mDynamicAdapter.setOnMoreBtnClickListener((dynamic_id, position) -> {
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
                }
        );
        RxBus.getBus().toMainThreadObservable(bindToLifecycle()).subscribe(o -> {
            if (o.equals(EvenConstant.KEY_REFRESH_DYNAMIC_LIST)) {
                mPresenter.getDynamic(userId,true);
            }
        });
    }

    @Override
    protected void lazyLoadData() {
        super.lazyLoadData();
        mPresenter.getDynamic(userId, true);
    }

    @Override
    public void onGetDynamicSuccess(List<DynamicInfo> dynamics, int removeCount) {
        mDataSource = dynamics;
        BaseDiffUtilCallBack<DynamicInfo> callBack = new BaseDiffUtilCallBack<>(mDynamicAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getId().equals(newData.getId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack);
        mDynamicAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mDynamicAdapter);
        mDynamicAdapter.setShowLoadMore(mDataSource.size() + removeCount >= page_size);
        mEmptyView.setVisibility(mDataSource.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loadMoreSuccess(List<DynamicInfo> dynamics, int removeCount) {
        mDataSource.addAll(dynamics);
        BaseDiffUtilCallBack<DynamicInfo> callBack = new BaseDiffUtilCallBack<>(mDynamicAdapter.getDataList(), mDataSource);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getId().equals(newData.getId()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mDynamicAdapter.setDataList(mDataSource);
        diffResult.dispatchUpdatesTo(mDynamicAdapter);
        mRecyclerView.scrollToPosition(mDynamicAdapter.getItemCount() - page_size - 2 + removeCount);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        if (isLoadMore){
            mDynamicAdapter.setCanLoading(false);
            ToastUtil.showShort("加载失败");
        }else {
            if (msg.equals(AppConstants.NOT_LOGIN)) {
                LoginActivity.launch(getActivity(),"MineDynamicFragment");
            } else {
                ToastUtil.showShort(msg);
            }
        }
    }

    @Override
    public void loadFinishAllData() {
        if (mDataSource.size() >= page_size) {
            mDynamicAdapter.setCanLoading(false);
            mDynamicAdapter.showLoadAllDataUI();
        } else {
            mDynamicAdapter.setShowLoadMore(false);
        }
    }

    @Override
    public void showProgressUI(boolean isShow) {
    }

    @Override
    public void onDeleteDynamicSuccess(int position) {
        mDynamicAdapter.getDataList().remove(position);
        mDynamicAdapter.notifyItemRemoved(position);
        mDynamicAdapter.notifyItemRangeChanged(position, mDynamicAdapter.getItemCount());
        mEmptyView.setVisibility(mDynamicAdapter.getDataList().size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void thumbDynamicSuccess(int type, int position) {
        mDynamicAdapter.updateLikeUI(position, type);
    }


}
