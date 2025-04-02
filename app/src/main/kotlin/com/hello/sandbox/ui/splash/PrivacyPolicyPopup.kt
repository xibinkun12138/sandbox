package com.hello.sandbox.ui.splash

import android.app.Activity
import android.content.Context
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import com.hello.sandbox.Constant
import com.hello.sandbox.util.StringUtils
import com.lxj.xpopup.core.CenterPopupView
import com.hello.sandbox.common.util.ViewUtil
import top.niunaijun.blackboxa.R

class PrivacyPolicyPopup(context: Context) : CenterPopupView(context) {

  override fun getImplLayoutId(): Int {
    return R.layout.privacy_policy_content
  }

  lateinit var textView: TextView

  var afterAgree: Runnable? = null
  var disAgree: Runnable? = null

  override fun onCreate() {
    super.onCreate()
    val userAgreement: String = context.getString(R.string.PRIVACY_POLICY_DLG_USER_AGREEMENT)
    val privacyPolicy: String = context.getString(R.string.PRIVACY_POLICY_DLG_PRIVACY_POLICY)
    textView = findViewById(R.id.tv_user_agreement_privacy_policy)
    textView.text =
      StringUtils.getLinkSubstringWithColorToWebView(
        context as Activity?,
        context.resources.getColor(R.color.button_bg),
        String.format(
          context.getString(R.string.PRIVACY_POLICY_DLG_FIRST_CONTENT_WITH_LINK),
          userAgreement,
          privacyPolicy
        ),
        userAgreement,
        Constant.URL_APP_AGREEMENT,
        privacyPolicy,
        Constant.URL_APP_PRIVACY
      )
    textView.setMovementMethod(LinkMovementMethod.getInstance())
    val agreeButton = findViewById<Button>(R.id.btn_agree)
    ViewUtil.singleClickListener(agreeButton) { afterAgree!!.run() }
    val disAgreeButton = findViewById<TextView>(R.id.tv_disagree)
    ViewUtil.singleClickListener(disAgreeButton) { disAgree!!.run() }
  }
}
