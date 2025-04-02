package com.hello.sandbox.util

import com.hello.sandbox.Constant.Companion.IS_STAGING
import top.niunaijun.blackboxa.BuildConfig
import top.niunaijun.blackboxa.app.App

class EnvUtil {
  companion object {
    var isStaging = getEnv()

    /** 之后可以提供切换staging环境的选项 */
    fun switchEnv(switch: Boolean) {
      isStaging = switch
      SharedPrefUtils.saveData(App.mContext, IS_STAGING, switch)
    }

    private fun getEnv(): Boolean {
      return SharedPrefUtils.getBooleanWithDefault(App.mContext, IS_STAGING, BuildConfig.DEBUG)
    }
  }
}
