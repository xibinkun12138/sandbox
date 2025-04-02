package com.hello.sandbox.common.util;

import android.content.Context;

public class UtilSDk {

  public static final String TAG = UtilSDk.class.getSimpleName();
  public static boolean DEBUG = false;
  public static boolean DEBUG_BUILD = false;

  public static void init(Context context, boolean debug, boolean debugBuild) {
    ContextHolder.init(context);
    LogUtils.setDebugable(debugBuild);
    DEBUG = debug;
    DEBUG_BUILD = debugBuild;
  }
}
