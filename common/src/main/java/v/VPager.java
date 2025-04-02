package v;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import com.hello.sandbox.common.util.Vu;

/** Created by molikto on 01/15/15. */
public class VPager extends ViewPager {
  private boolean isPagingEnabled = true;

  public VPager(Context context) {
    super(context);
    init(context);
  }

  private void init(Context context) {
    if (Vu.BIG_OVERSCROLL) setOverScrollMode(OVER_SCROLL_NEVER);
    if ("Lenovo".equalsIgnoreCase(Build.BRAND) && "Lenovo K10e70".equalsIgnoreCase(Build.MODEL)) {
      // 此手机 vp 开启硬件加速会导致 Native crash 需关闭
      setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
  }

  public VPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public void disableTouch() {
    isPagingEnabled = false;
  }

  private boolean scrollable = true;

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!scrollable) {
      return false;
    }
    try {
      return this.isPagingEnabled && super.onTouchEvent(event);
    } catch (Exception e) {
      return false;
    }
  }
  /**
   * 设置是否可以左右滑动
   *
   * @param scrollable
   */
  public void setScrollble(boolean scrollable) {
    this.scrollable = scrollable;
  }

  public boolean isScrollble() {
    return this.scrollable;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    try {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    } catch (Exception e) {
      // 目前看到有crash，不知道原因，先加保护，然后看一下能不能收集到具体的crash stack
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    try {
      return isPagingEnabled && super.dispatchTouchEvent(ev);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    // prevent NPE if fake dragging and touching ViewPager
    // fix indicator's bug about NPE
    // https://github.com/JakeWharton/ViewPagerIndicator/pull/257
    if (!scrollable) {
      return false;
    }
    if (isFakeDragging()) return false;
    try {
      return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    } catch (Exception ignored) {
      // catch the viewpaper exception when zoom the photoDraweeView
      return false;
    }
  }

  public static class DepthPageTransformer implements PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
      int pageWidth = view.getWidth();

      if (position < -1) { // [-Infinity,-1)
        // This page is way off-screen to the left.
        view.setAlpha(0);

      } else if (position <= 0) { // [-1,0]
        // Use the default slide transition when moving to the left page
        view.setAlpha(1);
        view.setTranslationX(0);
        view.setScaleX(1);
        view.setScaleY(1);

      } else if (position <= 1) { // (0,1]
        // Fade the page out.
        view.setAlpha(1 - position);

        // Counteract the default slide transition
        view.setTranslationX(pageWidth * -position);

        // Scale the page down (between MIN_SCALE and 1)
        float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
        view.setScaleX(scaleFactor);
        view.setScaleY(scaleFactor);

      } else { // (1,+Infinity]
        // This page is way off-screen to the right.
        view.setAlpha(0);
      }
    }
  }
}
