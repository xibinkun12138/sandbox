package v;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import androidx.core.util.Pair;
import com.hello.sandbox.common.util.Cu;

/** Created by molikto on 03/02/15. */
public class VLinear extends LinearLayout {
  public VLinear(Context context) {
    super(context);
  }

  public VLinear(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VLinear(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private boolean hasOnClickListener;

  @Override
  public void setOnClickListener(OnClickListener l) {
    hasOnClickListener = l != null;
    super.setOnClickListener(l);
  }

  public static Ts ts = new Ts();

  public static class Ts {
    private boolean badDeviceIdOrToolType = false;
    public static final float NOT_STARTED = -3231232;
    private float tsDetectP = NOT_STARTED,
        tsDetectXP = NOT_STARTED,
        tsDetectYP = NOT_STARTED; // -2 is I think this person is OK

    private boolean tsdStarted = false;

    public void tsdStart() {
      tsdStarted = true;
    }

    public Pair<Boolean, Boolean> tsdEnd() {
      tsdStarted = false;
      boolean b1 = badDeviceIdOrToolType;
      boolean b2 = tsDetectP != -2;
      badDeviceIdOrToolType = false;
      tsDetectP = NOT_STARTED;
      tsDetectXP = NOT_STARTED;
      tsDetectYP = NOT_STARTED;
      return Cu.pair(b1, b2);
    }

    public void process(MotionEvent event) {
      if (tsdStarted) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          if (event.getDeviceId() == 0 || event.getToolType(0) == MotionEvent.TOOL_TYPE_UNKNOWN) {
            badDeviceIdOrToolType = true;
          }
          // not initiaged
          if (tsDetectP == NOT_STARTED && tsDetectXP == NOT_STARTED && tsDetectYP == NOT_STARTED) {
            tsDetectP = event.getPressure();
            tsDetectXP = event.getXPrecision();
            tsDetectYP = event.getYPrecision();
          } else if (tsDetectP != event.getPressure()
              || tsDetectYP != event.getYPrecision()
              || tsDetectXP != event.getXPrecision()) {
            if (tsDetectP != -2) {
              tsDetectP = -2;
              tsDetectXP = -2;
              tsDetectYP = -2;
            }
          }
        }
      }
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    ts.process(event);
    return hasOnClickListener;
  }
}
