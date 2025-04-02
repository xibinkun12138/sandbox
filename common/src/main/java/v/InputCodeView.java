package v;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.ui.Toast;
import com.hello.sandbox.common.util.MetricsUtil;
import com.hello.sandbox.common.util.NullChecker;
import java.util.regex.Pattern;

public class InputCodeView extends View {

  private static final String TAG = "InputCodeView";

  public InputCodeView(Context context) {
    this(context, null);
  }

  public InputCodeView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public InputCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public interface OnTextChangListener {
    void afterTextChanged(String text);
  }

  private static final int BLINK = 500;
  private final int paddingRight = MetricsUtil.DP_47;
  private final int paddingLeft = MetricsUtil.DP_47;
  private final int mBgWidth = MetricsUtil.DP_60;
  private final int mBgHeight = MetricsUtil.DP_60;
  private final int mBgSpace =
      (MetricsUtil.displayMetrics().widthPixels - paddingRight - paddingLeft - mBgWidth * 4) / 3;
  private final int mBgCornerRadius = MetricsUtil.DP_14;
  private final int mCursorWidth = MetricsUtil.DP_3;
  private final int mCursorHeight = MetricsUtil.DP_32;
  private final int mTextSize = MetricsUtil.DP_28;

  private final Paint mFullBgPaint = new Paint();

  private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private Drawable mCursorDrawable;

  private Choreographer mChoreographer = Choreographer.getInstance();
  private FrameCallback mCallback;

  private OnTextChangListener mTextChangeListener;

  private long mShowCursor;

  private StringBuilder mTextBuilder = new StringBuilder();
  private VInputConnection inputConnection;

  private void init() {
    mFullBgPaint.setColor(Color.parseColor("#3EC0AA"));
    mFullBgPaint.setStrokeWidth(mBgWidth);
    mFullBgPaint.setAntiAlias(true);
    mFullBgPaint.setDither(true);
    mFullBgPaint.setStrokeCap(Cap.ROUND);

    mTextPaint.setColor(Color.parseColor("#000000"));
    mTextPaint.setAntiAlias(true);
    mTextPaint.setDither(true);
    mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    mTextPaint.setTextAlign(Align.CENTER);
    mTextPaint.setTextSize(mTextSize);

    mCursorDrawable = getResources().getDrawable(R.drawable.text_cursor);

    mCallback =
        frameTimeNanos -> {
          invalidate();
          mChoreographer.postFrameCallback(mCallback);
        };

    setFocusable(true);
    setFocusableInTouchMode(true);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mChoreographer.postFrameCallback(mCallback);
  }

  @Override
  protected void onDetachedFromWindow() {
    mChoreographer.removeFrameCallback(mCallback);
    super.onDetachedFromWindow();
  }

  @Override
  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    outAttrs.actionLabel = null;
    outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
    return getInputConnection(outAttrs);
  }

  private InputConnection getInputConnection(EditorInfo editorInfo) {
    if (NullChecker.notNull(inputConnection)) {
      return inputConnection;
    }
    inputConnection = new VInputConnection(this, false);
    inputConnection.setOnCommitTextListener(
        (text, newCursorPosition) -> {
          if (mTextBuilder.length() == 0) {
            if (text.equals("0")) {
              Toast.message("密码首位请输入0以外的数字");
              return true;
            }
          }
          if (Pattern.matches("[0-9]", text) || Pattern.matches("[0-9][0-9][0-9][0-9]", text)) {
            // if (Pattern.matches("[0-9][0-9][0-9][0-9]", text)) {}
            if (mTextBuilder.length() < 4) {
              mTextBuilder.append(text);
              if (NullChecker.notNull(mTextChangeListener)) {
                mTextChangeListener.afterTextChanged(mTextBuilder.toString());
              }
              invalidate();
            }
          }
          return true;
        });
    return inputConnection;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    super.onTouchEvent(event);
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      showSoftInput();
    }
    return true;
  }

  public void showSoftInput() {
    requestFocus();
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

    imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
  }

  public void hideSoftInput() {
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_DEL) {
      if (mTextBuilder.length() > 0) {
        mTextBuilder.deleteCharAt(mTextBuilder.length() - 1);
        if (NullChecker.notNull(mTextChangeListener)) {
          mTextChangeListener.afterTextChanged(mTextBuilder.toString());
        }
      }
    } else if (keyCode >= KeyEvent.KEYCODE_0
        && keyCode <= KeyEvent.KEYCODE_9
        && mTextBuilder.length() < 4) {
      mTextBuilder.append(keyCode - KeyEvent.KEYCODE_0);
      if (NullChecker.notNull(mTextChangeListener)) {
        mTextChangeListener.afterTextChanged(mTextBuilder.toString());
      }
    }

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return super.onKeyDown(keyCode, event);
    }

    return true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    drawBg(canvas);
  }

  private boolean shouldRenderCursor() {
    final long showCursorDelta = SystemClock.uptimeMillis() - mShowCursor;
    return showCursorDelta % (2 * BLINK) < BLINK;
  }

  public void setText(String text) {
    mTextBuilder = new StringBuilder(text);
    if (NullChecker.notNull(mTextChangeListener)) {
      mTextChangeListener.afterTextChanged(text);
    }
    invalidate();
  }

  public String getText() {
    return mTextBuilder.toString();
  }

  public void setTextChangeListener(OnTextChangListener listener) {
    this.mTextChangeListener = listener;
  }

  @Override
  protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
    super.onFocusChanged(focused, direction, previouslyFocusedRect);
    mShowCursor = SystemClock.uptimeMillis();
    if (focused) {
      mChoreographer.postFrameCallback(mCallback);
    } else {
      mChoreographer.removeFrameCallback(mCallback);
      invalidate();
    }
  }

  private void drawBg(Canvas canvas) {
    int length = mTextBuilder.length();
    Paint.FontMetricsInt fontMetricsInt = mTextPaint.getFontMetricsInt();
    int baseLine = mBgHeight / 2 - fontMetricsInt.ascent / 2 - fontMetricsInt.descent / 2;

    for (int i = 0; i < 4; i++) {

      canvas.drawRoundRect(
          i * (mBgWidth + mBgSpace) + paddingLeft,
          0,
          i * (mBgWidth + mBgSpace) + mBgWidth + paddingLeft,
          mBgHeight,
          mBgCornerRadius,
          mBgCornerRadius,
          mFullBgPaint);
    }

    for (int i = 0; i < 4; i++) {
      int centerX = i * (mBgWidth + mBgSpace) + mBgWidth / 2 + paddingLeft;
      if (i < length) {

        canvas.drawText(
            mTextBuilder.toString(), i, i + 1, centerX, baseLine - MetricsUtil.DP_2, mTextPaint);
      } else {

        if (i == length && isFocused() && shouldRenderCursor()) {
          int centerY = mBgHeight / 2;
          mCursorDrawable.setBounds(
              centerX - mCursorWidth / 2,
              centerY - mCursorHeight / 2,
              centerX + mCursorWidth / 2,
              centerY + mCursorHeight / 2);
          mCursorDrawable.draw(canvas);
        }
      }
    }
  }
}
