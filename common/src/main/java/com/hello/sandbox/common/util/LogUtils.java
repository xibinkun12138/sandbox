package com.hello.sandbox.common.util;

import android.util.Log;

/** Created by liangzhichao on 2017/8/23. */
public class LogUtils {

  public static final String TAG = LogUtils.class.getSimpleName();
  public static boolean DEBUG = false;

  public static void setDebugable(boolean debugable) {
    DEBUG = debugable;
  }

  /**
   * Send a VERBOSE log message.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void v(String tag, String msg) {
    if (DEBUG) {
      Log.v(tag, msg);
    }
  }

  /** if not set tag, use default TAG. {@link #v(String, String)}. */
  public static void v(String msg) {
    if (DEBUG) {
      v(TAG, msg);
    }
  }

  /**
   * Send a VERBOSE log message and log the exception.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   * @param tr An exception to log
   */
  public static void v(String tag, String msg, Throwable tr) {
    if (DEBUG) {
      Log.v(tag, msg, tr);
    }
  }

  /** if not set tag, use default TAG. {@link #v(String, String, Throwable)}. */
  public static void v(String msg, Throwable tr) {
    if (DEBUG) {
      v(TAG, msg, tr);
    }
  }

  /**
   * Send a DEBUG log message.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void d(String tag, String msg) {
    if (DEBUG) {
      Log.d(tag, msg);
    }
  }

  /** if not set tag, use default TAG. {@link #d(String, String)} */
  public static void d(String msg) {
    if (DEBUG) {
      d(TAG, msg);
    }
  }

  /**
   * Send a DEBUG log message and log the exception.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   * @param tr An exception to log
   */
  public static void d(String tag, String msg, Throwable tr) {
    if (DEBUG) {
      Log.d(tag, msg, tr);
    }
  }

  /** if not set tag, use default TAG. {@link #d(String, String, Throwable)} */
  public static void d(String msg, Throwable tr) {
    if (DEBUG) {
      d(TAG, msg, tr);
    }
  }

  /**
   * Send an INFO log message.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void i(String tag, String msg) {
    if (DEBUG) {
      Log.i(tag, msg);
    }
  }

  /** if not set tag, use default TAG. {@link #i(String, String)} */
  public static void i(String msg) {
    if (DEBUG) {
      i(TAG, msg);
    }
  }

  /**
   * Send a INFO log message and log the exception.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   * @param tr An exception to log
   */
  public static void i(String tag, String msg, Throwable tr) {
    if (DEBUG) {
      Log.i(tag, msg, tr);
    }
  }

  /** if not set tag, use default TAG. {@link #i(String, String, Throwable)} */
  public static void i(String msg, Throwable tr) {
    if (DEBUG) {
      i(TAG, msg, tr);
    }
  }

  /**
   * Send a WARN log message.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void w(String tag, String msg) {
    if (DEBUG) {
      Log.w(tag, msg);
    }
  }

  /** if not set tag, use default TAG. {@link #w(String, String)} */
  public static void w(String msg) {
    if (DEBUG) {
      w(TAG, msg);
    }
  }

  /**
   * Send a WARN log message and log the exception.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   * @param tr An exception to log
   */
  public static void w(String tag, String msg, Throwable tr) {
    if (DEBUG) {
      Log.w(tag, msg, tr);
    }
  }

  /**
   * Send a WARN log message and log the exception.
   *
   * @param msg The message you would like logged.
   * @param tr An exception to log
   */
  public static void w(String msg, Throwable tr) {
    if (DEBUG) {
      Log.w(TAG, msg, tr);
    }
  }

  /**
   * if not set tag, use default TAG. {@link #w(String, String, Throwable)}
   *
   * <p>Typically used for logging catched or ignored exceptions
   */
  public static void w(Throwable tr) {
    if (DEBUG) {
      w(TAG, tr);
    }
  }

  /**
   * Send an ERROR log message.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   */
  public static void e(String tag, String msg) {
    if (DEBUG) {
      Log.e(tag, msg);
    }
  }

  /** if not set tag, use default TAG. {@link #e(String, String)} */
  public static void e(String msg) {
    if (DEBUG) {
      e(TAG, msg);
    }
  }

  /**
   * Send a ERROR log message and log the exception.
   *
   * @param tag Used to identify the source of a log message. It usually identifies the class or
   *     activity where the log call occurs.
   * @param msg The message you would like logged.
   * @param tr An exception to log
   */
  public static void e(String tag, String msg, Throwable tr) {
    if (DEBUG) {
      Log.e(tag, msg, tr);
    }
  }

  /** if not set tag, use default TAG. {@link #e(String, String, Throwable)} */
  public static void e(String msg, Throwable tr) {
    if (DEBUG) {
      e(TAG, msg, tr);
    }
  }
}
