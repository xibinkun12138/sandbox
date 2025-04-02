package com.hello.sandbox.ui.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.common.ui.Toast
import com.hello.sandbox.util.singleClickListener
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityScreenOrientationBinding
import top.niunaijun.blackboxa.util.inflate

class ScreenOrientationActivity : AppCompatActivity() {
  private val viewBinding: ActivityScreenOrientationBinding by inflate()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
    viewBinding.vnNavigationbar.setLeftIconOnClick { finish() }
    var open = ScreenOrientationHelper.isSwitchOpen(this)
    if (open) {
      viewBinding.btnOpenIcon.text = getString(R.string.screen_close)
    } else {
      viewBinding.btnOpenIcon.text = getString(R.string.screen_open)
    }
    viewBinding.btnOpenIcon.singleClickListener {
      if (open) {
        open = false
        viewBinding.btnOpenIcon.text = getString(R.string.screen_open)
        ScreenOrientationHelper.instance.close()
        Toast.message(getString(R.string.screen_close_message))
      } else {
        viewBinding.btnOpenIcon.text = getString(R.string.screen_close)
        open = true
        ScreenOrientationHelper.instance.open()
        Toast.message(getString(R.string.screen_open_message))
      }
      ScreenOrientationHelper.updateSwitch(this, open)
    }
  }
}
