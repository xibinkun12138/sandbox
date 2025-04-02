package com.hello.sandbox.ui.home

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(fm: FragmentManager, var fragmentList: List<Fragment>) :
  FragmentPagerAdapter(fm) {
  // 根据Item的位置返回对应位置的Fragment，绑定item和Fragment
  @NonNull
  override fun getItem(position: Int): Fragment {
    return fragmentList[position]
  }

  // 设置item的数量
  override fun getCount(): Int {
    return fragmentList.size
  }
}
