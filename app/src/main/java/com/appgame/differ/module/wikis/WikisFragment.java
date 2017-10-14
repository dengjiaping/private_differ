package com.appgame.differ.module.wikis;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseFragment;
import com.appgame.differ.bean.wikis.WikisInfo;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.module.search.SearchClassActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzx on 2017/9/4.
 */

public class WikisFragment extends BaseFragment implements View.OnClickListener {

    public static WikisFragment newInstance() {
        return new WikisFragment();
    }

    private ImageView mBtnSearch, mBtnPersonal;
    private RecyclerView mRecyclerView;
    private List<WikisInfo> mWikisInfoList = new ArrayList<>();
    private WikisAdapter mWikisAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private PagerSnapHelper mLinearSnapHelper;
    private int position = 1;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wikis;
    }

    @Override
    public void initVariables() {
        super.initVariables();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mLinearSnapHelper = new PagerSnapHelper();
        mWikisAdapter = new WikisAdapter(getActivity());
    }

    @Override
    public void initWidget() {
        super.initWidget();
        mBtnSearch = (ImageView) $(R.id.btn_search);
        mBtnPersonal = (ImageView) $(R.id.btn_personal);
        mRecyclerView = (RecyclerView) $(R.id.recycler_view);

        mBtnSearch.setOnClickListener(this);
        mBtnPersonal.setOnClickListener(this);

        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mWikisAdapter);

        loadWikisData(position);

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

    private void loadWikisData(int position) {
        RetrofitHelper.getAppGameAPI().getWikis(String.valueOf(position))
                .compose(bindToLifecycle())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    List<WikisInfo> list = new ArrayList<WikisInfo>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        WikisInfo info = new WikisInfo();
                        info.setId(object.getString("id"));
                        JSONObject attrOb = jsonArray.getJSONObject(i).getJSONObject("attributes");
                        info.setTitle(attrOb.getString("title"));
                        info.setSmallTitle(attrOb.getString("small_title"));
                        info.setCover(attrOb.getString("cover"));
                        info.setUrl(attrOb.getString("url"));
                        list.add(info);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribe(wikisInfos -> {
                    mWikisInfoList = wikisInfos;
                    mWikisAdapter.setWikisInfoList(mWikisInfoList);
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        mWikisInfoList.clear();
                        ToastUtil.showShort(error.getTitle());
                    }
                });
    }

}
