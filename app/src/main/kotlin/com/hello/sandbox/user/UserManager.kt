package com.hello.sandbox.user

import com.hello.sandbox.util.*
import top.niunaijun.blackboxa.app.App

class UserManager private constructor() {
  companion object {
    val instance: UserManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { UserManager() }
  }

  private var userId: String? = null

  fun fullLogin(info: UserLoginInfo) {
    userId = info.userId
    SharedPrefUtils.saveData(App.mContext, UserUtils.KEY_SP_USERID, info.userId)
    refreshUserInfo()
  }

  private fun refreshUserInfo() {
    userId = SharedPrefUtils.getStringData(App.mContext, UserUtils.KEY_SP_USERID)
  }

  fun getUserId(): String {
    if (userId == null) {
      userId = SharedPrefUtils.getStringData(App.mContext, UserUtils.KEY_SP_USERID)
    }
    return userId!!
  }

  fun logout() {
    SharedPrefUtils.clear()
    userId = ""
  }
}
