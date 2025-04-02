package com.hello.sandbox.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.user.UserUtils
import com.hello.sandbox.view.BasePopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityLoginBinding

class LoginAct : AppCompatActivity() {
  private lateinit var binding: ActivityLoginBinding

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, LoginAct::class.java)
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
    showPrivacyDlg()
  }

  private fun showPrivacyDlg() {
    PrivacyPolicyHelper.showPrivacyPop(this, { login() }, { showPrompt() })
  }

  private fun showPrompt() {
    val prompt =
      BasePopup(
        this,
        getString(R.string.prompt_popup_title),
        getString(R.string.prompt_popup_description),
        { showPrivacyDlg() },
        { jumpFakeHome() },
        getString(R.string.prompt_popup_confirm),
        getString(R.string.prompt_popup_cancel),
        false
      )
    XPopup.Builder(this)
      .moveUpToKeyboard(false)
      .maxWidth(XPopupUtils.getScreenWidth(this) - XPopupUtils.dp2px(this, 40f))
      .isDestroyOnDismiss(true)
      .dismissOnTouchOutside(false)
      .dismissOnBackPressed(false)
      .asCustom(this.let { prompt })
      .show()
  }

  private fun jumpFakeHome() {
    FakeHomeAct.start(this)
    finish()
  }

  private fun login() {
    UserUtils.loginAndJumpHomeAct(this)
  }
}
