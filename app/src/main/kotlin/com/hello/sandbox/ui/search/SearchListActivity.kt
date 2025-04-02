package com.hello.sandbox.ui.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cbfg.rvadapter.RVAdapter
import com.ferfalk.simplesearchview.SimpleSearchView
import com.hello.sandbox.Constant.Companion.DOWNLOAD_DIR_NAME
import com.hello.sandbox.Constant.Companion.RECOMMEND_MOMO_APP_URL
import com.hello.sandbox.Constant.Companion.RECOMMEND_TANTAN_APP_URL
import com.hello.sandbox.SandBoxCore
import com.hello.sandbox.common.ui.Toast
import com.hello.sandbox.ui.base.BaseAct
import com.hello.sandbox.ui.home.HomeAct.Companion.userID
import com.hello.sandbox.util.MarketHelper
import com.hello.sandbox.utils.AbiUtils
import com.hello.sandbox.view.SideBarLayout
import java.io.File
import java.util.*
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.InstalledAppBean
import top.niunaijun.blackboxa.databinding.ActivitySearchlistBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.view.list.ListViewModel

class SearchListActivity : BaseAct() {

  private val viewBinding: ActivitySearchlistBinding by inflate()

  private lateinit var mAdapter: SearchAdapter
  private lateinit var mHeaderAdapter: RVAdapter<HeaderAppBean>
  private var headerAppList: ArrayList<HeaderAppBean> = ArrayList()

  private lateinit var viewModel: ListViewModel

  private var appList: List<InstalledAppBean> = ArrayList()

  private lateinit var sidebarLayout: SideBarLayout

  private lateinit var recyclerView: RecyclerView
  private lateinit var headerRecyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)

    viewBinding.vnNavigationbar.setLeftIconOnClick { finish() }

    mAdapter = SearchAdapter(this)
    recyclerView = viewBinding.recyclerView
    recyclerView.adapter = mAdapter
    recyclerView.layoutManager = LinearLayoutManager(this)

    initSearchView()
    initViewModel()
    initSideBarView()
    initHeaderView()
  }

  private fun initHeaderView() {
    headerRecyclerView = viewBinding.header.headerRecyclerview
    mHeaderAdapter = RVAdapter<HeaderAppBean>(this, HeaderAdapter()).bind(headerRecyclerView)
    headerRecyclerView.adapter = mHeaderAdapter
    headerRecyclerView.layoutManager = LinearLayoutManager(this)
    (headerRecyclerView.layoutManager as LinearLayoutManager).orientation =
      LinearLayoutManager.HORIZONTAL

    var bean1 =
      HeaderAppBean(
        "探探",
        resources.getDrawable(R.drawable.tantan_icon),
        "com.p1.mobile.putong",
        getExternalFilesDir(null)!!.absolutePath + "/" + DOWNLOAD_DIR_NAME + "/tantan.apk",
        RECOMMEND_TANTAN_APP_URL
      )
    var bean2 =
      HeaderAppBean(
        "陌陌",
        resources.getDrawable(R.drawable.momo_icon),
        "com.immomo.momo",
        getExternalFilesDir(null)!!.absolutePath + "/" + DOWNLOAD_DIR_NAME + "momo.apk",
        RECOMMEND_MOMO_APP_URL
      )
    headerAppList.add(bean1)
    headerAppList.add(bean2)
    mHeaderAdapter.setItems(headerAppList)
    mHeaderAdapter.setItemClickListener { _, data, _ ->
      if (!SandBoxCore.get().isPackageNotInstalled(data.packageName)) {
        var sourceDir = getSourceDir(data.packageName)
        if (!TextUtils.isEmpty(sourceDir)) {
          val file = File(sourceDir)
          if (!AbiUtils.isSupport(file)) {
            if (SandBoxCore.is64Bit()) {
              Toast.message(getString(R.string.install_compatible_message, data.name))
            } else {
              Toast.message(
                getString(R.string.install_compatible_message, getString(R.string.app_name))
              )
            }
            return@setItemClickListener
          }
          showConfirmPopup(data.packageName, data.name, data.icon, sourceDir!!)
        }
      } else {
        MarketHelper.goToAppMarket(this, data.downloadUrl)
      }
    }
  }

  private fun getSourceDir(packageName: String): String? {
    return try {
      packageManager.getPackageInfo(packageName, 0).applicationInfo.sourceDir
    } catch (e: Throwable) {
      null
    }
  }

  private fun initSideBarView() {
    sidebarLayout = viewBinding.sidebar
    sidebarLayout.setSideBarLayoutListener {
      for (i in appList.indices) {
        if (appList[i].firstLetter == it) {
          recyclerView.smoothScrollToPosition(i)
          break
        }
      }
    }
  }

  private fun initSearchView() {
    viewBinding.searchView.setOnQueryTextListener(
      object : SimpleSearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean {
          filterApp(newText)
          return true
        }

        override fun onQueryTextCleared(): Boolean {
          return true
        }

        override fun onQueryTextSubmit(query: String): Boolean {
          return true
        }
      }
    )
  }

  private fun initViewModel() {
    viewModel =
      ViewModelProvider(this, InjectionUtil.getListFactory()).get(ListViewModel::class.java)

    viewModel.previewInstalledList()
    viewModel.getInstallAppList(userID)
    viewBinding.vnNavigationbar.setTitle(R.string.add_app)
    showLoading()

    viewModel.appsLiveData.observe(this) {
      hideLoading()
      if (it != null) {
        this.appList = it
        Collections.sort(appList, SortComparator())
        viewBinding.searchView.setQuery("", false)
        filterApp("")
      }
    }
  }

  private fun filterApp(newText: String) {
    val newList =
      this.appList.filter {
        it.name.contains(newText, true) or it.packageName.contains(newText, true)
      }
    mAdapter.setItems(newList as ArrayList<InstalledAppBean>)
  }

  private val openDocumentedResult =
    registerForActivityResult(ActivityResultContracts.GetContent()) {
      it?.run { finishWithResult(it.toString(), true) }
    }

  fun finishWithResult(source: String, fromSystem: Boolean, appName: String? = "") {
    intent.putExtra("source", source)
    intent.putExtra("fromSystem", fromSystem)
    intent.putExtra("appName", appName)
    setResult(Activity.RESULT_OK, intent)
    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    window.peekDecorView()?.run { imm.hideSoftInputFromWindow(windowToken, 0) }
    finish()
  }

  override fun onBackPressed() {
    if (viewBinding.searchView.isSearchOpen) {
      viewBinding.searchView.closeSearch()
    } else {
      super.onBackPressed()
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.list_choose) {
      openDocumentedResult.launch("application/vnd.android.package-archive")
    }
    return true
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_list, menu)
    val item = menu!!.findItem(R.id.list_search)
    viewBinding.searchView.setMenuItem(item)

    return true
  }

  override fun onStop() {
    super.onStop()
    viewModel.loadingLiveData.postValue(true)
    viewModel.loadingLiveData.removeObservers(this)
    viewModel.appsLiveData.postValue(null)
    viewModel.appsLiveData.removeObservers(this)
  }

  fun showConfirmPopup(
    packageName: String,
    name: String,
    icon: Drawable,
    path: String,
  ) {
    if (SandBoxCore.get().isInstalled(packageName, userID)) {
      Toast.message(getString(R.string.install_repeatedly_message))
    } else {
      InstallPromptPopup.showConfirmPopup(
        this,
        name,
        icon,
        { finishWithResult(path, false, name) },
        {}
      )
    }
  }

  companion object {
    fun start(context: Context, onlyShowXp: Boolean) {
      val intent = Intent(context, SearchListActivity::class.java)
      intent.putExtra("onlyShowXp", onlyShowXp)
      context.startActivity(intent)
    }
  }
}
