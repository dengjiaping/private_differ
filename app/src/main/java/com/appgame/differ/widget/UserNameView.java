package com.appgame.differ.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.user.RankInfo;
import com.appgame.differ.utils.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by lzx on 2017/6/8.
 */

public class UserNameView extends RelativeLayout {

    private float nameSize;

    public UserNameView(Context context) {
        this(context, null, 0);
    }

    public UserNameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserNameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserNameView, defStyleAttr, 0);
        nameSize = a.getDimension(R.styleable.UserNameView_nameSize, -1);
        a.recycle();

        init();
    }

    private TextView mUserName;
    private ImageView mIconBadge;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_user_name, this, true);
        mUserName = (TextView) findViewById(R.id.user_name);
        mIconBadge = (ImageView) findViewById(R.id.icon_badge);
        if (nameSize != -1)
            mUserName.setTextSize(nameSize);
    }

    public UserNameView setUserName(String userName) {
        mUserName.setText(CommonUtil.getNickName(userName));
        return this;
    }

    public void setRankInfo(RankInfo rankInfo) {
        if (rankInfo != null) {
            String icon = !TextUtils.isEmpty(rankInfo.getIcon()) ? rankInfo.getIcon() : "";
            mIconBadge.setVisibility(!TextUtils.isEmpty(icon) ? View.VISIBLE : View.GONE);
            Glide.with(getContext()).load(rankInfo.getIcon()).diskCacheStrategy(DiskCacheStrategy.ALL).into(mIconBadge);
        } else {
            mIconBadge.setVisibility(GONE);
        }
    }


}
