package com.hello.sandbox.ui.cala

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.calc.frag.CalcFragment
import com.hello.sandbox.ui.home.HomeAct
import com.hello.sandbox.ui.password.SettingPasswordActivity
import com.hello.sandbox.util.SharedPrefUtils
import com.hello.sandbox.common.util.ViewUtil
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.App
import v.pushbubble.PushBubbleManager
import v.pushbubble.SimplePushBubble

class CalcActivity : AppCompatActivity() {
  private var appPassWord: String? = null
  private var noNeedJumpHome: Boolean = false

  private var checkTip: Boolean = true

  companion object {
    const val SHOW_SECRET_TIP = "show_secret_tip"
    const val NO_NEED_JUMP_PARAM = "tag_not_need_jump_to_home"
    fun start(context: Context, noNeedJumpHome: Boolean) {
      val intent = Intent(context, CalcActivity::class.java)
      intent.putExtra(NO_NEED_JUMP_PARAM, noNeedJumpHome)
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_calc)
    noNeedJumpHome = intent.getBooleanExtra(NO_NEED_JUMP_PARAM, false)
    appPassWord = SharedPrefUtils.getStringData(this, SettingPasswordActivity.APP_PASSWORD_KEY)
    val calcFragment = CalcFragment.newInstance()
    calcFragment.setCalcResultChangeListener {
      if (it.length == 4) {
        if (appPassWord == it) {
          if (!noNeedJumpHome) {
            HomeAct.start(this)
          }
          finish()
        }
      }
    }

    supportFragmentManager.beginTransaction().replace(R.id.calcFragment, calcFragment).commit()

    findViewById<Button>(R.id.btn_main_choose)
      .setOnClickListener(View.OnClickListener { v: View? -> calcFragment.chooseMode() })
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (checkTip) {
      checkAndShowSecretTip()
      checkTip = false
    }
  }

  private fun checkAndShowSecretTip() {
    var showTip = SharedPrefUtils.getBooleanWithDefault(this, SHOW_SECRET_TIP, true)
    if (showTip) {
      var contentView = LayoutInflater.from(this).inflate(R.layout.calculator_tip, null)
      var title = contentView.findViewById<TextView>(R.id.tv_title)
      title.text = Html.fromHtml("你的密码是:<font color='#54C7FC'>$appPassWord</font>，关闭后不再提示")
      var bubble =
        SimplePushBubble.Builder(this, contentView)
          .setPushStyle(SimplePushBubble.PushStyle.SMALL)
          .setAutoDismiss(false)
          .build()
      ViewUtil.singleClickListener(contentView.findViewById<ImageView>(R.id.img_close)) {
        SharedPrefUtils.saveData(App.mContext, SHOW_SECRET_TIP, false)
        bubble.hidePush()
      }
      PushBubbleManager.getInstance().showSequenceBubble(bubble)
    }
  }

  override fun onBackPressed() {
    super.onBackPressed()
    var home = Intent(Intent.ACTION_MAIN)
    home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    home.addCategory(Intent.CATEGORY_HOME)
    startActivity(home)
  }
}
