package com.hello.sandbox.ui.base

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import top.niunaijun.blackboxa.R

open class BaseAct : AppCompatActivity() {

  protected var progressDialog: Dialog? = null

  protected fun initToolbar(
    toolbar: Toolbar,
    title: Int,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null
  ) {
    setSupportActionBar(toolbar)
    toolbar.setTitle(title)
    if (showBack) {
      supportActionBar?.let {
        it.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
          if (onBack != null) {
            onBack()
          }
          finish()
        }
      }
    }
  }

  protected fun updateToolBarTitle(toolbar: Toolbar, title: String) {
    toolbar.title = title
  }

  fun showLoading() {
    progress("")?.show()
  }

  fun showLoading(describe: String) {
    progress(describe)?.show()
  }

  fun hideLoading() {
    progressDismiss()
  }

  open fun progress(id: Int, delay: Boolean): Dialog? {
    return progress(getString(id), delay)
  }

  open fun progress(text: String?): Dialog? {
    return progress(text, false)
  }

  open fun progress(text: String?, delay: Boolean): Dialog? {
    return progress(text, delay, true)
  }
  open fun progress(describe: String?, delay: Boolean, animateDim: Boolean): Dialog? {
    return if (progressDialog == null && !isFinishing) {
      val p: Dialog = progress(this, describe)
      p.show()
      progressDialog = p
      if (delay) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        val lp = p.window!!.attributes
        lp.alpha = 0f
        val finalDim = lp.dimAmount
        if (animateDim) {
          lp.dimAmount = 0f
        }
        p.window!!.attributes = lp
        animator.addUpdateListener { animation: ValueAnimator ->
          if (progressDialog === p) {
            val lp1 = p.window!!.attributes
            lp1.alpha = animation.animatedFraction
            if (animateDim) {
              lp1.dimAmount = finalDim
            }
            p.window!!.attributes = lp
          }
        }
        animator.startDelay = 400
        animator.duration = 150
        animator.interpolator = DecelerateInterpolator(1.5f)
        animator.start()
      }
      p
    } else {
      progressDialog
    }
  }

  open fun progress(act: Activity?, describe: String?): Dialog {
    val view: View
    if (TextUtils.isEmpty(describe)) {
      view = LayoutInflater.from(act).inflate(R.layout.progress_only_dialog, null)
    } else {
      view = LayoutInflater.from(act).inflate(R.layout.progress_dialog, null)
      val textDescribe = view.findViewById<TextView>(R.id.message)
      if (textDescribe != null) {
        if (TextUtils.isEmpty(describe)) {
          textDescribe.visibility = View.GONE
        } else {
          textDescribe.visibility = View.VISIBLE
          textDescribe.text = describe
        }
      }
    }
    val p =
      AlertDialog.Builder(act!!, R.style.progress_dialog)
        .setView(view)
        .setCancelable(false)
        .create()
    p.setCanceledOnTouchOutside(false)
    return p
  }

  open fun progressDismiss() {
    if (progressDialog != null) {
      try {
        progressDialog!!.dismiss()
      } catch (ignore: Exception) {}
      progressDialog = null
    }
  }
}
