package com.hello.sandbox.ui.screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.hello.sandbox.common.util.PackageUtil
import com.hello.sandbox.util.SharedPrefUtils
import top.niunaijun.blackboxa.app.App
import top.niunaijun.blackboxa.screen.IScreenOrientation
import top.niunaijun.blackboxa.screen.ScreenFlippedListener

class ScreenOrientationHelper : ServiceConnection {
  private var app: App? = null
  private var screenOrientationService: IScreenOrientation? = null
  private var runActions = mutableListOf<Runnable>()

  companion object {
    private const val KEY = "screen_flipped_switch"

    fun isSwitchOpen(context: Context): Boolean {
      return SharedPrefUtils.getBooleanWithDefault(context, KEY, false)
    }

    fun updateSwitch(context: Context, openStatus: Boolean) {
      SharedPrefUtils.saveData(context, KEY, openStatus)
    }
    val instance: ScreenOrientationHelper by
      lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { ScreenOrientationHelper() }
  }

  public fun init(app: App) {
    this.app = app
  }

  fun registerScreenFlippedListener() {
    bindService {
      screenOrientationService?.registerScreenFlippedListener(
        PackageUtil.currentProcessName(),
        object : ScreenFlippedListener.Stub() {
          override fun onFlipped() {
            app?.finishAllActivity()
          }
        }
      )
    }
  }

  fun open() {
    bindService {
      screenOrientationService?.open()
      registerScreenFlippedListener()
    }
  }

  fun close() {
    bindService { screenOrientationService?.close() }
  }

  private fun bindService(runAction: Runnable) {
    if (screenOrientationService == null) {
      val intent = Intent(app, ScreenOrientationService::class.java)
      val success = app?.bindService(intent, this, Context.BIND_AUTO_CREATE)
      runActions.add(runAction)
    } else {
      runAction.run()
    }
  }

  override fun onServiceConnected(name: ComponentName, service: IBinder) {
    screenOrientationService = IScreenOrientation.Stub.asInterface(service)
    runActions.forEach { runAction -> runAction.run() }
    runActions.clear()
  }

  override fun onServiceDisconnected(name: ComponentName?) {
    screenOrientationService = null
    runActions.clear()
  }
}
