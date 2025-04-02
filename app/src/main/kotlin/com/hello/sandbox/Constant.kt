package com.hello.sandbox

import com.hello.sandbox.util.EnvUtil
import top.niunaijun.blackboxa.BuildConfig

class Constant {
  companion object {

    val URL_APP_PRIVACY = "file:///android_asset/web/user-protocol/index.html"
    val URL_APP_AGREEMENT = "file:///android_asset/web/user-protocol/protocol.html"
    val URL_APP_QUESTION = "file:///android_asset/web/question/index.html"
    val URL_APP_FEEDBACK = "file:///android_asset/web/question/feedback.html"

    val DOWNLOAD_DIR_NAME = "DownLoadApks"

    const val RECOMMEND_TANTAN_APP_URL = "https://sj.qq.com/appdetail/com.p1.mobile.putong"
    const val RECOMMEND_MOMO_APP_URL = "https://sj.qq.com/appdetail/com.immomo.momo"

    const val IS_STAGING = "isStaging"
    val UPGRADE_URL =
      if (EnvUtil.isStaging) BuildConfig.upgrade_url_debug else BuildConfig.upgrade_url_release
    val APP_ID = if (EnvUtil.isStaging) BuildConfig.app_id_debug else BuildConfig.app_id_release
    val APP_SECRET =
      if (EnvUtil.isStaging) BuildConfig.app_secret_debug else BuildConfig.app_secret_release
    val APP_TYPE = "mihebox"
    // 埋点相关
    const val SCHEME_NAME = "vboxapp"

    // 埋点eventName
    const val UPDATEPOPUP_PV = "updatePopup_pv"
    const val SCREENOVER_SUC = "screenOver_suc"
    const val SCREEN_OVER_STATUS = "screen_over_status"
    const val CHANGEICON_SUC = "changeIcon_suc"
    const val CHANGE_ICON_ID = "change_icon_id"
    const val SETPASSWORD_SUC = "setPassword_suc"
    const val SET_PASSWORD_SUCCESS = "set_password_success"
    const val REGISTER_SUC = "register_suc"
    const val HOMEPAGE_MV = "homepage_mv"
    const val MY_MV = "my_mv"
    const val OPEN_APP_MC = "open_app_mc"
    const val OPEN_APP_MC_NAME = "open_app_mc_name"
    const val ADD_APP_MID_MC = "add_app_mid_mc"
    const val ADD_APP_SUCCESS_MC = "add_app_success"
    const val ADD_APP_SUCCESS_NAME = "add_app_success_name"
    const val UNINSTALL_APP_MC = "uninstall_app_mc"
    const val UNINSTALL_APP_NAME = "uninstall_app_name"
  }
}
