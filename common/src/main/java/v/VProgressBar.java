package v;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.hello.sandbox.common.R;
import v.progressbar.ProgressDrawable;

/** Created by castorflex on 11/10/13. */
public class VProgressBar extends ProgressBar {

  public VProgressBar(Context context) {
    this(context, null);
  }

  public VProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.cpbStyle);
  }

  public VProgressBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    if (isInEditMode()) {
      setIndeterminateDrawable(new ProgressDrawable.Builder(context).build());
      return;
    }

    Resources res = context.getResources();
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VProgressBar, defStyle, 0);

    final int color =
        a.getColor(R.styleable.VProgressBar_cpb_color, res.getColor(R.color.cpb_default_color));
    final float strokeWidth =
        a.getDimension(
            R.styleable.VProgressBar_cpb_stroke_width,
            res.getDimension(R.dimen.cpb_default_stroke_width));
    final float strokePercentage = a.getFloat(R.styleable.VProgressBar_cpb_stroke_percentage, -1);
    final float sweepSpeed =
        a.getFloat(
            R.styleable.VProgressBar_cpb_sweep_speed,
            Float.parseFloat(res.getString(R.string.cpb_default_sweep_speed)));
    final float rotationSpeed =
        a.getFloat(
            R.styleable.VProgressBar_cpb_rotation_speed,
            Float.parseFloat(res.getString(R.string.cpb_default_rotation_speed)));
    final int colorsId = a.getResourceId(R.styleable.VProgressBar_cpb_colors, 0);
    final int minSweepAngle =
        a.getInteger(
            R.styleable.VProgressBar_cpb_min_sweep_angle,
            res.getInteger(R.integer.cpb_default_min_sweep_angle));
    final int maxSweepAngle =
        a.getInteger(
            R.styleable.VProgressBar_cpb_max_sweep_angle,
            res.getInteger(R.integer.cpb_default_max_sweep_angle));
    a.recycle();

    int[] colors = null;
    // colors
    if (colorsId != 0) {
      colors = res.getIntArray(colorsId);
    }

    Drawable indeterminateDrawable;
    ProgressDrawable.Builder builder =
        new ProgressDrawable.Builder(context)
            .sweepSpeed(sweepSpeed)
            .rotationSpeed(rotationSpeed)
            .strokeWidth(strokeWidth)
            .strokePercentage(strokePercentage)
            .minSweepAngle(minSweepAngle)
            .maxSweepAngle(maxSweepAngle);

    if (colors != null && colors.length > 0) builder.colors(colors);
    else builder.color(color);

    indeterminateDrawable = builder.build();
    setIndeterminateDrawable(indeterminateDrawable);
  }

  private ProgressDrawable checkIndeterminateDrawable() {
    Drawable ret = getIndeterminateDrawable();
    if (ret == null || !(ret instanceof ProgressDrawable))
      throw new RuntimeException("The drawable is not a CircularProgressDrawable");
    return (ProgressDrawable) ret;
  }

  public void progressiveStop() {
    checkIndeterminateDrawable().progressiveStop();
  }

  public void progressiveStop(ProgressDrawable.OnEndListener listener) {
    checkIndeterminateDrawable().progressiveStop(listener);
  }

  public void startAnim() {
    checkIndeterminateDrawable().start();
  }

  public void stopAnim() {
    checkIndeterminateDrawable().stop();
  }
}
