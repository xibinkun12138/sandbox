package com.hello.sandbox.util

import android.view.View
import com.hello.sandbox.common.util.ViewUtil

inline fun View.singleClickListener(onClickListenr: View.OnClickListener) {
  ViewUtil.singleClickListener(this, onClickListenr)
}
