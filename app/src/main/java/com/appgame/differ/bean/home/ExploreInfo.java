package com.appgame.differ.bean.home;

import java.util.List;

/**
 * Created by lzx on 2017/4/18.
 * 386707112@qq.com
 */

public class ExploreInfo {
    public boolean isSuccess;
    public String flag;

    public ExploreInfo() {
    }

    public ExploreInfo(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    private RecommedInfo column;
    private List<RecommedInfo> list;

    public RecommedInfo getColumn() {
        return column;
    }

    public void setColumn(RecommedInfo column) {
        this.column = column;
    }

    public List<RecommedInfo> getList() {
        return list;
    }

    public void setList(List<RecommedInfo> list) {
        this.list = list;
    }
}
