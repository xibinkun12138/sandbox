package com.hello.sandbox.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.common.Au
import com.hello.sandbox.ui.cala.CalcActivity
import com.hello.sandbox.ui.home.HomeAct
import com.hello.sandbox.ui.screen.ScreenOrientationHelper
import com.hello.sandbox.user.UserUtils

open class SplashAct : AppCompatActivity() {

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val open = ScreenOrientationHelper.isSwitchOpen(this)
    if (open) {
      ScreenOrientationHelper.instance.open()
    }
    if (UserUtils.isLogin() && UserUtils.hasSetPassword()) {
      CalcActivity.start(this, !isTaskRoot)
      finish()
    } else {
      if (isTaskRoot) {
        Au.postDelayed(this, { jump() }, 1000L)
      } else {
        finish()
      }
    }
  }

  open fun jump() {
    if (UserUtils.isLogin()) {
      HomeAct.start(this)
    } else {
      LoginAct.start(this)
    }
    finish()
  }
}
