package com.appgame.differ.module.find;

import android.os.Bundle;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.statusbar.SystemBarHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by lzx on 2017/6/7.
 */

public class VideoPlayActivity extends BaseActivity {

    private JCVideoPlayerStandard mJCVideoPlayer;
    private String url;
    private String title;
    private String cover;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        SystemBarHelper.immersiveStatusBar(VideoPlayActivity.this);
        mJCVideoPlayer = (JCVideoPlayerStandard) findViewById(R.id.video_player);
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        cover = getIntent().getStringExtra("cover");
        Glide.with(this).load(cover).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(mJCVideoPlayer.thumbImageView);
        mJCVideoPlayer.setUp(url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        if (CommonUtil.isWifi(VideoPlayActivity.this)) {
            mJCVideoPlayer.startVideo();
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
