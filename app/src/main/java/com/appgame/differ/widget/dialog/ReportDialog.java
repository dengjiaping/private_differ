package com.appgame.differ.widget.dialog;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.base.other.SimpleDecoration;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportDialog extends RxBaseDialog {
    private RecyclerView mRecyclerView;
    private List<String> data;
    private String dynamic_id, target;

    public static ReportDialog newInstance(String dynamic_id, String target) {
        ReportDialog reportListFragment = new ReportDialog();
        Bundle bundle = new Bundle();
        bundle.putString("dynamic_id", dynamic_id);
        bundle.putString("target", target);
        reportListFragment.setArguments(bundle);
        return reportListFragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_report_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        dynamic_id = getArguments().getString("dynamic_id");
        target = getArguments().getString("target");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SimpleDecoration(getActivity(), LinearLayoutManager.VERTICAL, 1, Color.parseColor("#e1e1e1")));
        mRecyclerView.setAdapter(new ReportListAdapter());
        data = Arrays.asList(getResources().getStringArray(R.array.dynamic_report_list));
    }

    private class ReportListAdapter extends RecyclerView.Adapter<SimpleViewHolder> {
        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_list, parent, false);
            return new SimpleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            holder.mTitle.setText(data.get(position));
            holder.mTitle.setOnClickListener(v -> {
                if (SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false)) {
                    report(position);
                } else {
                    LoginActivity.launch(getActivity(),"ReportDialog");
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.report_title);
        }
    }

    private void report(int position) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        String type;
        switch (position) {
            case 0:
                type = "abuse";
                break;
            case 1:
                type = "ad";
                break;
            case 2:
                type = "sex";
                break;
            case 3:
                type = "reaction";
                break;
            case 4:
                type = "nosay";
                break;
            default:
                type = "";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("type", type);
        map.put("dynamic_id", dynamic_id);
        map.put("target", target);
        map.put("extra", CommonUtil.getExtraParam());
        RetrofitHelper.getAppGameAPI().dynamicReport(map)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    mHandler.post(() -> ToastUtil.showShort("举报成功"));
                    dismiss();
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        LogUtil.i("举报失败 = " + error.getTitle());
                        ToastUtil.showShort("举报失败");
                        dismiss();
                    }
                });
    }

    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
