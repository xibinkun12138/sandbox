package com.hello.sandbox.ui.screen

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import top.niunaijun.blackboxa.BuildConfig
import top.niunaijun.blackboxa.screen.ScreenFlippedListener

class ScreenOrientationService : Service(), SensorEventListener, ScreenOrientationAction {
  private var sensorManager: SensorManager? = null
  private var accelerometerSensor: Sensor? = null
  private var flipped = false
  private var screenFlippedListeners = mutableMapOf<String, ScreenFlippedListener>()

  override fun onBind(intent: Intent?): IBinder {
    return ScreenOrientationHandler(this)
  }

  private fun commandOpen() {
    if (sensorManager != null) return
    // 获取传感器管理器
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // 获取陀螺仪传感器
    accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    // val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    // 注册陀螺仪传感器监听器
    sensorManager?.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
  }
  private fun commandClose() {
    sensorManager?.unregisterListener(this, accelerometerSensor)
    sensorManager = null
    accelerometerSensor = null
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null) return
    if (event!!.sensor.type === Sensor.TYPE_ACCELEROMETER) {
      val i: Int = event.values[2].toInt()
      if (i < -5 && !flipped) {
        flipped = true
        var mainScreenFlippedListener: ScreenFlippedListener? = null
        screenFlippedListeners.toList().forEach { (packageName, u) ->
          if (packageName != getPackageName()) {
            onFlipped(u)
          } else {
            mainScreenFlippedListener = u
          }
        }
        onFlipped(mainScreenFlippedListener)
      } else if (i >= 0) {
        flipped = false
      }
    }
  }

  private fun onFlipped(listener: ScreenFlippedListener?) {
    if (listener == null) return
    try {
      listener.onFlipped()
    } catch (e: Exception) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace()
      }
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  override fun registerScreenFlippedListener(packageName: String, listener: ScreenFlippedListener) {
    screenFlippedListeners[packageName] = listener
    listener.asBinder().linkToDeath({ screenFlippedListeners.remove(packageName) }, 0)
  }

  override fun open() {
    commandOpen()
  }

  override fun close() {
    commandClose()
  }
}
