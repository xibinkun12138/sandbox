package com.hello.sandbox.ui.splash

import android.content.Context
import com.hello.sandbox.util.SharedPrefUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import top.niunaijun.blackboxa.app.App

object PrivacyPolicyHelper {

  private const val KEY = "has_show_privacy_policy_dlg"

  fun isNeedShowPrivacyPolicyDlg(context: Context): Boolean {
    return SharedPrefUtils.getBooleanWithDefault(context, KEY, true)
  }

  fun updateShowPrivacyPolicy(context: Context, needShow: Boolean) {
    SharedPrefUtils.saveData(context, KEY, needShow)
  }

  fun showPrivacyPop(context: Context, confirm: Runnable, cancel: Runnable) {
    val privacyPolicyPopup = PrivacyPolicyPopup(context)
    XPopup.Builder(context)
      .moveUpToKeyboard(false)
      .maxWidth(XPopupUtils.getScreenWidth(context) - XPopupUtils.dp2px(context, 40f))
      .isDestroyOnDismiss(true)
      .dismissOnTouchOutside(false)
      .dismissOnBackPressed(false)
      .asCustom(this.let { privacyPolicyPopup })
      .show()
    privacyPolicyPopup.afterAgree = Runnable {
      updateShowPrivacyPolicy(App.mContext, false)
      confirm.run()
    }
    privacyPolicyPopup.disAgree = Runnable {
      cancel.run()
      privacyPolicyPopup.dismiss()
    }
  }
}
