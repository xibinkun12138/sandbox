package com.hello.sandbox.common.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import com.hello.sandbox.common.Au;
import com.hello.sandbox.common.Config;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.ContextHolder;
import com.hello.sandbox.common.util.Copies;
import com.hello.sandbox.common.util.LogUtils;
import com.hello.sandbox.common.util.MetricsUtil;
import com.hello.sandbox.common.util.PackageUtil;
import com.hello.sandbox.common.util.ToastUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Toast {

  private static WeakReference<android.widget.Toast> showingToast;

  private static ThrottleToastEvent preToast;

  public static void message(int res) {
    message(ContextHolder.context().getString(res), true);
  }

  @Deprecated
  public static void message(int res, boolean onlyForeground) {
    message(ContextHolder.context().getString(res), onlyForeground);
  }

  public static void message(String message, View view) {
    Au.runOnUiThread(
        () ->
            CustomToast.make(view, message, ((int) (message.length() * 0.04 + 2000)), null).show());
  }

  /** 展示一个toast，该toast背景为灰色 */
  public static void message(String t) {
    message(t, true);
  }

  @Deprecated
  public static void message(String t, boolean onlyForeground) {
    message(t, onlyForeground, false);
  }

  @Deprecated
  public static void message(String t, boolean onlyForeground, boolean center) {
    showToastInner(null, t, null);
  }

  @Deprecated
  public static void message(String t, boolean onlyForeground, boolean center, boolean textCenter) {
    showToastInner(null, t, null);
  }

  public static ArrayList<String> debugHistory = new ArrayList<>();

  public static void debug(String t) {
    if (Config.DEBUG && Log.isLoggable("Toast", Log.ERROR)) {
      Au.runOnUiThread(
          () -> {
            debugHistory.add(0, t);
            if (Config.DEBUG) LogUtils.d("debug toast", t);
          });
    }
  }

  public static void alertDebug(String t) {
    if (Config.DEBUG && Log.isLoggable("Toast", Log.ERROR)) {
      Au.runOnUiThread(
          () -> {
            debugHistory.add(0, t);
            if (Config.DEBUG) LogUtils.d("alertDebug toast", t);
          });
    }
  }

  public static void alert(int res) {
    alert(ContextHolder.context().getString(res), true);
  }

  @Deprecated
  public static void alert(int res, boolean onlyForeground) {
    alert(ContextHolder.context().getString(res), onlyForeground);
  }

  /**
   * 展示toast，该toast背景为红色
   *
   * @param t
   */
  public static void alert(String t) {
    alert(t, true);
  }

  @Deprecated
  public static void alert(String t, boolean onlyForeground, boolean center) {
    alert(t, onlyForeground);
  }

  @Deprecated
  public static void alert(String t, boolean onlyForeground) {
    showToastInner(null, t, ContextHolder.context().getResources().getColor(R.color.common_red));
  }

  public static void toastAsBanner(String str, Drawable leftIcon, Drawable backGround) {
    if (Au.isAppOnForeground()) {
      Au.runOnUiThread(
          () -> {
            android.widget.Toast toast = buildToast();

            TextView textView = new TextView(ContextHolder.context());
            textView.setText(str);
            textView.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, null, null);
            textView.setCompoundDrawablePadding(MetricsUtil.dp(18));
            textView.setBackgroundDrawable(backGround);
            //				textView.setPadding(Metrics.dp(20), Metrics.dp(20), Metrics.dp(24),
            // Metrics.dp(20));
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(14);
            textView.setLayoutParams(
                new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            toast.setView(textView);
            toast.setDuration(android.widget.Toast.LENGTH_LONG);
            toast.show();
          });
    }
  }

  public static void toastWithLeftIcon(String text, Drawable backGround, Drawable leftIcon) {
    if (Au.isAppOnForeground()) {
      Au.runOnUiThread(
          () -> {
            android.widget.Toast toast = buildToast();
            TextView textView = new TextView(ContextHolder.context());
            textView.setText(text);
            textView.setGravity(Gravity.CENTER);
            textView.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, null, null);
            textView.setCompoundDrawablePadding(MetricsUtil.dp(10));
            textView.setBackgroundDrawable(backGround);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(14);
            textView.setLayoutParams(
                new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            toast.setView(textView);
            if (PackageUtil.targetSdkVersion() < Build.VERSION_CODES.R) {
              toast.setGravity(Gravity.BOTTOM, 0, 200);
            }
            toast.setDuration(android.widget.Toast.LENGTH_LONG);
            toast.show();
          });
    }
  }

  @Deprecated
  public static android.widget.Toast make(String text) {
    return makeToastWithBackground(
        text, ContextHolder.context().getResources().getColor(R.color.common_red));
  }

  @Deprecated
  public static android.widget.Toast makeBlackBgToast(String text) {
    return makeToastWithBackground(text, null);
  }

  public static android.widget.Toast buildToast() {
    return ToastUtil.buildToast();
  }

  /**
   * 在指定页面展示toast
   *
   * @param act 页面Act
   * @param message 消息
   */
  public static void showToast(Activity act, String message) {
    if (TextUtils.isEmpty(message)) return;
    showToastInner(act, message, null);
  }

  private static void showToastInner(Activity act, String message, @ColorInt Integer bgColor) {
    if (TextUtils.isEmpty(message)) return;

    // 默认只在前台展示
    if (Au.isAppOnForeground()) {
      Au.runOnUiThread(() -> sysShow(message, bgColor));
    }
  }

  /**
   * snackBar有问题
   *
   * <p>https://confluence.p1staff.com/pages/viewpage.action?pageId=117981629
   *
   * @param t
   * @param bgColor
   */
  private static void sysShow(String t, @ColorInt Integer bgColor) {
    dismissShowingToast();
    android.widget.Toast atoast = makeToastWithBackground(t, bgColor);
    showingToast = new WeakReference<>(atoast);
    atoast.show();
  }

  private static android.widget.Toast makeToastWithBackground(String t, @ColorInt Integer bgColor) {
    View view =
        LayoutInflater.from(ContextHolder.context()).inflate(R.layout.common_view_toast, null);
    View container = view.findViewById(R.id.toast_container);
    Drawable drawable = container.getBackground().mutate();
    if (bgColor != null) {
      if (drawable instanceof GradientDrawable) {
        ((GradientDrawable) drawable).setColor(bgColor);
        container.setBackground(drawable);
      }
    }
    String tt = Copies.removeFullStop(t);
    if (Config.DEBUG && tt.length() != t.length()) {
      LogUtils.d("Toast", "removed full stop " + tt);
    }
    ((TextView) view.findViewById(R.id.toast_content)).setText(tt);
    android.widget.Toast atoast = buildToast();
    atoast.setView(view);
    //    if (PackageUtil.targetSdkVersion() < Build.VERSION_CODES.R) {
    atoast.setGravity(Gravity.CENTER, 0, 0);
    //    }
    atoast.setDuration(android.widget.Toast.LENGTH_SHORT);
    return atoast;
  }

  private static void dismissShowingToast() {
    if (showingToast != null && showingToast.get() != null) {
      showingToast.get().cancel();
    }
  }

  public static void messageThrottle(int resId) {
    add2ThrottleQueue(ContextHolder.context().getString(resId), true);
  }

  public static void messageThrottle(String msg) {
    add2ThrottleQueue(msg, true);
  }

  public static void alertThrottle(int resId) {
    add2ThrottleQueue(ContextHolder.context().getString(resId), false);
  }

  public static void alertThrottle(String msg) {
    add2ThrottleQueue(msg, false);
  }

  private static void add2ThrottleQueue(String msg, boolean isMsgType) {
    if (TextUtils.isEmpty(msg)) return;
    long timeInMills = System.currentTimeMillis();
    if (preToast == null
        || !TextUtils.equals(preToast.msg, msg)
        || (timeInMills - preToast.emitTimeInMills >= 30000)) {
      synchronized (Toast.class) {
        timeInMills = System.currentTimeMillis();
        if (preToast == null
            || !TextUtils.equals(preToast.msg, msg)
            || (timeInMills - preToast.emitTimeInMills >= 30000)) {
          preToast = new ThrottleToastEvent(msg, timeInMills, isMsgType);
          LogUtils.e("TOAST", "show Toast:" + preToast);
          if (preToast.isMsgType) {
            Toast.message(preToast.msg);
          } else {
            Toast.alert(preToast.msg);
          }
        }
      }
    }
  }

  static class ThrottleToastEvent {
    final String msg;
    final long emitTimeInMills;
    final boolean isMsgType;

    public ThrottleToastEvent(String msg, long emitTimeInMills, boolean isMsgType) {
      this.msg = msg;
      this.emitTimeInMills = emitTimeInMills;
      this.isMsgType = isMsgType;
    }

    @Override
    public String toString() {
      return "ThrottleToastEvent{"
          + "msg='"
          + msg
          + '\''
          + ", emitTimeInMills="
          + emitTimeInMills
          + ", isMsgType="
          + isMsgType
          + '}';
    }
  }
}
