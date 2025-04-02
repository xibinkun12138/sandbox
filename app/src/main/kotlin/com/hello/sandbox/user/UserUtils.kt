package com.hello.sandbox.user

import android.app.Activity
import android.text.TextUtils
import com.hello.sandbox.ui.home.HomeAct
import com.hello.sandbox.ui.password.SettingPasswordActivity
import com.hello.sandbox.util.SharedPrefUtils
import kotlin.random.Random
import top.niunaijun.blackboxa.app.App

class UserUtils {
  companion object {
    const val KEY_SP_USERID = "user_id"

    fun isLogin(): Boolean {
      return !TextUtils.isEmpty(UserManager.instance.getUserId())
    }

    fun hasSetPassword(): Boolean {
      return !TextUtils.isEmpty(
        SharedPrefUtils.getStringData(App.mContext, SettingPasswordActivity.APP_PASSWORD_KEY)
      )
    }

    fun loginAndJumpHomeAct(activity: Activity) {
      var info = UserLoginInfo(Random.nextInt(Int.MAX_VALUE / 100).toString())
      UserManager.instance.fullLogin(info)
      App.app.initAfterConfirmPolicy()
      HomeAct.start(activity)
      activity.finish()
    }
  }
}
