package v;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * User: molikto Date: 12/29/14 Time: 22:57
 */
public class VImage extends AppCompatImageView {

  public VImage(Context context) {
    super(context);
  }

  public VImage(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VImage(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    try {
      super.onDraw(canvas);
    } catch (Throwable throwable) {
    }
  }

  @Override
  public void setImageDrawable(@Nullable @org.jetbrains.annotations.Nullable Drawable drawable) {
    super.setImageDrawable(drawable);
  }
}
