package v;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.hello.sandbox.common.util.CrashHelper;

/** User: molikto Date: 01/05/15 Time: 18:20 */
public class VFrame extends FrameLayout {
  public VFrame(Context context) {
    super(context);
  }

  public VFrame(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VFrame(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public boolean addViewWithoutLayout(View child, int index, LayoutParams lp) {
    return addViewInLayout(child, index, lp, true);
  }

  private boolean interceptTouchEvent;

  @Override
  public void setOnTouchListener(OnTouchListener l) {
    interceptTouchEvent = l != null;
    super.setOnTouchListener(l);
  }

  @Override
  public void setOnClickListener(OnClickListener l) {
    interceptTouchEvent = l != null;
    super.setOnClickListener(l);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return interceptTouchEvent;
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    try {
      if (getOnDispatchTouchEventListener() != null) {
        if (getOnDispatchTouchEventListener().dispatchTouchEvent(ev)) {
          return true;
        }
      }
    } catch (IllegalArgumentException e) {
      CrashHelper.reportError(e);
      // unused
    }
    return super.dispatchTouchEvent(ev);
  }

  private OnDispatchTouchEventListener onDispatchTouchEventListener;

  public OnDispatchTouchEventListener getOnDispatchTouchEventListener() {
    return onDispatchTouchEventListener;
  }

  public void setOnDispatchTouchEventListener(
      OnDispatchTouchEventListener onDispatchTouchEventListener) {
    this.onDispatchTouchEventListener = onDispatchTouchEventListener;
  }
}
