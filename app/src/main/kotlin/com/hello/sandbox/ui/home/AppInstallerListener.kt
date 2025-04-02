package com.hello.sandbox.ui.home

import android.net.Uri

interface AppInstallerListener {
  fun onAppInstallOrUnInstall(action: String, data: Uri?)
}
