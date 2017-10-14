package com.appgame.differ.widget.dialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.appgame.differ.R;
import com.appgame.differ.bean.daily.DailyComment;
import com.appgame.differ.bean.guest.UserGuest;
import com.appgame.differ.data.constants.AppConstants;
import com.appgame.differ.data.db.UserInfoManager;
import com.appgame.differ.utils.CommonUtil;
import com.appgame.differ.utils.LogUtil;
import com.appgame.differ.utils.SpUtil;
import com.appgame.differ.utils.ToastUtil;
import com.appgame.differ.utils.network.AppGameResponseError;
import com.appgame.differ.utils.network.ErrorActionAppGame;
import com.appgame.differ.utils.network.RetrofitHelper;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by lzx on 2017/6/7.
 */

public class DynamicDialog extends RxBaseDialog implements View.OnClickListener {

    public static DynamicDialog newInstance(String content, String dynamicId, DailyComment comment, int position, String target) {
        DynamicDialog dynamicDialog = new DynamicDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        bundle.putString("dynamicId", dynamicId);
        bundle.putString("target", target);
        bundle.putInt("position", position);
        bundle.putParcelable("comment", comment);
        dynamicDialog.setArguments(bundle);
        return dynamicDialog;
    }

    public static DynamicDialog newInstance(String content, UserGuest guest, int position, String target) {
        DynamicDialog dynamicDialog = new DynamicDialog();
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        bundle.putString("target", target);
        bundle.putParcelable("UserGuest", guest);
        bundle.putInt("position", position);
        dynamicDialog.setArguments(bundle);
        return dynamicDialog;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_dynamic;
    }

    private TextView mBtnReply, mBtnCopy, mBtnReport;
    private String content = "";
    private String dynamicId = "";
    private String target;
    private int position;
    private DailyComment mDailyComment;
    private OnReplayListener mOnReplayListener;
    private OnDeleteSuccessListener mOnDeleteSuccessListener;
    private UserGuest mUserGuest;

    @Override
    protected void init(Bundle savedInstanceState) {
        mBtnReply = (TextView) findViewById(R.id.btn_reply);
        mBtnCopy = (TextView) findViewById(R.id.btn_copy);
        mBtnReport = (TextView) findViewById(R.id.btn_report);
        mBtnReply.setOnClickListener(this);
        mBtnCopy.setOnClickListener(this);
        mBtnReport.setOnClickListener(this);

        content = getArguments().getString("content");
        mDailyComment = getArguments().getParcelable("comment");
        dynamicId = getArguments().getString("dynamicId");
        target = getArguments().getString("target");
        position = getArguments().getInt("position");
        mUserGuest = getArguments().getParcelable("UserGuest");

        if (CommonUtil.isLogin()) {
            //String userId = UserInfoManager.getImpl().getUserInfo().getUserId();
            String userId = UserInfoManager.getImpl().getUserId();
            String currUserId;
            if (mUserGuest == null) {
                currUserId = mDailyComment.getRelationships().getUserId();
                LogUtil.i("currUserId1 = " + currUserId);
            } else {
                currUserId = mUserGuest.getAuthor().getUserId();
                LogUtil.i("currUserId2 = " + currUserId);
            }
            if (userId.equals(currUserId)) {
                mBtnReport.setText("删除");
            }
        }

        if (mUserGuest != null) {
            dynamicId = mUserGuest.getId();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnReplayListener = (OnReplayListener) getActivity();
        mOnDeleteSuccessListener = (OnDeleteSuccessListener) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reply:
                if (mOnReplayListener != null) {
                    mOnReplayListener.replay(mDailyComment, mUserGuest);
                }
                break;
            case R.id.btn_copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", !TextUtils.isEmpty(content) ? content : "");
                cm.setPrimaryClip(mClipData);
                ToastUtil.showShort("已复制到粘贴板");
                dismiss();
                break;
            case R.id.btn_report:
                if (mBtnReport.getText().equals("举报")) {

                    LogUtil.i("dynamicId = " + dynamicId + "  target = " + target);

                    ReportDialog reportListFragment = ReportDialog.newInstance(dynamicId, target);
                    reportListFragment.show(getFragmentManager(), "reportFragment");
                    dismiss();
                } else {
                    if (mUserGuest != null) {
                        deleteGuest(mUserGuest.getId());
                    } else {
                        deleteComment(mDailyComment.getComId());
                    }
                }
                break;
        }
    }

    private void deleteComment(String commendId) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        RetrofitHelper.getAppGameAPI().commentsDelete(token, commendId, CommonUtil.getExtraParam())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    int status = jsonObject.getJSONObject("meta").getInt("status");
                    if (status == 200) {
                        if (mOnDeleteSuccessListener != null) {
                            mOnDeleteSuccessListener.OnDeleteCommentSuccess(position);
                        }
                    } else {
                        ToastUtil.showShort("删除失败");
                    }
                    dismiss();
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        ToastUtil.showShort(error.getTitle());
                        dismiss();
                    }
                });
    }

    public void deleteGuest(String id) {
        String token = SpUtil.getInstance().getString(AppConstants.ACCESS_TOKEN);
        RetrofitHelper.getAppGameAPI().guestDelete(token, id, CommonUtil.getExtraParam())
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        int status = jsonObject.getJSONObject("meta").getInt("status");
                        if (status == 200) {
                            if (mOnDeleteSuccessListener != null) {
                                mOnDeleteSuccessListener.OnDeleteCommentSuccess(position);
                            }
                        } else {
                            ToastUtil.showShort("删除失败");
                        }
                        dismiss();
                    }
                }, new ErrorActionAppGame() {
                    @Override
                    public void call(AppGameResponseError error) {
                        ToastUtil.showShort(error.getTitle());
                        dismiss();
                    }
                });
    }

    public interface OnReplayListener {
        void replay(DailyComment comment, UserGuest userGuest);
    }

    public interface OnDeleteSuccessListener {
        void OnDeleteCommentSuccess(int position);
    }

}
