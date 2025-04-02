package com.hello.sandbox.ui.appIcon

import android.content.Intent
import com.hello.sandbox.ui.cala.CalcActivity
import com.hello.sandbox.ui.splash.SplashAct
import com.hello.sandbox.user.UserUtils

class FakeSplashAct : SplashAct() {
  override fun jump() {
    if (!UserUtils.hasSetPassword()) {
      super.jump()
    } else {
      startActivity(Intent(this, CalcActivity::class.java))
      finish()
    }
  }
}
