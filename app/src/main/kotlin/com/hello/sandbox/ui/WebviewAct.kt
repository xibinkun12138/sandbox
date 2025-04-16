package com.hello.sandbox.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.hello.sandbox.ui.base.BaseAct
import top.niunaijun.blackboxa.databinding.ActivityWebviewBinding

class WebviewAct : BaseAct() {
  private lateinit var binding: ActivityWebviewBinding
  private var url: String? = null

  companion object {
    const val paramUrl = "url"
    const val paramTitle = "title"
    fun start(context: Context, url: String, title: String?) {
      val intent = Intent(context, WebviewAct::class.java)
      intent.putExtra(paramUrl, url)
      intent.putExtra(paramTitle, title)
      context.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityWebviewBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.vnNavigationbar.setLeftIconOnClick { finish() }
    url = intent.getStringExtra(paramUrl)
    title = intent.getStringExtra(paramTitle)
    binding.vnNavigationbar.setTitle(title)
    initWebview()
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun initWebview() {
    val webChromeClient = WebChromeClient()
    val client: WebViewClient =
      object : WebViewClient() {
        override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
          return true
        }

        override fun onReceivedError(
          view: WebView?,
          request: WebResourceRequest?,
          error: WebResourceError?
        ) {
          super.onReceivedError(view, request, error)
          binding.webview.visibility = View.GONE
          binding.errorWeb.visibility = View.VISIBLE
        }
      }
    binding.webview.webChromeClient = webChromeClient
    binding.webview.webViewClient = client
    val settings: WebSettings = binding.webview.settings
    settings.javaScriptEnabled = true
    settings.builtInZoomControls = true
    settings.displayZoomControls = false
    settings.cacheMode = WebSettings.LOAD_NO_CACHE
//    settings.setAppCacheEnabled(true)
    settings.domStorageEnabled = true
    settings.loadsImagesAutomatically = true
    settings.blockNetworkImage = false
    settings.blockNetworkLoads = false
    settings.loadWithOverviewMode = false
    settings.allowFileAccess = true
    // 请勿随意改动 viewport 基础设置，原因请看 ->
    // https://confluence.p1staff.com/pages/viewpage.action?pageId=91063000
    // 请勿随意改动 viewport 基础设置，原因请看 ->
    // https://confluence.p1staff.com/pages/viewpage.action?pageId=91063000
    settings.useWideViewPort = true
    // for image loading with http
    // for image loading with http
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    binding.webview.isHorizontalScrollBarEnabled = false
    // todo
    settings.userAgentString = "tantan-android"

    if (Build.VERSION.SDK_INT >= 21) {
      settings.mixedContentMode = 0
      binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    } else if (Build.VERSION.SDK_INT >= 19) {
      binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19) {
      binding.webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    url?.let { binding.webview.loadUrl(it) }
  }
}
