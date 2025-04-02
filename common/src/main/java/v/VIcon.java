package v;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.IntDef;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.MetricsUtil;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class VIcon extends VBaseIcon {
  public VIcon(Context context) {
    this(context, null);
  }

  public VIcon(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VIcon(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    if (attrs != null) {
      TypedArray typedArray =
          context.obtainStyledAttributes(attrs, R.styleable.VIcon, defStyleAttr, 0);
      int iconStyle = typedArray.getInt(R.styleable.VIcon_iconStyle, 0);
      typedArray.recycle();
      setIconStyle(iconStyle);
    }
    setScaleType(ScaleType.FIT_CENTER);
  }

  private int iconSize = 0;

  public void setIconStyle(@IconStyle int iconStyle) {
    switch (iconStyle) {
      case IconStyle.XXX_SMALL:
        iconSize = MetricsUtil.DP_12;
        break;
      case IconStyle.XX_SMALL:
        iconSize = MetricsUtil.DP_16;
        break;
      case IconStyle.X_SMALL:
        iconSize = MetricsUtil.DP_24;
        break;
      case IconStyle.SMALL:
        iconSize = MetricsUtil.DP_32;
        break;
      case IconStyle.MEDIUM:
        iconSize = MetricsUtil.DP_40;
        break;
      case IconStyle.LARGE:
        iconSize = MetricsUtil.DP_48;
        break;
      case IconStyle.X_LARGE:
        iconSize = MetricsUtil.DP_56;
        break;
      case IconStyle.XX_LARGE:
        iconSize = MetricsUtil.DP_64;
        break;
      case IconStyle.XXX_LARGE:
        iconSize = MetricsUtil.DP_72;
        break;
      default:
        iconSize = 0;
        break;
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if (iconSize == 0) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      return;
    }
    int size = iconSize;
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);

    int result = 0;
    switch (widthMode) {
      case MeasureSpec.AT_MOST:
        if (widthSize < size) {
          result = widthSize;
        } else {
          result = size;
        }
        break;
      case MeasureSpec.EXACTLY:
        result = widthSize;
        break;
      case MeasureSpec.UNSPECIFIED:
        result = size;
        break;
    }
    setMeasuredDimension(result, result);
  }

  @IntDef({
    IconStyle.XXX_SMALL,
    IconStyle.XX_SMALL,
    IconStyle.X_SMALL,
    IconStyle.SMALL,
    IconStyle.MEDIUM,
    IconStyle.LARGE,
    IconStyle.X_LARGE,
    IconStyle.XX_LARGE,
    IconStyle.XXX_LARGE
  })
  @Retention(RetentionPolicy.SOURCE)
  public @interface IconStyle {
    int XXX_SMALL = 1;
    int XX_SMALL = 2;
    int X_SMALL = 3;
    int SMALL = 4;
    int MEDIUM = 5;
    int LARGE = 6;
    int X_LARGE = 7;
    int XX_LARGE = 8;
    int XXX_LARGE = 9;
  }
}
