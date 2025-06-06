package top.niunaijun.blackboxa.view.main

import android.app.Application
import android.content.Context
import android.util.Log
import com.hello.sandbox.SandBoxCore
import com.hello.sandbox.app.BActivityThread
import com.hello.sandbox.app.configuration.AppLifecycleCallback
import com.hello.sandbox.app.configuration.ClientConfiguration
import java.io.File
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.biz.cache.AppSharedPreferenceDelegate

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/6 23:38
 */
class SandBoxLoader {

  private var mHideRoot by AppSharedPreferenceDelegate(App.getContext(), false)
  private var mHideXposed by AppSharedPreferenceDelegate(App.getContext(), false)
  private var mDaemonEnable by AppSharedPreferenceDelegate(App.getContext(), false)
  private var mShowShortcutPermissionDialog by AppSharedPreferenceDelegate(App.getContext(), true)

  fun hideRoot(): Boolean {
    return mHideRoot
  }

  fun invalidHideRoot(hideRoot: Boolean) {
    this.mHideRoot = hideRoot
  }

  fun hideXposed(): Boolean {
    return mHideXposed
  }

  fun invalidHideXposed(hideXposed: Boolean) {
    this.mHideXposed = hideXposed
  }

  fun daemonEnable(): Boolean {
    return mDaemonEnable
  }

  fun invalidDaemonEnable(enable: Boolean) {
    this.mDaemonEnable = enable
  }

  fun showShortcutPermissionDialog(): Boolean {
    return mShowShortcutPermissionDialog
  }

  fun invalidShortcutPermissionDialog(show: Boolean) {
    this.mShowShortcutPermissionDialog = show
  }

  fun getSandBoxCore(): SandBoxCore {
    return SandBoxCore.get()
  }

  fun addLifecycleCallback() {
    SandBoxCore.get()
      .addAppLifecycleCallback(
        object : AppLifecycleCallback() {
          override fun beforeCreateApplication(
            packageName: String?,
            processName: String?,
            context: Context?,
            userId: Int
          ) {
            Log.d(
              TAG,
              "beforeCreateApplication: pkg $packageName, processName $processName,userID:${BActivityThread.getUserId()}"
            )
          }

          override fun beforeApplicationOnCreate(
            packageName: String?,
            processName: String?,
            application: Application?,
            userId: Int
          ) {
            Log.d(TAG, "beforeApplicationOnCreate: pkg $packageName, processName $processName")
          }

          override fun afterApplicationOnCreate(
            packageName: String?,
            processName: String?,
            application: Application?,
            userId: Int
          ) {
            Log.d(TAG, "afterApplicationOnCreate: pkg $packageName, processName $processName")
            //                RockerManager.init(application,userId)
          }
        }
      )
  }

  fun attachBaseContext(context: Context) {
    SandBoxCore.get()
      .doAttachBaseContext(
        context,
        object : ClientConfiguration() {
          override fun getHostPackageName(): String {
            return context.packageName
          }

          override fun isHideRoot(): Boolean {
            return mHideRoot
          }

          override fun isHideXposed(): Boolean {
            return mHideXposed
          }

          override fun isEnableDaemonService(): Boolean {
            return mDaemonEnable
          }

          override fun requestInstallPackage(file: File?, userId: Int): Boolean {
            return false
          }
        }
      )
  }

  fun doOnCreate(context: Context) {
    SandBoxCore.get().doCreate()
  }

  companion object {

    val TAG: String = SandBoxLoader::class.java.simpleName
  }
}
