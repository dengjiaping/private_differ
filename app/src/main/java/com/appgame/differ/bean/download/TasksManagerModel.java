package com.appgame.differ.bean.download;

import com.liulishuo.filedownloader.model.FileDownloadStatus;

/**
 * 下载信息
 * Created by xian on 2017/4/22.
 */

public class TasksManagerModel {
    public int downloadId;
    public String downloadPath = "downloadPath";
    public String downloadUrl = "downloadUrl";
    public String gameName = "gameName";
    public String gameCategory = "gameCategory";
    public String gameCover = "gameCover";
    public String gameIcon = "gameIcon";
    public String packageName = "packageName";
    public String gameSize = "gameSize";
    public String gameId = "gameId";
    public String downloadLinkId = "downloadLinkId";
    public int downloadFlag = FileDownloadStatus.pending;
    public String isDownloadAuto = "false";  //0 true 1 false

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameCategory() {
        return gameCategory;
    }

    public void setGameCategory(String gameCategory) {
        this.gameCategory = gameCategory;
    }

    public String getGameCover() {
        return gameCover;
    }

    public void setGameCover(String gameCover) {
        this.gameCover = gameCover;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getGameSize() {
        return gameSize;
    }

    public void setGameSize(String gameSize) {
        this.gameSize = gameSize;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getDownloadLinkId() {
        return downloadLinkId;
    }

    public void setDownloadLinkId(String downloadLinkId) {
        this.downloadLinkId = downloadLinkId;
    }

    public int getDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(int downloadFlag) {
        this.downloadFlag = downloadFlag;
    }
}
