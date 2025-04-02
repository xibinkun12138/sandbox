package com.hello.sandbox.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SectionIndexer
import android.widget.TextView
import com.hello.sandbox.common.util.Vu
import com.hello.sandbox.util.StringUtils
import com.hello.sandbox.common.util.ViewUtil
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.InstalledAppBean
import v.RAdapter

class SearchAdapter(private val act: SearchListActivity) :
  RAdapter<InstalledAppBean>(), SectionIndexer {

  private var appList = ArrayList<InstalledAppBean>()

  override fun inflate(parent: ViewGroup?, itemViewType: Int): View? {
    return LayoutInflater.from(act)?.inflate(R.layout.item_search_package, parent, false)
  }

  override fun adapt(
    convertView: View?,
    item: InstalledAppBean?,
    itemViewType: Int,
    position: Int
  ) {
    var layout = convertView as? LinearLayout
    layout?.apply {
      var llItem = findViewById<LinearLayout>(R.id.ll_item)
      val sortLetter = findViewById<TextView>(R.id.tv_sort_letter)
      val icon = findViewById<ImageView>(R.id.icon)
      val appName = findViewById<TextView>(R.id.name)
      item?.let {
        icon?.setImageDrawable(it.icon)
        appName?.text = it.name
        sortLetter.text = it.firstLetter
        Vu.gone(sortLetter, isFirstInSection(position))
        ViewUtil.singleClickListener(llItem) {
          act.showConfirmPopup(item.packageName, item.name, item.icon, item.sourceDir)
        }
      }
    }
  }

  private fun isFirstInSection(position: Int): Boolean {
    return position == getPositionForSection(getSectionForPosition(position))
  }

  override fun getItem(position: Int): InstalledAppBean? {
    return appList[position]
  }

  override fun getCount(): Int {
    return appList.size
  }

  override fun getSections(): Array<String> {
    return StringUtils.ALL_LETTERS
  }

  override fun getPositionForSection(sectionIndex: Int): Int {
    for (i in appList.indices) {
      if (appList[i].firstLetter == StringUtils.ALL_LETTERS[sectionIndex]) {
        return i
      }
    }
    return 0
  }

  override fun getSectionForPosition(position: Int): Int {
    for (i in StringUtils.ALL_LETTERS.indices) {
      if (
        appList.isNotEmpty() &&
          appList[if (position >= appList.size) appList.size - 1 else position].firstLetter ==
            StringUtils.ALL_LETTERS[i]
      ) {
        return i
      }
    }
    return StringUtils.ALL_LETTERS.size - 1
  }

  fun setItems(newList: ArrayList<InstalledAppBean>) {
    this.appList.clear()
    this.appList = newList
    notifyDataSetChanged()
  }
}
