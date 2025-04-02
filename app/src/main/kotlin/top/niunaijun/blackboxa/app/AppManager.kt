package top.niunaijun.blackboxa.app

import android.content.Context
import android.content.SharedPreferences
import top.niunaijun.blackboxa.view.main.SandBoxLoader

object AppManager {
  @JvmStatic val mSandBoxLoader by lazy { SandBoxLoader() }

  @JvmStatic val mSandBoxCore by lazy { mSandBoxLoader.getSandBoxCore() }

  @JvmStatic
  val mRemarkSharedPreferences: SharedPreferences by lazy {
    App.getContext().getSharedPreferences("UserRemark", Context.MODE_PRIVATE)
  }

  fun doAttachBaseContext(context: Context) {
    try {
      mSandBoxLoader.attachBaseContext(context)
      mSandBoxLoader.addLifecycleCallback()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun doOnCreate(context: Context) {
    mSandBoxLoader.doOnCreate(context)
    initThirdService(context)
  }

  private fun initThirdService(context: Context) {}
}
