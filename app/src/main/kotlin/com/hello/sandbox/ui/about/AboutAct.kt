package com.hello.sandbox.ui.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.hello.sandbox.Constant
import com.hello.sandbox.common.ui.Toast
import com.hello.sandbox.common.util.PackageUtil
import com.hello.sandbox.ui.WebviewAct
import com.hello.sandbox.ui.base.BaseAct
import com.hello.sandbox.ui.upgrade.UpgradeAppViewModel
import com.hello.sandbox.ui.upgrade.UpgradeChecker
import com.hello.sandbox.common.util.ViewUtil
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ActivityAboutBinding

class AboutAct : BaseAct() {
  private lateinit var binding: ActivityAboutBinding
  private lateinit var llAgreement: LinearLayout
  private lateinit var rlPrivacy: RelativeLayout
  private lateinit var rlCheckUpdate: RelativeLayout
  private lateinit var tvVersion: TextView
  lateinit var upgradeAppViewModel: UpgradeAppViewModel

  companion object {
    fun start(context: Context) {
      val intent = Intent(context, AboutAct::class.java)
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityAboutBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.vnNavigationbar.setLeftIconOnClick { finish() }

    llAgreement = binding.llAgreement
    rlPrivacy = binding.rlPrivacy
    rlCheckUpdate = binding.rlCheckUpdate
    tvVersion = binding.tvVersion
    tvVersion.text = getString(R.string.ABOUT_VERSION, PackageUtil.getVersionName(this))
    ViewUtil.singleClickListener(llAgreement) {
      WebviewAct.start(this, Constant.URL_APP_AGREEMENT, "用户协议")
    }

    ViewUtil.singleClickListener(rlPrivacy) {
      WebviewAct.start(this, Constant.URL_APP_PRIVACY, "隐私政策")
    }
    upgradeAppViewModel = ViewModelProvider(this).get(UpgradeAppViewModel::class.java)
    ViewUtil.singleClickListener(rlCheckUpdate) { upgradeAppViewModel.checkAppUpgradeInfo() }
    upgradeAppViewModel.upgradeModel.observe(this) {
      if (it == null) {
        Toast.message(getString(R.string.ALREADY_NEWEST_VERSION))
        return@observe
      }
      if (UpgradeChecker.hasNewVersion(it)) {
        UpgradeChecker.showAppUpgradePopUp(this@AboutAct, it)
      } else {
        Toast.message(getString(R.string.ALREADY_NEWEST_VERSION))
      }
    }
  }
}
