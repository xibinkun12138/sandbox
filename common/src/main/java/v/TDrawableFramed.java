package v;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import com.hello.sandbox.common.R;

/** Created by molikto on 03/20/15. */
public class TDrawableFramed {

  private NinePatchDrawable shadow;
  private Rect padding;

  public void init(Context context, AttributeSet attrs, int defStyle) {
    TypedArray values =
        context.obtainStyledAttributes(attrs, R.styleable.TDrawableFramed, defStyle, 0);
    shadow = (NinePatchDrawable) values.getDrawable(R.styleable.TDrawableFramed_frameDrawable);
    if (shadow != null) {
      shadow(shadow);
    }
  }

  public void shadow(Drawable drawable) {
    shadow = (NinePatchDrawable) drawable;
    padding = new Rect();
    shadow.getPadding(padding);
  }

  public void draw(Canvas canvas) {
    Rect rect = canvas.getClipBounds();
    shadow.setBounds(
        rect.left - padding.left,
        rect.top - padding.top,
        rect.right + padding.right,
        rect.bottom + padding.bottom);
    shadow.draw(canvas);
  }
}
