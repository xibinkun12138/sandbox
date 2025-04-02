package com.hello.sandbox.ui.upgrade

import android.content.Context
import android.text.TextUtils
import com.hello.sandbox.common.util.PackageUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import top.niunaijun.blackboxa.app.App

class UpgradeChecker {
  companion object {
    fun hasNewVersion(info: UpgradeInfo?): Boolean {
      if (info == null || TextUtils.isEmpty(info.downloadUrl())) {
        return false
      }
      if (info.newVersionCode > PackageUtil.getVersionCode(App.mContext)) return true
      return false
    }

    fun showAppUpgradePopUp(context: Context, upgradeInfo: UpgradeInfo) {
      val appUpgradePopup = AppUpgradePopup(context, upgradeInfo)
      XPopup.Builder(context)
        .moveUpToKeyboard(false)
        .maxWidth(XPopupUtils.getScreenWidth(context) - XPopupUtils.dp2px(context, 40f))
        .isDestroyOnDismiss(true)
        .dismissOnTouchOutside(false)
        .dismissOnBackPressed(false)
        .asCustom(this.let { appUpgradePopup })
        .show()
    }
  }
}
