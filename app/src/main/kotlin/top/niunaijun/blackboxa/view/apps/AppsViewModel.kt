package top.niunaijun.blackboxa.view.apps

import androidx.lifecycle.MutableLiveData
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.data.AppsRepository
import top.niunaijun.blackboxa.view.base.BaseViewModel

/**
 *
 * @Description:
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 22:36
 */
class AppsViewModel(private val repo: AppsRepository) : BaseViewModel() {

  val appsLiveData = MutableLiveData<List<AppInfo>>()

  val resultLiveData = MutableLiveData<String>()

  val launchLiveData = MutableLiveData<Boolean>()

  // 利用LiveData只更新最后一次的特性，用来保存app顺序
  val updateSortLiveData = MutableLiveData<Boolean>()

  val allUninstallData = MutableLiveData<Boolean>()

  fun getInstalledApps(userId: Int) {
    launchOnUI { repo.getVmInstallList(userId, appsLiveData) }
  }

  fun install(source: String, userID: Int, fromSystem: Boolean, appName: String? = "") {
    launchOnUI { repo.installApk(source, userID, fromSystem, resultLiveData, appName) }
  }

  fun unInstall(packageName: String, userID: Int, appName: String? = "") {
    launchOnUI { repo.unInstall(packageName, userID, resultLiveData, appName) }
  }

  fun clearApkData(packageName: String, userID: Int) {
    launchOnUI { repo.clearApkData(packageName, userID, resultLiveData) }
  }

  fun launchApk(packageName: String, userID: Int) {
    launchOnUI { repo.launchApk(packageName, userID, launchLiveData) }
  }

  fun updateApkOrder(userID: Int, dataList: List<AppInfo>) {
    launchOnUI { repo.updateApkOrder(userID, dataList) }
  }

  fun unInstallApks(list: List<AppInfo>, userID: Int) {
    launchOnUI {
      list.forEach { repo.unInstall(it.packageName, userID) }
      allUninstallData.postValue(true)
    }
  }
}
