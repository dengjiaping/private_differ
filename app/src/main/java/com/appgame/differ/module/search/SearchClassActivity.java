package com.appgame.differ.module.search;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.base.other.SimpleTextWatcher;
import com.appgame.differ.bean.game.GameCategory;
import com.appgame.differ.bean.search.HotSearch;
import com.appgame.differ.module.personal.adapter.MinePagerAdapter;
import com.appgame.differ.module.search.adapter.HotSearchAdapter;
import com.appgame.differ.module.search.adapter.SearchClassAdapter;
import com.appgame.differ.module.search.contract.SearchClassContract;
import com.appgame.differ.module.search.presenter.SearchClassPresenter;
import com.appgame.differ.utils.KeyboardControlManager;
import com.appgame.differ.utils.ToastUtil;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.liulishuo.filedownloader.FileDownloadConnectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.appgame.differ.utils.KeyboardControlManager.clear;

/**
 * 搜索分类
 * Created by xian on 2017/5/20.
 */

public class SearchClassActivity extends BaseActivity<SearchClassContract.Presenter, String> implements SearchClassContract.View, View.OnClickListener {

    private RecyclerView mHotRecyclerView;
    private RecyclerView mClassRecyclerView;
    private SearchClassAdapter mSearchClassAdapter;
    private HotSearchAdapter mHotSearchAdapter;
    private NestedScrollView mNestedScrollView;
    private ImageView mBtnBack;
    private EditText mSearchEditText;

    public FileDownloadConnectListener listener;

    private LinearLayout mSearchResultLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private List<String> titles = new ArrayList<>();
    private MinePagerAdapter mMinePagerAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private String searchType = "游戏";
    private SearchResultFragment mGameFragment, mArticleFragment, mUserFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_class;
    }

    @Override
    protected void initPresenter() {
        mPresenter = new SearchClassPresenter();
        super.initPresenter();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mSearchEditText = (EditText) findViewById(R.id.search_editText);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.search_class_layout);
        mHotRecyclerView = (RecyclerView) findViewById(R.id.hot_search_recycle);
        mClassRecyclerView = (RecyclerView) findViewById(R.id.class_recycle);
        mSearchResultLayout = (LinearLayout) findViewById(R.id.search_result_layout);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        initRecyclerView();
        initAdapter();
        initTabLayout();
        mBtnBack.setOnClickListener(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPresenter.getHotSearch();
        mPresenter.getGameClass();

        mSearchEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                mNestedScrollView.setVisibility(s.length() == 0 ? View.VISIBLE : View.GONE);
                mSearchResultLayout.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
                if (s.length() == 0) {
                    mGameFragment.clear();
                    mArticleFragment.clear();
                    mUserFragment.clear();
                }
            }
        });
        RxTextView.textChangeEvents(mSearchEditText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textViewTextChangeEvent -> {
                    String text = textViewTextChangeEvent.text().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        if (searchType.equals("游戏")) {
                            mGameFragment.resetAdapter(searchType);
                            mGameFragment.setSearchTypeAndSearch(searchType, text);
                        } else if (searchType.equals("文章")) {
                            mArticleFragment.resetAdapter(searchType);
                            mArticleFragment.setSearchTypeAndSearch(searchType, text);
                        } else if (searchType.equals("玩家")) {
                            mUserFragment.resetAdapter(searchType);
                            mUserFragment.setSearchTypeAndSearch(searchType, text);
                        }
                    }
                }, throwable -> {

                });
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                String text = mSearchEditText.getText().toString().trim();
                if (position == 0) {
                    searchType = "游戏";
                    mGameFragment.setSearchTypeAndSearch(searchType, text);
                } else if (position == 1) {
                    searchType = "文章";
                    mArticleFragment.setSearchTypeAndSearch(searchType, text);
                } else if (position == 2) {
                    searchType = "玩家";
                    mUserFragment.setSearchTypeAndSearch(searchType, text);
                }
            }
        });
        KeyboardControlManager.observerKeyboardVisibleChange(this, (keyboardHeight, isVisible) -> {
            if (mNestedScrollView.getVisibility() == View.VISIBLE) {
                mNestedScrollView.fullScroll(isVisible ? View.FOCUS_DOWN : View.FOCUS_UP);
            }
        });
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        mHotRecyclerView.setFocusable(false);
        mClassRecyclerView.setFocusable(false);
        mHotRecyclerView.setNestedScrollingEnabled(true);
        mHotRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mHotRecyclerView.setHasFixedSize(true);
        mClassRecyclerView.setNestedScrollingEnabled(true);
        mClassRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mClassRecyclerView.setHasFixedSize(true);
    }

    private void initAdapter() {
        mHotSearchAdapter = new HotSearchAdapter(this);
        mHotRecyclerView.setAdapter(mHotSearchAdapter);
        mSearchClassAdapter = new SearchClassAdapter(this);
        mClassRecyclerView.setAdapter(mSearchClassAdapter);
        mHotSearchAdapter.setOnItemClickListener(gameName -> mSearchEditText.setText(gameName));
    }

    private void initTabLayout() {
        titles = Arrays.asList(getResources().getStringArray(R.array.search_type));
        mGameFragment = SearchResultFragment.newInstance(titles.get(0));
        mArticleFragment = SearchResultFragment.newInstance(titles.get(1));
        mUserFragment = SearchResultFragment.newInstance(titles.get(2));
        fragments.add(mGameFragment);
        fragments.add(mArticleFragment);
        fragments.add(mUserFragment);
        mMinePagerAdapter = new MinePagerAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mMinePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onHotSearchSuccess(List<HotSearch> hotSearches) {
        mHotSearchAdapter.setHotSearches(hotSearches);
    }

    @Override
    public void onGameClassSuccess(List<GameCategory> gameCategories) {
        mSearchClassAdapter.buildDataSource(gameCategories);
        mSearchClassAdapter.setList(gameCategories, false);
    }

    @Override
    public void showError(String msg, boolean isLoadMore) {
        super.showError(msg, isLoadMore);
        ToastUtil.showShort(msg);
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
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }
}
