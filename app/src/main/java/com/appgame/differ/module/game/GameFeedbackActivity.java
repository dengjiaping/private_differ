package com.appgame.differ.module.game;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.base.BaseActivity;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.KeyboardControlManager;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GameFeedbackActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private EditText mEditComm;
    private TextView mBtnComment;
    private FeedBackAdapter mFeedBackAdapter;
    private NestedScrollView mNestedScrollView;
    private String game_id;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_feedback;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        game_id = getIntent().getStringExtra("game_id");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEditComm = (EditText) findViewById(R.id.edit_comm);
        mBtnComment = (TextView) findViewById(R.id.btn_comment);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);
        mFeedBackAdapter = new FeedBackAdapter(this);
        mRecyclerView.setAdapter(mFeedBackAdapter);

        getFeedBackType();

        KeyboardControlManager.observerKeyboardVisibleChange(this, (keyboardHeight, isVisible) -> {
            mNestedScrollView.fullScroll(isVisible ? View.FOCUS_DOWN : View.FOCUS_UP);
        });

        mBtnComment.setEnabled(false);
        mBtnComment.setBackgroundResource(R.drawable.bg_gray_corner2);

        mFeedBackAdapter.setItemClickListener((type, list) -> {
            for (FeedBackType mFeedBackType : list) {
                mFeedBackType.isSelect = false;
            }
            type.isSelect = true;
            mFeedBackAdapter.notifyDataSetChanged();
            mBtnComment.setEnabled(true);
            mBtnComment.setBackgroundResource(R.drawable.bg_green_corner_click);
        });

        mBtnComment.setOnClickListener(this::sumbitFeedBack);
    }

    private void getFeedBackType() {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        RetrofitHelper.getAppGameAPI().getFeedbacksTypes(token,CommonUtil.getExtraParam())
                .compose(bindToLifecycle())
                .map(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    List<FeedBackType> list = new ArrayList<FeedBackType>();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        FeedBackType type = new FeedBackType();
                        JSONObject object = jsonArray.getJSONObject(i);
                        type.id = object.optString("id");
                        type.name = object.getJSONObject("attributes").getString("name");
                        type.isSelect = false;
                        list.add(type);
                    }
                    return list;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feedBackTypes -> mFeedBackAdapter.setList(feedBackTypes), new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        ToastUtil.showShort(error.getTitle());
                    }
                });
    }


    private void sumbitFeedBack(View view) {
        String content = mEditComm.getText().toString().trim();
        String typeId = null;
        for (FeedBackType mFeedBackType : mFeedBackAdapter.getList()) {
            if (mFeedBackType.isSelect) {
                typeId = mFeedBackType.id;
                break;
            }
        }
        if (!TextUtils.isEmpty(typeId)) {
            CommonUtil.HideKeyboard(mBtnComment);
            String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
            Map<String, String> map = new HashMap<>();
            map.put("game_id", game_id);
            map.put("access_token", token);
            map.put("content", content);
            map.put("type_id", typeId);
            map.put("extra", CommonUtil.getExtraParam());
            RetrofitHelper.getAppGameAPI().postFeedbacks(map)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        int status = jsonObject.getJSONObject("meta").getInt("status");
                        String massage = jsonObject.getJSONObject("meta").getString("message");
                        if (status == 200 && massage.equals("success")) {
                            ToastUtil.showShort("反馈成功");
                            mEditComm.setText("");
                            finish();
                        }
                    }, new ErrorActionAppGame() {
                        @Override
                        public void call(AppGameResponseError error) {
                            ToastUtil.showShort(error.getTitle());
                        }
                    });
        } else {
            ToastUtil.showShort("请选择反馈类型");
        }
    }


    private class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.FeedBackHolder> {
        private Context mContext;
        private List<FeedBackType> list;
        private OnItemClickListener mItemClickListener;
        private RelativeLayout.LayoutParams mBigParams;
        private RelativeLayout.LayoutParams mSmallParams;


        public FeedBackAdapter(Context context) {
            mContext = context;
            list = new ArrayList<>();
            mBigParams = new RelativeLayout.LayoutParams(CommonUtil.dip2px(mContext, 12), CommonUtil.dip2px(mContext, 12));
            mSmallParams = new RelativeLayout.LayoutParams(CommonUtil.dip2px(mContext, 8), CommonUtil.dip2px(mContext, 8));
            mBigParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mSmallParams.addRule(RelativeLayout.CENTER_VERTICAL);
        }

        public void setList(List<FeedBackType> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        @Override
        public FeedBackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);
            return new FeedBackHolder(view);
        }

        @Override
        public void onBindViewHolder(FeedBackHolder holder, int position) {
            FeedBackType type = list.get(position);
            holder.mTextReason.setText(type.name);
            holder.mIconImage.setImageResource(type.isSelect ? R.drawable.bg_green_round : R.drawable.bg_frame_gray_round);
            holder.mIconImage.setLayoutParams(type.isSelect ? mBigParams : mSmallParams);
            holder.itemView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.OnClick(type, list);
                }
            });
        }

        public List<FeedBackType> getList() {
            return list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class FeedBackHolder extends RecyclerView.ViewHolder {
            ImageView mIconImage;
            TextView mTextReason;

            public FeedBackHolder(View itemView) {
                super(itemView);
                mIconImage = (ImageView) itemView.findViewById(R.id.icon_image);
                mTextReason = (TextView) itemView.findViewById(R.id.text_reason);
            }
        }
    }

    public interface OnItemClickListener {
        void OnClick(FeedBackType type, List<FeedBackType> list);
    }

    class FeedBackType {
        public String id;
        public String name;
        public boolean isSelect;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KeyboardControlManager.clear();
    }
}
