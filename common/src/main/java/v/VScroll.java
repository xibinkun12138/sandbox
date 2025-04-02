package v;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import com.hello.sandbox.common.util.Vu;

public class VScroll extends ScrollView {

  private int addInLeft;
  private int addInTop;

  private ScrollableContent sc;

  public interface ScrollableContent {
    boolean isScrollable(int x, int y);
  }

  private OnScrollViewListener onScrollViewListener;

  public VScroll(Context context) {
    super(context);
    init(context);
  }

  private void init(Context context) {
    if (Vu.BIG_OVERSCROLL) setOverScrollMode(OVER_SCROLL_NEVER);
  }

  public VScroll(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public VScroll(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  public void setInterceptTouchEvent(ScrollableContent sc) {
    View view = (View) sc;
    if (sc == null) {
      this.sc = null;
    } else {
      this.sc = sc;
      addInLeft = 0;
      addInTop = 0;
      while (view.getParent() != this) {
        view = (View) view.getParent();
        addInLeft += view.getLeft() + view.getScrollX();
        addInTop += view.getTop() + view.getScrollY();
      }
    }
  }

  public void onScrollViewListener(OnScrollViewListener listener) {
    this.onScrollViewListener = listener;
  }

  protected void onScrollChanged(int x, int y, int oldX, int oldY) {
    if (onScrollViewListener != null) {
      onScrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
    }
    super.onScrollChanged(x, y, oldX, oldY);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (sc == null) {
      return super.onInterceptTouchEvent(ev);
    } else {
      int x = (int) (ev.getX() + getScrollX()) - addInLeft;
      int y = (int) (ev.getY() + getScrollY()) - addInTop;
      if (sc.isScrollable(x, y)) {
        return false;
      } else {
        try {
          return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
          return false;
        }
      }
    }
  }

  public interface OnScrollViewListener {
    void onScrollChanged(VScroll v, int x, int y, int oldX, int oldY);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean ret = false;
    // https://issuetracker.google.com/issues/36931456
    try {
      ret = super.dispatchTouchEvent(ev);
    } catch (Exception e) {
    }
    return ret;
  }
}
