package v;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.Vu;

/** Created by Wencharm on 01/12/2016. */
public class TVectorDrawable {

  // only support drawableLeft now
  public static void init(TextView view, Context context, AttributeSet attrs, int defStyle) {
    TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.VText, defStyle, 0);
    Drawable d = null;
    addDrawableLeftOrRight(d, view, values, context, true, R.styleable.VText_vectorDrawableLeft);
    addDrawableLeftOrRight(d, view, values, context, false, R.styleable.VText_vectorDrawableRight);
    values.recycle();
  }

  private static void addDrawableLeftOrRight(
      Drawable d,
      TextView view,
      TypedArray values,
      Context context,
      boolean isLeft,
      int styleable) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      d = values.getDrawable(styleable);
    } else {
      int resId = values.getResourceId(styleable, -1);
      if (resId != -1) d = AppCompatResources.getDrawable(context, resId);
    }
    if (d != null) {
      if (isLeft) {
        Vu.addCompoundDrawableLeft(view, d);
      } else {
        Vu.addCompoundDrawableRight(view, d);
      }
    }
  }
}
