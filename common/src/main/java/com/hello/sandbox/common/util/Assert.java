package com.hello.sandbox.common.util;

import android.os.Looper;

/** User: molikto Date: 12/29/14 Time: 04:42 */
public class Assert {

  private static final String TAG = "Assert";

  public static void isUiThread() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
      IllegalStateException e =
          new IllegalStateException(
              "Should be called from main thread,current thread Name:"
                  + Thread.currentThread().getName()
                  + " thread id:"
                  + Thread.currentThread().getId());
      printAndThrow(e);
    }
  }

  public static void uiThreadOrExit() {
    if (Looper.getMainLooper() != Looper.myLooper()) {
      new IllegalStateException("Should be called from main thread").printStackTrace();
      System.exit(2);
    }
  }

  private static void printAndThrow(RuntimeException e) {
    LogUtils.e(TAG, "logError:" + e);
    // 在debug下保证异常抛出不被catch
    if (UtilSDk.DEBUG_BUILD) {
      ThreadUtil.postDelayed(
          () -> {
            throw e;
          },
          5000);
    }
    throw e;
  }

  public static void notUiThread() {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      printAndThrow(new IllegalStateException("Should not be called from main thread"));
    }
  }

  public static void isNull(Object o) {
    if (o != null) printAndThrow(new IllegalArgumentException());
  }

  public static void isNull(Object o, String msg) {
    if (o != null) printAndThrow(new IllegalStateException(msg));
  }

  public static void notNull(Object o) {
    if (o == null) printAndThrow(new NullPointerException());
  }

  public static void notNull(Object o, String msg) {
    if (o == null) printAndThrow(new IllegalStateException(msg));
  }

  public static void t(boolean t) {
    if (!t) printAndThrow(new IllegalStateException());
  }

  public static void f(boolean f) {
    if (f) printAndThrow(new IllegalStateException());
  }
}
