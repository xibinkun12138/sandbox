package com.hello.sandbox.ui.search

import android.graphics.drawable.Drawable

data class HeaderAppBean(
  val name: String,
  val icon: Drawable,
  val packageName: String,
  val sourceDir: String,
  val downloadUrl: String
)
