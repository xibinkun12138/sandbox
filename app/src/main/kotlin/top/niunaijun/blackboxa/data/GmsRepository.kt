package top.niunaijun.blackboxa.data

import androidx.lifecycle.MutableLiveData
import com.hello.sandbox.SandBoxCore
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.app.AppManager
import top.niunaijun.blackboxa.bean.GmsBean
import top.niunaijun.blackboxa.bean.GmsInstallBean
import top.niunaijun.blackboxa.util.getString

/**
 *
 * @Description:
 * @Author: BlackBox
 * @CreateDate: 2022/3/2 21:14
 */
class GmsRepository {

  fun getGmsInstalledList(mInstalledLiveData: MutableLiveData<List<GmsBean>>) {
    val userList = arrayListOf<GmsBean>()

    SandBoxCore.get().users.forEach {
      val userId = it.id
      val userName =
        AppManager.mRemarkSharedPreferences.getString("Remark$userId", "User $userId") ?: ""
      val isInstalled = SandBoxCore.get().isInstallGms(userId)
      val bean = GmsBean(userId, userName, isInstalled)
      userList.add(bean)
    }

    mInstalledLiveData.postValue(userList)
  }

  fun installGms(userID: Int, mUpdateInstalledLiveData: MutableLiveData<GmsInstallBean>) {
    val installResult = SandBoxCore.get().installGms(userID)

    val result =
      if (installResult.success) {
        getString(R.string.install_success)
      } else {
        getString(R.string.install_fail, installResult.msg)
      }

    val bean = GmsInstallBean(userID, installResult.success, result)
    mUpdateInstalledLiveData.postValue(bean)
  }

  fun uninstallGms(userID: Int, mUpdateInstalledLiveData: MutableLiveData<GmsInstallBean>) {
    var isSuccess = false
    if (SandBoxCore.get().isInstallGms(userID)) {
      isSuccess = SandBoxCore.get().uninstallGms(userID)
    }

    val result =
      if (isSuccess) {
        getString(R.string.uninstall_success)
      } else {
        getString(R.string.uninstall_fail)
      }

    val bean = GmsInstallBean(userID, isSuccess, result)

    mUpdateInstalledLiveData.postValue(bean)
  }
}
