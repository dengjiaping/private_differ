package com.appgame.differ.module.dynamic.presenter;

import android.text.TextUtils;

import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.dynamic.contract.PostDynamicContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yukunlin on 17/5/11.
 */

public class PostDynamicPresenter implements PostDynamicContract.Presenter {
    private PostDynamicContract.View mView;

    public PostDynamicPresenter(PostDynamicContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void postDynamic(String game_id, String content, List<String> imgPaths, String dynamic_id, String postType, String target, String target_id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN, "");
        if (!postType.equals("share")) {
            if (TextUtils.isEmpty(content)) {
                mView.onPostDynamicError("内容不能为空");
                mView.showProgressDialog("", "dismiss");
                return;
            }
        }
        if (imgPaths == null || imgPaths.size() == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("access_token", token);
            map.put("game_id", game_id);
            map.put("content", content);
            map.put("target", target);
            map.put("target_id", target_id);
            if (!TextUtils.isEmpty(dynamic_id)) {
                map.put("forward_dynamic_id", dynamic_id);
            }
            map.put("extra", CommonUtil.getExtraParam());
            mView.showProgressDialog("动态发布中...", "show");
            RetrofitHelper.getAppGameAPI().uploadDynamic(map)
                    .compose(mView.bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        mView.onPostDynamicSuccess();
                        mView.showProgressDialog("", "dismiss");
                    }, new ErrorActionAppGame() {
                        @Override
                        public void call(AppGameResponseError error) {
                            LogUtil.e("PostDynamicPresenter = " + error.getTitle());
                            mView.onPostDynamicError(error.getTitle());
                            mView.showProgressDialog("", "dismiss");
                        }
                    });
        } else {
            List<Observable> observables = new ArrayList<>();
            for (String path : imgPaths) {
                Map<String, Object> map = new HashMap<>();
                map.put("image_data", path);
                map.put("access_token", token);
                map.put("extra", CommonUtil.getExtraParam());
                Observable<String> observable = RetrofitHelper.getAppGameAPI().uploadPicture(map)
                        .compose(mView.bindToLifecycle())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .map(responseBody -> {
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            int status = jsonObject.getJSONObject("meta").optInt("status");
                            if (status == 200) {
                                return jsonObject.getJSONObject("meta").optString("image_id");
                            } else {
                                return null;
                            }
                        });
                observables.add(observable);
            }
            Observable[] observablesArray = new Observable[observables.size()];
            for (int i = 0; i < observables.size(); i++) {
                observablesArray[i] = observables.get(i);
            }

            Observable.zipArray(objects -> {
                List<String> imageIdList = new ArrayList<>();
                for (Object object : objects) {
                    imageIdList.add((String) object);
                }
                return imageIdList;
            }, true, 1, observablesArray)
                    .compose(mView.bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResourceObserver<List<String>>() {
                        @Override
                        public void onNext(List<String> strings) {
                            mView.showProgressDialog("动态发布中...", "update");
                            Map<String, Object> map = new HashMap<>();
                            map.put("access_token", token);
                            map.put("game_id", game_id);
                            map.put("content", content);
                            map.put("target", target);
                            map.put("target_id", target_id);
                            if (!TextUtils.isEmpty(dynamic_id)) {
                                map.put("forward_dynamic_id", dynamic_id);
                            }
                            map.put("images", new Gson().toJson(strings));
                            map.put("extra", CommonUtil.getExtraParam());

                            LogUtil.i("json = " + new Gson().toJson(strings));

                            RetrofitHelper.getAppGameAPI().uploadDynamic(map)
                                    .compose(mView.bindToLifecycle())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(responseBody -> {
                                        mView.onPostDynamicSuccess();
                                        mView.showProgressDialog("", "dismiss");
                                    }, new ErrorActionAppGame() {
                                        @Override
                                        public void call(AppGameResponseError error) {
                                            mView.onPostDynamicError(error.getTitle());
                                            mView.showProgressDialog("", "dismiss");
                                        }
                                    });
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtil.i("上传失败 = " + e.getMessage());
                            mView.showProgressDialog("", "dismiss");
                            mView.onPostDynamicError("动态发布失败");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                            LogUtil.i("上传完成");
                        }
                    });
        }
    }
}
