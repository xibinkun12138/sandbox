package com.hello.sandbox.common.util;

import android.view.View;

/** User: molikto Date: 01/04/15 Time: 18:15 */
public class MeasureUtil {

  public static int size(int i) {
    return View.MeasureSpec.getSize(i);
  }

  public static int mode(int i) {
    return View.MeasureSpec.getMode(i);
  }

  public static int exactly(int i) {
    return View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.EXACTLY);
  }

  public static int atMost(int i) {
    return View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.AT_MOST);
  }

  public static int unspecified() {
    return View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
  }
}
