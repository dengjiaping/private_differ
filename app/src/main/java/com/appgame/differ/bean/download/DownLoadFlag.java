package com.appgame.differ.bean.download;

/**
 * 下载状态
 * Created by xian on 2017/4/22.
 */
public class DownLoadFlag {
//    public static final int PENDING = 0; //等待中
//    public static final int STARTED = 1; //开始
//    public static final int CONNECTED = 2; //已连接
//    public static final int PROGRESS = 3; //下载中
//    public static final int ERROR = 4; //下载错误
//    public static final int PAUSED = 5; //暂停
//    public static final int COMPLETED = 6; //下载完成


    public static final int has_app = 20; //有app
    public static final int has_pkg = 21; //有安装包
    public static final int download_normal = 22; //下载
    public static final int no_download_url = 23; //没有下载链接
}
