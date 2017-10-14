package com.appgame.differ.utils.helper;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.module.game.GameDetailsActivity;
import com.appgame.differ.module.game.GameImagePageActivity;
import com.appgame.differ.module.personal.view.PersonalActivity;
import com.appgame.differ.utils.CommonUtil;

import java.util.List;


/**
 * Created by lzx on 2017/4/24.
 * 386707112@qq.com
 */

public class IntentHelper {

    public static void startGameDetailActivity(Activity activity, View background, String gameId) {
        Intent intent = new Intent(activity, GameDetailsActivity.class);
        intent.putExtra("game_id", gameId);
        activity.startActivity(intent);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
//                    background,
//                    (int) background.getX(), (int) background.getY(),
//                    background.getMeasuredWidth(), background.getMeasuredHeight()
//            );
//            ActivityCompat.startActivity(activity, intent, options.toBundle());
//        } else {
//            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    activity,
//                    Pair.create(background, activity.getString(R.string.transition_game_detail_background)));
//            ActivityCompat.startActivity(activity, intent, options.toBundle());
//        }
    }

    public static void startPersionActivity(Activity activity, View avatar, String userId) {
        Intent intent = new Intent(activity, PersonalActivity.class);
        if (!CommonUtil.isLogin()) {
            intent.putExtra("action", "other");
            intent.putExtra("userId", userId);
        } else {
            //UserInfo userInfo = UserInfoManager.getImpl().getUserInfo();
            String selfUserId = UserInfoManager.getImpl().getUserId();
            if (!TextUtils.isEmpty(selfUserId)) {
                if (!selfUserId.equals(userId)) {
                    intent.putExtra("action", "other");
                    intent.putExtra("userId", userId);
                }
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent);
        } else {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    Pair.create(avatar, activity.getString(R.string.transition_user_avatar)));
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        }
    }

    public static void startGameImagePageActivity(Context mContext, View view, List<String> imgUrls, int position, String gameName) {
        Intent intent = GameImagePageActivity.luanch(mContext, gameName, position, imgUrls);
//        mContext.startActivity(intent);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mContext.startActivity(intent);
        } else {
            if (mContext instanceof PersonalActivity) {
                mContext.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation((PersonalActivity) mContext, view, imgUrls.get(position)).toBundle());
            } else if (mContext instanceof GameDetailsActivity) {
                mContext.startActivity(intent,
                        ActivityOptions.makeSceneTransitionAnimation((GameDetailsActivity) mContext, view, imgUrls.get(position)).toBundle());
            }
        }
    }


}
