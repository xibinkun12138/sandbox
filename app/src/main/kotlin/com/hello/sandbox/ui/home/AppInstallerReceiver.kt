package com.hello.sandbox.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppInstallerReceiver(var listener: AppInstallerListener) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    var action = intent?.action
    var data = intent?.data
    if (action == Intent.ACTION_PACKAGE_REMOVED || action == Intent.ACTION_PACKAGE_ADDED) {
      listener.onAppInstallOrUnInstall(action, data)
    }
  }
}
