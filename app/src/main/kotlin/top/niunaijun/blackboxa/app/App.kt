package top.niunaijun.blackboxa.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.hello.sandbox.SandBoxCore
import com.hello.sandbox.app.configuration.AppLifecycleCallback
import com.hello.sandbox.common.AppFrontBackManager
import com.hello.sandbox.core.system.notification.NotificationChannelManager
import com.hello.sandbox.fake.delegate.AppInstrumentation
import com.hello.sandbox.ui.appIcon.FakeSplashAct
import com.hello.sandbox.ui.cala.CalcActivity
import com.hello.sandbox.ui.screen.ScreenOrientationHelper
import com.hello.sandbox.ui.splash.PrivacyPolicyHelper
import com.hello.sandbox.ui.splash.SplashAct
import com.hello.sandbox.user.UserUtils
import com.hello.sandbox.common.util.UtilSDk

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:21
 */
class App : Application(), Application.ActivityLifecycleCallbacks {
  private val runningActivities = mutableListOf<Activity>()
  companion object {
    lateinit var app: App
    var needShowPrivacyPolicyDlg: Boolean = false
    @SuppressLint("StaticFieldLeak") @Volatile lateinit var mContext: Context

    @JvmStatic
    fun getContext(): Context {
      return mContext
    }
  }

  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    mContext = base!!
    ScreenOrientationHelper.instance.init(this)
    AppManager.doAttachBaseContext(base)
  }

  override fun onCreate() {
    super.onCreate()
    app = this
    AppManager.doOnCreate(mContext)
    UtilSDk.init(mContext, true, true)
    runInMainProcess {
      needShowPrivacyPolicyDlg = PrivacyPolicyHelper.isNeedShowPrivacyPolicyDlg(this)
      initFrontBackListener()
      if (!needShowPrivacyPolicyDlg) {
        initAfterConfirmPolicy()
      }
    }
    if (SandBoxCore.get().isBlackProcess) {
      // 测试使用，方便调试， 暂时不删除
      // Thread.sleep(10000)
      AppInstrumentation.get().fixRifleHook()
      ScreenOrientationHelper.instance.registerScreenFlippedListener()
      SandBoxCore.get()
        .addAppLifecycleCallback(
          object : AppLifecycleCallback() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
              runningActivities.add(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
              runningActivities.remove(activity)
            }
          }
        )
    }
    registerActivityLifecycleCallbacks(this)
  }

  fun initAfterConfirmPolicy() {
    SandBoxCore.get().initNotificationManager()
    NotificationChannelManager.get().registerAppChannel()
  }

  private fun runInMainProcess(function: () -> Unit) {
    if (SandBoxCore.get().isMainProcess) {
      function.invoke()
    }
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    runningActivities.add(activity)
  }

  override fun onActivityStarted(activity: Activity) {}

  override fun onActivityResumed(activity: Activity) {}

  override fun onActivityPaused(activity: Activity) {}

  override fun onActivityStopped(activity: Activity) {}

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

  override fun onActivityDestroyed(activity: Activity) {
    runningActivities.remove(activity)
  }

  public fun finishAllActivity() {
    runningActivities.toTypedArray().forEach { it.finishAndRemoveTask() }
    runningActivities.clear()
    SandBoxCore.get().stopPackage(packageName, 0)
    android.os.Process.killProcess(android.os.Process.myPid())
    System.exit(0)
  }

  private fun initFrontBackListener() {
    AppFrontBackManager.getInstance()
      .addListener(
        object : AppFrontBackManager.OnAppStatusListener {
          override fun onFront(activity: Activity) {
            if (activity is CalcActivity || activity is FakeSplashAct || activity is SplashAct) {
              return
            }
            if (UserUtils.isLogin() && UserUtils.hasSetPassword()) {
              CalcActivity.start(activity, true)
            }
          }

          override fun onBack() {}
        }
      )
  }
}
