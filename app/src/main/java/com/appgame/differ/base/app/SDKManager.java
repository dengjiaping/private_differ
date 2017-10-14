package com.appgame.differ.base.app;

import android.app.Notification;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import com.appgame.differ.R;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.meituan.android.walle.WalleChannelReader;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import static com.tencent.bugly.beta.tinker.TinkerManager.getApplication;


/**
 * SDK管理类
 * Created by lzx on 2017/4/24.
 * 386707112@qq.com
 */

public class SDKManager {

    public static void init(Context context) {
        initSpUtil(context);
        initBugly(context);
        initUmeng(context);
        initDownload(context);
    }

    /**
     * 初始化SpUtil
     */
    private static void initSpUtil(Context context) {
        SpUtil.getInstance().init(context);
    }

    /**
     * 初始化Bugly
     */
    private static void initBugly(Context context) {

       // String channel = BuildConfig.FLAVOR;
        String channel = WalleChannelReader.getChannel(context);

        Bugly.setAppChannel(getApplication(), channel);
        Bugly.init(context, "9b68412235", AppConstants.IS_DEBUG);
        CrashReport.setUserSceneTag(context, AppConstants.BUGLY_TAG); // 上报后的Crash会显示该标签
    }

    /**
     * 初始化友盟
     */
    private static void initUmeng(Context context) {
        //String channel = BuildConfig.FLAVOR;
        String channel = WalleChannelReader.getChannel(context);

        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context, "58f587328f4a9d47d700147b", channel,
                MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.startWithConfigure(config);

        initShare(context);
        //友盟推送
        initPush(context);
    }

    /**
     * 初始化分享
     */
    private static void initShare(Context context) {
        UMShareAPI.init(context, "58f587328f4a9d47d700147b");
        UMShareAPI.get(context);
        PlatformConfig.setWeixin("wxfa88e143e15b8297", "ad93241bec78c98b5ea2a817c33832bc");
        PlatformConfig.setQQZone("1106063028", "vMb3Fr1gZrRS4H1H");
        PlatformConfig.setSinaWeibo("3781674297", "c9dd4a8035bde215729ae0e362e8f7f7", "https://api.weibo.com/oauth2/default.html");
    }

    /**
     * 初始化推送
     */
    private static void initPush(Context context) {
        final Handler handler = new Handler(Looper.getMainLooper());
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.setDebugMode(AppConstants.IS_DEBUG);
        mPushAgent.setAppkeyAndSecret("58f587328f4a9d47d700147b", "795d5b2d6d56d0812932ecc7a73c4336");
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法 (不会显示在通知栏)
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(context).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(context).trackMsgDismissed(msg);
                        }
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.layout_push_notification);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理，参考文档：高级功能-通知的展示及提醒-自定义通知打开动作
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                //需要自定义行为
                LogUtil.i("msg_id = " + msg.msg_id);  //消息id
                LogUtil.i("ticker = " + msg.ticker); //股票代码
                LogUtil.i("title = " + msg.title);  //标题
                LogUtil.i("text = " + msg.text);  //内容
                LogUtil.i("custom = " + msg.custom);  //消息自定义内容

            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知，参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER); //声音
        mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SERVER);//呼吸灯
        mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SERVER);//振动

        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                LogUtil.i("deviceToken = " + deviceToken);
                SpUtil.getInstance().putString(AppConstants.DEVICE_TOKEN, deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                LogUtil.i("onFailure = " + s);
            }
        });
    }

    /**
     * 初始化下载
     */
    private static void initDownload(Context context) {
        FileDownloader.setup(context);
        FileDownloader.getImpl().setMaxNetworkThreadCount(3);
    }

}
