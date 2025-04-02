package com.hello.sandbox.common.util;

import android.app.Application;
import android.content.Context;

public class ContextHolder {

  public static boolean hadInit = false;
  private static Application context;

  public static void init(Context context) {
    if (context instanceof Application) {
      ContextHolder.context = (Application) context;
    } else {
      ContextHolder.context = (Application) context.getApplicationContext();
    }
    hadInit = true;
  }

  public static Application context() {
    if (hadInit) {
      return context;
    } else {
      throw new RuntimeException("you should call init first!");
    }
  }
}
