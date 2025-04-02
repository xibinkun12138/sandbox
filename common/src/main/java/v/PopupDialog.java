package v;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Space;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.viewpager.widget.PagerAdapter;
import com.hello.sandbox.common.DialogBase;
import com.hello.sandbox.common.DialogExtraState;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.MetricsUtil;
import com.hello.sandbox.common.util.Vu;
import v.pushbubble.VFrame;

public class PopupDialog extends DialogBase implements DialogExtraState {

  private final Builder builder;

  private VText _title;
  private VText _subtitle;
  private VButton _positive_primary_button;
  private VButton _positive_secondary_button;
  private VText _negative;
  private VText _bottom_text;
  private VCheckBox _checkBox;

  public final View getCustomView() {
    return builder.customView;
  }

  public void setOnPositivePrimaryRunnable(Runnable onPositivePrimary) {
    this.builder.onPositivePrimary = onPositivePrimary;
  }

  public void setOnPositiveSecondaryRunnable(Runnable onPositiveSecondary) {
    this.builder.onPositiveSecondary = onPositiveSecondary;
  }

  public void setOnNegativeRunnable(Runnable onNegative) {
    this.builder.onNegative = onNegative;
  }

  public void setTitleText(CharSequence title) {
    if (_title != null) {
      _title.setText(title);
    }
  }

  public void setSubTitleText(CharSequence subtitle) {
    if (_subtitle != null) {
      _subtitle.setText(subtitle);
      if (subtitle instanceof SpannableString) {
        _subtitle.setMovementMethod(LinkMovementMethod.getInstance());
      }
    }
  }

  public void setPositivePrimaryText(CharSequence positivePrimaryText) {
    if (_positive_primary_button != null) {
      _positive_primary_button.setText(positivePrimaryText);
    }
  }

  public void setPositiveSecondaryText(CharSequence positiveSecondaryText) {
    if (_positive_secondary_button != null) {
      _positive_secondary_button.setText(positiveSecondaryText);
    }
  }

  public void setNegativeText(CharSequence negativeText) {
    if (_negative != null) {
      _negative.setText(negativeText);
    }
  }

  public void setBottomText(CharSequence bottomText) {
    if (_bottom_text != null) {
      _bottom_text.setText(bottomText);
    }
  }

  public PopupDialog(Builder builder) {
    super(builder.context, true, getTheme(builder));
    this.builder = builder;

    View _root = View.inflate(builder.context, R.layout.common_view_popup, null);
    render(builder, _root);
    setOnShowListenerInternal();
    setOnDismissListenerInternal();

    if (builder.showListener != null) {
      setOnShowListener(builder.showListener);
    }
    if (builder.cancelListener != null) {
      setOnCancelListener(builder.cancelListener);
    }
    if (builder.dismissListener != null) {
      setOnDismissListener(builder.dismissListener);
    }
    this.setCancelable(builder.cancelable);

    setViewInternal(_root);
  }

  private static int getTheme(Builder builder) {
    return builder.theme != 0
        ? builder.theme
        : (Vu.screenWidth() >= 1080
            ? R.style.Theme_AppCompat_Light_Dialog_Alert_PopUp_Big
            : R.style.Theme_AppCompat_Light_Dialog_Alert_PopUp);
  }

  private void render(Builder builder, View content) {
    View _root = content.findViewById(R.id.popup_root);
    if (builder.theme != 0) {
      _root.setBackground(null);
    } else {
      _root.setBackground(builder.backgroundDrawable);
    }
    View _normal = content.findViewById(R.id.popup_normal);
    View _pagers = content.findViewById(R.id.popup_pagers);
    if (builder.pagerAdapter != null) {
      _pagers.setVisibility(View.VISIBLE);
      _normal.setVisibility(View.GONE);
      VPager _pager = content.findViewById(R.id.popup_pager);
      VPagerCircleIndicator _indicator = content.findViewById(R.id.popup_indicator);
      _pager.setAdapter(builder.pagerAdapter);
      _indicator.setViewPager(_pager, _pager.getCurrentItem());
      _indicator.invalidate();
    } else {
      _pagers.setVisibility(View.GONE);
      _normal.setVisibility(View.VISIBLE);
      VImage _image = content.findViewById(R.id.popup_image);
      Space _top_space = content.findViewById(R.id.popup_top_space);
      _title = content.findViewById(R.id.popup_title);
      _subtitle = content.findViewById(R.id.popup_subtitle);
      if (builder.imageRes != -1) {
        _image.setImageResource(builder.imageRes);
        _image.setVisibility(View.VISIBLE);
        _top_space.setVisibility(View.GONE);
      } else if (builder.customView != null) {
        _top_space.setVisibility(View.GONE);
        _image.setVisibility(View.GONE);
        VFrame _custom_root = content.findViewById(R.id.popup_custom_root);
        FrameLayout.LayoutParams customRootParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        _custom_root.addView(builder.customView, customRootParams);
      } else {
        _top_space.setVisibility(View.VISIBLE);
        _image.setVisibility(View.GONE);
      }
      if (!TextUtils.isEmpty(builder.title)) {
        _title.setTypeface(TTypeface.typeface(TTypeface.MEDIUM), Typeface.BOLD);
        _title.setText(builder.title);
        _title.setVisibility(View.VISIBLE);
      } else {
        _title.setVisibility(View.GONE);
      }
      if (!TextUtils.isEmpty(builder.subtitle)) {
        if (builder.imageRes == -1 && builder.pagerAdapter == null) {
          // 无图片且不是多页，文字提示框则修改标题与子标题之间间距
          LayoutParams subtitleParams = (LayoutParams) _subtitle.getLayoutParams();
          subtitleParams.topMargin =
              builder.subtitleMarginTop > -1 ? builder.subtitleMarginTop : MetricsUtil.DP_18;
          subtitleParams.bottomMargin =
              builder.subtitleMarginBottom > -1 ? builder.subtitleMarginBottom : MetricsUtil.DP_24;
          _subtitle.setLayoutParams(subtitleParams);
        }
        _subtitle.setText(builder.subtitle);
        if (builder.subtitle instanceof SpannableString) {
          _subtitle.setMovementMethod(LinkMovementMethod.getInstance());
        }
        _subtitle.setGravity(builder.subtitleGravity);
        _subtitle.setVisibility(View.VISIBLE);
      } else {
        _subtitle.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(builder.title)) {
          LayoutParams titleParam = (LayoutParams) _title.getLayoutParams();
          titleParam.topMargin = MetricsUtil.DP_18;
          titleParam.bottomMargin = MetricsUtil.DP_24;
          _title.setLayoutParams(titleParam);
        }
      }

      if (!TextUtils.isEmpty(builder.checkBoxTxt)) {
        _checkBox = content.findViewById(R.id.check);
        ViewGroup vp = content.findViewById(R.id.checkbox);
        Vu.gone(vp, true);
        TextView tv = content.findViewById(R.id.reminder);
        tv.setText(builder.checkBoxTxt);
        vp.setOnClickListener(v -> _checkBox.setChecked(!_checkBox.isChecked()));
      }
    }
    _positive_primary_button = content.findViewById(R.id.popup_positive_primary_button);
    _positive_secondary_button = content.findViewById(R.id.popup_positive_secondary_button);
    _negative = content.findViewById(R.id.popup_negative);
    Space _bottom_space = content.findViewById(R.id.popup_bottom_space);
    _bottom_text = content.findViewById(R.id.popup_bottom_text);
    if (!TextUtils.isEmpty(builder.positivePrimaryText)) {
      _positive_primary_button.setText(builder.positivePrimaryText);
      if (builder.positivePrimaryColorRes != -1) {
        _positive_primary_button.setBackgroundResource(builder.positivePrimaryColorRes);
      }
      _positive_primary_button.setOnClickListener(
          v -> {
            if (builder.onPositivePrimary != null) {
              builder.onPositivePrimary.run();
            }
            if (builder.autoDismiss) {
              dismiss();
            }
          });
      _positive_primary_button.setVisibility(View.VISIBLE);
    } else {
      _positive_primary_button.setVisibility(View.GONE);
    }
    if (builder.isRipple) {
      _positive_primary_button.setBackgroundResource(R.drawable.common_primary_ripple);
    }
    if (!TextUtils.isEmpty(builder.positiveSecondaryText)) {
      _positive_secondary_button.setText(builder.positiveSecondaryText);
      if (builder.positiveSecondaryColorRes != -1) {
        _positive_secondary_button.setBackgroundResource(builder.positiveSecondaryColorRes);
      }
      _positive_secondary_button.setOnClickListener(
          v -> {
            if (builder.onPositiveSecondary != null) {
              builder.onPositiveSecondary.run();
            }
            if (builder.autoDismiss) {
              dismiss();
            }
          });
      _positive_secondary_button.setVisibility(View.VISIBLE);
    } else {
      _positive_secondary_button.setVisibility(View.GONE);
    }

    if (!TextUtils.isEmpty(builder.negativeText)) {
      _negative.setText(builder.negativeText);
      _negative.setOnClickListener(
          v -> {
            if (builder.onNegative != null) {
              builder.onNegative.run();
            }
            if (builder.autoDismiss) {
              dismiss();
            }
          });
      if (builder.negativeTextColor != 0) {
        _negative.setTextColor(builder.negativeTextColor);
      }
      _negative.setVisibility(View.VISIBLE);
      _bottom_space.setVisibility(View.GONE);
    } else if (builder.customView != null
        && TextUtils.isEmpty(builder.positivePrimaryText)
        && TextUtils.isEmpty(builder.positiveSecondaryText)
        && TextUtils.isEmpty(builder.negativeText)) {
      _negative.setVisibility(View.GONE);
      _bottom_space.setVisibility(View.GONE);
    } else {
      _negative.setVisibility(View.GONE);
      _bottom_space.setVisibility(View.VISIBLE);
    }
    if (!TextUtils.isEmpty(builder.bottomText) && builder.theme == 0) {
      if (builder.bottomTextColor != 0) {
        _bottom_text.setTextColor(builder.bottomTextColor);
      }
      _bottom_text.setText(builder.bottomText);
      _bottom_text.setVisibility(View.VISIBLE);
    } else {
      _bottom_text.setVisibility(View.GONE);
    }
  }

  @Override
  public void show() {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      throw new IllegalStateException("Dialogs can only be shown from the UI thread.");
    }
    Window window = getWindow();
    window.setWindowAnimations(R.style.PopupOvershootAnimation);
    super.show();
  }

  public boolean getCheckBox() {
    return _checkBox.isChecked();
  }

  public static class Builder {

    private final Context context;

    private @DrawableRes int imageRes = -1;
    private CharSequence title;
    private CharSequence subtitle;
    // 复选框 需要有文字才能显示
    private CharSequence checkBoxTxt;
    private int subtitleGravity = Gravity.CENTER;

    private CharSequence positivePrimaryText;
    private @DrawableRes int positivePrimaryColorRes = -1;
    private @DrawableRes int positiveSecondaryColorRes = -1;
    private CharSequence positiveSecondaryText;
    private Runnable onPositivePrimary;
    private Runnable onPositiveSecondary;
    private boolean isRipple; // 水波纹效果

    private CharSequence negativeText;
    private int negativeTextColor = 0;
    private Runnable onNegative;

    private int bottomTextColor;
    private CharSequence bottomText;

    private View customView;

    private PagerAdapter pagerAdapter;

    private boolean cancelable = true;
    private boolean autoDismiss = true;

    private OnShowListener showListener;
    private OnDismissListener dismissListener;
    private OnCancelListener cancelListener;
    private Drawable backgroundDrawable;
    private int theme;

    private int subtitleMarginTop = -1;
    private int subtitleMarginBottom = -1;

    public Builder(@NonNull Context context) {
      this.context = context;
      this.backgroundDrawable = context.getDrawable(R.drawable.common_view_popup_bg);
    }

    /** 默认设置对话框边界，会根据屏幕分辨率设置不同的2种左右间距；如果重新设置相关属性，需要考虑是否区分不同的分辨率 */
    public Builder theme(int theme) {
      this.theme = theme;
      return this;
    }

    public Builder backgroundDrawable(@DrawableRes int background) {
      this.backgroundDrawable = this.context.getDrawable(background);
      return this;
    }

    public Builder backgroundDrawable(Drawable background) {
      this.backgroundDrawable = background;
      return this;
    }

    public Builder setCheckBoxTxt(CharSequence checkBoxTxt) {
      this.checkBoxTxt = checkBoxTxt;
      return this;
    }

    public Builder image(@DrawableRes int imageRes) {
      this.imageRes = imageRes;
      return this;
    }

    public Builder title(@StringRes int titleRes, Object... formatArgs) {
      return title(this.context.getString(titleRes, formatArgs));
    }

    public Builder title(CharSequence title) {
      this.title = title;
      return this;
    }

    public Builder subtitle(@StringRes int subtitleRes, Object... formatArgs) {
      return subtitle(this.context.getString(subtitleRes, formatArgs));
    }

    public Builder subtitle(CharSequence subtitle) {
      this.subtitle = subtitle;
      return this;
    }

    public Builder subtitleMargin(int marginTop, int marginBottom) {
      this.subtitleMarginTop = marginTop;
      this.subtitleMarginBottom = marginBottom;
      return this;
    }

    public Builder setPagerAdapter(PagerAdapter pagerAdapter) {
      this.pagerAdapter = pagerAdapter;
      return this;
    }

    public Builder positivePrimary(@StringRes int positivePrimaryRes) {
      return positivePrimary(this.context.getString(positivePrimaryRes), onPositivePrimary);
    }

    public Builder positivePrimary(CharSequence positivePrimary) {
      return positivePrimary(positivePrimary, onPositivePrimary);
    }

    public Builder positivePrimary(@StringRes int positivePrimaryRes, Runnable onPositivePrimary) {
      return positivePrimary(this.context.getString(positivePrimaryRes), onPositivePrimary);
    }

    public Builder positiveRipple(boolean isRipple) {
      this.isRipple = isRipple;
      return this;
    }

    public Builder positivePrimary(CharSequence positivePrimary, Runnable onPositive) {
      this.positivePrimaryText = positivePrimary;
      this.onPositivePrimary = onPositive;
      return this;
    }

    public Builder positivePrimaryColorRes(@DrawableRes int positivePrimaryColorRes) {
      this.positivePrimaryColorRes = positivePrimaryColorRes;
      return this;
    }

    public Builder positiveSecondary(@StringRes int positiveSecondaryRes) {
      return positiveSecondary(this.context.getString(positiveSecondaryRes), onPositiveSecondary);
    }

    public Builder positiveSecondary(CharSequence positiveSecondary) {
      return positiveSecondary(positiveSecondary, onPositiveSecondary);
    }

    public Builder positiveSecondary(
        @StringRes int positiveSecondaryRes, Runnable onPositiveSecondary) {
      return positiveSecondary(this.context.getString(positiveSecondaryRes), onPositiveSecondary);
    }

    public Builder positiveSecondary(CharSequence positiveSecondary, Runnable onPositiveSecondary) {
      this.positiveSecondaryText = positiveSecondary;
      this.onPositiveSecondary = onPositiveSecondary;
      return this;
    }

    public Builder positiveSecondaryColorRes(@DrawableRes int positiveSecondaryColorRes) {
      this.positiveSecondaryColorRes = positiveSecondaryColorRes;
      return this;
    }

    public Builder negative(CharSequence negativeText) {
      negative(negativeText, onNegative);
      return this;
    }

    public Builder negative(@StringRes int negativeText, Object... formatArgs) {
      negative(this.context.getString(negativeText, formatArgs), onNegative);
      return this;
    }

    public Builder negative(@StringRes int positivePrimaryRes, Runnable onNegative) {
      return negative(this.context.getString(positivePrimaryRes), onNegative);
    }

    public Builder negative(CharSequence negativeText, Runnable onNegative) {
      this.negativeText = negativeText;
      this.onNegative = onNegative;
      return this;
    }

    public Builder negativeTextColor(@ColorInt int negativeTextColor) {
      this.negativeTextColor = negativeTextColor;
      return this;
    }

    public Builder bottomTextColor(@ColorInt int bottomTextColor) {
      this.bottomTextColor = bottomTextColor;
      return this;
    }

    public Builder bottomText(@StringRes int bottomTextRes, Object... formatArgs) {
      return bottomText(this.context.getString(bottomTextRes, formatArgs));
    }

    public Builder bottomText(CharSequence bottomText) {
      this.bottomText = bottomText;
      return this;
    }

    public Builder customView(@LayoutRes int layoutRes) {
      LayoutInflater inflater = LayoutInflater.from(this.context);
      return customView(inflater.inflate(layoutRes, null));
    }

    public Builder customView(View view) {
      this.customView = view;
      return this;
    }

    public Builder cancelable(boolean cancelable) {
      this.cancelable = cancelable;
      return this;
    }

    public Builder autoDismiss(boolean dismiss) {
      this.autoDismiss = dismiss;
      return this;
    }

    public Builder setOnShowListener(OnShowListener listener) {
      this.showListener = listener;
      return this;
    }

    public Builder setOnDismissListener(OnDismissListener listener) {
      this.dismissListener = listener;
      return this;
    }

    public Builder setOnCancelListener(OnCancelListener listener) {
      this.cancelListener = listener;
      return this;
    }

    public Builder setSubTittleGravity(int gravity) {
      this.subtitleGravity = gravity;
      return this;
    }

    public PopupDialog build() {
      return new PopupDialog(this);
    }

    public PopupDialog show() {
      PopupDialog dialog = build();
      dialog.show();
      return dialog;
    }

    public PopupDialog showImmediate() {
      PopupDialog dialog = build();
      dialog.showImmediate();
      return dialog;
    }

    public PopupDialog showWithPriority(int priority) {
      PopupDialog dialog = build();
      dialog.showWithPriority(priority);
      return dialog;
    }
  }
}
