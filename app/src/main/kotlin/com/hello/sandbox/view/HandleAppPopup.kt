package com.hello.sandbox.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.hello.sandbox.common.util.ViewUtil
import com.lxj.xpopup.core.CenterPopupView
import top.niunaijun.blackboxa.R

class HandleAppPopup(
  context: Context,
  var name: String,
  var icon: Drawable,
  var agree: Runnable?,
  var disAgree: Runnable?,
  var description: String?,
  var confirm: String,
  var cancel: String
) : CenterPopupView(context) {

  override fun getImplLayoutId(): Int {
    return R.layout.app_popup
  }

  lateinit var textName: TextView
  lateinit var textDescription: TextView
  lateinit var textConfirm: TextView
  lateinit var textCancel: TextView
  lateinit var appIcon: ImageView

  override fun onCreate() {
    super.onCreate()
    textName = findViewById(R.id.name)
    appIcon = findViewById(R.id.icon)
    textDescription = findViewById(R.id.description)
    textConfirm = findViewById(R.id.confirm)
    textCancel = findViewById(R.id.cancel)
    textName.text = name
    textDescription.text = description
    appIcon.setImageDrawable(icon)
    textConfirm.text = confirm
    textCancel.text = cancel
    ViewUtil.singleClickListener(textConfirm) {
      agree?.run()
      dismiss()
    }
    ViewUtil.singleClickListener(textCancel) {
      disAgree?.run()
      dismiss()
    }

    ViewUtil.singleClickListener(findViewById(R.id.img_close)) { dismiss() }
  }
}
