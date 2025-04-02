package top.niunaijun.blackboxa.view.setting

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.hello.sandbox.SandBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.AppManager
import top.niunaijun.blackboxa.util.toast
import top.niunaijun.blackboxa.view.gms.GmsManagerActivity
import top.niunaijun.blackboxa.view.xp.XpActivity

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/6 22:13
 */
class SettingFragment : PreferenceFragmentCompat() {

  private lateinit var xpEnable: SwitchPreferenceCompat

  private lateinit var xpModule: Preference

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.setting, rootKey)

    xpEnable = findPreference("xp_enable")!!
    xpEnable.isChecked = SandBoxCore.get().isXPEnable

    xpEnable.setOnPreferenceChangeListener { _, newValue ->
      SandBoxCore.get().isXPEnable = (newValue == true)
      true
    }
    // xp模块跳转
    xpModule = findPreference("xp_module")!!
    xpModule.setOnPreferenceClickListener {
      val intent = Intent(requireActivity(), XpActivity::class.java)
      requireContext().startActivity(intent)
      true
    }
    initGms()

    invalidHideState {
      val xpHidePreference: Preference = (findPreference("xp_hide")!!)
      val hideXposed = AppManager.mSandBoxLoader.hideXposed()
      xpHidePreference.setDefaultValue(hideXposed)
      xpHidePreference
    }

    invalidHideState {
      val rootHidePreference: Preference = (findPreference("root_hide")!!)
      val hideRoot = AppManager.mSandBoxLoader.hideRoot()
      rootHidePreference.setDefaultValue(hideRoot)
      rootHidePreference
    }

    invalidHideState {
      val daemonPreference: Preference = (findPreference("daemon_enable")!!)
      val mDaemonEnable = AppManager.mSandBoxLoader.daemonEnable()
      daemonPreference.setDefaultValue(mDaemonEnable)
      daemonPreference
    }
  }

  private fun initGms() {
    val gmsManagerPreference: Preference = (findPreference("gms_manager")!!)

    if (SandBoxCore.get().isSupportGms) {

      gmsManagerPreference.setOnPreferenceClickListener {
        GmsManagerActivity.start(requireContext())
        true
      }
    } else {
      gmsManagerPreference.summary = getString(R.string.no_gms)
      gmsManagerPreference.isEnabled = false
    }
  }

  private fun invalidHideState(block: () -> Preference) {
    val pref = block()
    pref.setOnPreferenceChangeListener { preference, newValue ->
      val tmpHide = (newValue == true)
      when (preference.key) {
        "xp_hide" -> {
          AppManager.mSandBoxLoader.invalidHideXposed(tmpHide)
        }
        "root_hide" -> {

          AppManager.mSandBoxLoader.invalidHideRoot(tmpHide)
        }
        "daemon_enable" -> {
          AppManager.mSandBoxLoader.invalidDaemonEnable(tmpHide)
        }
      }

      toast(R.string.restart_module)
      return@setOnPreferenceChangeListener true
    }
  }
}
