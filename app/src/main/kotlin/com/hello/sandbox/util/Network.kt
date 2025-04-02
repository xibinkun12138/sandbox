package com.hello.sandbox.util

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

class Network {

  companion object {
    var httpBuilder: OkHttpClient.Builder =
      OkHttpClient().newBuilder().apply {
        connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS)
      }

    private val okHttpClient: OkHttpClient = httpBuilder.build()
    fun getOkHttpClient(): OkHttpClient {
      return okHttpClient
    }
  }
}
