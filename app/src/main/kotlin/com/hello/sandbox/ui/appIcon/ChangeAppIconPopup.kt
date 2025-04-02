package com.hello.sandbox.ui.appIcon

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.hello.sandbox.common.ui.Toast
import com.hello.sandbox.common.util.MetricsUtil
import com.hello.sandbox.ui.password.SettingPasswordActivity
import com.hello.sandbox.ui.splash.SplashAct
import com.hello.sandbox.util.SharedPrefUtils
import com.hello.sandbox.util.singleClickListener
import com.lxj.xpopup.core.CenterPopupView
import top.niunaijun.blackboxa.R
import v.VButton
import v.VText

class ChangeAppIconPopup(context: Context) : CenterPopupView(context) {

  private lateinit var icDefaultItem: ItemView
  private lateinit var icFakeItem: ItemView

  override fun getImplLayoutId(): Int {
    return R.layout.activity_change_app_icon_popu
  }

  override fun onCreate() {
    super.onCreate()
    val appIcon = SharedPrefUtils.getStringData(context, APP_ICON_KEY)

    findViewById<View>(R.id.img_close).singleClickListener { dismiss() }
    val icDefault = findViewById<View>(R.id.ic_default)
    icDefault.singleClickListener {
      icDefaultItem.selected()
      icFakeItem.unSelected()
    }
    val icFake = findViewById<View>(R.id.ic_fake)
    icFake.singleClickListener {
      icDefaultItem.unSelected()
      icFakeItem.selected()
    }
    icDefaultItem = ItemView(icDefault, R.drawable.splash_icon, "初始")
    icFakeItem = ItemView(icFake, R.drawable.icon_fake, "计算器")
    val lp = icFakeItem.itemView.layoutParams as LinearLayout.LayoutParams
    lp.leftMargin = MetricsUtil.DP_10
    icFakeItem.itemView.layoutParams = lp
    if (appIcon == APP_ICON_DEFAULT || appIcon == "") {
      icDefaultItem.selected()
      icFakeItem.unSelected()
    } else {
      icDefaultItem.unSelected()
      icFakeItem.selected()
    }
    findViewById<VButton>(R.id.btn_change_app_icon).singleClickListener {
      if (icDefaultItem.isSelected() && appIcon != APP_ICON_DEFAULT) {
        SharedPrefUtils.saveData(context, APP_ICON_KEY, APP_ICON_DEFAULT)
        setDefaultAlias()
        dismiss()
        Toast.message("正在替换桌面图标")
      } else if (icFakeItem.isSelected() && appIcon != APP_ICON_FAKE) {
        val appPassWord =
          SharedPrefUtils.getStringData(context, SettingPasswordActivity.APP_PASSWORD_KEY)
        if (appPassWord.isNullOrEmpty()) {
          Toast.message(context.getString(R.string.change_app_icon_toast))
          context.startActivity(Intent(context, SettingPasswordActivity::class.java))
        } else {
          SharedPrefUtils.saveData(context, APP_ICON_KEY, APP_ICON_FAKE)
          setFakeSplash()
          dismiss()
          Toast.message("正在替换桌面图标 隐藏密码为$appPassWord")
        }
      }
    }
  }

  private companion object {
    const val APP_ICON_KEY = "app_icon_key"
    const val APP_ICON_DEFAULT = "app_icon_default"
    const val APP_ICON_FAKE = "app_icon_fake"
    const val TAG = "SettingPasswordActivity"
  }

  /** 设置默认的别名为启动入口 */
  fun setDefaultAlias() {
    val name1 = ComponentName(context, SplashAct::class.java)
    context.packageManager.setComponentEnabledSetting(
      name1,
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )

    val name2 = ComponentName(context, FakeSplashAct::class.java)
    context.packageManager.setComponentEnabledSetting(
      name2,
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )
  }

  /** 设置别名1为启动入口 */
  private fun setFakeSplash() {

    val name1 = ComponentName(context, SplashAct::class.java)
    context.packageManager.setComponentEnabledSetting(
      name1,
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )

    val name2 = ComponentName(context, FakeSplashAct::class.java)
    context.packageManager.setComponentEnabledSetting(
      name2,
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )
  }
}

class ItemView(temView: View, appIcon: Int, appName: String) {
  val itemView = temView

  val imgAppIcon = temView.findViewById<ImageView>(R.id.img_app_icon)
  val tvAppName = temView.findViewById<VText>(R.id.tv_app_name)
  val tvStatus = temView.findViewById<VText>(R.id.tv_status)

  init {
    imgAppIcon.setImageResource(appIcon)
    tvAppName.text = appName
  }

  fun unSelected() {
    tvAppName.setTextColor(Color.parseColor("#FFFFFF"))
    tvAppName.alpha = 0.6f
    tvAppName.setTypeface(null, Typeface.NORMAL)
    tvStatus.visibility = View.GONE
    itemView.isSelected = false
  }

  fun selected() {
    tvAppName.setTextColor(tvAppName.resources.getColor(R.color.color_3EC0AA))
    tvAppName.alpha = 1f
    tvAppName.setTypeface(null, Typeface.BOLD)
    tvStatus.visibility = View.VISIBLE
    itemView.isSelected = true
  }

  fun isSelected() = itemView.isSelected
}
