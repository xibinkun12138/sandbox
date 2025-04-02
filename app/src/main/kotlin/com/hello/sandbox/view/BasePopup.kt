package com.hello.sandbox.view

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.lxj.xpopup.core.CenterPopupView
import com.hello.sandbox.common.util.ViewUtil
import top.niunaijun.blackboxa.R

class BasePopup(
  context: Context,
  var name: String,
  var description: String?,
  var confirmRunnable: Runnable?,
  var cancelRunnable: Runnable?,
  private var confirm: String,
  private var cancel: String,
  private var needShowClose: Boolean = true
) : CenterPopupView(context) {

  override fun getImplLayoutId(): Int {
    return R.layout.popup_base_layout
  }

  private lateinit var textTitle: TextView
  private lateinit var textDescription: TextView
  private lateinit var confirmButton: Button
  private lateinit var cancelButton: TextView
  private lateinit var closeImg: ImageView

  override fun onCreate() {
    super.onCreate()
    textTitle = findViewById(R.id.title)
    textDescription = findViewById(R.id.description)
    confirmButton = findViewById(R.id.btn_confirm)
    cancelButton = findViewById(R.id.btn_cancel)
    closeImg = findViewById(R.id.img_close)
    if (!needShowClose) {
      closeImg.visibility = INVISIBLE
    }
    textTitle.text = name
    textDescription.text = description
    confirmButton.text = confirm
    cancelButton.text = cancel
    ViewUtil.singleClickListener(confirmButton) {
      confirmRunnable?.run()
      dismiss()
    }
    ViewUtil.singleClickListener(cancelButton) {
      cancelRunnable?.run()
      dismiss()
    }
    ViewUtil.singleClickListener(closeImg) { dismiss() }
  }
}
