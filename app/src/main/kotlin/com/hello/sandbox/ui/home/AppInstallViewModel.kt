package com.hello.sandbox.ui.home

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.view.base.BaseViewModel

class AppInstallViewModel : BaseViewModel(), AppInstallerListener {
  val appinstallOrUninstallData = MutableLiveData<String>()

  private var installerReceiver: AppInstallerReceiver = AppInstallerReceiver(this)
  private lateinit var context: Context

  fun registerReceiver(context: Context) {
    this.context = context
    val filter = IntentFilter()
    filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
    filter.addAction(Intent.ACTION_PACKAGE_ADDED)
    filter.addDataScheme("package")
    this.context.registerReceiver(installerReceiver, filter)
  }

  override fun onAppInstallOrUnInstall(action: String, data: Uri?) {
    appinstallOrUninstallData.postValue(action)
  }

  fun unregister() {
    context?.unregisterReceiver(installerReceiver)
  }
}
