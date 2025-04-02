package com.hello.sandbox.network

import com.hello.sandbox.network.gson.GsonUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject

class HttpUtil {

  companion object {

    /** 同步的post请求 body: 会自动使用Gson把body序列化为String */
    @Throws(Exception::class)
    fun post(
      okHttpClient: OkHttpClient,
      urlString: String,
      body: Any?,
      headers: MutableMap<String, String>?
    ): String {
      val requestBuilder = Request.Builder().url(urlString)
      val json = if (body != null) GsonUtils.toJson(body) else ""
      val requestBody =
        RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json.toString())
      requestBuilder.post(requestBody)
      headers?.let { requestBuilder.headers(Headers.of(it)) }
      var res: String? = null
      try {
        val response = okHttpClient.newCall(requestBuilder.build()).execute()
        val exception = ExceptionChecker.check(response)
        if (exception != null) {
          throw exception
        }
        res = response.body()?.string()
      } catch (e: Exception) {
        throw e
      }
      return res ?: ""
    }

    @Throws(Exception::class)
    fun post(
      okHttpClient: OkHttpClient,
      urlString: String,
      body: JSONObject?,
      headers: MutableMap<String, String>?
    ): String {
      val requestBuilder = Request.Builder().url(urlString)
      val JSON = MediaType.parse("application/json;charset=utf-8")
      val requestBody = RequestBody.create(JSON, body.toString())
      requestBuilder.post(requestBody)
      headers?.let { requestBuilder.headers(Headers.of(it)) }
      var res: String? = null
      try {
        val response = okHttpClient.newCall(requestBuilder.build()).execute()
        val exception = ExceptionChecker.check(response)
        if (exception != null) {
          throw exception
        }
        res = response.body()?.string()
      } catch (e: Exception) {
        throw e
      }
      return res ?: ""
    }

    /** 异步post请求 body: 需要@Keep，会自动使用Gson把body序列化为String success & error 均回调在子线程！！！ */
    fun postWithCallback(
      okHttpClient: OkHttpClient,
      urlString: String,
      body: Any?,
      headers: MutableMap<String, String>?,
      success: (str: String) -> Unit,
      error: ((exception: Exception) -> Unit)? = null
    ) {
      GlobalScope.launch(Dispatchers.Default + CoroutineName("http")) {
        try {
          val result = post(okHttpClient, urlString, body, headers)
          success.invoke(result)
        } catch (e: Exception) {
          error?.invoke(e)
        }
      }
    }
  }
}
