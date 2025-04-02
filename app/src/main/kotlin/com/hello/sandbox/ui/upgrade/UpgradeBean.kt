package com.hello.sandbox.ui.upgrade

import androidx.annotation.Keep
import top.niunaijun.blackboxa.BuildConfig

@Keep
data class UpgradeInfo(
  var url: String?,
  var toast: String?,
  var newVersion: String?,
  var newVersionCode: Long,
  var extra: Map<String, String>
) {
  fun downloadUrl() =
    if (BuildConfig.APP_ARME_ABI_TYPE == 64) {
      url
    } else {
      if (extra == null) {
        ""
      } else {
        extra["url_32"] ?: ""
      }
    }
}

@Keep
class RequestInfo(
  var appId: String?,
  var versionCode: Int?,
  var timestamp: Long?,
  var sign: String
)

@Keep class Meta(var code: Int?, var message: String?)

@Keep data class UpgradeResult(var meta: Meta, var data: UpgradeInfo?)
