package com.hello.sandbox.ui.guide

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.lxj.xpopup.core.BottomPopupView
import com.hello.sandbox.common.util.ViewUtil
import top.niunaijun.blackboxa.R

class HideGuidePopup(context: Context) : BottomPopupView(context) {

  private lateinit var viewPager2: ViewPager2
  private lateinit var btnNext: Button
  private lateinit var imgClose: ImageView
  private var currentPosition: Int = 0

  override fun getImplLayoutId(): Int {
    return R.layout.popup_hide_guide
  }

  override fun onCreate() {
    super.onCreate()
    viewPager2 = findViewById(R.id.viewPager2)
    btnNext = findViewById(R.id.btn_next)
    imgClose = findViewById(R.id.img_close)
    viewPager2.isUserInputEnabled = false
    var guideList = ArrayList<HideGuideInfo>()
    guideList.add(
      HideGuideInfo(
        context.getString(R.string.hide_guide_title_1),
        context.resources.getDrawable(R.drawable.hide_bg_guide1),
        context.getString(R.string.hide_guide_description_1)
      )
    )
    guideList.add(
      HideGuideInfo(
        context.getString(R.string.hide_guide_title_2),
        context.resources.getDrawable(R.drawable.hide_bg_guide2),
        context.getString(R.string.hide_guide_description_2)
      )
    )
    guideList.add(
      HideGuideInfo(
        context.getString(R.string.hide_guide_title_3),
        context.resources.getDrawable(R.drawable.hide_bg_guide3),
        context.getString(R.string.hide_guide_description_3)
      )
    )
    viewPager2.adapter = GuideAdapter(guideList)
    viewPager2.registerOnPageChangeCallback(
      object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          currentPosition = position
          if (position == guideList.size - 1) {
            btnNext.text = "好的"
          } else {
            btnNext.text = "下一步"
          }
        }
      }
    )

    ViewUtil.singleClickListener(btnNext) {
      if (currentPosition != guideList.size - 1) {
        viewPager2.currentItem = currentPosition + 1
      } else {
        dismiss()
      }
    }

    ViewUtil.singleClickListener(imgClose) { dismiss() }
  }
}
