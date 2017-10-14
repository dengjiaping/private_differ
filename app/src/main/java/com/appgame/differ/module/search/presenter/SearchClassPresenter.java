package com.appgame.differ.module.search.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.bean.game.GameCategory;
import com.appgame.differ.bean.search.HotSearch;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.search.contract.SearchClassContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.appgame.differ.utils.network.RetrofitHelper.getAppGameAPI;

/**
 * Created by lzx on 2017/5/24.
 * 386707112@qq.com
 */

public class SearchClassPresenter extends RxPresenter<SearchClassContract.View> implements SearchClassContract.Presenter<SearchClassContract.View> {

    @Override
    public void getHotSearch() {
        BaseSubscriber<List<HotSearch>> subscriber = getAppGameAPI().getSearchHot()
                .map(responseBody -> {
                    List<HotSearch> list = new ArrayList<HotSearch>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        HotSearch hotSearch = new HotSearch();
                        JSONObject gameOb = jsonArray.getJSONObject(i);
                        hotSearch.setGameId(gameOb.getString("id"));
                        hotSearch.setGameName(gameOb.getJSONObject("attributes").getString("game_name_cn"));
                        list.add(hotSearch);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<HotSearch>>(mView, false) {
                    @Override
                    public void onSuccess(List<HotSearch> hotSearches) {
                        mView.onHotSearchSuccess(hotSearches);
                    }
                });
        addSubscribe(subscriber);
    }

    @Override
    public void getGameClass() {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        BaseSubscriber<List<GameCategory>> subscriber = RetrofitHelper.getAppGameAPI().getTagsHot(token, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    List<GameCategory> list = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i).getJSONObject("attributes");
                        GameCategory tagsInfo = new GameCategory();
                        tagsInfo.setId(object.getString("id"));
                        tagsInfo.setName(object.getString("name"));
                        tagsInfo.setIcon(object.getString("cover"));
                        list.add(tagsInfo);
                    }
                    return list;
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<List<GameCategory>>(mView, false) {
                    @Override
                    public void onSuccess(List<GameCategory> gameCategories) {
                        mView.onGameClassSuccess(gameCategories);
                    }
                });
        addSubscribe(subscriber);
    }
}
