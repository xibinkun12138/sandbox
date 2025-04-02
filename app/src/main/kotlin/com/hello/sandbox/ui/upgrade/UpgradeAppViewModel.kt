package com.hello.sandbox.ui.upgrade

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.hello.sandbox.Constant.Companion.APP_ID
import com.hello.sandbox.Constant.Companion.APP_SECRET
import com.hello.sandbox.Constant.Companion.UPGRADE_URL
import com.hello.sandbox.common.util.PackageUtil
import com.hello.sandbox.network.HttpUtil
import com.hello.sandbox.network.gson.GsonUtils
import com.hello.sandbox.util.EncryptUtil
import com.hello.sandbox.util.Network
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.view.base.BaseViewModel

class UpgradeAppViewModel : BaseViewModel() {
  val upgradeModel = MutableLiveData<UpgradeInfo>()

  fun checkAppUpgradeInfo() {
    launchOnUI { getData() }
  }

  private fun getData() {
    HttpUtil.postWithCallback(
      Network.getOkHttpClient(),
      UPGRADE_URL,
      RequestInfo(
        APP_ID,
        PackageUtil.getVersionCode(App.mContext),
        System.currentTimeMillis() / 1000,
        generateSign()
      ),
      null,
      { convertResult(it) },
      { e: Exception -> {} }
    )
  }

  private fun generateSign(): String {
    val appId = APP_ID
    val appSecret = APP_SECRET
    val timeStamp = System.currentTimeMillis() / 1000
    val versionCode = PackageUtil.getVersionCode(App.mContext)
    return EncryptUtil.getSHA256(appId + appSecret + timeStamp + versionCode)
  }

  private fun convertResult(str: String?) {
    if (TextUtils.isEmpty(str)) {
      return
    }
    var upgradeResult: UpgradeResult? = null
    try {
      upgradeResult = GsonUtils.fromJson(str, UpgradeResult::class.java)
      if (upgradeResult != null) {
        upgradeModel.postValue(upgradeResult.data)
      }
    } catch (e: Exception) {}
  }
}
