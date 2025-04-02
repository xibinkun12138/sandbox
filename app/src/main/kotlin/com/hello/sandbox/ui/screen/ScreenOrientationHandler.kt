package com.hello.sandbox.ui.screen

import top.niunaijun.blackboxa.screen.IScreenOrientation
import top.niunaijun.blackboxa.screen.ScreenFlippedListener

class ScreenOrientationHandler(val action: ScreenOrientationAction) : IScreenOrientation.Stub() {
  override fun registerScreenFlippedListener(packageName: String, listener: ScreenFlippedListener) {
    action.registerScreenFlippedListener(packageName, listener)
  }

  override fun open() {
    action.open()
  }

  override fun close() {
    action.close()
  }
}
