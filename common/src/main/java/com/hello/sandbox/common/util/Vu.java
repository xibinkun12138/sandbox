package com.hello.sandbox.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import com.hello.sandbox.common.util.ContextHolder;
import com.hello.sandbox.common.util.CrashHelper;
import java.lang.reflect.Field;

public class Vu {

  public static boolean isInfinityOrNaN(float value) {
    return value == 1.0F / 0.0 || value == -1.0F / 0.0 || Float.compare(value, 0.0F / 0.0f) == 0;
  }

  public static boolean isInfinityOrNaN(double value) {
    return value == 1.0D / 0.0 || value == -1.0D / 0.0 || Double.compare(value, 0.0D / 0.0) == 0;
  }

  public static Activity getActivityFromContext(Context context) {
    while (context instanceof ContextWrapper) {
      if (context instanceof Activity) {
        return (Activity) context;
      }
      context = ((ContextWrapper) context).getBaseContext();
    }
    return null;
  }

  public static void addCompoundDrawableLeft(TextView view, Drawable drawable) {
    addCompoundDrawables(view, drawable, null, null, null);
  }

  public static void addCompoundDrawableRight(TextView view, Drawable drawable) {
    addCompoundDrawables(view, null, null, drawable, null);
  }

  public static void addCompoundDrawables(
      TextView view, Drawable left, Drawable top, Drawable right, Drawable bottom) {
    Drawable[] old = view.getCompoundDrawables();
    if (old == null) {
      view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    } else {
      view.setCompoundDrawablesWithIntrinsicBounds(
          left == null ? old[0] : left,
          top == null ? old[1] : top,
          right == null ? old[2] : right,
          bottom == null ? old[3] : bottom);
    }
  }

  public static final boolean BIG_OVERSCROLL = getBigOverscroll();

  public static final boolean getBigOverscroll() {
    try {
      return Build.BRAND.equals("Meizu")
          || Math.max(
                  ViewConfiguration.get(ContextHolder.context()).getScaledOverscrollDistance(),
                  ViewConfiguration.get(ContextHolder.context()).getScaledOverflingDistance())
              > MetricsUtil.dp(12);
    } catch (Exception e) {
    }
    return false;
  }

  public static int screenWidth() {
    return ContextHolder.context().getResources().getDisplayMetrics().widthPixels;
  }

  public static void gone(View v, boolean show) {
    v.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  public static int screenHeight() {
    return ContextHolder.context().getResources().getDisplayMetrics().heightPixels;
  }

  public static void setTextViewTextCursorDrawable(
      TextView textView, @DrawableRes int textCursorDrawable) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      try {
        Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
        mCursorDrawableRes.setAccessible(true);
        mCursorDrawableRes.set(textView, textCursorDrawable);
      } catch (NoSuchFieldException | IllegalAccessException e) {
        CrashHelper.reportError(e);
      }
    } else {
      textView.setTextCursorDrawable(textCursorDrawable);
    }
  }
}
