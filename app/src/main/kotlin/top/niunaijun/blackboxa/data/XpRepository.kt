package top.niunaijun.blackboxa.data

import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import com.hello.sandbox.SandBoxCore
import com.hello.sandbox.SandBoxCore.getPackageManager
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.XpModuleInfo
import top.niunaijun.blackboxa.util.getString

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/5/2 20:55
 */
class XpRepository {
  fun getInstallModules(modulesLiveData: MutableLiveData<List<XpModuleInfo>>) {
    val moduleList = SandBoxCore.get().installedXPModules
    val result = mutableListOf<XpModuleInfo>()
    moduleList.forEach {
      val info =
        XpModuleInfo(
          it.name,
          it.desc,
          it.packageName,
          it.packageInfo.versionName,
          it.enable,
          it.application.loadIcon(getPackageManager())
        )
      result.add(info)
    }

    modulesLiveData.postValue(result)
  }

  fun installModule(source: String, resultLiveData: MutableLiveData<String>) {
    val sandBoxCore = SandBoxCore.get()

    val installResult =
      if (URLUtil.isValidUrl(source)) {
        val uri = Uri.parse(source)
        sandBoxCore.installXPModule(uri)
      } else {
        // source == packageName
        sandBoxCore.installXPModule(source)
      }

    if (installResult.success) {
      resultLiveData.postValue(getString(R.string.install_success))
    } else {
      resultLiveData.postValue(getString(R.string.install_fail, installResult.msg))
    }
  }

  fun unInstallModule(packageName: String, resultLiveData: MutableLiveData<String>) {
    SandBoxCore.get().uninstallXPModule(packageName)
    resultLiveData.postValue(getString(R.string.remove_success))
  }
}
