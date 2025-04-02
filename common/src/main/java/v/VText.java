package v;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;
import com.hello.sandbox.common.R;
import java.util.Locale;

/** User: molikto Date: 08/08/14 Time: 15:34 */
public class VText extends AppCompatTextView {
  private int extras = 0;
  private static int EMOJI_ENABLED = 0x1;
  private static int LARGE_EMOJI_ENABLED = 0x10;
  private static int AUTO_EMOJI_SIZE = 0x1000;
  private static int COMPRESS_IF_TOO_WIDE = 0x100;
  private long text;
  private boolean isLargeEmojiForceDisabled = false;
  boolean autoSizeEmoji = false;
  private boolean isApngEmojiEnabled;
  // 是否表情及文案居中，默认为底部对齐
  private boolean centerEmojis;
  // 居中对齐
  public static final int ALIGN_CENTER = 2;

  public VText(Context context) {
    super(context);
    init(context, null, 0);
  }

  public VText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public VText(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs, defStyle);
  }

  private void init(Context context, AttributeSet attrs, int defStyle) {
    TTypeface.init(this, context, attrs, defStyle);
    TVectorDrawable.init(this, context, attrs, defStyle);
    if (!isInEditMode()) {
      if (!"th".equals(Locale.getDefault().getLanguage())) this.setIncludeFontPadding(false);
      if (attrs != null) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.VText, defStyle, 0);
        if (values.getBoolean(R.styleable.VText_enableEmojis, false)) {
          extras |= EMOJI_ENABLED;
        }
        if (values.getBoolean(R.styleable.VText_largeEmojis, false)) {
          extras |= LARGE_EMOJI_ENABLED;
        }
        if (values.getBoolean(R.styleable.VText_autoEmojis, false)) {
          extras |= AUTO_EMOJI_SIZE;
        }
        centerEmojis = values.getBoolean(R.styleable.VText_centerEmojis, false);
        isApngEmojiEnabled = values.getBoolean(R.styleable.VText_enableApngEmojis, false);
        values.recycle();
      }
    }
  }

  @Override
  public void setText(CharSequence text, BufferType type) {
    superSetText(text, type);
  }

  public void superSetText(CharSequence text, BufferType type) {
    if (Build.VERSION.SDK_INT == 21) {
      try {
        super.setText(text, type);
      } catch (Exception e) {
      }
    } else {
      super.setText(text, type);
    }
  }

  public void setTextWithoutEmoticonify(CharSequence text) {
    superSetText(text, BufferType.NORMAL);
  }

  @Override
  public void setTextAppearance(Context context, int resid) {
    super.setTextAppearance(context, resid);
    TTypeface.setTextAppearance(this, context, resid);
  }

  public void setLargeEmojiForceDisabled(boolean isSmallSingleEmojiEnabled) {
    this.isLargeEmojiForceDisabled = isSmallSingleEmojiEnabled;
  }

  public boolean isLargeEmojiForceDisabled() {
    return isLargeEmojiForceDisabled;
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    removeOldSpan();
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    runApngSpan();
  }

  /** 如果是表情动图的话，invalide动图 */
  private void runApngSpan() {
    CharSequence text = getText();
    if (TextUtils.isEmpty(text)) {
      return;
    }
  }

  /** 如果是表情动图的话，暂定动图的刷新绘制 */
  private void removeOldSpan() {
    CharSequence text = getText();
    if (TextUtils.isEmpty(text)) {
      return;
    }
  }
}
