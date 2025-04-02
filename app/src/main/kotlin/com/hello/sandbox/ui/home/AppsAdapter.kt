package com.hello.sandbox.ui.home

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.HomeItemAppBinding

/**
 *
 * @Description: 软件显示界面适配器
 * @Author: wukaicheng
 * @CreateDate: 2021/4/29 21:52
 */
class AppsAdapter : RVHolderFactory() {

  override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
    return AppsVH(inflate(R.layout.home_item_app, parent))
  }

  class AppsVH(itemView: View) : RVHolder<AppInfo>(itemView) {

    val binding = HomeItemAppBinding.bind(itemView)

    override fun setContent(item: AppInfo, isSelected: Boolean, payload: Any?) {
      binding.icon.setImageDrawable(item.icon)
      binding.name.text = item.name
      if (item.isDefault) {
        binding.cornerLabel.visibility = View.INVISIBLE
      } else {
        binding.cornerLabel.visibility = View.VISIBLE
        if (item.isHide) {
          binding.cornerLabel.text = "隐藏"
        } else {
          binding.cornerLabel.text = "双开"
        }
      }
    }
  }
}
