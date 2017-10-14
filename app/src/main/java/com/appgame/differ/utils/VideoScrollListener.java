package com.appgame.differ.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appgame.differ.R;
import com.appgame.differ.widget.JCVideoView;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by lzx on 2017/7/18.
 */

public class VideoScrollListener extends RecyclerView.OnScrollListener {
    private int _firstItemPosition = -1, _lastItemPosition;
    private View fistView, lastView;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //判断是当前layoutManager是否为LinearLayoutManager
        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            //获取最后一个可见view的位置
            int lastItemPosition = linearManager.findLastVisibleItemPosition();
            //获取第一个可见view的位置
            int firstItemPosition = linearManager.findFirstVisibleItemPosition();
            //获取可见view的总数
            int visibleItemCount = linearManager.getChildCount();
            if (_firstItemPosition < firstItemPosition) {
                _firstItemPosition = firstItemPosition;
                _lastItemPosition = lastItemPosition;
                GCView(fistView);
                fistView = recyclerView.getChildAt(0);
                lastView = recyclerView.getChildAt(visibleItemCount - 1);
            } else if (_lastItemPosition > lastItemPosition) {
                _firstItemPosition = firstItemPosition;
                _lastItemPosition = lastItemPosition;
                GCView(lastView);
                fistView = recyclerView.getChildAt(0);
                lastView = recyclerView.getChildAt(visibleItemCount - 1);
            } else {

            }
        }
    }

    /**
     * 回收播放
     */
    void GCView(View gcView) {
        if (gcView != null && gcView.findViewById(R.id.video_player) != null) {
            JCVideoView video = (JCVideoView) gcView.findViewById(R.id.video_player);
            if (video != null && (video.currentState == JCVideoPlayer.CURRENT_STATE_PLAYING || video.currentState == JCVideoPlayer.CURRENT_STATE_ERROR)) {
                JCVideoPlayer.releaseAllVideos();
            }
        }
    }
}
