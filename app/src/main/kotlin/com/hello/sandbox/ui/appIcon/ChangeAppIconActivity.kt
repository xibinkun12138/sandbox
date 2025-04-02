package com.hello.sandbox.ui.appIcon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.util.singleClickListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import top.niunaijun.blackboxa.databinding.ActivityChangeAppIconBinding
import top.niunaijun.blackboxa.util.inflate

class ChangeAppIconActivity : AppCompatActivity() {
  private val viewBinding: ActivityChangeAppIconBinding by inflate()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
    viewBinding.vnNavigationbar.setLeftIconOnClick { finish() }
    viewBinding.btnChangeAppIcon.singleClickListener {
      val changeAppIconPopup = ChangeAppIconPopup(this)
      XPopup.Builder(this)
        .moveUpToKeyboard(false)
        .maxWidth(XPopupUtils.getScreenWidth(this) - XPopupUtils.dp2px(this, 40f))
        .isDestroyOnDismiss(true)
        .dismissOnTouchOutside(false)
        .dismissOnBackPressed(false)
        .asCustom(this.let { changeAppIconPopup })
        .show()
    }
  }
}
