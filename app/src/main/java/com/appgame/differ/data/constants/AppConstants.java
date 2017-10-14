package com.appgame.differ.data.constants;

/**
 * 全局变量
 * Created by lzx on 2017/2/21.
 * 386707112@qq.com
 */
public class AppConstants {
    //是否为测试服务器
    public static final boolean IS_TEST_SERVER = true;

    public static final String ACCOUNT_SERVER = "https://passport.appgame.com/";
    //用户系统测试服务器地址
    public static final String ACCOUNT_TEST_SERVER = "http://passport.test.appgame.com/";

    //正式服务器地址
    public static final String FORMAL_SERVER = "http://games-planet.4gvv.com/";  //192.168.177.160
    //测试服务器地址
    public static final String TEST_SERVER = "http://games-planet.test.appgame.com/";
    //用户协议
    public static final String USER_AGREEMENT;
    //分享链接
    public static final String SHARE_URL;

    public static String CLIENT_ID;
    public static String CLIENT_SECRET;
    public static int BUGLY_TAG;
    public static boolean IS_DEBUG;

    static {
        if (IS_TEST_SERVER) {  //differ测试
            CLIENT_ID = "shouyoushe";
            CLIENT_SECRET = "8da8d1d6b9f98229f4b465f68cd1a7c6";
            BUGLY_TAG = 41251;
            USER_AGREEMENT = TEST_SERVER + "h5/agreement";
            IS_DEBUG = true;
            SHARE_URL = TEST_SERVER;
        } else {     //differ正式
            CLIENT_ID = "dad92827941075a0";
            CLIENT_SECRET = "634d2518632ca421a796254f31950e6e";
            BUGLY_TAG = 41252;
            USER_AGREEMENT = FORMAL_SERVER + "h5/agreement";
            IS_DEBUG = true;
            SHARE_URL = FORMAL_SERVER;
        }
    }

    public static final int page_size = 15;

    //QQ
    public static final String QQ_APP_ID = "1106063028";
    public static final String QQ_APP_KEY = "vMb3Fr1gZrRS4H1H";
    //access_token
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in"; //token过期时间
    public static final String LAST_REFRESH_TIME = "last_refresh_time";
    //是否已经登录
    public static final String IS_LOGIN = "is_login";
    public static final String NOT_LOGIN = "not_login";
    public static final String INCOGNITO_LOGIN = "incognito_login";  //匿名登陆
    public static final String IS_OPEN_PUSH = "is_open_push"; //是否开启推送
    public static final String IS_DELETE_PKG = "is_delete_pkg"; //是否开启安装完后删除安装包
    public static final String DEVICE_TOKEN = "device_token"; //设备token
    public static final String IS_SHOW_MSG_DOT = "is_show_msg_dot"; //是否显示消息红点
    public static final String MSG_TIME_MILLIS = "msg_time_millis";//
    public static final String IS_FIRST_OPEN = "is_first_open"; //是否第一次打开
    public static final String EXPLORE_TIME = "explore_time"; //探索时间

    public static final int MAX_COUNT = 2;
    public static final String update_url = "update_url";

    /**
     * 缓存key
     */
    public static final String KEY_BANNER = "BannerEntity";
    public static final String KEY_NAVIGATION = "NavigationInfo";
    public static final String KEY_DISCOVER = "DiscoverConventions";
    public static final String KEY_DYNAMIC_LIST = "Dynamic_list";

    public static final String EXTRA_PARAM = "extra_param";

    public static final String USER_ID = "user_Id";
    public static final String USER_AVATAR = "user_avatar";
    public static final String USER_NICKNAME = "user_nickname";

    public static final String ACTIVITY_URL = "http://differ.appgame.com/act/index.html"; //抽奖活动页面
}
