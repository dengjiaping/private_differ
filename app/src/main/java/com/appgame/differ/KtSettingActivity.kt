package com.appgame.differ

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.appgame.differ.base.BaseActivity
import com.appgame.differ.base.mvp.BaseContract
import com.appgame.differ.bean.VersionInfo
import com.appgame.differ.data.constants.AppConstants
import com.appgame.differ.data.constants.EvenConstant
import com.appgame.differ.data.db.UserInfoManager
import com.appgame.differ.module.MainActivity
import com.appgame.differ.utils.CommonUtil
import com.appgame.differ.utils.LoginUtil
import com.appgame.differ.utils.SpUtil
import com.appgame.differ.utils.ToastUtil
import com.appgame.differ.utils.rx.RxBus
import com.appgame.differ.widget.dialog.OutLoginDialog
import com.appgame.differ.widget.dialog.ShareDialog
import com.appgame.differ.widget.dialog.VersionDialog
import com.meituan.android.walle.WalleChannelReader
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.message.IUmengCallback
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.activity_setting.*
import okhttp3.ResponseBody

class KtSettingActivity : BaseActivity<BaseContract.BasePresenter<String>, String>(), View.OnClickListener {

    var mVersionDialog: VersionDialog? = null
    var mRxPermissions: RxPermissions = RxPermissions(this)
    var mVersionInfo: VersionInfo? = null
    var isNeedToUpdate = false

    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initWidget() {
        super.initWidget()
        curr_ver.text = "当前版本:" + CommonUtil.getVersionName(this)

        btn_outlogin.visibility = if (CommonUtil.isLogin()) View.VISIBLE else View.GONE
        btn_outlogin.setOnClickListener(this)
        curr_ver.setOnClickListener(this)
        new_ver.setOnClickListener(this)
        btn_share.setOnClickListener(this)

        val isOpenPush = SpUtil.getInstance().getBoolean(AppConstants.IS_OPEN_PUSH, true)
        val isDeletePkg = SpUtil.getInstance().getBoolean(AppConstants.IS_DELETE_PKG, true)
        slide_switch_pull.isChecked = isOpenPush
        slide_switch_pkg.isChecked = isDeletePkg
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        slide_switch_pkg.setOnCheckedChangeListener({ _, isChecked ->
            mRxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)?.subscribe { aBoolean ->
                if (aBoolean) {
                    SpUtil.getInstance().putBoolean(AppConstants.IS_DELETE_PKG, isChecked)
                } else {
                    ToastUtil.showShort("权限拒绝，操作失败")
                }
            }
        })
        slide_switch_pull.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                mPushAgent.enable(object : IUmengCallback {
                    override fun onSuccess() {}

                    override fun onFailure(s: String, s1: String) {}
                })
            } else {
                mPushAgent.disable(object : IUmengCallback {
                    override fun onSuccess() {}

                    override fun onFailure(s: String, s1: String) {}
                })
            }
        })

        checkVersion()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_outlogin -> {
                val dialog = OutLoginDialog(this)
                dialog.show()
                dialog.setListener(object : OutLoginDialog.OnOutLoginListener {
                    override fun onYes() {
                        LoginUtil.getImpl().clearLoginInfo()
                        RxBus.getBus().send(EvenConstant.KEY_LOGIN_OUT)
                        startActivity(Intent(this@KtSettingActivity, MainActivity::class.java))
                        finish()
                    }

                    override fun onNo() {

                    }
                })
            }
            R.id.curr_ver -> {
                if (mVersionInfo == null) {
                    ToastUtil.showShort("检查更新失败")
                } else {
                    if (isNeedToUpdate) {
                        mVersionDialog = VersionDialog(this@KtSettingActivity, mVersionInfo, mRxPermissions, supportFragmentManager)
                        mVersionDialog?.show()
                    } else {
                        ToastUtil.showShort("已是最新版本")
                    }
                }
            }
            R.id.btn_share -> {
                //String channel = BuildConfig.FLAVOR;
                val channel = WalleChannelReader.getChannel(this)
                val shareDialog = ShareDialog()
                val bundle = Bundle()
                bundle.putString("shareType", "setting")
                bundle.putString("shareImage", "")
                bundle.putBoolean("isShareToDynamic", false)
                bundle.putBoolean("isHideCardUI", true)
                bundle.putString("shareTitle", getString(R.string.share_title))

                val userId = UserInfoManager.getImpl().userId

                if (!TextUtils.isEmpty(userId)) {
                    bundle.putString("shareUrl", AppConstants.SHARE_URL + "h5/download?user_id=" + userId + "&channel=" + channel)
                } else {
                    bundle.putString("shareUrl", AppConstants.SHARE_URL + "h5/download?channel=" + channel)
                }

                shareDialog.arguments = bundle
                val ft = supportFragmentManager.beginTransaction()
                ft.add(shareDialog, "ShareDialog")
                ft.commitAllowingStateLoss()
            }
        }
    }

    /**
     * 检查版本更新
     */
    private fun checkVersion() {
        CommonUtil.checkVersion(this, this.bindToLifecycle<ResponseBody>(), object : CommonUtil.OnCheckVersionListener {
            override fun onSuccess(mVersionInfo: VersionInfo, isNeedToUpdate: Boolean) {
                this@KtSettingActivity.mVersionInfo = mVersionInfo
                this@KtSettingActivity.isNeedToUpdate = isNeedToUpdate
            }

            override fun onFailure() {
                this@KtSettingActivity.isNeedToUpdate = false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        UMShareAPI.get(this).release()
    }

}
