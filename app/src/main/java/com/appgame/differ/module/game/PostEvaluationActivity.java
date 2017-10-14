package com.appgame.differ.module.game;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.constants.EvenConstant;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.appgame.differ.utils.rx.RxBus;
import com.appgame.differ.widget.star.StarView;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PostEvaluationActivity extends BaseActivity {

    private NestedScrollView mNestedScrollView;
    private StarView mRatingBar;
    private TextView mDescribe;
    private EditText mEvaEditText;
    private TagFlowLayout mTagFlowLayout;
    private LinearLayout mPostLayout;
    private EditText mTagContent;
    private TextView mBtnSend;

    private String gameId;
    private String content;
    private String star;
    private String id;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_evaluation;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollview);
        mEvaEditText = (EditText) findViewById(R.id.content);
        mRatingBar = (StarView) findViewById(R.id.ratingBar);
        mDescribe = (TextView) findViewById(R.id.describe);
        mTagFlowLayout = (TagFlowLayout) findViewById(R.id.tag_flow_layout);

        mPostLayout = (LinearLayout) findViewById(R.id.post_layout);
        mTagContent = (EditText) findViewById(R.id.tag_content);
        mBtnSend = (TextView) findViewById(R.id.btn_send);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        gameId = getIntent().getStringExtra("game_id");
        content = getIntent().getStringExtra("content");
        star = getIntent().getStringExtra("star");
        id = getIntent().getStringExtra("id");

        if (!TextUtils.isEmpty(star)) {
            mRatingBar.setStarMark(Float.parseFloat(star));
            mRatingBar.setIndicate(true);
            mEvaEditText.setText(content);
            float mark = Float.parseFloat(star);
            initMark(mark / 2);
        } else {
            mRatingBar.setStarMark(2);
        }

        setBarRightTextUI("发布", v -> commitEvaluation());

        mRatingBar.setIntegerMark(true);
        mRatingBar.setOnStarChangeListener(this::initMark);

        mEvaEditText.setOnTouchListener((v, event) -> {
            switch (v.getId()) {
                case R.id.content:
                    // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
            }
            return false;
        });
    }

    private void initMark(float mark) {
        if (mark == 1.0f) {
            mDescribe.setText(getResources().getString(R.string.star1_hint));
        } else if (mark == 2.0f) {
            mDescribe.setText(getResources().getString(R.string.star2_hint));
        } else if (mark == 3.0f) {
            mDescribe.setText(getResources().getString(R.string.star3_hint));
        } else if (mark == 4.0f) {
            mDescribe.setText(getResources().getString(R.string.star4_hint));
        } else if (mark == 5.0f) {
            mDescribe.setText(getResources().getString(R.string.star5_hint));
        }
    }


    private void commitEvaluation() {
        if (mEvaEditText.getText().toString().trim().length() == 0) {
            ToastUtil.showShort(getString(R.string.game_evaluation_empty));
        } else if (mEvaEditText.getText().toString().trim().length() < 3) {
            ToastUtil.showShort(getString(R.string.game_evaluation_little));
        } else if (mEvaEditText.getText().toString().trim().length() > 2000) {
            ToastUtil.showShort(getString(R.string.game_evaluation_large));
        } else {
            String access_token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
            Map<String, String> map = new HashMap<>();
            map.put("game_id", gameId);
            map.put("access_token", access_token);
            // map.put("tags", json);
            map.put("star", mRatingBar.getStarMark() * 2 + "");
            map.put("content", mEvaEditText.getText().toString().trim());
            map.put("appraise_id", TextUtils.isEmpty(id) ? "" : id);
            map.put("extra", CommonUtil.getExtraParam());
            RetrofitHelper.getAppGameAPI().submitGameComment(map)
                    .compose(this.<ResponseBody>bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        int status = jsonObject.getJSONObject("meta").optInt("status", -1);
                        if (status == 200) {
                            RxBus.getBus().send(EvenConstant.KEY_REFRESH_GAMES_COMMENTS);
                            ToastUtil.showShort("评价成功");
                            finish();
                        } else {
                            ToastUtil.showShort("评价失败");
                        }
                        CommonUtil.HideKeyboard(mEvaEditText);
                    }, new ErrorActionAppGame() {
                        @Override
                        public void call(AppGameResponseError error) {
                            ToastUtil.showShort(error.getTitle());
                        }
                    });
        }
    }
}
