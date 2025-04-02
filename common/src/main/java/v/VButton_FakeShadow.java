package v;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import com.hello.sandbox.common.R;

/** Created by molikto on 03/06/15. */
public class VButton_FakeShadow extends VButton {

  TDrawableFramed frame = new TDrawableFramed();
  private boolean drawShadow = true;

  public VButton_FakeShadow(Context context) {
    super(context);
    init0(context, null, 0);
  }

  public VButton_FakeShadow(Context context, AttributeSet attrs) {
    super(context, attrs);
    init0(context, attrs, 0);
  }

  public VButton_FakeShadow(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init0(context, attrs, defStyle);
  }

  public int resId() {
    return R.drawable.rect_rounded_shadow;
  }

  private void init0(Context context, AttributeSet o, int i) {
    frame.shadow(context.getResources().getDrawable(resId()));
  }

  public void setDrawShadow(boolean draw) {
    this.drawShadow = draw;
    invalidate();
  }

  @Override
  public void draw(Canvas canvas) {
    if (drawShadow) {
      if (Build.VERSION.SDK_INT < 21) {
        frame.draw(canvas);
      }
    }
    super.draw(canvas);
  }
}
