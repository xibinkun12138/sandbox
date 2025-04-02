package com.hello.sandbox.ui.search

import android.app.Activity
import android.graphics.drawable.Drawable
import com.hello.sandbox.view.HandleAppPopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import top.niunaijun.blackboxa.R

object InstallPromptPopup {
  fun showConfirmPopup(
    act: Activity,
    name: String,
    icon: Drawable,
    confirm: Runnable,
    cancel: Runnable
  ) {
    var handleAppPopup =
      HandleAppPopup(
        act,
        name,
        icon,
        { confirm.run() },
        { cancel.run() },
        act.getString(R.string.install_description, name),
        act.getString(R.string.install_confirm),
        act.getString(R.string.install_concel)
      )
    XPopup.Builder(act)
      .moveUpToKeyboard(false)
      .maxWidth(XPopupUtils.getScreenWidth(act) - XPopupUtils.dp2px(act, 40f))
      .isDestroyOnDismiss(true)
      .dismissOnTouchOutside(false)
      .dismissOnBackPressed(false)
      .asCustom(this.let { handleAppPopup })
      .show()
  }
}
