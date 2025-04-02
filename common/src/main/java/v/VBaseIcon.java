package v;

import android.content.Context;
import android.util.AttributeSet;

public class VBaseIcon extends VImage {
  public VBaseIcon(Context context) {
    this(context, null);
  }

  public VBaseIcon(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VBaseIcon(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
}
