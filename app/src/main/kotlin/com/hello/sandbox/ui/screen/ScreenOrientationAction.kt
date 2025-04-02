package com.hello.sandbox.ui.screen

import top.niunaijun.blackboxa.screen.ScreenFlippedListener

interface ScreenOrientationAction {

  fun registerScreenFlippedListener(packageName: String, listener: ScreenFlippedListener)

  fun open()

  fun close()
}
