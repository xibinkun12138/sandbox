package v;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import androidx.annotation.StyleableRes;
import com.hello.sandbox.common.R;

/**
 * Custom {@link android.widget.Button Button} using the Helvetica Neue font
 *
 * @author Viktor Nyblom
 */
public class VButton extends CompoundButton {

  private boolean buttonIconFollowCenter = false;
  private float buttonIconFollowMinPadding = 0;

  public VButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  public VButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public VButton(Context context) {
    super(context);
    init(context, null, 0);
  }

  private void init(Context context, AttributeSet attrs, int defStyle) {
    TTypeface.init(this, context, attrs, defStyle);
    TVectorDrawable.init(this, context, attrs, defStyle);
    initButtonIconStyle(attrs);
  }

  private void initButtonIconStyle(AttributeSet attrs) {
    if (attrs != null) {
      TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VButton);
      Drawable drawableLeft =
          getDrawable(
              typedArray,
              R.styleable.VButton_android_drawableLeft,
              R.styleable.VButton_buttonIconLeftSize);
      Drawable drawableRight =
          getDrawable(
              typedArray,
              R.styleable.VButton_android_drawableRight,
              R.styleable.VButton_buttonIconRightSize);
      Drawable drawableTop =
          getDrawable(
              typedArray,
              R.styleable.VButton_android_drawableTop,
              R.styleable.VButton_buttonIconTopSize);
      Drawable drawableBottom =
          getDrawable(
              typedArray,
              R.styleable.VButton_android_drawableBottom,
              R.styleable.VButton_buttonIconBottomSize);
      this.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
      if (drawableLeft != null
          || drawableRight != null
          || drawableTop != null
          || drawableBottom != null) {
        buttonIconFollowCenter =
            typedArray.getBoolean(R.styleable.VButton_buttonIconFollowContentCenter, false);
      }

      typedArray.recycle();
    }
  }

  private Drawable getDrawable(
      TypedArray typedArray, @StyleableRes int drawableRes, @StyleableRes int dimensionRes) {
    Drawable drawable = typedArray.getDrawable(drawableRes);
    float drawableSize = typedArray.getDimension(dimensionRes, 0);
    if (drawable != null) {
      if (drawableSize == 0) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
      } else {
        drawable = drawable.mutate();
        drawable.setBounds(0, 0, (int) drawableSize, (int) drawableSize);
      }
    }
    return drawable;
  }

  @Override
  public void setTextAppearance(Context context, int resid) {
    super.setTextAppearance(context, resid);
    TTypeface.setTextAppearance(this, context, resid);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    drawIconFollowCenter();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
  }

  public void drawIconFollowCenter() {
    if (buttonIconFollowCenter) {
      Drawable[] drawables = getCompoundDrawables();
      float textSize = getPaint().measureText(getText().toString()); // 获取文本所占尺寸
      float drawPadding = getCompoundDrawablePadding(); // 获取中间空余的尺寸
      float drawablesLeftWidth =
          drawables[0] == null
              ? 0
              : drawables[0].getBounds().width() + drawPadding; // 获取图片+图片padding所占尺寸
      float drawablesRightWidth =
          drawables[2] == null
              ? 0
              : drawables[2].getBounds().width() + drawPadding; // 获取图片+图片padding所占尺寸
      float contentWidth = textSize + drawablesLeftWidth + drawablesRightWidth; // 计算当前所占尺寸
      float remain = (getWidth() - contentWidth) / 2;
      float paddingLeft = remain;
      float paddingRight = remain;
      paddingLeft = paddingLeft < getPaddingLeft() ? getPaddingLeft() : paddingLeft;
      paddingRight = paddingRight < getPaddingRight() ? getPaddingRight() : paddingRight;
      setPadding((int) paddingLeft, getPaddingTop(), (int) paddingRight, getPaddingBottom());
    }
  }
}
