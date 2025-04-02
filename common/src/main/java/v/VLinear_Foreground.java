package v;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/** Created by molikto on 9/20/16. */
public class VLinear_Foreground extends LinearLayout implements TForeground.Interface {

  TForeground foregroundAttacher;

  public VLinear_Foreground(Context context) {

    super(context);
    init(context, null, 0);
  }

  public VLinear_Foreground(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public VLinear_Foreground(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, 0);
  }

  private void init(Context context, AttributeSet attrs, int i) {
    foregroundAttacher = new TForeground(this);
    foregroundAttacher.initFromAttrsAndDefStyle(context, attrs, i);
  }

  public String getMeasureLogs() {
    return stringBuilder.toString();
  }

  private StringBuilder stringBuilder = new StringBuilder();

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);

    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    stringBuilder.append(
        "widthMode="
            + widthMode
            + " widthSize="
            + widthSize
            + " heightMode="
            + heightMode
            + " heightSize="
            + heightSize);

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int measuredWidth = getMeasuredWidth();
    int measuredHeight = getMeasuredHeight();
    stringBuilder.append(" measuredWidth=" + measuredWidth + " measuredHeight=" + measuredHeight);
    stringBuilder.append("\n");
  }

  @Override
  public void setForeground(Drawable drawable) {
    foregroundAttacher.setForeground(drawable);
  }

  @Override
  public Drawable getForeground() {
    return foregroundAttacher.getForeground();
  }

  @Override
  protected boolean verifyDrawable(Drawable dr) {
    return super.verifyDrawable(dr) || foregroundAttacher.callOnVerifyDrawable(dr);
  }

  @Override
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    foregroundAttacher.callOnJumpDrawablesToCurrentState();
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    foregroundAttacher.callOndrawableStateChanged();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    foregroundAttacher.callOnSizeChanged();
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    foregroundAttacher.callOnDraw(canvas);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    foregroundAttacher.callOnTouchEvent(event);
    return super.onTouchEvent(event);
  }
}
