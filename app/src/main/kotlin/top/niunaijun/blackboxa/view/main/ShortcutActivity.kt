package top.niunaijun.blackboxa.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hello.sandbox.SandBoxCore
import kotlinx.coroutines.launch

/**
 *
 * @Description: 快捷方式跳转activity
 * @Author: wukaicheng
 * @CreateDate: 2022/2/11 23:13
 */
class ShortcutActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val pkg = intent.getStringExtra("pkg")
    val userID = intent.getIntExtra("userId", 0)

    lifecycleScope.launch {
      SandBoxCore.get().launchApk(pkg, userID)
      finish()
    }
  }
}
