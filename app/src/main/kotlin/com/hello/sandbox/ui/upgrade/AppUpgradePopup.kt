package com.hello.sandbox.ui.upgrade

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import com.hello.sandbox.Constant
import com.hello.sandbox.util.DownloadHelper
import com.lxj.xpopup.core.CenterPopupView
import com.hello.sandbox.common.util.ViewUtil
import java.io.File
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.App

class AppUpgradePopup(context: Context, var upgradeInfo: UpgradeInfo) : CenterPopupView(context) {

  override fun getImplLayoutId(): Int {
    return R.layout.app_upgrade_popup
  }

  lateinit var textView: TextView

  override fun onCreate() {
    super.onCreate()
    textView = findViewById(R.id.content)
    textView.text = upgradeInfo.toast
    val agreeButton = findViewById<Button>(R.id.btn_agree)
    val imgClose = findViewById<View>(R.id.img_close)
    ViewUtil.singleClickListener(imgClose) { dismiss() }
    ViewUtil.singleClickListener(agreeButton) {
      upgradeInfo.downloadUrl()?.let {
        dismiss()
        downloadApkAction(App.mContext, upgradeInfo)
      }
    }
    val disAgreeButton = findViewById<TextView>(R.id.tv_disagree)
    ViewUtil.singleClickListener(disAgreeButton) { dismiss() }
  }

  private fun downloadApkAction(context: Context, info: UpgradeInfo) {
    var file =
      File(
        context.getExternalFilesDir(null)!!.absolutePath +
          "/" +
          Constant.DOWNLOAD_DIR_NAME +
          "/miheapp_" +
          info.newVersionCode +
          ".apk"
      )
    DownloadHelper.download(context, info.downloadUrl()!!, file.absolutePath) {
      kotlin.run { installApk(context, file) }
    }
  }

  fun installApk(context: Context, file: File) {
    try {
      val promptInstall =
        Intent()
          .setAction(Intent.ACTION_VIEW)
          .setDataAndType(
            getInstallApkUri(context, file),
            "application/vnd.android.package-archive"
          )
          .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        promptInstall.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      context.startActivity(promptInstall)
    } catch (e: Exception) {}
  }

  private fun getInstallApkUri(context: Context, file: File): Uri? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      FileProvider.getUriForFile(context, "files." + context.packageName, file)
    } else {
      Uri.fromFile(file)
    }
  }
}
