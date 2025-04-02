package com.hello.sandbox.ui.guide

import android.content.Context
import com.hello.sandbox.util.SharedPrefUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils

object GuideHelper {
  private const val KEY = "has_show_hide_guide_dlg"

  fun isNeedShowGuideDlg(context: Context): Boolean {
    return SharedPrefUtils.getBooleanWithDefault(context, KEY, true)
  }

  fun updateShowGuide(context: Context, needShow: Boolean) {
    SharedPrefUtils.saveData(context, KEY, needShow)
  }

  fun showGuideDlg(context: Context) {
    val hideGuidePopup = HideGuidePopup(context)
    XPopup.Builder(context)
      .maxHeight(XPopupUtils.getScreenHeight(context) * 3 / 4)
      .moveUpToKeyboard(false)
      .isDestroyOnDismiss(true)
      .dismissOnTouchOutside(true)
      .dismissOnBackPressed(true)
      .asCustom(this.let { hideGuidePopup })
      .show()
  }
}
