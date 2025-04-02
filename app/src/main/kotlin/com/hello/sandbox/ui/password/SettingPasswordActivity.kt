package com.hello.sandbox.ui.password

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.common.ui.Toast
import com.hello.sandbox.ui.cala.CalcActivity
import com.hello.sandbox.util.SharedPrefUtils
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivitySettingPasswordBinding
import top.niunaijun.blackboxa.util.inflate

class SettingPasswordActivity : AppCompatActivity() {
  private val viewBinding: ActivitySettingPasswordBinding by inflate()
  private var appPassWordNew: String = ""
  private var appPassWordNewAgain: String = ""

  private var showInputPassWordNewAgain = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
    val appPassWord = SharedPrefUtils.getStringData(this, APP_PASSWORD_KEY)
    initView(appPassWord)
  }

  private fun initView(appPassWord: String) {
    if (appPassWord.isEmpty()) {
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordTitle.setText(
        R.string.password_setting
      )
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordNextFrag.setText(
        R.string.password_setting_btn_text
      )
    } else {
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordTitle.setText(
        R.string.password_setting_change
      )
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordNextFrag.setText(
        R.string.password_setting_change
      )
    }
    viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordNextFrag.setOnClickListener {
      viewBinding.activitySettingPasswordTipFrag.root.visibility = View.GONE
      viewBinding.activitySettingPasswordFrag.root.visibility = View.VISIBLE
      viewBinding.activitySettingPasswordFrag.icvPasswordCode.showSoftInput()
    }

    viewBinding.vnNavigationbar.setLeftIconOnClick { onBackPressed() }
    viewBinding.activitySettingPasswordFrag.icvPasswordCode.setTextChangeListener {
      val tmpPassword = it
      if (showInputPassWordNewAgain) {
        appPassWordNewAgain = tmpPassword
      } else {
        appPassWordNew = tmpPassword
      }

      if (showInputPassWordNewAgain) {
        if (appPassWordNewAgain.length == 4) {
          if (appPassWordNew != appPassWordNewAgain) {
            Toast.message(getString(R.string.input_password_error), true, true)
          } else {
            Toast.message(getString(R.string.input_password_success), true, true)
            SharedPrefUtils.saveData(this, APP_PASSWORD_KEY, appPassWordNew)
            viewBinding.activitySettingPasswordFrag.icvPasswordCode.hideSoftInput()
            CalcActivity.start(this, true)
            finish()
          }
        }
      } else {
        if (appPassWordNew.length == 4) {
          showInputPassWordNewAgain = true
          viewBinding.activitySettingPasswordFrag.tvInputPassword.setText(
            R.string.input_password_again
          )
          viewBinding.activitySettingPasswordFrag.icvPasswordCode.text = ""
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    hideInput()
  }

  override fun onBackPressed() {
    if (viewBinding.activitySettingPasswordTipFrag.root.visibility == View.VISIBLE) {
      super.onBackPressed()
    } else {
      viewBinding.activitySettingPasswordFrag.icvPasswordCode.hideSoftInput()
      viewBinding.activitySettingPasswordTipFrag.root.visibility = View.VISIBLE
      viewBinding.activitySettingPasswordFrag.root.visibility = View.GONE
      viewBinding.activitySettingPasswordFrag.icvPasswordCode.text = ""
      viewBinding.activitySettingPasswordFrag.tvInputPassword.setText(R.string.input_password)
      showInputPassWordNewAgain = false
      appPassWordNewAgain = ""
    }
  }
  private fun imm(): InputMethodManager {
    return getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
  }

  fun hideInput() {
    hideInput(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
  }

  private fun hideInput(flag: Int) {
    try {
      window.setSoftInputMode(flag)
      if (window.currentFocus != null) {
        imm().hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
      } else {
        imm().hideSoftInputFromWindow(window.decorView.windowToken, 0)
      }
    } catch (ignored: Exception) {}
  }

  companion object {
    const val APP_PASSWORD_KEY = "setting_password"
    const val TAG = "SettingPasswordActivity"
  }
}
