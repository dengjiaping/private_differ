package com.appgame.differ.utils.network;

import android.text.TextUtils;

import com.appgame.differ.base.app.DifferApplication;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LoginUtil;
import com.appgame.differ.utils.SpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit帮助类
 * Created by lzx on 2017/2/23.
 * 386707112@qq.com
 */

public class RetrofitHelper {

    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    public static AccountService getAccountAPI() {
        String url;
        if (AppConstants.IS_TEST_SERVER) {
            url = AppConstants.ACCOUNT_TEST_SERVER;
        } else {
            url = AppConstants.ACCOUNT_SERVER;
        }
        return createApi(AccountService.class, url);
    }


    public static AppGameService getAppGameAPI() {
        String url;
        if (AppConstants.IS_TEST_SERVER) {
            url = AppConstants.TEST_SERVER;
        } else {
            url = AppConstants.FORMAL_SERVER;
        }
        return createApi(AppGameService.class, url);
    }

    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private static <T> T createApi(Class<T> clazz, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志,设置UA拦截器
     */
    private static void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (AppConstants.IS_DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(DifferApplication.getContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 10);
                    mOkHttpClient = new OkHttpClient.Builder()
                            //.cache(cache)
                            .addInterceptor(interceptor)
                            .addInterceptor(new TokenInterceptor())
                            .addNetworkInterceptor(new CacheInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    /**
     * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
     */
    private static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60 * 60;
            // 无网络时，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (CommonUtil.isNetworkAvailable(DifferApplication.getContext())) {
                //有网络时只从网络获取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (CommonUtil.isNetworkAvailable(DifferApplication.getContext())) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }

    /**
     * 检查token拦截器
     */
    private static class TokenInterceptor implements Interceptor {

        private static final Charset UTF8 = Charset.forName("UTF-8");

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();//原始接口请求
            Response originalResponse = chain.proceed(originalRequest);//原始接口结果
            //取出接口结果
            ResponseBody responseBody = originalResponse.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            String bodyString = buffer.clone().readString(charset); //拿到请求结果
            try {
                JSONObject jsonObject = new JSONObject(bodyString);

                JSONArray array = jsonObject.optJSONArray("errors");
                if (array == null) {
                    return originalResponse;
                }

                int code = array.getJSONObject(0).getInt("code");

                if (code == 4002) {
                    originalResponse.body().close();//关闭 释放原有的资源

                    Request refreshRequest = getRefreshTokenRequest(); //刷新token接口请求
                    Response refreshResponse = chain.proceed(refreshRequest); //请求接口
                    //成功：重新保存登陆信息，失败：退出登陆
                    if (refreshResponse.isSuccessful()) {
                        JSONObject object = new JSONObject(refreshResponse.body().string());
                        LoginUtil.getImpl().saveLoginInfo(object);
                        refreshResponse.body().close();//释放刷新成功的资源
                    } else {
                        LoginUtil.getImpl().clearLoginInfo();
                        UserInfoManager.getImpl().clear();
                    }
                    return chain.proceed(originalRequest);//重新执行
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return originalResponse;
            }
            return originalResponse;
        }

        private Request getRefreshTokenRequest() {
            String refreshToken = SpUtil.getInstance().getString(AppConstants.REFRESH_TOKEN);
            if (!TextUtils.isEmpty(refreshToken)) {
                String url;
                if (AppConstants.IS_TEST_SERVER) {
                    url = AppConstants.ACCOUNT_TEST_SERVER;
                } else {
                    url = AppConstants.ACCOUNT_SERVER;
                }
                url = url + "oauth/access_token";
                RequestBody body = new FormBody.Builder()
                        .add("grant_type", "refresh_token")
                        .add("client_id", AppConstants.CLIENT_ID)
                        .add("client_secret", AppConstants.CLIENT_SECRET)
                        .add("refresh_token", refreshToken)
                        .build();
                return new Request.Builder().url(url).post(body).build();
            }
            return null;
        }
    }

}
