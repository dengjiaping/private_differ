package com.appgame.differ.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;

import com.appgame.differ.BuildConfig;
import com.appgame.differ.bean.VersionInfo;
import com.appgame.differ.bean.game.TagsInfo;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.google.gson.Gson;
import com.meituan.android.walle.WalleChannelReader;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 通用的工具类
 * Created by lzx on 2017/2/23.
 * 386707112@qq.com
 */

public class CommonUtil {
    /**
     * 检查是否有网络
     */
    public static boolean isNetworkAvailable(Context context) {

        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable();
    }


    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 检查是否是WIFI
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否是移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    /**
     * 获取屏幕的分辨率
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getPhoneHeight(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getPhoneWidth(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return dm.widthPixels;
    }

    public static String getPhoneWidthString(Context context) {
        DisplayMetrics dm = getDisplayMetrics(context);
        return String.valueOf(dm.widthPixels);
    }

    /**
     * dip 转 px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue) {
        if (context != null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dipValue * scale + 0.5f);
        }
        return (int) dipValue;
    }

    public static int[] getScreenSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 获取状态栏高度——方法
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    /**
     * base64加密字符串
     *
     * @param data
     * @return
     */
    public static String Base64Encode(String data) {
        return Base64.encodeToString(data.getBytes(), 10);
    }

    /**
     * 判断手机号码
     */
    public static boolean checkMobileNum(String mobiles) {
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        String telRegex = "[1][34578]\\d{9}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取版本名
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, File files) throws Exception{
        if (files != null) {
            Uri uri = Uri.fromFile(files);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (uri.toString().startsWith("content")) {
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else if (uri.toString().startsWith("file")) {
                intent.setDataAndType(getUriForFile(context, files), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } else {
            ToastUtil.showShort("文件不存在");
        }
    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.appgame.differ.provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 启动程序
     */
    public static void startApp(Context mContext, String appPackageName) {
        try {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(appPackageName);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort("没有安装该程序");
        }
    }

    public static String formatYMD(String dateStr) {
        if (TextUtils.isEmpty(dateStr) || dateStr.equals("0")) {
            return "";
        }
        long l = Long.parseLong(dateStr);
        long time = l * 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return simpleDateFormat.format(time);
    }

    public static String formatTime(String dateStr) {
        if (TextUtils.isEmpty(dateStr) || dateStr.equals("0")) {
            return "";
        }

        long l = Long.parseLong(dateStr);
        long time = l * 1000;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM--dd  HH:mm");
        return simpleDateFormat.format(time);
    }

    public static String formatDate(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        long l = Long.parseLong(dateStr);
        long time = l * 1000;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(time);
    }

    /**
     * 隐藏键盘
     */
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    public static void showKeyboard(View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static String getNickName(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            return "differ";
        } else {
            return nickname;
        }
    }

    /**
     * 判断是否有虚拟按键方法
     * 使用方法：
     * Point point = getNavigationBarSize(this);
     * bottomHeight = point.y;
     */
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }
        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }
        // navigation bar is not present
        return new Point();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            } catch (NoSuchMethodException e) {
            }
        }
        return size;
    }


    public static Animation getScaleAnimation(float fromX,
                                              float toX,
                                              float fromY,
                                              float toY,
                                              int pivotXType,
                                              float pivotXValue,
                                              int pivotYType,
                                              float pivotYValue) {
        Animation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
                pivotYValue
        );
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }

    /**
     * 判断是否已经登录
     */
    public static boolean isLogin() {
        return SpUtil.getInstance().getBoolean(AppConstants.IS_LOGIN, false);
    }

    /**
     * 判断是否显示红点
     */
    public static boolean isShowMsgDot(boolean hasNewMsg) {
        long time = SpUtil.getInstance().getLong(AppConstants.MSG_TIME_MILLIS, 0);
        if (time == 0) {
            SpUtil.getInstance().putLong(AppConstants.MSG_TIME_MILLIS, System.currentTimeMillis());
        }
        if (!isLogin()) {
            return false;
        } else if (!hasNewMsg) {
            return false;
        } else {
            boolean isShow = SpUtil.getInstance().getBoolean(AppConstants.IS_SHOW_MSG_DOT, false);
            if (System.currentTimeMillis() - time > 604800 * 1000 && isShow) {  //大于7天和标记是
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 检查版本更新
     */
    public static void checkVersion(final Context context, LifecycleTransformer<ResponseBody> lifecycleTransformer, final OnCheckVersionListener listener) {
      //  String channel = BuildConfig.FLAVOR;
        String channel = WalleChannelReader.getChannel(context);
        RetrofitHelper.getAppGameAPI().requestVersion("android", channel,CommonUtil.getExtraParam())
                .compose(lifecycleTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        LogUtil.i("版本更新 =" + jsonObject.toString());
                        JSONObject verOb = jsonObject.getJSONObject("data").getJSONObject("attributes");
                        VersionInfo mVersionInfo = new Gson().fromJson(verOb.toString(), VersionInfo.class);
                        try {
                            String currVersion = CommonUtil.getVersionName(context);
                            String[] verArray = mVersionInfo.version.split("\\.");
                            String[] currArray = currVersion.split("\\.");
                            int verOne = Integer.parseInt(verArray[0]);
                            int verTwo = Integer.parseInt(verArray[1]);
                            int verThree = Integer.parseInt(verArray[2]);
                            int currOne = Integer.parseInt(currArray[0]);
                            int currTwo = Integer.parseInt(currArray[1]);
                            int currThree = Integer.parseInt(currArray[2]);
                            boolean isNeedToUpdate;
                            if (verOne > currOne) {
                                isNeedToUpdate = true;
                            } else if (verTwo > currTwo) {
                                isNeedToUpdate = true;
                            } else if (verThree > currThree) {
                                isNeedToUpdate = true;
                            } else {
                                isNeedToUpdate = false;
                            }
                            listener.onSuccess(mVersionInfo, isNeedToUpdate);
                        } catch (Exception e) {
                            LogUtil.i("版本更新#Exception = " + e.getMessage());
                            e.printStackTrace();
                            listener.onFailure();
                        }
                    }
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        LogUtil.i("-版本更新->" + error.getTitle());
                        listener.onFailure();
                    }
                });
    }

    public interface OnCheckVersionListener {
        void onSuccess(VersionInfo mVersionInfo, boolean isNeedToUpdate);

        void onFailure();
    }

    /**
     * 将时间戳转为代表"距现在多久之前"的字符串
     *
     * @param timeStr 时间戳
     * @return
     */
    public static String getStandardDate(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        long t = Long.parseLong(timeStr);
        long time = System.currentTimeMillis() - (t * 1000);
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            sb.append(day).append("天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour).append("小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute).append("分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill).append("秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }

    /**
     * 得到动态内容
     */
    public static Spanned getDynamicContent(String dyContent) {
        if (dyContent.contains("@#Forwarding#")) {
            dyContent = dyContent.replace("@#Forwarding#", "");
        }
        Spanned html = Html.fromHtml(dyContent);
        return html;
    }

    /**
     * 得到带颜色的字
     */
    public static String getUserNameWithColor(String nickName) {
        return "<font color=\"#15B1B8\">@" + getNickName(nickName) + "</font> ";
    }

    /**
     * 包装转发内容
     */
    public static String getShareContent(String nickName, String content) {
        return "//@#Forwarding#<font color=\"#15B1B8\">@" + getNickName(nickName) + "</font>:" + content;
    }

    /**
     * 限制标签个数
     */
    public static List<TagsInfo> getTagsInfoWithNum(List<TagsInfo> tags, int num) {
        List<TagsInfo> tagsInfos = new ArrayList<>();
        if (tags.size() > num) {
            for (int i = 0; i < num; i++) {
                tagsInfos.add(tags.get(i));
            }
        } else {
            tagsInfos.addAll(tags);
        }
        return tagsInfos;
    }

    /**
     * 计算点赞个数
     */
    public static String getThumbNum(int thumb, String thumbUp) {
        int thumbs_up = Integer.parseInt(thumbUp);
        if (thumb == 1) {
            thumbs_up = thumbs_up + 1;
        } else {
            thumbs_up = thumbs_up - 1;
        }
        return String.valueOf(thumbs_up);
    }

    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取设备参数
     */
    public static void initExtraParam(Context context) {
        String appVersion = getVersionName(context);   //APP版本号
        String system = "Android";   //系统类型，比如iOS ，Android
        String systemVersion = AppStatisticsUtils.getPhoneName();  //系统版本，比如9.3.1
        String deviceModel = AppStatisticsUtils.getModelName(); //设备型号，比如iPhone5，XiaoMi
        String deviceId = AppStatisticsUtils.getIMEI(context); //设备唯一标识
        String distributeChannel = BuildConfig.FLAVOR; //应用分发渠道，比如：AppStore，Yingyongbao
        Map<String, String> map = new HashMap<>();
        map.put("appVersion", appVersion);
        map.put("system", system);
        map.put("systemVersion", systemVersion);
        map.put("deviceModel", deviceModel);
        map.put("deviceId", deviceId);
        map.put("distributeChannel", distributeChannel);
        String json = new JSONObject(map).toString();
        SpUtil.getInstance().putString(AppConstants.EXTRA_PARAM, json);
    }

    /**
     * 得到设备参数json
     */
    public static String getExtraParam() {
        return SpUtil.getInstance().getString(AppConstants.EXTRA_PARAM);
    }


    /**
     * 生成分享图片
     * 参数：分享的布局
     */
    public static String drawShareImage(Context context, View view) {
        String imagePath = "";
        view.setDrawingCacheEnabled(true);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache(true);
        Bitmap bmp = duplicateBitmap(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        // clear the cache
        view.setDrawingCacheEnabled(false);
        if (bmp != null && !bmp.isRecycled()) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            //将文件保存到本地
            imagePath = saveInputStreamToFile(is);
        }
        return imagePath;
    }

    public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }
        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();
        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }
        return bmpDest;
    }

    /**
     * 保存InputStream到本地
     */
    public static String saveInputStreamToFile(InputStream data) {
        String path = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
            String picName = simpleDateFormat.format(new Date());
            File file = new File(FileUtil.getImagePath() + "share_" + picName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int len;
            while ((len = data.read(b)) > -1) {
                fos.write(b, 0, len);
            }
            fos.close();
            path = file.getAbsolutePath();
        } catch (IOException ex) {
            path = "";
        }
        return path;
    }

    /**
     * 判断服务是否在运行
     */
    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }


}
