package com.hello.sandbox.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.hello.sandbox.Constant.Companion.DOWNLOAD_DIR_NAME
import java.io.File

object DownloadHelper {
  fun download(context: Context, downloadUrl: String, sourceDir: String, afterDownload: Runnable?) {
    val apkFile = File(sourceDir)
    if (apkFile.exists()) {
      afterDownload?.run()
      return
    }
    val dir = File(apkFile.parent!!)
    if (!dir.exists()) {
      dir.mkdirs()
    }
    val downloadManager: DownloadManager =
      context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(downloadUrl))
    val subPath = sourceDir.substring(sourceDir.lastIndexOf('/') + 1)
    try {
      request.setDestinationInExternalFilesDir(context, DOWNLOAD_DIR_NAME, subPath)
    } catch (e: Exception) {
      e.printStackTrace()
    }

    request.setDestinationUri(Uri.fromFile(apkFile))
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setTitle("正在下载")
    //    request.setDescription("Open when finish download!")
    request.setMimeType("application/vnd.android.package-archive")
    val dlid = downloadManager.enqueue(request)

    val intentFilter = IntentFilter()
    intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
    val broadcastReceiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
          if (dlid == id) {
            afterDownload?.run()
            context.unregisterReceiver(this)
          }
        }
      }

    context.registerReceiver(broadcastReceiver, intentFilter)
  }
}
