package v;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.IntDef;
import com.hello.sandbox.common.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** User: molikto Date: 12/29/14 Time: 04:23 */
public class TTypeface {

  public static final String[] names =
      new String[] {
        "fonts/Roboto-Thin.ttf",
        "fonts/Roboto-Light.ttf",
        "fonts/Roboto-Regular.ttf",
        "fonts/Roboto-Medium.ttf",
        "fonts/Roboto-Bold.ttf",
        "fonts/Roboto-Black.ttf",
        "fonts/RobotoCondensed-Light.ttf",
        "fonts/RobotoCondensed-Regular.ttf",
        "fonts/RobotoCondensed-Bold.ttf",
        "fonts/ArialRounded-Bold.ttf",
        "fonts/Roboto-Italic.ttf",
        "fonts/DINCondensed-Bold.ttf",
        "fonts/Semibold-Italic.ttf",
        "fonts/Aqum-classic.otf"
      };

  @IntDef(
      value = {
        THIN,
        LIGHT,
        REGULAR,
        MEDIUM,
        BOLD,
        BLACK,
        CONDENSED_LIGHT,
        CONDENSED_REGULAR,
        CONDENSED_BOLD,
        ARIAL_ROUNDED_BOLD,
        ITALIC,
        DIN_CONDENSED,
        SEMIBOLD_ITALIC,
        AQUM_CLASSIC
      })
  @Retention(RetentionPolicy.SOURCE)
  public @interface ATTypeface {}

  public static final String FAMILY = "sans-serif";

  public static final int THIN = 0;
  public static final int LIGHT = 1;
  public static final int REGULAR = 2;
  public static final int MEDIUM = 3;
  public static final int BOLD = 4;
  public static final int BLACK = 5;
  public static final int CONDENSED_LIGHT = 6;
  public static final int CONDENSED_REGULAR = 7;
  public static final int CONDENSED_BOLD = 8;
  public static final int ARIAL_ROUNDED_BOLD = 9;
  public static final int ITALIC = 10;
  public static final int DIN_CONDENSED = 11;
  public static final int SEMIBOLD_ITALIC = 12;
  public static final int AQUM_CLASSIC = 13;

  public static final Typeface[] typefaces = new Typeface[names.length];

  public static void init(TextView view, Context context, AttributeSet attrs, int defStyle) {
    if (!view.isInEditMode()) {

      int i = REGULAR;

      final Resources.Theme theme = context.getTheme();
      TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.VText, 0, defStyle);
      TypedArray appearance = null;
      int ap = a.getResourceId(R.styleable.VText_android_textAppearance, -1);
      a.recycle();
      if (ap != -1) {
        appearance = theme.obtainStyledAttributes(ap, R.styleable.VText);
      }
      if (appearance != null) {
        int n = appearance.getIndexCount();
        for (int j = 0; j < n; j++) {
          int attr = appearance.getIndex(j);
          if (attr == R.styleable.VText_typeface) {
            i = appearance.getInt(R.styleable.VText_typeface, i);
          }
        }

        appearance.recycle();
      }

      if (attrs != null) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.VText, defStyle, 0);
        i = values.getInt(R.styleable.VText_typeface, i);
        values.recycle();
      }
      view.setTypeface(typeface(i));
      view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }
  }

  public static Typeface typeface(int i) {
    return TTypefaceManager.typefaceFromeAsset(names[i]);
  }

  public static void setTextAppearance(TextView view, Context context, int resid) {
    Typeface t = view.getTypeface();
    view.setTypeface(t);
  }
}
