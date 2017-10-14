package com.appgame.differ.module.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.bean.collection.CollectionInfo;
import com.appgame.differ.module.dynamic.adapter.SearchAdapter;
import com.appgame.differ.module.dynamic.contract.SelectGameContract;
import com.appgame.differ.module.dynamic.presenter.SelectGamePresenter;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.widget.CustomEmptyView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class SelectGameActivity extends BaseActivity<SelectGameContract.Presenter, String> implements SelectGameContract.View {

    private RecyclerView mRecyclerView;
    private ImageView mBackImageView;
    private EditText mSearchText;
    private TextView mLabel;
    private CustomEmptyView mEmptyView;

    private SearchAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_game;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new SelectGamePresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyView = (CustomEmptyView) findViewById(R.id.empty_view);
        mBackImageView = (ImageView) findViewById(R.id.back);
        mSearchText = (EditText) findViewById(R.id.search_editText);
        mLabel = (TextView) findViewById(R.id.label);
        mBackImageView.setOnClickListener(v -> finish());
        mEmptyView.initBigUI();
        mLabel.setText("最近玩过");
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        RxTextView.textChangeEvents(mSearchText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textViewTextChangeEvent -> {
                    String text = textViewTextChangeEvent.text().toString();
                    if (TextUtils.isEmpty(text)) {
                        mPresenter.getPlayingGame("played", "0", "");
                    } else {
                        mPresenter.getGameBySearch(text);
                    }
                }, throwable -> {
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAdapter = new SearchAdapter(this);
        mPresenter.getPlayingGame("played", "0", "");
        mAdapter.setOnItemClickListener((simpleGame, postType) -> {
            Intent intent = new Intent(SelectGameActivity.this, PostDynamicActivity.class);
            intent.putExtra("game", simpleGame);
            intent.putExtra("postType", "post");
            intent.putExtra("target", "dynamic");
            intent.putExtra("targetId", "0");
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onPlayingGameSuccess(List<CollectionInfo> collectionInfos) {
        mLabel.setText("最近玩过");
        List<CollectionInfo> list = new ArrayList<>();
        if (collectionInfos.size() > 5) {
            list.add(collectionInfos.get(0));
            list.add(collectionInfos.get(1));
            list.add(collectionInfos.get(2));
            list.add(collectionInfos.get(3));
            list.add(collectionInfos.get(4));
        } else {
            list.addAll(collectionInfos);
        }
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
        mEmptyView.setVisibility(collectionInfos.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSearchGameSuccess(List<CollectionInfo> collectionInfos) {
        mLabel.setText("搜索结果");
        mAdapter.setData(collectionInfos);
        mAdapter.notifyDataSetChanged();
        mEmptyView.setVisibility(collectionInfos.size() > 0 ? View.GONE : View.VISIBLE);
        CommonUtil.HideKeyboard(mRecyclerView);
    }
}
