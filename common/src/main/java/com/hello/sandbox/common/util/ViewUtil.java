package com.hello.sandbox.common.util;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import com.hello.sandbox.common.rx.RxLogHelper;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.joor.Reflect;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/** User: molikto Date: 12/29/14 Time: 18:25 */
public class ViewUtil {

  public static final boolean getBigOverscroll() {
    try {
      return Build.BRAND.equals("Meizu")
          || Math.max(
                  ViewConfiguration.get(ContextHolder.context()).getScaledOverscrollDistance(),
                  ViewConfiguration.get(ContextHolder.context()).getScaledOverflingDistance())
              > dp(12);
    } catch (Exception e) {
      CrashHelper.reportError(new Exception("Vu getBigOverscroll :" + e.getMessage(), e));
    }
    return false;
  }

  public static int dp(float p) {
    return (int) (ContextHolder.context().getResources().getDisplayMetrics().density * p);
  }

  public static final boolean BIG_OVERSCROLL = getBigOverscroll();

  public static void singleClickListener(View v, View.OnClickListener onClickListenr) {
    if (onClickListenr != null) {
      v.setOnClickListener(
          new View.OnClickListener() {
            long lastClick;

            @Override
            public void onClick(View v) {
              if (SystemClock.uptimeMillis() - lastClick > 500) {
                lastClick = SystemClock.uptimeMillis();
                onClickListenr.onClick(v);
              }
            }
          });
    } else {
      v.setOnClickListener(null);
    }
  }

  private static long globalLastClick;

  public static void globalSingleClickListener(
      View v, @NonNull View.OnClickListener clickListener) {
    v.setOnClickListener(
        v1 -> {
          if (SystemClock.uptimeMillis() - globalLastClick < 500) {
            return;
          }
          globalLastClick = SystemClock.uptimeMillis();
          clickListener.onClick(v1);
        });
  }

  public static void doubleClickListener(View v, Action1<View> onDoubleClick) {
    v.setOnClickListener(
        new View.OnClickListener() {
          long lastClick = SystemClock.uptimeMillis();

          @Override
          public void onClick(View v) {
            if (Math.abs(SystemClock.uptimeMillis() - lastClick) < 500) {
              onDoubleClick.call(v);
            } else {
              lastClick = SystemClock.uptimeMillis();
            }
          }
        });
  }

  public static void fadeIn(Window window, int delay, int duration) {
    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.alpha = 0;
    float finalDim = lp.dimAmount;
    lp.dimAmount = 0;
    window.setAttributes(lp);
    animator.addUpdateListener(
        animation -> {
          WindowManager.LayoutParams lp1 = window.getAttributes();
          lp1.alpha = animation.getAnimatedFraction();
          lp1.dimAmount = finalDim;
          window.setAttributes(lp);
        });
    animator.setStartDelay(delay);
    animator.setDuration(duration);
    animator.setInterpolator(new DecelerateInterpolator(1.5f));
    animator.start();
  }

  public static TextView textView(View view) {
    if (view instanceof ViewGroup) {
      ViewGroup v = (ViewGroup) view;
      for (int i = 0; i < v.getChildCount(); i++) {
        TextView r = textView(v.getChildAt(i));
        if (r != null) return r;
      }
    } else if (view instanceof TextView) {
      return (TextView) view;
    }
    return null;
  }

  public static void withMeasureNonZeroSize(final View view, final Action1<int[]> action) {
    if (view.getHeight() == 0 || view.getWidth() == 0) {
      view.addOnLayoutChangeListener(
          new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(
                View v,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
              int oldWidth = oldRight - oldLeft;
              int oldHeight = oldBottom - oldTop;
              int width = right - left;
              int height = bottom - top;
              boolean changed = oldWidth != width || oldHeight != height;
              if (changed && width != 0 && height != 0) {
                action.call(new int[] {width, height});
                view.removeOnLayoutChangeListener(this);
              }
            }
          });
    } else {
      runOnUiThread(
          () -> {
            action.call(new int[] {view.getWidth(), view.getHeight()});
          });
    }
  }

  private static final Handler handler = new Handler(Looper.getMainLooper());

  public static void runOnUiThread(Runnable r) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      r.run();
    } else {
      handler.post(r);
    }
  }

  public static int MP = LayoutParams.MATCH_PARENT;
  public static int WC = LayoutParams.WRAP_CONTENT;

  public static void drawText(Canvas canvas, String t) {
    TextPaint p = new TextPaint();
    p.setColor(Color.BLACK);
    p.setAlpha(255);
    p.setTextSize(dp(48));
    canvas.drawText(t, 0, canvas.getHeight(), p);
  }

  public static void gone(View v, boolean show) {
    v.setVisibility(show ? View.VISIBLE : View.GONE);
  }

  public static void textOrGone(TextView v, CharSequence chars) {
    v.setText(chars);
    gone(v, !TextUtils.isEmpty(chars));
  }

  public static boolean visible(View v) {
    return v.getVisibility() == View.VISIBLE;
  }

  public static void visibility(View v, boolean show) {
    if (show) {
      if (v.getVisibility() != View.VISIBLE) v.setVisibility(View.VISIBLE);
    } else if (v.getVisibility() == View.VISIBLE) {
      v.setVisibility(View.INVISIBLE);
    }
  }

  public static void enabled(MenuItem item, boolean enabled) {
    item.setEnabled(enabled);
    Drawable d = item.getIcon();
    d.setAlpha((int) (255 * (!enabled ? 0.3f : 1)));
    item.setIcon(d);
  }

  public static void remove(View view) {
    if (view.getParent() instanceof ViewGroup) {
      ((ViewGroup) view.getParent()).removeView(view);
    }
  }

  public static void scale(View v, float scale) {
    if (isInfinityOrNaN(scale)) return;
    v.setScaleX(scale);
    v.setScaleY(scale);
  }

  public static int visibleChildCount(ViewGroup g) {
    int i = 0;
    for (int j = 0; j < g.getChildCount(); j++) {
      if (g.getChildAt(j).getVisibility() == View.VISIBLE) {
        i++;
      }
    }
    return i;
  }

  public static boolean isAbove(View v, View c) {
    ArrayList<Integer> pathA = path(v);
    ArrayList<Integer> pathB = path(c);
    for (int i = 0; i < Math.min(pathA.size(), pathB.size()); i++) {
      if (pathA.get(i) < pathB.get(i)) {
        return true;
      }
    }
    return false;
  }

  public static ArrayList<Integer> path(View v) {
    ArrayList<Integer> arr = new ArrayList<>();
    View c = v;
    while (c != v.getRootView()) {
      ViewGroup p = (ViewGroup) c.getParent();
      arr.add(0, indexOf(p, c));
      c = p;
    }
    return arr;
  }

  public static int indexOf(View c) {
    return indexOf((ViewGroup) c.getParent(), c);
  }

  public static int indexOf(ViewGroup p, View c) {
    for (int i = 0; i < p.getChildCount(); i++) {
      if (p.getChildAt(i) == c) {
        return i;
      }
    }
    return -1;
  }

  public static void transformByPos(View view, Pos fromPos, Pos toPos) {
    transformByPos(view, fromPos, toPos, false);
  }

  public static void transformByPos(View view, Pos fromPos, Pos toPos, boolean keepSize) {
    view.setPivotX(0);
    view.setPivotY(0);
    float scaleX = 1f * fromPos.w / toPos.w;
    float scaleY = 1f * fromPos.h / toPos.h;
    if (!isInfinityOrNaN(scaleX) && !isInfinityOrNaN(scaleY)) {
      if (keepSize) {
        if (scaleY >= scaleX) {
          view.setScaleX(scaleY);
          view.setScaleY(scaleY);
          view.setTranslationX(fromPos.x - toPos.x - (toPos.w * scaleY - fromPos.w) / 2);
          view.setTranslationY(fromPos.y - toPos.y);
        } else {
          view.setScaleX(scaleX);
          view.setScaleY(scaleX);
          view.setTranslationX(fromPos.x - toPos.x);
          view.setTranslationY(fromPos.y - toPos.y - (toPos.h * scaleX - fromPos.h) / 2);
        }
      } else {
        view.setScaleX(scaleX);
        view.setScaleY(scaleY);
        view.setTranslationX(fromPos.x - toPos.x);
        view.setTranslationY(fromPos.y - toPos.y);
      }
    }
  }

  public static void noClipUntilContent(View view) {
    Object v = view.getParent();
    while (v instanceof ViewGroup && ((ViewGroup) v).getId() != android.R.id.content) {
      ((ViewGroup) v).setClipChildren(false);
      ((ViewGroup) v).setClipToPadding(false);
      v = ((ViewGroup) v).getParent();
    }
  }

  public static void clipUntilContent(View view) {
    Object v = view.getParent();
    while (v instanceof ViewGroup && ((ViewGroup) v).getId() != android.R.id.content) {
      ((ViewGroup) v).setClipChildren(true);
      ((ViewGroup) v).setClipToPadding(true);
      v = ((ViewGroup) v).getParent();
    }
  }

  public static void noClipAll(View view) {
    Object v = view.getParent();
    while (v instanceof ViewGroup) {
      ((ViewGroup) v).setClipChildren(false);
      ((ViewGroup) v).setClipToPadding(false);
      v = ((ViewGroup) v).getParent();
    }
  }

  public static void setDrawableEnabledState(Drawable d, boolean b) {
    if (d == null) return;
    if (d instanceof LayerDrawable) {
      LayerDrawable layer = (LayerDrawable) d;
      for (int i = 0; i < layer.getNumberOfLayers(); i++) {
        setDrawableEnabledState(layer.getDrawable(i), b);
      }
    } else if (d instanceof StateListDrawable) {
      StateListDrawable sld = (StateListDrawable) d;
      int[] state = sld.getState();
      if (b && Arrays.binarySearch(state, android.R.attr.state_enabled) < 0) {
        state = Arrays.copyOf(state, state.length + 1);
        state[state.length - 1] = android.R.attr.state_enabled;
      } else if (!b && Arrays.binarySearch(state, android.R.attr.state_enabled) >= 0) {
        int[] newState = new int[state.length - 1];
        int io = 0;
        int in = 0;
        while (io < state.length) {
          if (state[io] != android.R.attr.state_enabled) {
            newState[in] = state[io];
            in++;
          }
          io++;
        }
        state = newState;
      }
      sld.setState(state);
    }
  }

  public static void setBackgroundEnabledState(View v, boolean b) {
    setDrawableEnabledState(v.getBackground(), b);
  }

  public static void downAndCancelTouchEvent(View scroll) {
    try {
      long time = SystemClock.uptimeMillis();
      scroll.dispatchTouchEvent(
          MotionEvent.obtain(
              time,
              time,
              MotionEvent.ACTION_DOWN,
              scroll.getWidth() / 2,
              scroll.getHeight() / 2,
              0));
      scroll.dispatchTouchEvent(
          MotionEvent.obtain(
              time,
              time,
              MotionEvent.ACTION_CANCEL,
              scroll.getWidth() / 2,
              scroll.getHeight() / 2,
              0));
    } catch (Exception e) {
      com.hello.sandbox.common.util.CrashHelper.reportError(
          new Exception("Vu downAndCancelTouchEvent:" + e.getMessage(), e));
    }
  }

  public static void downTouchEvent(View scroll) {
    try {
      long time = SystemClock.uptimeMillis();
      scroll.dispatchTouchEvent(
          MotionEvent.obtain(
              time,
              time,
              MotionEvent.ACTION_DOWN,
              scroll.getWidth() / 2,
              scroll.getHeight() / 2,
              0));
      scroll.dispatchTouchEvent(
          MotionEvent.obtain(
              time,
              time,
              MotionEvent.ACTION_MOVE,
              scroll.getWidth() / 2,
              scroll.getHeight() / 2,
              0));
    } catch (Exception e) {
      com.hello.sandbox.common.util.CrashHelper.reportError(
          new Exception("Vu downTouchEvent:" + e.getMessage(), e));
    }
  }

  public static void downAndUpEvent(View scroll) {
    try {
      long time = SystemClock.uptimeMillis();
      scroll.dispatchTouchEvent(
          MotionEvent.obtain(
              time,
              time,
              MotionEvent.ACTION_DOWN,
              scroll.getWidth() / 2,
              scroll.getHeight() / 2,
              0));
      scroll.dispatchTouchEvent(
          MotionEvent.obtain(
              time, time, MotionEvent.ACTION_UP, scroll.getWidth() / 2, scroll.getHeight() / 2, 0));
    } catch (Exception e) {
      com.hello.sandbox.common.util.CrashHelper.reportError(
          new Exception("Vu downAndUpEvent:" + e.getMessage(), e));
    }
  }

  public static void reLayoutNowWrapContentWithOldLeftTop(View view) {
    view.measure(MeasureUtil.unspecified(), MeasureUtil.unspecified());
    view.layout(
        view.getLeft(),
        view.getTop(),
        view.getLeft() + view.getMeasuredWidth(),
        view.getTop() + view.getMeasuredHeight());
  }

  public static void reMeasureAndLayoutWithGivenPos(View view, int x, int y, boolean reverse) {
    view.measure(MeasureUtil.unspecified(), MeasureUtil.unspecified());
    if (reverse) {
      view.layout(x - view.getMeasuredWidth(), y, x, y + view.getMeasuredHeight());
    } else {
      view.layout(x, y, x + view.getMeasuredWidth(), y + view.getMeasuredHeight());
    }
  }

  public static void reLayoutNowWrapContentWithOldRightTop(View view) {
    view.measure(MeasureUtil.unspecified(), MeasureUtil.unspecified());
    view.layout(
        view.getRight() - view.getMeasuredWidth(),
        view.getTop(),
        view.getRight(),
        view.getTop() + view.getMeasuredHeight());
  }

  public static void reLayoutNowWithNewTopOldCenterX(
      View view, int newTop, boolean wrapWidth, boolean wrapHeight) {
    int oldCenterX = (view.getLeft() + view.getRight()) / 2;
    view.measure(
        wrapWidth ? MeasureUtil.unspecified() : MeasureUtil.exactly(view.getWidth()),
        wrapHeight ? MeasureUtil.unspecified() : MeasureUtil.exactly(view.getHeight()));
    view.layout(
        oldCenterX - view.getMeasuredWidth() / 2,
        newTop,
        oldCenterX + view.getMeasuredWidth() / 2,
        newTop + view.getMeasuredHeight());
  }

  public static void reLayoutNowByExactOriginalSize(View view) {
    view.measure(MeasureUtil.exactly(view.getWidth()), MeasureUtil.exactly(view.getHeight()));
    view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
  }

  public static void reLayoutNowWithWrapHeightAndOldLeftAndBottom(View view) {
    if (LayoutParams.WRAP_CONTENT == view.getLayoutParams().height) {
      view.measure(MeasureUtil.exactly(view.getWidth()), MeasureUtil.unspecified());
    } else {
      view.measure(MeasureUtil.exactly(view.getWidth()), MeasureUtil.exactly(view.getHeight()));
    }
    view.layout(
        view.getLeft(),
        view.getBottom() - view.getMeasuredHeight(),
        view.getLeft() + view.getMeasuredWidth(),
        view.getBottom());
  }

  public static void reLayoutNowWithTranslationXY(View view, int translationX, int translationY) {
    view.layout(
        view.getLeft() + translationX,
        view.getTop() + translationY,
        view.getRight() + translationX,
        view.getBottom() + translationY);
  }

  public static void ensureFocus(View v) {
    ensureFocus(v, true);
  }

  public static void ensureFocus(View v, boolean focus) {
    if (focus) {
      if (!v.hasFocus()) {
        v.requestFocus();
      }
    } else {
      if (v.hasFocus()) {
        v.clearFocus();
      }
    }
  }

  public static void imageSrc(ImageView view, int i) {
    Drawable d = view.getContext().getResources().getDrawable(i);
    if (view.getDrawable() == null || view.getDrawable() != d) {
      view.setImageDrawable(d);
    }
  }

  public static void addPadding(View v, int i, int i1, int dp, int i2) {
    v.setPadding(
        v.getPaddingLeft() + i,
        v.getPaddingTop() + i1,
        v.getPaddingRight() + dp,
        v.getPaddingBottom() + i2);
  }

  public static void scalePadding(View v, float scale) {
    v.setPadding(
        (int) (v.getPaddingLeft() * scale),
        (int) (v.getPaddingTop() * scale),
        (int) (v.getPaddingRight() * scale),
        (int) (v.getPaddingBottom() * scale));
  }

  public static Bitmap bitmap(ImageView image) {
    if (image.getDrawable() instanceof BitmapDrawable) {
      return ((BitmapDrawable) image.getDrawable()).getBitmap();
    } else {
      return null;
    }
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

  public static void addCompoundDrawableLeft(TextView view, Drawable drawable) {
    addCompoundDrawables(view, drawable, null, null, null);
  }

  public static void addCompoundDrawableRight(TextView view, Drawable drawable) {
    addCompoundDrawables(view, null, null, drawable, null);
  }

  static int statusBarHeight = -1;

  /** 默认不需要主动计算stausBar的高度。为了减少计算 */
  public static int statusBarHeight() {
    return statusBarHeight(false);
  }

  /**
   * 这个是为了计算statusBar的高度。两种方式可以重新计算statusBar的高度： 1）第一次计算高度，及高度等于默认值
   * 2）isNeedRefresh等于True，及需要主动刷新statusBar的高度
   *
   * @param isNeedRefresh 是否需要主动刷新statusBar的高度
   * @return statusBar的高度
   */
  public static int statusBarHeight(boolean isNeedRefresh) {
    if (statusBarHeight == -1 || isNeedRefresh) {
      Context c = ContextHolder.context();
      int resourceId =
          c.getResources().getIdentifier("android:dimen/status_bar_height", "dimen", "android");
      if (resourceId > 0) {
        statusBarHeight = c.getResources().getDimensionPixelSize(resourceId);
      }
    }
    return statusBarHeight;
  }

  public static void marginRight(View v, int px) {
    LayoutParams lp = v.getLayoutParams();
    if (lp instanceof MarginLayoutParams) {
      ((MarginLayoutParams) lp).rightMargin = px;
    }
    v.setLayoutParams(lp);
  }

  public static void marginLeft(View v, int px) {
    LayoutParams lp = v.getLayoutParams();
    if (lp instanceof MarginLayoutParams) {
      ((MarginLayoutParams) lp).leftMargin = px;
    }
    v.setLayoutParams(lp);
  }

  public static void marginTop(View v, int px) {
    LayoutParams lp = v.getLayoutParams();
    if (lp instanceof MarginLayoutParams) {
      ((MarginLayoutParams) lp).topMargin = px;
    }
    v.setLayoutParams(lp);
  }

  public static void marginBottom(View v, int px) {
    LayoutParams lp = v.getLayoutParams();
    if (lp instanceof MarginLayoutParams) {
      ((MarginLayoutParams) lp).bottomMargin = px;
    }
    v.setLayoutParams(lp);
  }

  public static void setHeight(View v, int px) {
    LayoutParams lp = v.getLayoutParams();
    lp.height = px;
    v.setLayoutParams(lp);
  }

  public static void paddings(View login_new, int dp) {
    login_new.setPadding(dp, dp, dp, dp);
  }

  public static void goneChildWithoutIgnoreId(
      ViewGroup viewGroup, @IdRes Integer... ignoreChildIds) {
    if (viewGroup == null
        || viewGroup.getChildCount() == 0
        || ignoreChildIds == null
        || ignoreChildIds.length == 0) {
      return;
    }
    int childCount = viewGroup.getChildCount();
    List<Integer> ids = Cu.seq(ignoreChildIds);
    HashSet<Integer> childIdSet = new HashSet<>(ids);
    for (int i = 0; i < childCount; i++) {
      View child = viewGroup.getChildAt(i);
      if (!childIdSet.contains(child.getId())) {
        gone(child, false);
      }
    }
  }

  public static class Pos {
    public int x;
    public int y;
    public int w;
    public int h;

    @Override
    public String toString() {
      if (UtilSDk.DEBUG_BUILD) {
        return "x = " + x + ", y = " + y + ", w = " + w + ", h = " + h;
      }
      return super.toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof Pos) {
        Pos temp = (Pos) obj;
        return temp.x == x && temp.h == h && temp.w == w && temp.y == y;
      }
      return false;
    }
  }

  public static Pos pos(View v) {
    View root = getActivityFromView(v).getWindow().getDecorView();
    return pos(v, root);
  }

  /**
   * 使用的android.support.design.widget或者v7中的view获取的context并不是Activity，之前直接强转的话有问题，需要替换下
   *
   * @param view
   * @return
   */
  public static Activity getActivityFromView(View view) {
    return getActivityFromContext(view.getContext());
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

  public static Pos pos(View v, View root) {
    Pos pos = new Pos();
    View c = v;
    pos.w = v.getWidth();
    pos.h = v.getHeight();
    while (c != null && c != root) {
      pos.x += c.getLeft() - c.getScrollX() + c.getTranslationX();
      pos.y += c.getTop() - c.getScrollY() + c.getTranslationY();
      if (c.getParent() instanceof View) {
        c = (View) c.getParent();
      } else {
        c = null;
      }
    }
    return pos;
  }

  public static int relativeTop(View c, View root) {
    int top = 0;
    while (c != root) {
      top += c.getTop();
      c = (View) c.getParent();
    }
    return top;
  }

  public static int relativeBottom(View c, View root) {
    int bottom = 0;
    while (c != root) {
      bottom += c.getBottom();
      c = (View) c.getParent();
    }
    return bottom;
  }

  public static void visibilityByText(TextView t) {
    gone(t, t.getText() != null && t.getText().length() > 0);
  }

  public static boolean text(TextView t, String s) {
    if (!t.getText().equals(s)) {
      t.setText(s);
      return true;
    }
    return false;
  }

  public static int screenWidth() {
    return ContextHolder.context().getResources().getDisplayMetrics().widthPixels;
  }

  public static int screenHeight() {
    return ContextHolder.context().getResources().getDisplayMetrics().heightPixels;
  }

  public static int screenHeight(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    if (context instanceof Activity && VERSION.SDK_INT >= 17) {
      ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(dm);
      return dm.heightPixels;
    }
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  public static float scaledDensity(Context context) {
    DisplayMetrics dm = new DisplayMetrics();
    if (context instanceof Activity && VERSION.SDK_INT >= 17) {
      ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(dm);
      return dm.scaledDensity;
    }
    return context.getResources().getDisplayMetrics().scaledDensity;
  }

  public static int screenWidth(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  public static boolean inBound(View view, MotionEvent event) {
    return event.getX() > 0
        && event.getY() > 0
        && event.getX() < view.getWidth()
        && event.getY() < view.getHeight();
  }

  public static void enableAndClickable(View v, boolean b) {
    v.setEnabled(b);
    v.setClickable(b);
  }

  public static void paddingTop(View v, int padding) {
    v.setPadding(v.getPaddingLeft(), padding, v.getPaddingRight(), v.getPaddingBottom());
  }

  public static void paddingBottom(View v, int padding) {
    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), padding);
  }

  public static void paddingRight(View v, int padding) {
    v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), padding, v.getPaddingBottom());
  }

  public static void paddingLeft(View v, int padding) {
    v.setPadding(padding, v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom());
  }

  public static void paddingTopForStatusBar(View... views) {
    if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) return;
    Observable.from(views)
        .doOnNext(
            v -> {
              v.setPadding(
                  v.getPaddingLeft(),
                  v.getPaddingTop() + statusBarHeight(),
                  v.getPaddingRight(),
                  v.getPaddingBottom());
            })
        .subscribe();
  }

  public static void marginTopForStatusBar(View... views) {
    if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) return;
    Observable.from(views)
        .doOnNext(
            v -> {
              MarginLayoutParams marginParams = null;
              try {
                marginParams = (MarginLayoutParams) v.getLayoutParams();
              } catch (ClassCastException ignored) {
                marginParams = new MarginLayoutParams(v.getWidth(), v.getHeight());
              }

              marginParams.topMargin += statusBarHeight();
              v.setLayoutParams(marginParams);
            })
        .subscribe();
  }

  public static void marginTopForActionBar(int actionBarHeight, View... views) {
    Observable.from(views)
        .doOnNext(
            v -> {
              MarginLayoutParams marginParams = null;
              try {
                marginParams = (MarginLayoutParams) v.getLayoutParams();
              } catch (ClassCastException ignored) {
                marginParams = new MarginLayoutParams(v.getWidth(), v.getHeight());
              }

              marginParams.topMargin += actionBarHeight;
              v.setLayoutParams(marginParams);
            })
        .subscribe();
  }

  public static void resizeForStatusBar(View... views) {
    if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) return;
    Observable.from(views)
        .doOnNext(
            v -> {
              LayoutParams params = v.getLayoutParams();
              if (params == null) params = new LayoutParams(v.getWidth(), v.getHeight());
              params.height += statusBarHeight();
              v.setLayoutParams(params);
            })
        .subscribe();
  }

  public static void resizeForActionBar(int actionBarHeight, View... views) {
    if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) return;
    Observable.from(views)
        .doOnNext(
            v -> {
              LayoutParams params = v.getLayoutParams();
              if (params == null) params = new LayoutParams(v.getWidth(), v.getHeight());
              params.height += actionBarHeight;
              v.setLayoutParams(params);
            })
        .subscribe();
  }

  public static void setWidth(int width, View... views) {
    if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) return;
    Observable.from(views)
        .doOnNext(
            v -> {
              LayoutParams params = v.getLayoutParams();
              if (params == null) params = new LayoutParams(v.getWidth(), v.getHeight());
              params.width = width;
              v.setLayoutParams(params);
            })
        .subscribe();
  }

  public static int depth(View v) {
    if (v instanceof ViewGroup) {
      ArrayList<Integer> i = Cu.map(ViewUtil.childs((ViewGroup) v), c -> depth(c) + 1);
      return i.size() == 0 ? 1 : Collections.max(i);
    }
    return 1;
  }

  public static Collection<View> childs(ViewGroup v) {
    return Cu.map(Cu.range(v.getChildCount()), i -> v.getChildAt(i));
  }

  public static View childsFindRecursive(ViewGroup v, Func1<View, Boolean> filter) {
    return childsFindRecursiveInner(v, filter);
  }

  public static void getChildRectRaw(View v, Rect window, Rect temp) {
    v.getGlobalVisibleRect(temp);
    temp.top = Math.abs(temp.top);
    temp.bottom = Math.abs(temp.bottom);
    temp.left = Math.abs(temp.left);
    temp.right = Math.abs(temp.right);
    if (temp.top > temp.bottom) {
      int t = temp.top;
      temp.top = temp.bottom;
      temp.bottom = t;
    }
    if (temp.left > temp.right) {
      int t = temp.left;
      temp.left = temp.right;
      temp.right = t;
    }
    temp.top += window.top;
    temp.left += window.left;
    temp.bottom += window.top;
    temp.right += window.left;
  }

  public static View childsFindRecursiveInner(View v, Func1<View, Boolean> filter) {
    if (filter.call(v)) {
      return v;
    } else if (v instanceof ViewGroup) {
      ViewGroup g = (ViewGroup) v;
      for (int i = 0; i < g.getChildCount(); i++) {
        View k = childsFindRecursiveInner(g.getChildAt(i), filter);
        if (k != null) {
          return k;
        }
      }
      return null;
    }
    return null;
  }

  public static List<View> childsRecursive(ViewGroup v) {
    return Cu.flatMap(
        Cu.range(v.getChildCount()),
        i -> {
          View c = v.getChildAt(i);
          if (c instanceof ViewGroup) {
            return Cu.add(childsRecursive((ViewGroup) c), c);
          } else {
            return Collections.singletonList(c);
          }
        });
  }

  public static Bitmap drawViewToBitmap(View view, int downSampling) {
    float scale = 1f / downSampling;
    int vwidth = view.getMeasuredWidth();
    int vheight = view.getMeasuredHeight();
    int bmpWidth = (int) (vwidth * scale);
    int bmpHeight = (int) (vheight * scale);
    Bitmap dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(dest);
    if (downSampling > 1) {
      c.scale(scale, scale);
    }
    view.draw(c);
    return dest;
  }

  public static void onPreDrawOnce(View view, Func0<Boolean> func) {
    ViewTreeObserver vto = view.getViewTreeObserver();
    vto.addOnPreDrawListener(
        new ViewTreeObserver.OnPreDrawListener() {
          boolean called = false;

          @Override
          public boolean onPreDraw() {
            if (!called) {
              called = true;
              if (vto.isAlive()) vto.removeOnPreDrawListener(this);
              return func.call();
            }
            return true;
          }
        });
  }

  public static ImageView imageView(View v) {
    if (v instanceof ViewGroup) {
      ViewGroup g = (ViewGroup) v;
      for (int i = 0; i < g.getChildCount(); i++) {
        ImageView image = imageView(g.getChildAt(i));
        if (image != null) {
          return image;
        }
      }
    } else if (v instanceof ImageView) {
      return (ImageView) v;
    }
    return null;
  }

  public static ColorFilter saturationColorFilter(float i) {
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(i);
    return new ColorMatrixColorFilter(matrix);
  }

  public static View findVisibleViewBy(Activity act, Func1<View, Boolean> pred) {
    View root = act.getWindow().getDecorView();
    return findVisibleViewBy(root, pred);
  }

  private static View findVisibleViewBy(View root, Func1<View, Boolean> pred) {
    if (root.getVisibility() == View.VISIBLE) {
      if (pred.call(root)) {
        return root;
      } else if (root instanceof ViewGroup) {
        ViewGroup g = (ViewGroup) root;
        for (int i = 0; i < g.getChildCount(); i++) {
          View v = findVisibleViewBy(g.getChildAt(i), pred);
          if (v != null) return v;
        }
      }
    }
    return null;
  }

  public static void callOnClick(View v) {
    if (VERSION.SDK_INT >= 15) {
      v.callOnClick();
    } else {
      Object o = Reflect.on(v).field("mListenerInfo").get();
      if (o != null) {
        Object l = Reflect.on(o).field("mOnClickListener");
        if (l != null) {
          ((View.OnClickListener) l).onClick(v);
        }
      }
    }
  }

  public static void drawSingleDrawableCentered(Canvas canvas, Drawable drawable) {
    int mw = canvas.getWidth() / 2;
    int mh = canvas.getHeight() / 2;
    int playSize = Math.min(Math.min(drawable.getIntrinsicWidth() / 2, mw), mh);
    drawable.setBounds(mw - playSize, mh - playSize, mw + playSize, mh + playSize);
    drawable.draw(canvas);
  }

  public static Drawable replaceWindowContentOverlay(Activity act, Drawable d) {
    View contentView = act.findViewById(android.R.id.content);

    // Make sure it's a valid instance of a FrameLayout
    if (contentView instanceof FrameLayout) {
      FrameLayout fl = (FrameLayout) contentView;
      Drawable res = fl.getForeground();
      fl.setForeground(d);
      return res;
    }
    return null;
  }

  public static boolean isNavigationBarAvailable() {
    boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
    boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
    return (!(hasBackKey && hasHomeKey));
  }

  public static Point getAppUsableScreenSize(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    return size;
  }

  public static Point getRealScreenSize(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = windowManager.getDefaultDisplay();
    Point size = new Point();
    if (VERSION.SDK_INT >= 17) {
      display.getRealSize(size);
    } else if (VERSION.SDK_INT >= 14) {
      try {
        size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
        size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
      } catch (NoSuchMethodException e) {
        com.hello.sandbox.common.util.CrashHelper.reportError(e);
      } catch (SecurityException e) {
        com.hello.sandbox.common.util.CrashHelper.reportError(e);
      } catch (IllegalAccessException e) {
        com.hello.sandbox.common.util.CrashHelper.reportError(e);
      } catch (InvocationTargetException e) {
        com.hello.sandbox.common.util.CrashHelper.reportError(e);
      }
    }
    return size;
  }

  public static void enableTextMarquee(TextView textView) {
    textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    textView.setSingleLine(true);
    textView.setSelected(true);
    textView.setFocusable(true);
    textView.setFocusableInTouchMode(true);
    textView.setMarqueeRepeatLimit(-1);
    textView.setHorizontallyScrolling(true);
  }

  public static void addResBgToAct(Activity context, int res) {
    ImageView imageView = new ImageView(context);
    imageView.setBackgroundResource(res);
    ((ViewGroup) context.getWindow().getDecorView()).addView(imageView, 0);
    imageView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
    imageView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
  }

  public static int getNavigateHeight(Context context) {
    try {
      Resources resources = context.getResources();
      int resId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
      if (resId > 0) return resources.getDimensionPixelSize(resId);
    } catch (Exception e) {
      CrashHelper.reportError(e);
    }
    return 0;
  }

  public static int getActionBarHeight(Context context) {
    TypedValue tv = new TypedValue();
    if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      return TypedValue.complexToDimensionPixelSize(
          tv.data, context.getResources().getDisplayMetrics());
    }
    return 0;
  }

  public static Observable<View> throttleForViewClick(final View v) {
    return Observable.create(new ViewClickOnSubscribe(v))
        .compose(RxLogHelper.subscribeOnTransformer(AndroidSchedulers.mainThread()))
        .observeOn(AndroidSchedulers.mainThread());
  }

  private static final class ViewClickOnSubscribe implements OnSubscribe<View> {
    View v;
    long lastClickTime;
    long itl = 500;

    ViewClickOnSubscribe(View view) {
      v = view;
    }

    ViewClickOnSubscribe(View view, long interval) {
      v = view;
      itl = interval;
    }

    @Override
    public void call(Subscriber<? super View> subscriber) {
      View.OnClickListener listener =
          v -> {
            if (!subscriber.isUnsubscribed()) {
              long current = System.currentTimeMillis();
              if (Math.abs(current - lastClickTime) > itl) {
                subscriber.onNext(v);
                lastClickTime = current;
              }
            }
          };
      v.setOnClickListener(listener);
      subscriber.add(Subscriptions.create(() -> v.setOnClickListener(null)));
    }
  }

  // 屏幕密度dpi
  public static int screenDensityDpi() {
    return ContextHolder.context().getResources().getDisplayMetrics().densityDpi;
  }

  private static long lastClickTime = 0;

  public static boolean isFastDoubleClick() {
    long newTime = SystemClock.elapsedRealtime();
    long timeD = newTime - lastClickTime;
    if (lastClickTime > 0 && timeD < 500) {
      return true;
    }
    lastClickTime = newTime;
    return false;
  }

  // 判断浮点型数字是否是正负无穷活着NAN
  public static boolean isInfinityOrNaN(float value) {
    return (value == Float.POSITIVE_INFINITY)
        || (value == Float.NEGATIVE_INFINITY)
        || (Float.compare(value, Float.NaN) == 0);
  }

  public static boolean isInfinityOrNaN(double value) {
    return (value == Double.POSITIVE_INFINITY)
        || (value == Double.NEGATIVE_INFINITY)
        || (Double.compare(value, Double.NaN) == 0);
  }

  public static Observable<Integer> getWidthObs(View view) {
    Observable<Integer> ob =
        Observable.create(
            new OnSubscribe<Integer>() {
              @Override
              public void call(Subscriber<? super Integer> subscriber) {
                OnGlobalLayoutListener listener =
                    () -> {
                      final int width = view.getWidth();
                      subscriber.onNext(width);
                    };
                if (!subscriber.isUnsubscribed()) {
                  view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
                }
                subscriber.add(
                    Subscriptions.create(
                        () -> view.getViewTreeObserver().removeGlobalOnLayoutListener(listener)));
              }
            });
    return ob.compose(RxLogHelper.subscribeOnTransformer(AndroidSchedulers.mainThread()));
  }

  /** 给view设置背景 */
  public static void setBackground(Context context, View view, @DrawableRes int drawableId) {
    setBackground(view, ContextCompat.getDrawable(context, drawableId));
  }

  /** 给view设置背景 */
  public static void setBackground(View view, Drawable drawable) {
    ViewCompat.setBackground(view, drawable);
  }

  public static boolean isLandscape(Context context) {
    boolean result = false;
    Configuration mConfiguration = context.getResources().getConfiguration(); // 获取设置的配置信息
    int ori = mConfiguration.orientation;
    if (ori == Configuration.ORIENTATION_LANDSCAPE) {
      result = true;
    }
    return result;
  }
}
