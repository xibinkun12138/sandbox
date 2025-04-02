package com.hello.sandbox.network

class BaseResponse<T> {
  val data: T? = null

  val errorCode: Int? = null

  val errorMsg: String? = null

  var exception: Exception? = null
}
