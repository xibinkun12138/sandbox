package com.hello.sandbox.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.hello.sandbox.user.UserUtils
import top.niunaijun.blackboxa.databinding.ActivityFakeHomeBinding

class FakeHomeAct : AppCompatActivity() {
  private lateinit var binding: ActivityFakeHomeBinding
  private var isPrivacyShowing = false

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, FakeHomeAct::class.java)
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityFakeHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    showPrivacy()
    return true
  }

  private fun showPrivacy() {
    if (!isPrivacyShowing) {
      PrivacyPolicyHelper.showPrivacyPop(this, { login() }, { isPrivacyShowing = false })
      isPrivacyShowing = true
    }
  }

  private fun login() {
    UserUtils.loginAndJumpHomeAct(this)
  }
}
