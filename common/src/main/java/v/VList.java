package v;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.hello.sandbox.common.util.Vu;

/** Created by molikto on 06/30/15. */
public class VList extends ListView {

  public static interface DisableDisableScroll {}

  private int position;

  public VList(Context context) {
    super(context);
    init(context);
  }

  private void init(Context context) {
    if (Vu.BIG_OVERSCROLL || "SMARTISAN".equals(Build.BRAND)) setOverScrollMode(OVER_SCROLL_NEVER);
  }

  public VList(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public VList(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  String crashLogFlag = "default";

  public void setCrashLogFlag(String flag) {
    this.crashLogFlag = flag;
  }

  @Override
  public int getId() {
    return super.getId();
  }

  private boolean disable = false;
  private boolean disableDisable = false;

  @Override
  public void setAdapter(ListAdapter adapter) {
    super.setAdapter(adapter);
    if (adapter instanceof RecyclerListener) {
      setRecyclerListener((RecyclerListener) adapter);
    }
  }

  public void disableScroll(boolean disable) {
    this.disable = disable;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (disable) {
      final int actionMasked = ev.getActionMasked() & MotionEvent.ACTION_MASK;

      if (actionMasked == MotionEvent.ACTION_DOWN) {
        // Record the position the list the touch landed on
        position = pointToPosition((int) ev.getX(), (int) ev.getY());
        return super.dispatchTouchEvent(ev);
      }

      if (actionMasked == MotionEvent.ACTION_MOVE) {
        // Ignore move events
        return true;
      }

      if (actionMasked == MotionEvent.ACTION_UP) {
        // Check if we are still within the same view
        if (pointToPosition((int) ev.getX(), (int) ev.getY()) == position) {
          super.dispatchTouchEvent(ev);
        } else {
          // Clear pressed state, cancel the action
          setPressed(false);
          invalidate();
          return true;
        }
      }
    }

    return super.dispatchTouchEvent(ev);
  }

  public void setOverScrollListener(OverScrollListener overScrollListener) {
    this.overScrollListener = overScrollListener;
  }

  public OverScrollListener overScrollListener;

  public interface OverScrollListener {
    void overScrollBy(
        int deltaX,
        int deltaY,
        int scrollX,
        int scrollY,
        int scrollRangeX,
        int scrollRangeY,
        int maxOverScrollX,
        int maxOverScrollY,
        boolean isTouchEvent);
  }

  @Override
  protected boolean overScrollBy(
      int deltaX,
      int deltaY,
      int scrollX,
      int scrollY,
      int scrollRangeX,
      int scrollRangeY,
      int maxOverScrollX,
      int maxOverScrollY,
      boolean isTouchEvent) {
    if (overScrollListener != null) {
      overScrollListener.overScrollBy(
          deltaX,
          deltaY,
          scrollX,
          scrollY,
          scrollRangeX,
          scrollRangeY,
          maxOverScrollX,
          maxOverScrollY,
          isTouchEvent);
    }
    return super.overScrollBy(
        deltaX,
        deltaY,
        scrollX,
        scrollY,
        scrollRangeX,
        scrollRangeY,
        maxOverScrollX,
        maxOverScrollY,
        isTouchEvent);
  }
}
