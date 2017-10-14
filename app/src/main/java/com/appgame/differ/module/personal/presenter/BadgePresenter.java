package com.appgame.differ.module.personal.presenter;

import com.appgame.differ.base.mvp.BaseSubscriber;
import com.appgame.differ.base.mvp.RxPresenter;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.module.personal.contract.BadgeContract;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzx on 2017/5/15.
 * 386707112@qq.com
 */

public class BadgePresenter extends RxPresenter<BadgeContract.View> implements BadgeContract.Presenter<BadgeContract.View> {


    @Override
    public void getMineBadge() {

    }

    @Override
    public void getAllBadge() {

    }

    @Override
    public void changeIsShowBadge(int position, String achieve_id) {
        boolean isLogin = SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN);
        if (!isLogin) {
            mView.showError(AppConstants.NOT_LOGIN, false);
            return;
        }
        String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", access_token);
        map.put("achieve_id", achieve_id);
        map.put("extra", CommonUtil.getExtraParam());
        BaseSubscriber<Boolean> subscriber = RetrofitHelper.getAppGameAPI().changeAchieveStatus(map)
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").optInt("status", -1);
                    String message = jsonObject.getJSONObject("meta").optString("message");
                    return status == 200 && message.equals("success");
                })
                .compose(RxUtils.rxSchedulerObservable())
                .subscribeWith(new BaseSubscriber<Boolean>(mView, false) {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mView.changeIsShowBadgeSuccess(position);
                        } else {
                            mView.showError("操作失败", false);
                        }
                    }
                });
        addSubscribe(subscriber);
    }
}
