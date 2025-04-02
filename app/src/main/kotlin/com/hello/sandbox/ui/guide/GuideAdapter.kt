package com.hello.sandbox.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import top.niunaijun.blackboxa.R

class GuideAdapter(var mList: ArrayList<HideGuideInfo>) :
  RecyclerView.Adapter<GuideAdapter.PagerViewHolder>() {

  class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(info: HideGuideInfo, position: Int) {
      val imageView = itemView.findViewById<ImageView>(R.id.image)
      imageView.setImageDrawable(info.icon)
      val title = itemView.findViewById<TextView>(R.id.title)
      title.text = info.title
      val description = itemView.findViewById<TextView>(R.id.description)
      description.text = info.description
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.guide_pager_item, parent, false)
    return PagerViewHolder(view)
  }

  override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
    holder.bind(mList[position], position)
  }

  override fun getItemCount(): Int {
    return mList.size
  }
}
