package com.hello.sandbox.ui.search

import android.view.View
import android.view.ViewGroup
import cbfg.rvadapter.RVHolder
import cbfg.rvadapter.RVHolderFactory
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.databinding.ItemSearchHeaderBinding

class HeaderAdapter : RVHolderFactory() {

  override fun createViewHolder(parent: ViewGroup?, viewType: Int, item: Any): RVHolder<out Any> {
    return AppHolder(inflate(R.layout.item_search_header, parent))
  }

  class AppHolder(itemView: View) : RVHolder<HeaderAppBean>(itemView) {

    val binding = ItemSearchHeaderBinding.bind(itemView)

    override fun setContent(item: HeaderAppBean, isSelected: Boolean, payload: Any?) {
      binding.icon.setImageDrawable(item.icon)
      binding.name.text = item.name
    }
  }
}
