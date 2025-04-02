package v;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.hello.sandbox.common.R;

/** Created by kingty on 16/1/19. */
public class TForeground {
  private View view;
  // UI
  private Drawable foreground;

  // Controller/logic fields
  private final Rect rectPadding = new Rect();
  private boolean foregroundPadding = false;
  private boolean foregroundBoundsChanged = false;
  private boolean backgroundAsForeground = false;

  // Constructors
  public TForeground(View view) {
    this.view = view;
  }

  public interface Interface {
    void setForeground(Drawable drawable);

    Drawable getForeground();
  }

  protected void initFromAttrsAndDefStyle(Context context, AttributeSet attrs, int defStyle) {
    rectPadding.left = view.getPaddingLeft();
    rectPadding.top = view.getPaddingTop();
    rectPadding.right = view.getPaddingRight();
    rectPadding.bottom = view.getPaddingBottom();
    final TypedArray a =
        context.obtainStyledAttributes(attrs, R.styleable.ForegroundLayout, defStyle, 0);

    final Drawable d = a.getDrawable(R.styleable.ForegroundLayout_foreground);
    foregroundPadding = a.getBoolean(R.styleable.ForegroundLayout_foregroundInsidePadding_, false);

    backgroundAsForeground =
        a.getBoolean(R.styleable.ForegroundLayout_backgroundAsForeground, false);

    // Apply foreground padding for ninepatches automatically
    if (!foregroundPadding && view.getBackground() instanceof NinePatchDrawable) {
      final NinePatchDrawable npd = (NinePatchDrawable) view.getBackground();
      if (npd != null && npd.getPadding(rectPadding)) {
        foregroundPadding = true;
      }
    }

    final Drawable b = view.getBackground();
    if (backgroundAsForeground && b != null) {
      setForeground(b);
    } else if (d != null) {
      setForeground(d);
    }

    a.recycle();
  }

  /**
   * Supply a Drawable that is to be rendered on top of all of the child views in the layout.
   *
   * @param drawable The Drawable to be drawn on top of the children.
   */
  public void setForeground(Drawable drawable) {
    if (foreground != drawable) {
      if (foreground != null) {
        foreground.setCallback(null);
        view.unscheduleDrawable(foreground);
      }

      foreground = drawable;

      if (drawable != null) {
        view.setWillNotDraw(false);
        drawable.setCallback(view);
        if (drawable.isStateful()) {
          drawable.setState(view.getDrawableState());
        }
      } else {
        view.setWillNotDraw(true);
      }
      view.requestLayout();
      view.invalidate();
    }
  }

  /**
   * Returns the drawable used as the foreground of this layout. The foreground drawable, if
   * non-null, is always drawn on top of the children.
   *
   * @return A Drawable or null if no foreground was set.
   */
  public Drawable getForeground() {
    return foreground;
  }

  protected void callOndrawableStateChanged() {
    if (foreground != null && foreground.isStateful()) {
      foreground.setState(view.getDrawableState());
    }
  }

  protected boolean callOnVerifyDrawable(Drawable who) {
    return who == foreground;
  }

  protected void callOnJumpDrawablesToCurrentState() {

    if (foreground != null) {
      foreground.jumpToCurrentState();
    }
  }

  protected void callOnSizeChanged() {
    foregroundBoundsChanged = true;
  }

  protected void callOnDraw(Canvas canvas) {

    if (foreground != null) {
      final Drawable foreground = this.foreground;

      if (foregroundBoundsChanged) {
        foregroundBoundsChanged = false;

        final int w = view.getRight() - view.getLeft();
        final int h = view.getBottom() - view.getTop();

        if (foregroundPadding) {
          foreground.setBounds(
              rectPadding.left, rectPadding.top, w - rectPadding.right, h - rectPadding.bottom);
        } else {
          foreground.setBounds(0, 0, w, h);
        }
      }
      foreground.draw(canvas);
    }
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  protected void callOnTouchEvent(MotionEvent e) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
        if (foreground != null) {
          foreground.setHotspot(e.getX(), e.getY());
        }
      }
    }
  }
}
