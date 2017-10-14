package com.appgame.differ.module.find.model;

import android.support.annotation.NonNull;

import com.appgame.differ.bean.find.FindInfo;
import com.appgame.differ.bean.find.FindListInfo;
import com.appgame.differ.bean.find.FindTypeArticle;
import com.appgame.differ.bean.find.FindTypeExternal;
import com.appgame.differ.bean.find.FindTypeGame;
import com.appgame.differ.bean.find.FindTypeTopic;
import com.appgame.differ.bean.find.FindTypeVideo;
import com.appgame.differ.bean.find.NavigationInfo;
import com.appgame.differ.callback.DataCallBack;
import com.appgame.differ.data.cache.DataCacheManager;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.widget.banner.BannerEntity;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static com.appgame.differ.data.constants.AppConstants.page_size;

/**
 * Created by xian on 2017/6/4.
 */

public class FindModel {

    public void request(String position, @NonNull LifecycleTransformer<ResponseBody> lifecycleTransformer, DataCallBack<FindInfo> callBack) {
        callBack.start();
        boolean hasCacheBanner = DataCacheManager.getImpl().hasCache(AppConstants.KEY_BANNER);
        boolean hasCacheNav = DataCacheManager.getImpl().hasCache(AppConstants.KEY_NAVIGATION);
        boolean hasDiscoverConventions = DataCacheManager.getImpl().hasCache(AppConstants.KEY_DISCOVER);
        if (hasCacheBanner && hasCacheNav && hasDiscoverConventions) {
            JSONObject bannerObject = DataCacheManager.getImpl().getCacheJSONObject(AppConstants.KEY_BANNER);
            JSONObject navObject = DataCacheManager.getImpl().getCacheJSONObject(AppConstants.KEY_NAVIGATION);
            JSONObject conObject = DataCacheManager.getImpl().getCacheJSONObject(AppConstants.KEY_DISCOVER);
            FindInfo findInfo = makeFindInfo(bannerObject, navObject, conObject);
            callBack.requestSuccess(findInfo);
        }
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        FindInfo findInfo = new FindInfo();
        RetrofitHelper.getAppGameAPI().getDiscoverBanner(token, CommonUtil.getExtraParam())
                .compose(lifecycleTransformer)
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(@NonNull ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        List<BannerEntity> entityList = new ArrayList<BannerEntity>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            BannerEntity bannerEntity = new BannerEntity();
                            bannerEntity.resolveJson(jsonArray.getJSONObject(i));
                            entityList.add(bannerEntity);
                        }
                        findInfo.mBannerEntityList = entityList;
                        DataCacheManager.getImpl().addDataCacheInfo(AppConstants.KEY_BANNER, jsonObject.toString());
                        return RetrofitHelper.getAppGameAPI().getDiscoverNavigation(token, CommonUtil.getExtraParam());
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(@NonNull ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<NavigationInfo> list = new ArrayList<NavigationInfo>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            NavigationInfo navigationInfo = new NavigationInfo();
                            navigationInfo.resolveJson(jsonArray.getJSONObject(i));
                            list.add(navigationInfo);
                        }
                        findInfo.mNavigationInfoList = list;
                        DataCacheManager.getImpl().addDataCacheInfo(AppConstants.KEY_NAVIGATION, jsonObject.toString());
                        return RetrofitHelper.getAppGameAPI().getDiscoverConventions(token, String.valueOf(page_size), position, "", CommonUtil.getExtraParam());
                    }
                })
                .subscribeOn(Schedulers.io())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    findInfo.mFindListInfos = makeFindListInfo(jsonObject);
                    DataCacheManager.getImpl().addDataCacheInfo(AppConstants.KEY_DISCOVER, jsonObject.toString());
                    return findInfo;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::requestSuccess, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        callBack.requestFail(error.getTitle());
                    }
                });
    }

    public Observable<List<BannerEntity>> requestBanner() {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        return RetrofitHelper.getAppGameAPI().getDiscoverBanner(token, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONArray bannerArray = new JSONObject(responseBody.string()).optJSONArray("data");
                    List<BannerEntity> entityList = new ArrayList<>();
                    for (int i = 0; i < bannerArray.length(); i++) {
                        BannerEntity bannerEntity = new BannerEntity();
                        bannerEntity.resolveJson(bannerArray.optJSONObject(i));

                        if (bannerArray.getJSONObject(i).getJSONObject("attributes").getInt("status") == 1) {
                            entityList.add(bannerEntity);
                        }
                    }
                    return entityList;
                });
    }


    public Observable<List<NavigationInfo>> requestNavigation() {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        return RetrofitHelper.getAppGameAPI().getDiscoverNavigation(token, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONArray jsonArray = new JSONObject(responseBody.string()).getJSONArray("data");
                    List<NavigationInfo> list = new ArrayList<NavigationInfo>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        NavigationInfo navigationInfo = new NavigationInfo();
                        navigationInfo.resolveJson(jsonArray.getJSONObject(i));
                        list.add(navigationInfo);
                    }
                    return list;
                });
    }

    public Observable<List<FindListInfo>> requestConventions(String position, String target) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        return RetrofitHelper.getAppGameAPI().getDiscoverConventions(token, String.valueOf(page_size), position, target, CommonUtil.getExtraParam())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    return makeFindListInfo(jsonObject);
                });
    }

    private FindInfo makeFindInfo(JSONObject bannerObject, JSONObject navObject, JSONObject conObject) {
        FindInfo findInfo = new FindInfo();
        JSONArray bannerArray = bannerObject.optJSONArray("data");
        List<BannerEntity> entityList = new ArrayList<BannerEntity>();
        for (int i = 0; i < bannerArray.length(); i++) {
            BannerEntity bannerEntity = new BannerEntity();
            bannerEntity.resolveJson(bannerArray.optJSONObject(i));
            entityList.add(bannerEntity);
        }
        findInfo.mBannerEntityList = entityList;
        JSONArray navArray = navObject.optJSONArray("data");
        List<NavigationInfo> list = new ArrayList<NavigationInfo>();
        for (int i = 0; i < navArray.length(); i++) {
            NavigationInfo navigationInfo = new NavigationInfo();
            navigationInfo.resolveJson(navArray.optJSONObject(i));
            list.add(navigationInfo);
        }
        findInfo.mNavigationInfoList = list;
        JSONArray jsonArray = conObject.optJSONObject("data").optJSONArray("list");
        List<FindListInfo> listInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            FindListInfo findListInfo = new FindListInfo();
            JSONObject object = jsonArray.optJSONObject(i);
            String target = object.optJSONObject("attributes").optString("target");
            findListInfo.target = target;
            switch (target) {
                case "game":
                    FindTypeGame typeGame = new FindTypeGame();
                    typeGame.resolveJson(object);
                    findListInfo.typeGame = typeGame;
                    break;
                case "article":
                    FindTypeArticle typeArticle = new FindTypeArticle();
                    typeArticle.resolveJson(object);
                    findListInfo.typeArticle = typeArticle;
                    break;
                case "video":
                    FindTypeVideo typeVideo = new FindTypeVideo();
                    typeVideo.resolveJson(object);
                    findListInfo.typeVideo = typeVideo;
                    break;
                case "external":  //外链
                    FindTypeExternal typeExternal = new FindTypeExternal();
                    typeExternal.resolveJson(object);
                    findListInfo.typeExternal = typeExternal;
                    break;
            }
            listInfos.add(findListInfo);
        }
        findInfo.mFindListInfos = listInfos;
        return findInfo;
    }

    private List<FindListInfo> makeFindListInfo(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("list");
        List<FindListInfo> listInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            FindListInfo findListInfo = new FindListInfo();
            findListInfo.position = jsonObject.optJSONObject("meta").optString("position");
            JSONObject object = jsonArray.optJSONObject(i);

            findListInfo.id = object.optString("id");
            String target = object.optJSONObject("attributes").optString("target");
            int status = object.optJSONObject("attributes").optInt("status");
            findListInfo.target = target;
            findListInfo.status = status;
            switch (target) {
                case "game":
                    FindTypeGame typeGame = new FindTypeGame();
                    typeGame.resolveJson(object);
                    findListInfo.typeGame = typeGame;
                    break;
                case "article":
                    FindTypeArticle typeArticle = new FindTypeArticle();
                    typeArticle.resolveJson(object);
                    findListInfo.typeArticle = typeArticle;
                    break;
                case "video":
                    FindTypeVideo typeVideo = new FindTypeVideo();
                    typeVideo.resolveJson(object);
                    findListInfo.typeVideo = typeVideo;
                    break;
                case "external":  //外链
                    FindTypeExternal typeExternal = new FindTypeExternal();
                    typeExternal.resolveJson(object);
                    findListInfo.typeExternal = typeExternal;
                    break;
                case "topic":
                    FindTypeTopic typeTopic = new FindTypeTopic();
                    typeTopic.resolveJson(object);
                    findListInfo.typeTopic = typeTopic;
                    break;
            }
            listInfos.add(findListInfo);
        }
        return listInfos;
    }

}
