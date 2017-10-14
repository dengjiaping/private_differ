package com.appgame.differ.widget.dialog;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.DailyInfo;
import com.appgame.differ.bean.dynamic.DynamicInfo;
import com.appgame.differ.bean.evaluation.UserAppraise;
import com.appgame.differ.bean.game.GameInfo;
import com.appgame.differ.bean.game.SimpleGame;
import com.appgame.differ.bean.home.RecommedInfo;
import com.appgame.differ.module.dynamic.PostDynamicActivity;
import com.appgame.differ.module.login.view.LoginActivity;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.FileUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lzx on 2017/7/4.
 */

public class ShareDialog extends RxBaseDialog implements View.OnClickListener {

    private TextView mShareCard, mShareWeChat, mShareFriends, mShareSina, mShareQQ, mShareQzone, mShareLine, mShareDynamic;
    private TextView mShareTitleView;

    private View mParentLayout;

    private String shareImage;
    private String shareTitle;
    private String defaultShareTitle = "下载differ，和我一起畅玩精品游戏";
    private String defaultShareText = "发掘游戏乐趣，推荐个性爆棚的游戏和内涵丰富的文章，更有无数同好者圈子分享游戏趣事、新鲜事。";
    private String shareText;
    private String shareUrl;
    private String shareThumb = null;

    private UserAppraise mUserAppraise;
    private GameInfo mGameInfo;
    private DailyInfo mDailyInfo;
    private DynamicInfo mDynamicInfo;
    private boolean isShareToDynamic;
    private boolean isHideCardUI;
    private boolean isHideCopyLineUI;
    private boolean isHideSina;
    private String shareType;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_share_detail;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        shareImage = getArguments().getString("shareImage");
        shareTitle = getArguments().getString("shareTitle");
        shareText = getArguments().getString("shareText");
        shareUrl = getArguments().getString("shareUrl");
        shareType = getArguments().getString("shareType");
        mUserAppraise = getArguments().getParcelable("userAppraise");
        mGameInfo = getArguments().getParcelable("gameInfo");
        mDailyInfo = getArguments().getParcelable("dailyInfo");
        mDynamicInfo = getArguments().getParcelable("dynamicInfo");
        isShareToDynamic = getArguments().getBoolean("isShareToDynamic", false);
        isHideCardUI = getArguments().getBoolean("isHideCardUI", false);
        isHideCopyLineUI = getArguments().getBoolean("isHideCopyLineUI", false);
        isHideSina = getArguments().getBoolean("isHideSina", false);

        mShareCard = (TextView) findViewById(R.id.share_card);
        mShareWeChat = (TextView) findViewById(R.id.share_wechat);
        mShareFriends = (TextView) findViewById(R.id.share_friends);
        mShareSina = (TextView) findViewById(R.id.share_sina);
        mShareQQ = (TextView) findViewById(R.id.share_qq);
        mShareQzone = (TextView) findViewById(R.id.share_qzone);
        mShareLine = (TextView) findViewById(R.id.share_line);
        mShareTitleView = (TextView) findViewById(R.id.share_title_view);
        mShareDynamic = (TextView) findViewById(R.id.share_dynamic);
        mParentLayout = findViewById(R.id.parent_ll);
        mParentLayout.getBackground().setAlpha(70);

        mShareTitleView.setVisibility(isShareToDynamic ? View.VISIBLE : View.GONE);
        mShareDynamic.setVisibility(isShareToDynamic ? View.VISIBLE : View.GONE);
        mShareCard.setVisibility(isHideCardUI ? View.GONE : View.VISIBLE);
        mShareLine.setVisibility(isHideCopyLineUI ? View.GONE : View.VISIBLE);
        mShareSina.setVisibility(isHideSina ? View.GONE : View.VISIBLE);

        initShareThumb();

        mShareCard.setOnClickListener(this);
        mShareWeChat.setOnClickListener(this);
        mShareFriends.setOnClickListener(this);
        mShareSina.setOnClickListener(this);
        mShareQQ.setOnClickListener(this);
        mShareQzone.setOnClickListener(this);
        mShareLine.setOnClickListener(this);
        mShareDynamic.setOnClickListener(this);
        mParentLayout.setOnClickListener(this);


    }


    private void initShareThumb() {
        if (mGameInfo != null) {
            shareThumb = TextUtils.isEmpty(mGameInfo.getCover()) ? mGameInfo.getIcon() : mGameInfo.getCover();
        } else if (mDailyInfo != null) {
            shareThumb = mDailyInfo.getCover();
        } else if (mDynamicInfo != null) {
            String cover;
            if (mDynamicInfo.getImages().size() == 0) {
                if (mDynamicInfo.getGameInfo() != null) {
                    cover = TextUtils.isEmpty(mDynamicInfo.getGameInfo().getCover()) ? mDynamicInfo.getGameInfo().getIcon() : mDynamicInfo.getGameInfo().getCover();
                } else {
                    cover = mDynamicInfo.getArticle().getCover();
                }
            } else {
                cover = mDynamicInfo.getImages().get(0);
            }
            shareThumb = cover;
        } else if (shareType.equals("comment") && mUserAppraise != null) {
            shareThumb = TextUtils.isEmpty(mGameInfo.getCover()) ? mGameInfo.getIcon() : mGameInfo.getCover();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_card:
                shareToCard();
                break;
            case R.id.share_wechat:
                shareToWeChat();
                break;
            case R.id.share_friends:
                shareToFriends();
                break;
            case R.id.share_sina:
                shareToSina();
                break;
            case R.id.share_qq:
                shareToQQ();
                break;
            case R.id.share_qzone:
                shareToQzone();
                break;
            case R.id.share_line:
                shareToLine();
                break;
            case R.id.parent_ll:
                dismiss();
                break;
            case R.id.share_dynamic:
                if (CommonUtil.isLogin()) {
                    shareDynamic();
                } else {
                    LoginActivity.launch(getActivity(), "GameDetailActivity");
                }
                dismiss();
                break;
        }
    }


    /**
     * 卡片分享
     */
    private void shareToCard() {
        ShareCardDialog dialog = new ShareCardDialog();
        Bundle bundle = new Bundle();
        bundle.putString("shareType", shareType);
        bundle.putParcelable("gameInfo", mGameInfo);
        bundle.putParcelable("dailyInfo", mDailyInfo);
        bundle.putParcelable("dynamicInfo", mDynamicInfo);
        bundle.putParcelable("userAppraise", mUserAppraise);
        bundle.putBoolean("isShareToDynamic", isShareToDynamic);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "ShareCardDialog");
        dismiss();
    }

    /**
     * 分享到动态
     */
    private void shareDynamic() {
        if (mGameInfo != null) {
            if (CommonUtil.isLogin()) {
                SimpleGame simpleGame = new SimpleGame();
                simpleGame.setGameNameCn(mGameInfo.getGameNameCn());
                simpleGame.setGameIcon(mGameInfo.getIcon());
                simpleGame.setGameId(mGameInfo.getGameId());
                simpleGame.setTagsInfos(mGameInfo.getTags());
                Intent intent = new Intent(getActivity(), PostDynamicActivity.class);
                intent.putExtra("postType", "post");
                intent.putExtra("target", "dynamic");
                intent.putExtra("targetId", "0");
                intent.putExtra("game", simpleGame);
                startActivity(intent);
            } else {
                LoginActivity.launch(getActivity(), "GameDetailActivity");
            }
        } else if (mDynamicInfo != null) {
            if (CommonUtil.isLogin()) {
                RecommedInfo game = mDynamicInfo.getGameInfo();
                SimpleGame simpleGame = new SimpleGame();
                simpleGame.setGameId(game != null ? game.getGameId() : mDynamicInfo.getArticle().getId());
                simpleGame.setGameNameCn(game != null ? game.getGameNameCn() : "");
                simpleGame.setGameIcon(game != null ? game.getIcon() : "");
                simpleGame.setTagsInfos(game != null ? game.getTags() : new ArrayList<>());
                Intent intent = new Intent(getActivity(), PostDynamicActivity.class);
                intent.putExtra("dynamic_id", mDynamicInfo.getIsForward().equals("0") ? mDynamicInfo.getId() : mDynamicInfo.getForward().getId());
                intent.putExtra("postType", "share");
                String target, targetId;
                if (mDynamicInfo.getTarget().equals("article")) {
                    target = "article";
                    targetId = mDynamicInfo.getTargetId();
                } else if (mDynamicInfo.getTarget().equals("appraise")) {
                    target = "appraise";
                    targetId = mDynamicInfo.getTargetId();
                } else {
                    target = "dynamic";
                    targetId = "0";
                }
                intent.putExtra("target", target);
                intent.putExtra("targetId", targetId);
                String shareContent;
                if (mDynamicInfo.getIsForward().equals("0")) {
                    shareContent = "";
                } else {
                    shareContent = "//@#Forwarding#<font color=\"#15B1B8\">@" + mDynamicInfo.getAuthor().getNickName() + "</font>:" + mDynamicInfo.getContent();
                }
                intent.putExtra("shareContent", shareContent);
                intent.putExtra("game", simpleGame);
                startActivity(intent);
            } else {
                LoginActivity.launch(getActivity(), "DynamicDetailActivity");
            }
        }
        dismiss();
    }

    /**
     * 复制链接
     */
    private void shareToLine() {
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", shareUrl);
        cm.setPrimaryClip(mClipData);
        ToastUtil.showShort("已复制到粘贴板");
        dismiss();
    }

    /**
     * 分享到qq
     */
    private void shareToQQ() {
        ShareAction action = new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.QQ);
        if (!TextUtils.isEmpty(shareImage)) {
            UMImage image = new UMImage(getActivity(), new File(shareImage));
            image.compressStyle = UMImage.CompressStyle.SCALE;
            action.withMedia(image);
            action.withMedia(image).setCallback(umShareListener).share();
        } else {
            switch (shareType) {
                case "article":
                    shareTitle = mDailyInfo.getTitle();
                    shareText = "来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    break;
                case "game":
                case "comment":
                    shareTitle = mGameInfo.getGameNameCn() + "—繁华世界 只玩不同";
                    shareText = mGameInfo.getRecommendReason();
                    break;
                case "setting":
                    shareTitle = defaultShareTitle;
                    shareText = defaultShareText;
                    break;
            }
            UMImage umImage;
            if (!TextUtils.isEmpty(shareThumb)) {
                umImage = new UMImage(getActivity(), shareThumb);
            } else {
                umImage = new UMImage(getActivity(), R.mipmap.ic_launcher);
            }
            UMWeb web = new UMWeb(shareUrl);
            web.setTitle(shareTitle);//标题
            web.setThumb(umImage);
            web.setDescription(TextUtils.isEmpty(shareText) ? defaultShareText : shareText);//描述
            action.withMedia(web).setCallback(umShareListener).share();
        }
    }

    /**
     * 分享到QQ空间
     */
    private void shareToQzone() {
        LogUtil.i("shareImage = " + shareImage);
        ShareAction action = new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.QZONE);
        if (!TextUtils.isEmpty(shareImage)) {
            File file = new File(shareImage);
            LogUtil.i("file = " + file.exists());
            UMImage image = new UMImage(getActivity(), file);//本地文件
            image.compressStyle = UMImage.CompressStyle.SCALE;
            action.withMedia(image);
            action.withMedia(image).setCallback(umShareListener).share();
        } else {
            switch (shareType) {
                case "article":
                    shareTitle = mDailyInfo.getTitle() + "—来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    //shareText = "来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    break;
                case "game":
                case "comment":
                    shareTitle = mGameInfo.getGameNameCn() + "—繁华世界 只玩不同";
                    shareText = mGameInfo.getRecommendReason();
                    break;
                case "setting":
                    shareTitle = defaultShareTitle;
                    shareText = defaultShareText;
                    break;
            }
            UMImage umImage;
            if (!TextUtils.isEmpty(shareThumb)) {
                umImage = new UMImage(getActivity(), shareThumb);
            } else {
                umImage = new UMImage(getActivity(), R.mipmap.ic_launcher);
            }
            UMWeb web = new UMWeb(shareUrl);
            web.setTitle(shareTitle);//标题
            web.setThumb(umImage);
            web.setDescription(TextUtils.isEmpty(shareText) ? defaultShareText : shareText);//描述
            action.withMedia(web).setCallback(umShareListener).share();
        }
    }

    /**
     * 分享到微信
     */
    private void shareToWeChat() {
        ShareAction action = new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN);
        if (!TextUtils.isEmpty(shareImage)) {
            UMImage image = new UMImage(getActivity(), new File(shareImage));//本地文件
            image.compressStyle = UMImage.CompressStyle.SCALE;
            action.withMedia(image).setCallback(umShareListener).share();
        } else {
            switch (shareType) {
                case "article":
                    shareTitle = mDailyInfo.getTitle();
                    shareText = "来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    break;
                case "game":
                case "comment":
                    shareTitle = mGameInfo.getGameNameCn() + "—繁华世界 只玩不同";
                    shareText = mGameInfo.getRecommendReason();
                    break;
                case "setting":
                    shareTitle = defaultShareTitle;
                    shareText = defaultShareText;
                    break;
            }
            UMImage umImage;
            if (!TextUtils.isEmpty(shareThumb)) {
                umImage = new UMImage(getActivity(), shareThumb);
            } else {
                umImage = new UMImage(getActivity(), R.mipmap.ic_launcher);
            }
            UMWeb web = new UMWeb(shareUrl);
            web.setTitle(shareTitle);//标题
            web.setDescription(TextUtils.isEmpty(shareText) ? defaultShareText : shareText);//描述
            web.setThumb(umImage);
            action.withMedia(web).setCallback(umShareListener).share();
        }
    }

    /**
     * 分享到朋友圈
     */
    private void shareToFriends() {
        ShareAction action = new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
        if (!TextUtils.isEmpty(shareImage)) {
            UMImage image = new UMImage(getActivity(), new File(shareImage));//本地文件
            image.compressStyle = UMImage.CompressStyle.SCALE;
            action.withMedia(image);
            action.setCallback(umShareListener).share();
        } else {
            switch (shareType) {
                case "article":
                    shareTitle = mDailyInfo.getTitle() + "——来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    // shareText = "——来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    break;
                case "game":
                case "comment":
                    shareTitle = mGameInfo.getGameNameCn() + "——" + mGameInfo.getRecommendReason();
                    //shareText = mGameInfo.getRecommendReason();
                    break;
                case "setting":
                    shareTitle = defaultShareTitle;
                    shareText = defaultShareText;
                    break;
            }
            UMImage umImage;
            if (!TextUtils.isEmpty(shareThumb)) {
                umImage = new UMImage(getActivity(), shareThumb);
            } else {
                umImage = new UMImage(getActivity(), R.mipmap.ic_launcher);
            }
            UMWeb web = new UMWeb(shareUrl);
            web.setThumb(umImage);
            web.setTitle(shareTitle);//标题
            web.setDescription(TextUtils.isEmpty(shareText) ? defaultShareText : shareText);//描述
            action.withMedia(web).setCallback(umShareListener).share();
        }
    }

    /**
     * 分享到新浪
     */
    private void shareToSina() {
        ShareAction action = new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.SINA);
        if (!TextUtils.isEmpty(shareImage)) {
            UMImage image = new UMImage(getActivity(), new File(shareImage));//本地文件
            image.compressStyle = UMImage.CompressStyle.SCALE;
            image.isLoadImgByCompress = true;
            action.withMedia(image);
            action.setCallback(umShareListener).share();
        } else {
            switch (shareType) {
                case "article":
                    shareTitle = mDailyInfo.getTitle() + "——来自" + mDailyInfo.getUser().getNickName() + "的文章_differ " + shareUrl;
                    //shareText = "——来自" + mDailyInfo.getUser().getNickName() + "的文章";
                    break;
                case "game":
                case "comment":
                    shareTitle = mGameInfo.getGameNameCn() + "——" + mGameInfo.getRecommendReason() + "_differ " + shareUrl;
                    //shareText = mGameInfo.getRecommendReason();
                    break;
                case "setting":
                    shareTitle = defaultShareTitle + "_differ " + shareUrl;
                    //shareText = defaultShareText;
                    break;
            }
            action.withText(TextUtils.isEmpty(shareText) ? shareTitle : shareText).setCallback(umShareListener).share();
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            ToastUtil.showShort("分享成功");
            FileUtil.deleteImage(FileUtil.getImagePath() + "/share_temp.png");
            dismissAllowingStateLoss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            ToastUtil.showShort("分享失败");
            FileUtil.deleteImage(FileUtil.getImagePath() + "/share_temp.png");
            t.printStackTrace();
            dismissAllowingStateLoss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            //ToastUtil.showShort("分享取消");
            FileUtil.deleteImage(FileUtil.getImagePath() + "/share_temp.png");
            dismissAllowingStateLoss();
        }
    };

}
