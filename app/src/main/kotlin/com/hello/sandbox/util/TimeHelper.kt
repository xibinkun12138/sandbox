package com.hello.sandbox.util

import java.util.*

class TimeHelper {

  companion object {
    fun isSameDay(curTime: Long, time: Long): Boolean {
      val date = Date(time)
      val calendar1 = Calendar.getInstance()
      calendar1.time = date
      val calendar2 = Calendar.getInstance()
      calendar2.time = Date(curTime)
      return calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH] &&
        calendar1[Calendar.MONTH] == calendar2[Calendar.MONTH] &&
        calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR]
    }
  }
}
