package com.appgame.differ.widget.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.utils.CommonUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <p/>
 * 自定义Banner无限轮播控件
 */
public class BannerView extends RelativeLayout
        implements BannerAdapter.ViewPagerOnItemClickListener {

    ViewPager viewPager;

    LinearLayout points;

    private CompositeDisposable compositeSubscription;

    //默认轮播时间，10s
    private int delayTime = 5;

    private List<View> imageViewList;

    private List<BannerEntity> bannerList = new ArrayList<>();

    //选中显示Indicator
    private int selectRes = R.drawable.shape_dots_select;

    //非选中显示Indicator
    private int unSelcetRes = R.drawable.shape_dots_default;

    //当前页的下标
    private int currrentPos;

    private ImageView mImageBanner;
    private TextView mBannerTitle, mGameName;
    private View leftLine, rightLine;
    private RelativeLayout mRootLayout;
    private View mShadowView;
    private int bannerHeight;


    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_banner, this, true);
        viewPager = (ViewPager) findViewById(R.id.layout_banner_viewpager);
        points = (LinearLayout) findViewById(R.id.layout_banner_points_group);
        imageViewList = new ArrayList<>();
        bannerHeight = CommonUtil.getPhoneWidth(getContext()) * 9 / 16;
    }

    /**
     * 设置轮播间隔时间
     *
     * @param time 轮播间隔时间，单位秒
     */
    public BannerView delayTime(int time) {
        this.delayTime = time;
        return this;
    }

    /**
     * 设置Points资源 Res
     *
     * @param selectRes   选中状态
     * @param unselcetRes 非选中状态
     */
    public void setPointsRes(int selectRes, int unselcetRes) {
        this.selectRes = selectRes;
        this.unSelcetRes = unselcetRes;
    }


    LinearLayout.LayoutParams paramsNormal, paramsSelect;

    /**
     * 图片轮播需要传入参数
     */
    public void build(List<BannerEntity> list) {
        paramsNormal = new LinearLayout.LayoutParams(CommonUtil.dip2px(getContext(), 10), CommonUtil.dip2px(getContext(), 4));
        paramsSelect = new LinearLayout.LayoutParams(CommonUtil.dip2px(getContext(), 28), CommonUtil.dip2px(getContext(), 4));
        paramsNormal.leftMargin = 10;
        paramsSelect.leftMargin = 10;
        destory();
        if (list.size() == 0) {
            this.setVisibility(GONE);
            return;
        }else {
            this.setVisibility(VISIBLE);
        }
        currrentPos = 0;
        imageViewList.clear();
        bannerList.clear();
        bannerList.addAll(list);
        int pointSize = bannerList.size();
        if (pointSize == 2) {
            bannerList.addAll(list);
        }
        //判断是否清空 指示器点
        points.removeAllViews();

        //初始化与个数相同的指示器点
        for (int i = 0; i < pointSize; i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(unSelcetRes);
            dot.setLayoutParams(paramsNormal);
            dot.setEnabled(false);
            points.addView(dot);
        }

        points.getChildAt(0).setBackgroundResource(selectRes);
        points.getChildAt(0).setLayoutParams(paramsSelect);

        for (int i = 0; i < bannerList.size(); i++) {
            View bannerView = LayoutInflater.from(getContext()).inflate(R.layout.item_banner, this, false);
            mImageBanner = (ImageView) bannerView.findViewById(R.id.image_banner);
            mBannerTitle = (TextView) bannerView.findViewById(R.id.banner_title);
            mGameName = (TextView) bannerView.findViewById(R.id.game_name);
            mShadowView = bannerView.findViewById(R.id.shadow_view);
            leftLine = bannerView.findViewById(R.id.line_left);
            rightLine = bannerView.findViewById(R.id.line_right);
            mRootLayout = (RelativeLayout) bannerView.findViewById(R.id.root_layout);

            mRootLayout.getLayoutParams().height = bannerHeight;
            mRootLayout.requestLayout();

            BannerEntity entity = bannerList.get(i);
            if (TextUtils.isEmpty(entity.smallTitle) || entity.smallTitle.equals("null")) {
                leftLine.setVisibility(INVISIBLE);
                rightLine.setVisibility(INVISIBLE);
                mGameName.setText("");
                mShadowView.setVisibility(GONE);
            } else {
                leftLine.setVisibility(VISIBLE);
                rightLine.setVisibility(VISIBLE);
                mGameName.setText(entity.smallTitle);
                mShadowView.setVisibility(VISIBLE);
            }
            if (TextUtils.isEmpty(entity.title) || entity.title.equals("null")) {
                mBannerTitle.setText("");
            } else {
                mBannerTitle.setText(entity.title);
            }

            Glide.with(getContext()).load(entity.cover).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(mImageBanner);
            imageViewList.add(bannerView);
        }

        //监听图片轮播，改变指示器状态
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
                pos = pos % pointSize;
                currrentPos = pos;
                for (int i = 0; i < points.getChildCount(); i++) {
                    points.getChildAt(i).setBackgroundResource(unSelcetRes);
                    points.getChildAt(i).setLayoutParams(paramsNormal);
                }
                points.getChildAt(pos).setBackgroundResource(selectRes);
                points.getChildAt(pos).setLayoutParams(paramsSelect);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (isStopScroll) {
                            startScroll();
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        stopScroll();
                        compositeSubscription.dispose();
                        break;
                }
            }
        });

        BannerAdapter bannerAdapter = new BannerAdapter(imageViewList);
        viewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        bannerAdapter.setmViewPagerOnItemClickListener(this);

        //图片开始轮播
        startScroll();
    }


    private boolean isStopScroll = false;


    /**
     * 图片开始轮播
     */
    private void startScroll() {
        compositeSubscription = new CompositeDisposable();
        isStopScroll = false;
        Disposable subscription = Observable.timer(delayTime, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (isStopScroll) {
                        return;
                    }
                    isStopScroll = true;
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                });
        compositeSubscription.add(subscription);
    }


    /**
     * 图片停止轮播
     */
    private void stopScroll() {
        isStopScroll = true;
    }

    public void destory() {
        if (compositeSubscription != null) {
            compositeSubscription.dispose();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 设置ViewPager的Item点击回调事件
     */
    @Override
    public void onItemClick(int position) {
        if (mViewPagerOnItemClickListener != null) {
            mViewPagerOnItemClickListener.onItemClick(currrentPos, bannerList.get(currrentPos));
        }
    }

    private ViewPagerOnItemClickListener mViewPagerOnItemClickListener;

    public void setOnItemClickListener(ViewPagerOnItemClickListener mViewPagerOnItemClickListener) {
        this.mViewPagerOnItemClickListener = mViewPagerOnItemClickListener;
    }

    public interface ViewPagerOnItemClickListener {
        void onItemClick(int position, BannerEntity entity);
    }

}
