package com.hello.sandbox.common;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.util.Pair;
import com.hello.sandbox.common.util.MetricsUtil;
import com.hello.sandbox.common.util.Vu;
import com.hello.sandbox.common.util.ContextHolder;
import com.hello.sandbox.common.util.Cu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joor.Reflect;
import v.AAdapter;
import v.Anu;
import v.TTypeface;
import v.VButton_FakeShadow;
import v.VLinear_Foreground;
import v.VList;
import v.VList_ScrollableHeight;
import v.VText;

/** @author Aidan Follestad (afollestad) */
public class Dialog extends DialogBase implements View.OnClickListener, DialogExtraState {

  // all dps used in this class , avoid double counting
  private static final int DP_6 = MetricsUtil.dp(6);
  private static final int DP_8 = MetricsUtil.dp(8);
  private static final int DP_12 = MetricsUtil.dp(12);
  private static final int DP_16 = MetricsUtil.dp(16);
  private static final int DP_24 = MetricsUtil.dp(24);
  private static final int DP_32 = MetricsUtil.dp(32);
  private static final int DP_40 = MetricsUtil.dp(40);
  private static final int DP_42 = MetricsUtil.dp(42);
  private static final int DP_48 = MetricsUtil.dp(48);
  private static final int DP_64 = MetricsUtil.dp(64);
  private static final int DP_72 = MetricsUtil.dp(72);
  private static final int DP_144 = MetricsUtil.dp(144);

  public enum Theme {
    LIGHT,
    DARK
  }

  public enum Action {
    POSITIVE,
    NEUTRAL,
    NEGATIVE
  }

  public final VLinear_Foreground view;

  // 当outBoxedDescription不为空时，viewInbox!=view，其他时候，viewInbox==view，用viewInbox进行addView操作
  public VLinear_Foreground viewInbox;

  protected final Builder builder;
  protected VList listView;
  protected View icon;
  public TextView title;
  public TextView subTitle;
  protected TextView content;
  protected LinearLayout titleFrame;
  protected FrameLayout customViewFrame;
  protected FrameLayout contentFrame;
  protected LinearLayout mainFrame;
  protected FrameLayout itemsFrame;
  protected ViewGroup buttonFrame;
  public TextView extraDerection;

  /**
   * This defaults to true. If set to false, the dialog will not automatically be dismissed when an
   * action button is pressed, and not automatically dismissed when the user selects a list item.
   *
   * @param dismiss Whether or not to dismiss the dialog automatically.
   * @return The Builder instance so you can chain calls to it.
   */
  public Dialog autoDismiss(boolean dismiss) {
    builder.autoDismiss = dismiss;
    return this;
  }

  protected FrameLayout positiveButton;
  TextView positiveText;

  public TextView setPositiveText(String text) {
    if (positiveText != null) {
      if (builder.bigPositiveButton) {
        positiveText.setText(text.toUpperCase());
      } else {
        positiveText.setText(text);
      }
    }
    return positiveText;
  }

  public void setPositiveRunnable(Runnable r) {
    builder.onPositive = r;
  }

  public View positiveButton() {
    if (positiveButton != null) {
      return positiveButton;
    }
    return findViewById(R.id.buttonDefaultPositive);
  }

  protected FrameLayout neutralButton;

  public View neutralButton() {
    if (neutralButton != null) {
      return neutralButton;
    }
    return findViewById(R.id.buttonDefaultNeutral);
  }

  protected FrameLayout negativeButton;
  TextView negativeText;

  public TextView setNegativeText(String text) {
    if (negativeText != null) {
      if (builder.bigNegativeButton) {
        negativeText.setText(text.toUpperCase());
      } else {
        negativeText.setText(text);
      }
    }
    return negativeText;
  }

  public TextView setNegativeCharText(CharSequence text) {
    if (negativeText != null) {
      negativeText.setText(text);
    }
    return negativeText;
  }

  public void setNegativeRunnable(Runnable r) {
    builder.onNegative = r;
  }

  public View negativeButton() {
    if (negativeButton != null) {
      return negativeButton;
    }
    return findViewById(R.id.buttonDefaultNegative);
  }

  protected boolean isStacked;
  protected boolean alwaysCallMultiChoiceCallback;
  protected boolean alwaysCallSingleChoiceCallback;
  protected int defaultItemColor;
  protected ListType listType;
  protected List<Integer> selectedIndicesList;
  /**
   * button的textcolor是否被设置过
   *
   * @param context
   */
  private boolean positivebuttonTextColorHadset;

  private boolean negativebuttonTextColorHadset;

  private static ContextThemeWrapper getTheme(Builder builder) {
    TypedArray a =
        builder.context.getTheme().obtainStyledAttributes(new int[] {R.attr.md_dark_theme});
    boolean darkTheme = builder.theme == Theme.DARK;
    if (!darkTheme) {
      try {
        darkTheme = a.getBoolean(0, false);
        builder.theme = darkTheme ? Theme.DARK : Theme.LIGHT;
      } finally {
        a.recycle();
      }
    }
    return new ContextThemeWrapper(builder.context, darkTheme ? R.style.MD_Dark : R.style.MD_Light);
  }

  @SuppressLint("InflateParams")
  protected Dialog(Builder b) {
    super(getTheme(b), b.bigRoundedCorner, b.customTheme);

    builder = b;

    viewInbox = new VLinear_Foreground(builder.context);
    viewInbox.setOrientation(LinearLayout.VERTICAL);
    viewInbox.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    if (TextUtils.isEmpty(b.outBoxedDescription)) {
      view = viewInbox;
    } else {
      viewInbox.setBackgroundResource(
          R.drawable.abc_dialog_material_background_roundedbig_revealinner);

      view = new VLinear_Foreground(builder.context);
      view.setOrientation(LinearLayout.VERTICAL);
      view.setLayoutParams(
          new ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      view.addView(viewInbox);

      extraDerection = new VText(builder.context);
      LinearLayout.LayoutParams layoutParams =
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      layoutParams.topMargin = DP_12;
      layoutParams.gravity = Gravity.CENTER;
      extraDerection.setLayoutParams(layoutParams);
      extraDerection.setTextSize(b.descriptionTextSize);
      extraDerection.setTypeface(TTypeface.typeface(TTypeface.REGULAR));
      if (b.descriptionColor != -1) {
        extraDerection.setTextColor(b.descriptionColor);
      } else {
        extraDerection.setTextColor(getContext().getResources().getColor(R.color.common_grey_04));
      }
      extraDerection.setText(builder.outBoxedDescription);
      extraDerection.setGravity(Gravity.CENTER);
      if (b.descriptionClickDismiss) {
        extraDerection.setOnClickListener(v -> dismiss());
      }
      view.addView(extraDerection);
    }

    mainFrame = new LinearLayout(builder.context);
    mainFrame.setOrientation(LinearLayout.VERTICAL);
    mainFrame.setLayoutParams(
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

    if (b.bigRoundedStyle) {
      buildBigRoundedStyle(b);
    } else {
      buildNormalStyle(b);
    }

    if (b.showListener != null) {
      setOnShowListener(b.showListener);
    }
    if (b.cancelListener != null) {
      setOnCancelListener(b.cancelListener);
    }
    if (b.dismissListener != null) {
      setOnDismissListener(b.dismissListener);
    }
    if (b.keyListener != null) {
      setOnKeyListener(b.keyListener);
    }
    viewInbox.addView(mainFrame);

    this.setCancelable(b.cancelable);
    this.setCanceledOnTouchOutside(b.cancelable);

    if (builder.backgroundColor == 0) {
      builder.backgroundColor = resolveColor(getContext(), R.attr.md_background_color);
    } else {
      this.view.setBackgroundColor(builder.backgroundColor);
    }

    final int mdAccentColor = resolveColor(getContext(), R.attr.md_accent_color);

    positivebuttonTextColorHadset = builder.positiveColor != 0;
    negativebuttonTextColorHadset = builder.negativeColor != 0;

    if (mdAccentColor != 0) {
      builder.positiveColor = builder.positiveColor != 0 ? builder.positiveColor : mdAccentColor;
      builder.negativeColor = builder.negativeColor != 0 ? builder.negativeColor : mdAccentColor;
      builder.neutralColor = builder.neutralColor != 0 ? builder.neutralColor : mdAccentColor;
    }

    if (b.forceStacking || b.bigPositiveButton || b.bigNegativeButton) {
      isStacked = true;
    }
    invalidateActions();
    setOnShowListenerInternal();
    setOnDismissListenerInternal();

    setViewInternal(view);
  }

  public DialogTag getDialogTag() {
    return builder.dialogTag;
  }

  private void buildNormalStyle(Builder b) {
    boolean hasTitle = b.title != null && b.title.toString().trim().length() != 0;
    boolean hasSubTitle = b.subTitle != null && b.subTitle.toString().trim().length() != 0;

    if (hasTitle) {
      titleFrame = new LinearLayout(builder.context);
      titleFrame.setOrientation(
          builder.bigTopIcon ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
      titleFrame.setGravity(Gravity.CENTER_VERTICAL);
      titleFrame.setLayoutParams(
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      titleFrame.setPadding(
          DP_24, builder.frameTopPadding == -1 ? DP_24 : builder.frameTopPadding, DP_24, DP_16);

      if (b.icon != null) {
        ImageView icon = new ImageView(builder.context);
        this.icon = icon;
        icon.setImageDrawable(b.icon);
        if (!builder.bigTopIcon) icon.setScaleType(ImageView.ScaleType.FIT_XY);
      }
      if (b.iconView != null) {
        this.icon = b.iconView;
      }
      if (this.icon != null) {
        LinearLayout.LayoutParams iconLP =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (!builder.bigTopIcon) {
          iconLP.setMargins(0, 0, DP_16, 0);
        } else {
          iconLP.setMargins(DP_16, DP_16, DP_16, DP_16);
          iconLP.gravity = Gravity.CENTER_HORIZONTAL;
        }
        icon.setLayoutParams(iconLP);
        titleFrame.addView(icon);
      }

      title = new VText(builder.context);
      LinearLayout.LayoutParams titleLp =
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      if (builder.bigTopIcon) {
        titleLp.gravity = Gravity.CENTER_VERTICAL;
      }
      title.setLayoutParams(titleLp);
      title.setTextSize(20);
      title.setTypeface(TTypeface.typeface(TTypeface.MEDIUM));
      title.setText(builder.title);
      if (b.titleColor != -1) {
        title.setTextColor(b.titleColor);
      } else {
        final int fallback = resolveColor(getContext(), android.R.attr.textColorPrimary);
        title.setTextColor(resolveColor(getContext(), R.attr.md_title_color, fallback));
      }
      title.setGravity(b.titleGravity);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //noinspection ResourceType
        title.setTextAlignment(gravityToAlignment(b.titleGravity));
      }
      titleFrame.addView(title);
      mainFrame.addView(titleFrame);
    }
    if (hasSubTitle) {
      subTitle = new VText(builder.context);
      LinearLayout.LayoutParams subTitleLp =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      if (builder.bigTopIcon) {
        subTitleLp.gravity = Gravity.CENTER_VERTICAL;
      }
      subTitleLp.setMargins(0, hasTitle ? 0 : DP_24, 0, DP_16);
      subTitle.setLayoutParams(subTitleLp);
      subTitle.setTextSize(14);
      subTitle.setTypeface(TTypeface.typeface(TTypeface.REGULAR));
      subTitle.setText(builder.subTitle);
      subTitle.setPadding(DP_24, 0, DP_24, 0);
      if (b.subTitleColor != -1) {
        subTitle.setTextColor(b.subTitleColor);
      } else {
        final int fallback = resolveColor(getContext(), android.R.attr.textColorSecondary);
        final int subtitleColor = resolveColor(getContext(), R.attr.md_subtitle_color, fallback);
        subTitle.setTextColor(subtitleColor);
      }
      subTitle.setGravity(b.subTitleGravity);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //noinspection ResourceType
        title.setTextAlignment(gravityToAlignment(b.titleGravity));
      }
      mainFrame.addView(subTitle);
    }

    if (b.content != null) {
      contentFrame = new FrameLayout(builder.context);
      contentFrame.setLayoutParams(
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      // contentFrame.setPadding(0, 0, 0, DP_8);
      contentFrame.setPadding(DP_24, 0, DP_24, DP_16);
      content = new VText(builder.context);
      content.setLayoutParams(
          new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      if (b.enableContentMovementMethod) {
        content.setMovementMethod(LinkMovementMethod.getInstance());
        content.setHighlightColor(getContext().getResources().getColor(R.color.transparent));
      }
      content.setTypeface(TTypeface.typeface(TTypeface.REGULAR));
      content.setTextSize(builder.contentTextSize > 0 ? builder.contentTextSize : 16);
      content.setText(builder.content);
      if (b.contentColor != -1) {
        content.setTextColor(b.contentColor);
      } else {
        final int fallback = resolveColor(getContext(), android.R.attr.textColorSecondary);
        final int contentColor = resolveColor(getContext(), R.attr.md_content_color, fallback);
        content.setTextColor(contentColor);
      }
      if (hasTitle || hasSubTitle) {
        content.setGravity(b.contentGravity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          //noinspection ResourceType
          content.setTextAlignment(gravityToAlignment(b.contentGravity));
        }
      } else {
        contentFrame.setMinimumHeight(DP_72);
        contentFrame.setPadding(DP_24, DP_24, DP_24, DP_16);
      }
      contentFrame.addView(content);
      mainFrame.addView(contentFrame);
    }
    if (b.customView != null) {
      customViewFrame = new FrameLayout(builder.context);
      LinearLayout.LayoutParams contentFrameLP =
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      contentFrameLP.setMargins(
          b.constomViewMarginLeft == -1 ? 0 : b.constomViewMarginLeft,
          b.constomViewMarginTop == -1 ? 0 : b.constomViewMarginTop,
          b.constomViewMarginRight == -1 ? 0 : b.constomViewMarginRight,
          b.constomViewMarginBottom == -1 ? 0 : b.constomViewMarginBottom);

      customViewFrame.setLayoutParams(contentFrameLP);
      customViewFrame.addView(b.customView);
      if (b.customViewAtTop) {
        mainFrame.addView(customViewFrame, 0);
      } else {
        mainFrame.addView(customViewFrame);
      }
    }
    if ((b.items != null && b.items.length > 0) || b.adapter != null) {
      itemsFrame = new FrameLayout(builder.context);
      itemsFrame.setLayoutParams(
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      listView = new VList_ScrollableHeight(builder.context);
      listView.setLayoutParams(
          new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      listView.setDivider(null);
      listView.setDividerHeight(0);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        listView.setScrollBarDefaultDelayBeforeFade(1000);
      }
      listView.setScrollBarStyle(VList.SCROLLBARS_OUTSIDE_OVERLAY);
      listView.setFastScrollEnabled(builder.fastScroll);
      listView.setSelector(resolveDrawable(getContext(), R.attr.md_selector));
      if (b.itemColor != 0) {
        defaultItemColor = b.itemColor;
      } else if (b.theme == Theme.LIGHT) {
        defaultItemColor = Color.BLACK;
      } else {
        defaultItemColor = Color.WHITE;
      }
      if (builder.adapter == null) {
        if (builder.listCallbackSingle != null) {
          listType = ListType.SINGLE;
          alwaysCallSingleChoiceCallback = b.alwaysCallSingleChoiceCallback;
        } else if (builder.listCallbackMulti != null) {
          listType = ListType.MULTI;
          if (builder.selectedIndices != null) {
            selectedIndicesList = new ArrayList<>(Arrays.asList(builder.selectedIndices));
          } else {
            selectedIndicesList = new ArrayList<>();
          }
          alwaysCallMultiChoiceCallback = b.alwaysCallMultiChoiceCallback;
        } else {
          listType = ListType.REGULAR;
        }
        builder.adapter =
            new MaterialDialogAdapter(
                builder.context, ListType.getLayoutForType(listType), R.id.title, builder.items);
      }
      itemsFrame.setPadding(
          0,
          (hasTitle || hasSubTitle || b.content != null || b.customView != null) ? 0 : DP_8,
          0,
          !hasActionButtons() ? DP_8 : 0);
      itemsFrame.addView(listView);
      mainFrame.addView(itemsFrame);
    }
    if (hasTitle
        && !hasSubTitle
        && b.content == null
        && b.customView == null
        && (b.items == null || b.items.length == 0)
        && b.adapter == null) {
      // 如果只有title，最小高度72dp
      titleFrame.setMinimumHeight(DP_72);
    }
  }

  private void buildBigRoundedStyle(Builder b) {
    boolean hasTitle = b.title != null && b.title.toString().trim().length() != 0;
    boolean hasSubTitle = b.subTitle != null && b.subTitle.toString().trim().length() != 0;
    boolean hasContent = b.content != null && b.content.toString().trim().length() != 0;
    boolean hasCustomView = b.customView != null;
    boolean hasItems = (b.items != null && b.items.length > 0) || b.adapter != null;

    if (builder.frameTopPadding != -1) {
      mainFrame.setPadding(0, builder.frameTopPadding, 0, 0);
    }

    if (b.icon != null) {
      ImageView icon = new ImageView(builder.context);
      this.icon = icon;
      icon.setImageDrawable(b.icon);
      if (builder.topIconScaleType == null) {
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      } else {
        icon.setScaleType(builder.topIconScaleType);
      }
    }
    if (b.iconView != null) {
      this.icon = b.iconView;
    }

    if (this.icon != null) {
      LinearLayout.LayoutParams iconLP =
          new LinearLayout.LayoutParams(builder.topIconWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
      if (b.iconTopMargin == -1) {
        iconLP.topMargin = DP_32;
      } else {
        iconLP.topMargin = b.iconTopMargin;
      }
      iconLP.gravity = Gravity.CENTER_HORIZONTAL;
      icon.setMinimumHeight(DP_144);
      mainFrame.addView(icon, iconLP);
    }
    if (hasTitle || hasSubTitle || hasContent) {
      int wrapperBottomMargin = 0;
      if ((!hasCustomView || b.customViewAtTop) && !hasItems) {
        wrapperBottomMargin = builder.contentBottomMargin;
      }

      LinearLayout contentWrapperFrame = new LinearLayout(builder.context);
      contentWrapperFrame.setOrientation(LinearLayout.VERTICAL);
      LinearLayout.LayoutParams contentWrapperFrameLP =
          new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
      contentWrapperFrameLP.setMargins(
          DP_24,
          Vu.screenHeight() < 960 ? DP_8 : DP_32,
          DP_24,
          wrapperBottomMargin); // fix bug 23084
      contentWrapperFrame.setLayoutParams(contentWrapperFrameLP);

      if (hasTitle) {
        title = new VText(builder.context);
        LinearLayout.LayoutParams titleLp =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleLp.gravity = Gravity.CENTER;
        title.setLayoutParams(titleLp);
        title.setTextSize(20);
        title.setTypeface(TTypeface.typeface(TTypeface.REGULAR));
        if (b.titleColor != -1) {
          title.setTextColor(b.titleColor);
        } else {
          final int fallback = getContext().getResources().getColor(R.color.common_grey_01);
          title.setTextColor(resolveColor(getContext(), R.attr.md_title_color, fallback));
        }
        title.setText(builder.title);
        title.setGravity(Gravity.CENTER);
        contentWrapperFrame.addView(title);
      }
      if (hasSubTitle) {
        subTitle = new VText(builder.context);
        LinearLayout.LayoutParams subTitleLp =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subTitleLp.gravity = Gravity.CENTER_VERTICAL;
        subTitleLp.setMargins(0, hasTitle ? DP_12 : 0, 0, 0);
        subTitle.setLayoutParams(subTitleLp);
        subTitle.setTextSize(14);
        subTitle.setTypeface(TTypeface.typeface(TTypeface.REGULAR));
        if (b.subTitleColor != -1) {
          subTitle.setTextColor(b.subTitleColor);
        } else {
          final int fallback = getContext().getResources().getColor(R.color.common_grey_02);
          final int subtitleColor = resolveColor(getContext(), R.attr.md_subtitle_color, fallback);
          subTitle.setTextColor(subtitleColor);
        }
        subTitle.setGravity(Gravity.CENTER);
        subTitle.setText(builder.subTitle);
        contentWrapperFrame.addView(subTitle);
      }

      if (hasContent) {
        content = new VText(builder.context);
        LinearLayout.LayoutParams contentLp =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentLp.gravity = Gravity.CENTER_VERTICAL;
        contentLp.setMargins(0, hasTitle || hasSubTitle ? DP_12 : 0, 0, 0);
        if (b.enableContentMovementMethod) {
          content.setMovementMethod(LinkMovementMethod.getInstance());
          content.setHighlightColor(getContext().getResources().getColor(R.color.transparent));
        }
        content.setLayoutParams(contentLp);
        content.setTypeface(TTypeface.typeface(TTypeface.REGULAR));
        content.setTextSize(builder.contentTextSize > 0 ? builder.contentTextSize : 14);
        content.setText(builder.content);
        if (b.contentColor != -1) {
          content.setTextColor(b.contentColor);
        } else {
          final int fallback = getContext().getResources().getColor(R.color.common_grey_02);
          final int contentColor = resolveColor(getContext(), R.attr.md_content_color, fallback);
          content.setTextColor(contentColor);
        }
        content.setGravity(b.bigRoundedStyleContentGravity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
          //noinspection ResourceType
          content.setTextAlignment(gravityToAlignment(b.bigRoundedStyleContentGravity));
        }
        contentWrapperFrame.addView(content);
      }
      mainFrame.addView(contentWrapperFrame);
    }
    if (hasCustomView) {
      customViewFrame = new FrameLayout(builder.context);
      LinearLayout.LayoutParams contentFrameLP =
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      contentFrameLP.setMargins(
          b.constomViewMarginLeft == -1 ? 0 : b.constomViewMarginLeft,
          b.constomViewMarginTop == -1 ? 0 : b.constomViewMarginTop,
          b.constomViewMarginRight == -1 ? 0 : b.constomViewMarginRight,
          b.constomViewMarginBottom == -1 ? 0 : b.constomViewMarginBottom);

      customViewFrame.setLayoutParams(contentFrameLP);
      customViewFrame.addView(b.customView);
      if (b.customViewAtTop) {
        mainFrame.addView(customViewFrame, 0);
      } else {
        mainFrame.addView(customViewFrame);
      }
    }
    if (hasItems) {
      itemsFrame = new FrameLayout(builder.context);
      itemsFrame.setLayoutParams(
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      listView = new VList_ScrollableHeight(builder.context);
      listView.setLayoutParams(
          new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      listView.setDivider(null);
      listView.setDividerHeight(0);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        listView.setScrollBarDefaultDelayBeforeFade(1000);
      }
      listView.setScrollBarStyle(VList.SCROLLBARS_OUTSIDE_OVERLAY);
      listView.setFastScrollEnabled(builder.fastScroll);
      listView.setSelector(resolveDrawable(getContext(), R.attr.md_selector));
      if (b.itemColor != 0) {
        defaultItemColor = b.itemColor;
      } else if (b.theme == Theme.LIGHT) {
        defaultItemColor = Color.BLACK;
      } else {
        defaultItemColor = Color.WHITE;
      }
      if (builder.adapter == null) {
        if (builder.listCallbackSingle != null) {
          listType = ListType.SINGLE;
          alwaysCallSingleChoiceCallback = b.alwaysCallSingleChoiceCallback;
        } else if (builder.listCallbackMulti != null) {
          listType = ListType.MULTI;
          if (builder.selectedIndices != null) {
            selectedIndicesList = new ArrayList<>(Arrays.asList(builder.selectedIndices));
          } else {
            selectedIndicesList = new ArrayList<>();
          }
          alwaysCallMultiChoiceCallback = b.alwaysCallMultiChoiceCallback;
        } else {
          listType = ListType.REGULAR;
        }
        builder.adapter =
            new MaterialDialogAdapter(
                builder.context, ListType.getLayoutForType(listType), R.id.title, builder.items);
      }
      itemsFrame.setPadding(
          0,
          (hasTitle || hasSubTitle || b.content != null || b.customView != null) ? 0 : DP_8,
          0,
          !hasActionButtons() ? DP_8 : 0);
      itemsFrame.addView(listView);
      mainFrame.addView(itemsFrame);
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
  private static int gravityToAlignment(int gravity) {
    switch (gravity) {
      case Gravity.CENTER:
        return View.TEXT_ALIGNMENT_CENTER;
      case Gravity.END:
        return View.TEXT_ALIGNMENT_VIEW_END;
      default:
        return View.TEXT_ALIGNMENT_VIEW_START;
    }
  }

  @Override
  public void onShow(DialogInterface dialog) {
    super.onShow(dialog); // calls any external show listeners
    checkIfStackingNeeded();
  }

  /** Constructs the dialog's list content and sets up click listeners. */
  private void invalidateList() {
    if ((builder.items == null || builder.items.length == 0) && builder.adapter == null) return;
    listView.setAdapter(builder.adapter);

    if (listType != null) {
      // Only set listener for 1st-party adapter, leave custom adapter implementation to user with
      // getVList()
      listView.setOnItemClickListener(
          new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              if (listType == ListType.MULTI) {
                // Keep our selected items up to date
                boolean isChecked =
                    !((CheckBox) view.findViewById(R.id.control))
                        .isChecked(); // Inverted because the view's click listener is called before
                // the check is toggled
                boolean previouslySelected = selectedIndicesList.contains(position);
                if (isChecked) {
                  if (!previouslySelected) {
                    if (builder.maxChoices != -1
                        && selectedIndicesList.size() >= builder.maxChoices) {
                      builder.maxChoicesAction.run();
                      return;
                    } else {
                      selectedIndicesList.add(position);
                    }
                  }
                } else if (previouslySelected) {
                  selectedIndicesList.remove(Integer.valueOf(position));
                }
              } else if (listType == ListType.SINGLE) {
                // Keep our selected item up to date
                if (builder.selectedIndex != position) {
                  builder.selectedIndex = position;
                  ((MaterialDialogAdapter) builder.adapter).notifyDataSetChanged();
                }
              }
              onClick(view);
            }
          });
    } else {
      listView.setOnItemClickListener(
          new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (builder.listCallback != null) {
                if (builder.autoDismiss) dismiss();
                builder.listCallback.onSelection(Dialog.this, view, position, null);
              }
            }
          });
    }
  }

  /**
   * Find the view touching the bottom of this ViewGroup. Non visible children are ignored, however
   * getChildDrawingOrder is not taking into account for simplicity and because it behaves
   * inconsistently across platform versions.
   *
   * @return View touching the bottom of this viewgroup or null
   */
  @Nullable
  private static View getBottomView(ViewGroup viewGroup) {
    View bottomView = null;
    for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
      View child = viewGroup.getChildAt(i);
      if (child.getVisibility() == View.VISIBLE && child.getBottom() == viewGroup.getBottom()) {
        bottomView = child;
        break;
      }
    }
    return bottomView;
  }

  @Nullable
  private static View getTopView(ViewGroup viewGroup) {
    View topView = null;
    for (int i = viewGroup.getChildCount() - 1; i >= 0; i--) {
      View child = viewGroup.getChildAt(i);
      if (child.getVisibility() == View.VISIBLE && child.getTop() == viewGroup.getTop()) {
        topView = child;
        break;
      }
    }
    return topView;
  }

  private static boolean canAdapterViewScroll(AdapterView lv) {
    /* Force it to layout it's children */
    if (lv.getLastVisiblePosition() == -1) return false;

    /* We can scroll if the first or last item is not visible */
    boolean firstItemVisible = lv.getFirstVisiblePosition() == 0;
    boolean lastItemVisible = lv.getLastVisiblePosition() == lv.getCount() - 1;

    if (firstItemVisible && lastItemVisible) {
      /* Or the first item's top is above or own top */
      if (lv.getChildAt(0).getTop() < lv.getPaddingTop()) return true;

      /* or the last item's bottom is beyond our own bottom */
      return lv.getChildAt(lv.getChildCount() - 1).getBottom()
          > lv.getHeight() - lv.getPaddingBottom();
    }

    return true;
  }

  private int calculateMaxButtonWidth() {
    /**
     * Max button width = (DialogWidth - Side margins) / [Number of buttons] From:
     * http://www.google.com/design/spec/components/dialogs.html#dialogs-specs
     */
    final int dialogWidth = getWindow().getDecorView().getMeasuredWidth();
    final int margins =
        (int) getContext().getResources().getDimension(R.dimen.md_button_padding_frame_side);
    return (dialogWidth - 2 * margins) / numberOfActionButtons();
  }

  /**
   * Measures the action button's and their text to decide whether or not the button should be
   * stacked.
   */
  private void checkIfStackingNeeded() {
    boolean oldIsStacked = isStacked;
    if (numberOfActionButtons() <= 1) {
      return;
    } else if (builder.forceStacking && !isStacked) {
      isStacked = true;
      invalidateActions();
      return;
    }

    final int maxWidth = calculateMaxButtonWidth();
    isStacked = false;

    if (builder.positiveText != null) {
      final int positiveWidth = positiveButton.getWidth();
      isStacked = positiveWidth > maxWidth;
    }

    if (!isStacked && builder.neutralText != null) {
      final int neutralWidth = neutralButton.getWidth();
      isStacked = neutralWidth > maxWidth;
    }

    if (!isStacked && builder.negativeText != null) {
      final int negativeWidth = negativeButton.getWidth();
      isStacked = negativeWidth > maxWidth;
    }
    if (oldIsStacked != isStacked) {
      invalidateActions();
    }
  }

  private Drawable getButtonSelector() {
    if (isStacked) {
      if (builder.selector != null) return builder.selector;
      Drawable custom = resolveDrawable(builder.context, R.attr.md_selector);
      if (custom != null) return custom;
    } else {
      if (builder.btnSelector != null) return builder.btnSelector;
      Drawable custom = resolveDrawable(builder.context, R.attr.md_btn_selector);
      if (custom != null) return custom;
    }
    return resolveDrawable(getContext(), isStacked ? R.attr.md_selector : R.attr.md_btn_selector);
  }

  private void setBigPositiveButton() {
    // 如果不区分LayoutParams的类型，在pixel2上会导致margin消失的bug
    if (isStacked) {
      LinearLayout.LayoutParams positiveButtonLP =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      positiveButtonLP.bottomMargin = DP_12;
      positiveButton.setLayoutParams(positiveButtonLP);
    } else {
      RelativeLayout.LayoutParams positiveButtonLP =
          new RelativeLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      positiveButtonLP.bottomMargin = DP_12;
      positiveButton.setLayoutParams(positiveButtonLP);
    }
    positiveText.setSingleLine(true);
    positiveText.setTextAppearance(getContext(), R.style.common_text_style_body_01_a);

    if (positivebuttonTextColorHadset)
      positiveText.setTextColor(getActionTextStateList(builder.positiveColor));

    positiveText.setGravity(Gravity.CENTER);
    positiveText.setEnabled(true);
    if (builder.positiveTextBacground != null) {
      positiveText.setBackgroundDrawable(builder.positiveTextBacground);
    } else {
      positiveText.setBackgroundDrawable(
          ContextHolder.context()
              .getResources()
              .getDrawable(R.drawable.rect_rounded_large_tantan_orange));
    }
    positiveText.setMinHeight(DP_48);
    positiveText.setDuplicateParentStateEnabled(true);
    positiveText.setLayoutParams(
        new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER_VERTICAL));
    positiveButton.setClipChildren(false);
    positiveButton.setClipToPadding(false);
    buttonFrame.setClipChildren(false);
    buttonFrame.setClipToPadding(false);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      positiveText.setStateListAnimator(
          AnimatorInflater.loadStateListAnimator(
              ContextHolder.context(), R.anim.widget_button_sla));
    }
    positiveButton.addView(positiveText);
    positiveButton.setId(R.id.buttonDefaultPositive);
  }

  private void setBigNegativeButton() {
    if (isStacked) {
      LinearLayout.LayoutParams negativeButtonLP =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      negativeButtonLP.bottomMargin = DP_12;
      negativeButton.setLayoutParams(negativeButtonLP);
    } else {
      RelativeLayout.LayoutParams negativeButtonLP =
          new RelativeLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      negativeButtonLP.bottomMargin = DP_12;
      negativeButton.setLayoutParams(negativeButtonLP);
    }
    negativeText.setTextAppearance(getContext(), R.style.common_text_style_body_01_d);

    if (negativebuttonTextColorHadset)
      negativeText.setTextColor(getActionTextStateList(builder.negativeColor));

    negativeText.setSingleLine(true);
    negativeText.setGravity(Gravity.CENTER);
    negativeText.setEnabled(true);
    if (builder.negativeTextBacground != null) {
      negativeText.setBackgroundDrawable(builder.negativeTextBacground);
    } else {
      negativeText.setBackgroundDrawable(
          ContextHolder.context().getResources().getDrawable(R.drawable.tf_rect_rounded_light));
    }
    negativeText.setMinHeight(DP_48);
    negativeText.setDuplicateParentStateEnabled(true);
    negativeText.setLayoutParams(
        new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER_VERTICAL));
    negativeButton.setClipChildren(false);
    negativeButton.setClipToPadding(false);
    buttonFrame.setClipChildren(false);
    buttonFrame.setClipToPadding(false);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      negativeText.setStateListAnimator(
          AnimatorInflater.loadStateListAnimator(
              ContextHolder.context(), R.anim.widget_button_sla));
    }
    negativeButton.addView(negativeText);
    negativeButton.setId(R.id.buttonDefaultNegative);
  }

  /**
   * Invalidates the positive/neutral/negative action buttons. Decides whether they should be
   * visible and sets their properties (such as height, text color, etc.).
   */
  private boolean invalidateActions() {
    if (!hasActionButtons()) {
      invalidateList();
      if (viewInbox.getChildCount() < 2 && itemsFrame != null) {
        //        if (titleFrame == null) {
        //          if (listView.getAdapter().getCount() > 3)
        //            listView.setPadding(0, DP_8, 0, DP_8);
        //        } else {
        //          listView.setPadding(0, 0, 0, DP_8);
        //        }
        listView.setClipToPadding(false);
      }
      return false;
    }

    if (buttonFrame != null && viewInbox.indexOfChild(buttonFrame) != -1) {
      viewInbox.removeView(buttonFrame);
    }

    positiveButton = new FrameLayout(builder.context);
    negativeButton = new FrameLayout(builder.context);
    neutralButton = new FrameLayout(builder.context);
    positiveText =
        builder.bigPositiveButton
            ? new VButton_FakeShadow(builder.context)
            : new VText(builder.context);
    negativeText = new VText(builder.context);
    VText neutralText = new VText(builder.context);

    if (isStacked) {
      buttonFrame = new LinearLayout(builder.context);
      LinearLayout.LayoutParams buttonStackedLP =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      buttonFrame.setLayoutParams(buttonStackedLP);
      ((LinearLayout) buttonFrame).setOrientation(LinearLayout.VERTICAL);

      setStackedButtonStyle(positiveButton, false);
      if (builder.bigPositiveButton) {
        setBigPositiveButton();
      } else {
        setStackedTextStyle(positiveText);
        positiveButton.addView(positiveText);
      }

      setStackedButtonStyle(negativeButton, true);
      if (builder.bigNegativeButton) {
        setBigNegativeButton();
      } else {
        setStackedTextStyle(negativeText);
        negativeButton.addView(negativeText);
      }

      setStackedButtonStyle(neutralButton, false);
      setStackedTextStyle(neutralText);
      neutralButton.addView(neutralText);

      buttonFrame.addView(positiveButton);
      buttonFrame.addView(negativeButton);
      buttonFrame.addView(neutralButton);

      neutralButton.setId(R.id.buttonDefaultNeutral);
      positiveButton.setId(R.id.buttonDefaultPositive);
      negativeButton.setId(R.id.buttonDefaultNegative);
    } else {
      buttonFrame = new RelativeLayout(builder.context);
      LinearLayout.LayoutParams buttonDefaultLP = null;
      if (builder.bigPositiveButton) {
        buttonDefaultLP =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonFrame.setPadding(DP_12, builder.customView == null ? DP_24 : 0, DP_12, DP_12);
      } else {
        buttonDefaultLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DP_64);
      }
      buttonFrame.setLayoutParams(buttonDefaultLP);

      RelativeLayout.LayoutParams neutralButtonLP =
          new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP_32);
      neutralButtonLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
      neutralButtonLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
      neutralButtonLP.setMargins(DP_8, 0, DP_8, DP_8);
      neutralButton.setLayoutParams(neutralButtonLP);
      neutralButton.setMinimumWidth(DP_72);
      setDefaultTextStyle(neutralText);
      neutralText.setLayoutParams(
          new LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT,
              Gravity.CENTER));
      neutralButton.addView(neutralText);
      neutralButton.setId(R.id.buttonDefaultNeutral);

      if (builder.bigNegativeButton) {
        setBigNegativeButton();
      } else {
        RelativeLayout.LayoutParams negativeButtonLP =
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP_32);
        negativeButtonLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        negativeButton.setLayoutParams(negativeButtonLP);
        setDefaultTextStyle(negativeText);
        negativeText.setLayoutParams(
            new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        negativeButton.setMinimumWidth(DP_72);
        negativeButton.addView(negativeText);
        negativeButton.setId(R.id.buttonDefaultNegative);
      }

      if (builder.bigPositiveButton) {
        setBigPositiveButton();
      } else {
        RelativeLayout.LayoutParams positiveButtonLP =
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DP_32);
        positiveButtonLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        positiveButtonLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        positiveButtonLP.setMargins(DP_8, 0, DP_8, DP_8);
        positiveButton.setLayoutParams(positiveButtonLP);
        positiveButton.setMinimumWidth(DP_72);
        setDefaultTextStyle(positiveText);
        positiveText.setLayoutParams(
            new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER));
        positiveButton.addView(positiveText);
        positiveButton.setId(R.id.buttonDefaultPositive);
      }

      buttonFrame.addView(neutralButton);
      buttonFrame.addView(negativeButton);
      buttonFrame.addView(positiveButton);
    }

    if (builder.positiveIcon != null) {
      Vu.addCompoundDrawableLeft(positiveText, builder.positiveIcon);
      positiveText.setCompoundDrawablePadding(DP_6);
    }
    if (builder.positiveText != null) {
      positiveText.setText(builder.positiveText.toString().toUpperCase());
      positiveButton.setTag(POSITIVE);
      positiveButton.setOnClickListener(this);
      if (!builder.bigPositiveButton) {
        positiveText.setTextColor(getActionTextStateList(builder.positiveColor));
        positiveButton.setBackgroundDrawable(getButtonSelector());
      }
    } else {
      positiveButton.setVisibility(View.GONE);
    }

    if (builder.neutralText != null) {
      neutralText.setTextColor(getActionTextStateList(builder.neutralColor));
      neutralButton.setBackgroundDrawable(getButtonSelector());
      neutralText.setText(builder.neutralText.toString().toUpperCase());
      neutralButton.setTag(NEUTRAL);
      neutralButton.setOnClickListener(this);
    } else {
      neutralButton.setVisibility(View.GONE);
    }

    if (builder.negativeText != null) {
      negativeText.setText(builder.negativeText.toString().toUpperCase());
      negativeButton.setTag(NEGATIVE);
      negativeButton.setOnClickListener(this);
      if (!builder.bigNegativeButton) {
        negativeText.setTextColor(getActionTextStateList(builder.negativeColor));
        negativeButton.setBackgroundDrawable(getButtonSelector());
      }

      if (!isStacked && !builder.bigNegativeButton) {
        RelativeLayout.LayoutParams params =
            new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, DP_32);
        if (builder.positiveText != null) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.addRule(RelativeLayout.START_OF, positiveButton.getId());
          } else {
            params.addRule(RelativeLayout.LEFT_OF, positiveButton.getId());
          }
          params.setMargins(DP_8, 0, 0, DP_8);
        } else {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
          } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
          }
          params.setMargins(DP_8, 0, DP_8, DP_8);
        }
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        negativeButton.setLayoutParams(params);
      }
    } else {
      negativeButton.setVisibility(View.GONE);
    }
    if (!isStacked) {
      positiveButton.setPadding(DP_8, 0, DP_8, 0);
      negativeButton.setPadding(DP_8, 0, DP_8, 0);
      neutralButton.setPadding(DP_8, 0, DP_8, 0);
    }
    viewInbox.addView(buttonFrame);
    invalidateList();
    return true;
  }

  private void setStackedButtonStyle(FrameLayout button, boolean topMargain) {
    LinearLayout.LayoutParams buttonLP =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DP_48);
    buttonLP.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
    if (topMargain) {
      buttonLP.setMargins(0, DP_8, 0, 0);
    }
    button.setLayoutParams(buttonLP);
    button.setEnabled(true);
    button.setPadding(DP_12, 0, DP_12, 0);
  }

  private void setStackedTextStyle(TextView textView) {
    setDefaultTextStyle(textView);
    textView.setPadding(DP_8, 0, DP_8, 0);
    LayoutParams textLP =
        new LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            builder.bigPositiveButton ? Gravity.CENTER : Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    textView.setLayoutParams(textLP);
  }

  private void setDefaultTextStyle(TextView textView) {
    textView.setTextSize(14);
    textView.setSingleLine(true);
    textView.setTypeface(TTypeface.typeface(TTypeface.MEDIUM));
    textView.setGravity(Gravity.CENTER);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      textView.setStateListAnimator(null);
    }
    textView.setEnabled(true);
    textView.setBackgroundDrawable(null);
    textView.setMinWidth(DP_42);
    textView.setDuplicateParentStateEnabled(true);
  }

  private void sendSingleChoiceCallback(View v) {
    CharSequence text = null;
    if (builder.selectedIndex >= 0) {
      text = builder.items[builder.selectedIndex];
    }
    builder.listCallbackSingle.onSelection(this, v, builder.selectedIndex, text);
  }

  private void sendMultichoiceCallback() {
    List<CharSequence> selectedTitles = new ArrayList<>();
    for (Integer i : selectedIndicesList) {
      selectedTitles.add(builder.items[i]);
    }
    builder.listCallbackMulti.onSelection(
        this,
        selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]),
        selectedTitles.toArray(new CharSequence[selectedTitles.size()]));
  }

  @Override
  public final void onClick(View v) {
    String tag = (String) v.getTag();
    if (POSITIVE.equals(tag)) {
      if (builder.onPositive != null) builder.onPositive.run();
      if (builder.listCallbackSingle != null) sendSingleChoiceCallback(v);
      if (builder.listCallbackMulti != null) sendMultichoiceCallback();
      if (builder.autoDismiss) dismiss();
    } else if (NEGATIVE.equals(tag)) {
      if (builder.onNegative != null) builder.onNegative.run();
      if (builder.autoDismiss) dismiss();
    } else if (NEUTRAL.equals(tag)) {
      if (builder.onNeutral != null) builder.onNeutral.run();
      if (builder.autoDismiss) dismiss();
    } else {
      if (builder.listCallback != null) {
        if (builder.autoDismiss) dismiss();
        if (tag != null) {
          String[] split = tag.split(":");
          String text = "";
          int index = 0;
          if (split.length == 2) {
            index = Integer.parseInt(split[0]);
            text = split[1];
          } else {
            if (split.length == 1) {
              index = Integer.parseInt(split[0]);
            }
          }
          builder.listCallback.onSelection(this, v, index, text);
        }
      } else if (builder.listCallbackSingle != null) {
        RadioButton cb = (RadioButton) ((LinearLayout) v).getChildAt(1);
        if (!cb.isChecked()) cb.setChecked(true);
        if (builder.autoDismiss && builder.positiveText == null) {
          dismiss();
          sendSingleChoiceCallback(v);
        } else if (alwaysCallSingleChoiceCallback) {
          sendSingleChoiceCallback(v);
        }
      } else if (builder.listCallbackMulti != null) {
        CheckBox cb = (CheckBox) ((LinearLayout) v).getChildAt(0);
        cb.setChecked(!cb.isChecked());
        if (alwaysCallMultiChoiceCallback) {
          sendMultichoiceCallback();
        }
      } else if (builder.autoDismiss) dismiss();
    }
  }

  /** The class used to construct a MaterialDialog. */
  public static class Builder {

    protected final Context context;
    protected CharSequence title;
    protected CharSequence subTitle;
    protected int titleGravity = Gravity.START;
    protected int subTitleGravity = Gravity.START;
    protected int contentGravity = Gravity.START;
    protected int bigRoundedStyleContentGravity = Gravity.CENTER;
    protected int titleColor = -1;
    protected int descriptionColor = -1;
    protected int descriptionTextSize = 12;
    protected int subTitleColor = -1;
    protected int contentColor = -1;
    protected CharSequence content;
    protected CharSequence[] items;
    protected CharSequence positiveText;
    protected Drawable positiveTextBacground; // if need to modify background
    protected Drawable negativeTextBacground; // if need to modify background
    protected CharSequence neutralText;
    protected CharSequence negativeText;
    protected View customView;
    protected int positiveColor;
    protected int negativeColor;
    protected int neutralColor;
    protected Runnable onPositive;
    protected Runnable onNegative;
    protected Runnable onNeutral;
    protected ListCallback listCallback;
    protected ListCallback listCallbackSingle;
    protected ListCallbackMulti listCallbackMulti;
    protected boolean alwaysCallMultiChoiceCallback = false;
    protected boolean alwaysCallSingleChoiceCallback = false;
    protected Theme theme = Theme.LIGHT;
    protected boolean cancelable = true;
    protected float contentLineSpacingMultiplier = 1.3f;
    protected int selectedIndex = -1;
    protected Integer[] selectedIndices = null;
    protected boolean autoDismiss = true;
    protected Drawable icon;
    protected int iconTopMargin = -1;
    protected View iconView;
    protected ListAdapter adapter;
    protected OnDismissListener dismissListener;
    protected OnCancelListener cancelListener;
    protected OnKeyListener keyListener;
    protected OnShowListener showListener;
    protected OnTouchOutsideListener touchOutsideListener;
    protected boolean forceStacking;
    protected boolean wrapCustomViewInScroll;
    protected int dividerColor;
    protected int backgroundColor;
    protected int itemColor;
    protected Drawable selector;
    protected Drawable btnSelector;
    private boolean fastScroll;
    protected int maxChoices = -1;
    protected Runnable maxChoicesAction = null;
    protected float contentTextSize = -1;
    protected boolean bigTopIcon = false;
    protected int topIconWidth = LayoutParams.WRAP_CONTENT;
    protected ImageView.ScaleType topIconScaleType = null;
    protected boolean enableContentMovementMethod = false;

    protected Drawable positiveIcon;
    protected boolean bigRoundedCorner;
    protected int windowAnimations = -1;
    protected boolean
        bigRoundedStyle; // 大圆角风格对话框，等于bigRoundedCorner,bigNegativeButton和bigPositiveButton同时设置
    protected int customTheme = 0;
    protected boolean circularReveal;
    protected Point globalVisibleCenter;
    protected int radius;
    protected int revealColor;
    private int gravity = -1;
    private int frameTopPadding = -1;
    private boolean bigPositiveButton;
    private boolean bigNegativeButton;

    private Dialog builtDialog;

    private boolean customViewAtTop;
    private int constomViewMarginLeft = -1;
    private int constomViewMarginTop = -1;
    private int constomViewMarginRight = -1;
    private int constomViewMarginBottom = -1;

    private int contentBottomMargin = DP_40;

    private CharSequence outBoxedDescription;

    private boolean descriptionClickDismiss;
    private DialogTag dialogTag;

    public Builder(@NonNull Context context) {
      this.context = context;
    }

    public Builder appChoice(String... packageNames) {
      ArrayList<Pair<String, Drawable>> data =
          Cu.map(
              Arrays.<String>asList(packageNames),
              (String name) -> {
                try {
                  ApplicationInfo info =
                      ContextHolder.context().getPackageManager().getApplicationInfo(name, 0);
                  PackageManager pm = ContextHolder.context().getPackageManager();
                  return Cu.pair(
                      pm.getApplicationLabel(info).toString(), pm.getApplicationIcon(info));
                } catch (PackageManager.NameNotFoundException e) {
                  return Cu.pair(name, null);
                }
              });
      return title(R.string.GENERAL_CHOOSE_APP)
          .adapter(
              new AAdapter<Pair<String, Drawable>>() {
                @Override
                public View inflate(ViewGroup parent, int itemViewType) {
                  return LayoutInflater.from(parent.getContext())
                      .inflate(R.layout.md_listitem_package, parent, false);
                }

                @Override
                public void adapt(
                    View convertView, Pair<String, Drawable> item, int itemViewType, int position) {
                  ImageView image = (ImageView) convertView.findViewById(R.id.control);
                  image.setImageDrawable(item.second);
                  TextView text = (TextView) convertView.findViewById(R.id.title);
                  text.setText(item.first);
                }

                @Override
                public List<Pair<String, Drawable>> list() {
                  return data;
                }
              });
    }
    ;

    public Builder title(@StringRes int titleRes) {
      title(this.context.getString(titleRes));
      return this;
    }

    public Builder title(@StringRes int contentRes, Object... formatArgs) {
      title(this.context.getString(contentRes, formatArgs));
      return this;
    }

    public Builder subTitle(@StringRes int subTitleRes) {
      subTitle(this.context.getString(subTitleRes));
      return this;
    }

    public Builder topIconScaleType(ImageView.ScaleType scaleType) {
      topIconScaleType = scaleType;
      return this;
    }

    public Builder topIconWidth(int width) {
      topIconWidth = width;
      return this;
    }

    public Builder bigTopIcon(boolean b) {
      bigTopIcon = b;
      return this;
    }

    public Builder bigHeaderAndGravityCenter() {
      bigTopIcon = true;
      titleGravity = Gravity.CENTER_HORIZONTAL;
      subTitleGravity = Gravity.CENTER_HORIZONTAL;
      return this;
    }

    public Builder title(CharSequence title) {
      this.title = title;
      return this;
    }

    public Builder subTitle(CharSequence subTitle) {
      this.subTitle = subTitle;
      return this;
    }

    public Builder titleGravity(int gravity) {
      this.titleGravity = gravity;
      return this;
    }

    public Builder subTitleGravity(int gravity) {
      this.subTitleGravity = gravity;
      return this;
    }

    public Builder titleColorRes(@ColorRes int colorRes) {
      titleColor(this.context.getResources().getColor(colorRes));
      return this;
    }

    public Builder subTitleColorRes(@ColorRes int colorRes) {
      subTitleColor(this.context.getResources().getColor(colorRes));
      return this;
    }

    public Builder setDialogTag(DialogTag dialogTag) {
      this.dialogTag = dialogTag;
      return this;
    }

    public Builder descriptionColorRes(@ColorRes int colorRes) {
      descriptionColor(this.context.getResources().getColor(colorRes));
      return this;
    }

    public Builder descriptionTextSize(int textSize) {
      this.descriptionTextSize = textSize;
      return this;
    }

    public Builder titleColor(int color) {
      this.titleColor = color;
      return this;
    }

    public Builder descriptionColor(int color) {
      this.descriptionColor = color;
      return this;
    }

    public Builder subTitleColor(int color) {
      this.subTitleColor = color;
      return this;
    }

    public Builder iconTopMargin(int iconTopMargin) {
      this.iconTopMargin = iconTopMargin;
      return this;
    }

    public Builder icon(View icon) {
      this.iconView = icon;
      return this;
    }

    public Builder icon(Drawable icon) {
      this.icon = icon;
      return this;
    }

    public Builder iconRes(@DrawableRes int icon) {
      this.icon = context.getResources().getDrawable(icon);
      return this;
    }

    public Builder iconAttr(@AttrRes int iconAttr) {
      this.icon = resolveDrawable(context, iconAttr);
      return this;
    }

    public Builder contentColor(int color) {
      this.contentColor = color;
      return this;
    }

    public Builder contentColorRes(@ColorRes int colorRes) {
      contentColor(this.context.getResources().getColor(colorRes));
      return this;
    }

    public Builder content(@StringRes int contentRes) {
      content(this.context.getString(contentRes));
      return this;
    }

    public Builder content(CharSequence content) {
      this.content = content;
      return this;
    }

    public Builder contentMovementMethod(boolean enableContentMovementMethod) {
      this.enableContentMovementMethod = enableContentMovementMethod;
      return this;
    }

    public Builder contentTextSize(float size) {
      this.contentTextSize = size;
      return this;
    }

    public Builder content(@StringRes int contentRes, Object... formatArgs) {
      content(this.context.getString(contentRes, formatArgs));
      return this;
    }

    public Builder contentGravity(int gravity) {
      this.contentGravity = gravity;
      return this;
    }

    public Builder bigRoundedStyleContentGravity(int gravity) {
      this.bigRoundedStyleContentGravity = gravity;
      return this;
    }

    public Builder contentLineSpacing(float multiplier) {
      this.contentLineSpacingMultiplier = multiplier;
      return this;
    }

    public Builder titleFrameTopPadding(int padding) {
      this.frameTopPadding = padding;
      return this;
    }

    public Builder items(@ArrayRes int itemsRes) {
      items(this.context.getResources().getTextArray(itemsRes));
      return this;
    }

    public Builder items(CharSequence[] items) {
      this.items = items;
      return this;
    }

    public Builder items(List<String> items) {
      CharSequence[] is = new CharSequence[items.size()];
      for (int i = 0; i < items.size(); i++) {
        is[i] = items.get(i);
      }
      this.items = is;
      return this;
    }

    public Builder itemsCallback(ListCallback callback) {
      this.listCallback = callback;
      this.listCallbackSingle = null;
      this.listCallbackMulti = null;
      return this;
    }

    /**
     * Pass anything below 0 (such as -1) for the selected index to leave all options unselected
     * initially. Otherwise pass the index of an item that will be selected initially.
     *
     * @param selectedIndex The checkbox index that will be selected initially.
     * @param callback The callback that will be called when the presses the positive button.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder itemsCallbackSingleChoice(int selectedIndex, ListCallback callback) {
      this.selectedIndex = selectedIndex;
      this.listCallback = null;
      this.listCallbackSingle = callback;
      this.listCallbackMulti = null;
      return this;
    }

    /**
     * By default, the single choice callback is only called when the user clicks the positive
     * button or if there are no buttons. Call this to force it to always call on item clicks even
     * if the positive button exists.
     *
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder alwaysCallSingleChoiceCallback() {
      this.alwaysCallSingleChoiceCallback = true;
      return this;
    }

    /**
     * Pass null for the selected indices to leave all options unselected initially. Otherwise pass
     * an array of indices that will be selected initially.
     *
     * @param selectedIndices The radio button indices that will be selected initially.
     * @param callback The callback that will be called when the presses the positive button.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder itemsCallbackMultiChoice(Integer[] selectedIndices, ListCallbackMulti callback) {
      this.selectedIndices = selectedIndices;
      this.listCallback = null;
      this.listCallbackSingle = null;
      this.listCallbackMulti = callback;
      return this;
    }

    /**
     * By default, the multi choice callback is only called when the user clicks the positive button
     * or if there are no buttons. Call this to force it to always call on item clicks even if the
     * positive button exists.
     *
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder alwaysCallMultiChoiceCallback() {
      this.alwaysCallMultiChoiceCallback = true;
      return this;
    }

    public Builder positive(@StringRes int postiveRes) {
      return positive(this.context.getString(postiveRes), onPositive);
    }

    public Builder bigPositiveButton() {
      this.bigPositiveButton = true;
      return this;
    }

    public Builder positive(@StringRes int postiveRes, Runnable onPositive) {
      return positive(this.context.getString(postiveRes), onPositive);
    }

    public Builder positive(CharSequence message) {
      return positive(message, onPositive);
    }

    public Builder positive(CharSequence message, Runnable onPositive) {
      this.positiveText = message;
      this.onPositive = onPositive;
      return this;
    }

    public Builder postiveBackground(Drawable positiveTextBacground) {
      this.positiveTextBacground = positiveTextBacground;
      return this;
    }

    public Builder negaiveBackground(Drawable negativeTextBacground) {
      this.negativeTextBacground = negativeTextBacground;
      return this;
    }

    public Builder neutral(@StringRes int neutralRes) {
      return neutral(this.context.getString(neutralRes), onNeutral);
    }

    public Builder neutral(@StringRes int neutralRes, Runnable onNeutral) {
      return neutral(this.context.getString(neutralRes), onNeutral);
    }

    public Builder neutral(CharSequence message) {
      return neutral(message, onNeutral);
    }

    public Builder neutral(CharSequence message, Runnable onNeutral) {
      this.neutralText = message;
      this.onNeutral = onNeutral;
      return this;
    }

    public Builder negative(@StringRes int negativeRes) {
      return negative(this.context.getString(negativeRes), onNegative);
    }

    public Builder negative(@StringRes int negativeRes, Runnable onNegative) {
      return negative(this.context.getString(negativeRes), onNegative);
    }

    public Builder negative(CharSequence message) {
      return negative(message, onNegative);
    }

    public Builder negative(CharSequence message, Runnable onNegative) {
      this.negativeText = message;
      this.onNegative = onNegative;
      return this;
    }

    public Builder customViewAtTop() {
      this.customViewAtTop = true;
      return this;
    }

    public Builder customView(@LayoutRes int layoutRes, boolean wrapInScrollView) {
      LayoutInflater li = LayoutInflater.from(this.context);
      return customView(li.inflate(layoutRes, null), wrapInScrollView);
    }

    public Builder constomViewMargin(int left, int top, int right, int bottom) {
      constomViewMarginLeft = left;
      constomViewMarginTop = top;
      constomViewMarginRight = right;
      constomViewMarginBottom = bottom;
      return this;
    }

    public Builder customView(@LayoutRes int layoutRes) {
      LayoutInflater li = LayoutInflater.from(this.context);
      return customView(li.inflate(layoutRes, null), false);
    }

    /** Use {@link #customView(View, boolean)} instead. */
    @Deprecated
    public Builder customView(View view) {
      return customView(view, true);
    }

    public Builder customView(View view, boolean wrapInScrollView) {
      this.customView = view;
      this.wrapCustomViewInScroll = wrapInScrollView;
      return this;
    }

    /**
     * Convience method for setting the positive, neutral, and negative color all at once.
     *
     * @param colorRes The new color resource to use.
     * @return An instance of the Builder so calls can be chained.
     */
    public Builder accentColorRes(@ColorRes int colorRes) {
      return accentColor(this.context.getResources().getColor(colorRes));
    }

    /**
     * Convience method for setting the positive, neutral, and negative color all at once.
     *
     * @param color The new color to use.
     * @return An instance of the Builder so calls can be chained.
     */
    public Builder accentColor(int color) {
      this.positiveColor = color;
      this.negativeColor = color;
      this.neutralColor = color;
      return this;
    }

    public Builder negativeColor(int color) {
      this.negativeColor = color;
      return this;
    }

    public Builder dividerColorRes(@ColorRes int colorRes) {
      return dividerColor(this.context.getResources().getColor(colorRes));
    }

    public Builder dividerColor(int color) {
      this.dividerColor = color;
      return this;
    }

    public Builder backgroundColorRes(@ColorRes int colorRes) {
      return backgroundColor(this.context.getResources().getColor(colorRes));
    }

    public Builder backgroundColor(int color) {
      this.backgroundColor = color;
      return this;
    }

    public Builder itemColorRes(@ColorRes int colorRes) {
      return itemColor(this.context.getResources().getColor(colorRes));
    }

    public Builder itemColor(int color) {
      this.itemColor = color;
      return this;
    }

    public Builder theme(Theme theme) {
      this.theme = theme;
      return this;
    }

    public Builder cancelable(boolean cancelable) {
      this.cancelable = cancelable;
      return this;
    }

    /**
     * This defaults to true. If set to false, the dialog will not automatically be dismissed when
     * an action button is pressed, and not automatically dismissed when the user selects a list
     * item.
     *
     * @param dismiss Whether or not to dismiss the dialog automatically.
     * @return The Builder instance so you can chain calls to it.
     */
    public Builder autoDismiss(boolean dismiss) {
      this.autoDismiss = dismiss;
      return this;
    }

    /**
     * Sets a custom {@link ListAdapter} for the dialog's list
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public Builder adapter(ListAdapter adapter) {
      this.adapter = adapter;
      return this;
    }

    public Builder showListener(OnShowListener listener) {
      this.showListener = listener;
      return this;
    }

    public Builder dismissListener(OnDismissListener listener) {
      this.dismissListener = listener;
      return this;
    }

    public Builder cancelListener(OnCancelListener listener) {
      this.cancelListener = listener;
      return this;
    }

    public Builder keyListener(OnKeyListener listener) {
      this.keyListener = listener;
      return this;
    }

    public Builder touchOutsideListener(OnTouchOutsideListener listener) {
      this.touchOutsideListener = listener;
      return this;
    }

    public Builder forceStacking(boolean stacked) {
      this.forceStacking = stacked;
      return this;
    }

    public Builder fastScroll(boolean b) {
      this.fastScroll = b;
      return this;
    }

    public Dialog build() {
      builtDialog = new Dialog(this);
      return builtDialog;
    }

    public Dialog getBuiltDialog() {
      return builtDialog;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View view, boolean reveal, final Dialog dialog) {

      if (reveal) {

      } else {
        //				Animator anim =
        //						ViewAnimationUtils.createCircularReveal(view, w / 2, h / 2, maxRadius, 0);
        //
        //				anim.addListener(new AnimatorListenerAdapter() {
        //					@Override
        //					public void onAnimationEnd(Animator animation) {
        //						super.onAnimationEnd(animation);
        //						dialog.dismiss();
        //						view.setVisibility(View.INVISIBLE);
        //
        //					}
        //				});

        //				anim.start();
      }
    }

    public Dialog show() {
      Dialog dialog = build();
      dialog.show();
      return dialog;
    }

    public Dialog showImmediate() {
      Dialog dialog = build();
      dialog.showImmediate();
      return dialog;
    }

    public Dialog showWithPriority(int priority) {
      Dialog dialog = build();
      dialog.showWithPriority(priority);
      return dialog;
    }

    public Builder outBoxedDescription(CharSequence description) {
      this.outBoxedDescription = description;
      if (!TextUtils.isEmpty(outBoxedDescription)) {
        customTheme = R.style.Theme_AppCompat_Light_Dialog_Alert_Roundedbig_Transparent;
      }
      return this;
    }

    public Builder descriptionClickDismiss(boolean descriptionClickDismiss) {
      this.descriptionClickDismiss = descriptionClickDismiss;
      return this;
    }

    public Builder bigRoundedStyle() {
      this.bigRoundedStyle = true;
      this.bigNegativeButton = true;
      this.bigPositiveButton = true;
      this.forceStacking = true;
      this.bigRoundedCorner = true;
      return this;
    }

    public Builder bigStyleContentBottomMargin(int contentBottomMargin) {
      this.contentBottomMargin = contentBottomMargin;
      return this;
    }

    public Builder bigRoundedCorner() {
      this.bigRoundedCorner = true;
      return this;
    }

    public Builder setNoAnimEnterOrExit(int resId) {
      this.windowAnimations = resId;
      return this;
    }

    public Builder customTheme(int theme) {
      this.customTheme = theme;
      return this;
    }

    public Builder gravity(int g) {
      this.gravity = g;
      return this;
    }

    public Builder circularReveal(Point globalVisibleCenter, int radius, int fromColor) {
      this.circularReveal = true;
      this.globalVisibleCenter = globalVisibleCenter;
      this.radius = radius;
      this.revealColor = fromColor;
      return this;
    }

    public Builder maxChoices(int i, Runnable p1) {
      this.maxChoices = i;
      this.maxChoicesAction = p1;
      return this;
    }

    public Builder positiveIcon(Drawable drawable) {
      this.positiveIcon = drawable;
      return this;
    }

    public Builder menu(Menu menu) {
      ArrayList<MenuItem> is = Reflect.on(menu).<ArrayList<MenuItem>>get("mItems");
      items(Cu.map(Cu.range(menu.size()), i -> menu.getItem(i).getTitle().toString()))
          .itemsCallback(
              new ListCallback() {
                @Override
                public void onSelection(
                    Dialog dialog, View itemView, int which, CharSequence text) {
                  Reflect.on(is.get(which))
                      .<MenuItem.OnMenuItemClickListener>get("mClickListener")
                      .onMenuItemClick(is.get(which));
                }
              });
      return this;
    }
  }

  public void setAllChildsTranslationY(ViewGroup v, float t) {
    for (int i = 0; i < v.getChildCount(); i++) {
      v.getChildAt(i).setTranslationY(t);
    }
  }

  public void setAllChildsAlpha(ViewGroup v, float t) {
    for (int i = 0; i < v.getChildCount(); i++) {
      v.getChildAt(i).setAlpha(t);
    }
  }

  public static int ANIM_FACTOR(int i) {
    return (int) (i * 0.9f);
  }

  @Override
  public void show() {
    if (Looper.myLooper() != Looper.getMainLooper())
      throw new IllegalStateException("Dialogs can only be shown from the UI thread.");
    Window window = getWindow();
    window.setWindowAnimations(R.style.PopupOvershootAnimation);
    if (builder.gravity != -1) {
      WindowManager.LayoutParams wlp = window.getAttributes();
      wlp.gravity = builder.gravity;
      window.setAttributes(wlp);
    }

    if (builder.windowAnimations != -1) {
      getWindow().setWindowAnimations(builder.windowAnimations);
    }
    if (Build.VERSION.SDK_INT >= 22) {
      if (builder.circularReveal && builder.bigRoundedCorner) {
        view.setBackgroundResource(
            R.drawable.abc_dialog_material_background_roundedbig_revealinner);
        Drawable foreground =
            getContext()
                .getDrawable(R.drawable.abc_dialog_material_background_roundedbig_revealinner);
        foreground.setColorFilter(new LightingColorFilter(builder.revealColor, 0));
        int initialTranslation = DP_48;
        setAllChildsTranslationY(view, initialTranslation);
        setAllChildsAlpha(view, 1);
        getWindow()
            .setBackgroundDrawableResource(
                R.drawable.abc_dialog_material_background_roundedbig_reveaoutter);
        view.setForeground(foreground);
        // this is hard coded for now
        getWindow().setDimAmount(0.7f);
        getWindow().setWindowAnimations(R.style.DialogAnimationNoEnter);
        setOnShowListener(
            new OnShowListener() {
              @TargetApi(Build.VERSION_CODES.LOLLIPOP)
              @Override
              public void onShow(DialogInterface dialogInterface) {
                getWindow().setDimAmount(0.7f);
                int w = view.getWidth();
                int h = view.getHeight();
                float maxRadius = (float) Math.sqrt(w * w / 4 + h * h / 4);
                Animator revealAnimator =
                    ViewAnimationUtils.createCircularReveal(
                        view, w / 2, h / 2, builder.radius, maxRadius);

                view.setVisibility(View.VISIBLE);
                revealAnimator.addListener(
                    new AnimatorListenerAdapter() {
                      @Override
                      public void onAnimationEnd(Animator animation) {
                        Au.postDelayed(
                            getContext(),
                            () -> {
                              view.setBackgroundDrawable(null);
                              getWindow()
                                  .setBackgroundDrawableResource(
                                      R.drawable.abc_dialog_material_background_roundedbig);
                            },
                            100);
                      }
                    });
                revealAnimator.setDuration(ANIM_FACTOR(250));
                revealAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                ValueAnimator xAnimator, yAnimator = null;
                Point center = builder.globalVisibleCenter;
                if (center == null) {
                  xAnimator = ValueAnimator.ofInt(0, 1);
                  yAnimator = ValueAnimator.ofInt(0, 1);
                } else {
                  int[] outLocation = new int[2];
                  view.getLocationOnScreen(outLocation);
                  int centerX = outLocation[0] + view.getWidth() / 2;
                  int centerY = outLocation[1] + view.getHeight() / 2;
                  xAnimator = ObjectAnimator.ofFloat(view, "translationX", center.x - centerX, 0);
                  yAnimator = ObjectAnimator.ofFloat(view, "translationY", center.y - centerY, 0);
                }
                xAnimator.setDuration(ANIM_FACTOR(250));
                xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                yAnimator.setDuration(ANIM_FACTOR(250));
                yAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                ValueAnimator foregroundAnimator = ValueAnimator.ofInt(255, 0);
                foregroundAnimator.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                      @Override
                      public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setAllChildsAlpha(view, valueAnimator.getAnimatedFraction());
                      }
                    });
                foregroundAnimator.setStartDelay(ANIM_FACTOR(150));
                foregroundAnimator.setDuration(ANIM_FACTOR(100));

                ValueAnimator childsAnimator = ValueAnimator.ofInt(255, 0);
                childsAnimator.addUpdateListener(
                    new ValueAnimator.AnimatorUpdateListener() {
                      @Override
                      public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        setAllChildsTranslationY(
                            view, (1 - valueAnimator.getAnimatedFraction()) * initialTranslation);
                        foreground.setAlpha(
                            Math.max(
                                0, (int) ((1 - 1.3F * valueAnimator.getAnimatedFraction()) * 255)));
                      }
                    });
                childsAnimator.setStartDelay(ANIM_FACTOR(100));
                childsAnimator.setDuration(ANIM_FACTOR(150));
                childsAnimator.setInterpolator(Anu.FAST_IN_SLOW_OUT);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                    revealAnimator,
                    yAnimator,
                    childsAnimator); // add later if we need this ,xAnimator);
                set.start();
              }
            });
      }
    }
    super.show();
  }

  private ColorStateList getActionTextStateList(int newPrimaryColor) {
    int[][] states =
        new int[][] {
          new int[] {-android.R.attr.state_enabled}, // disabled
          new int[] {} // enabled
        };
    int[] colors = new int[] {adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor};
    return new ColorStateList(states, colors);
  }

  /**
   * This will not return buttons that are actually in the layout itself, since the layout doesn't
   * contain buttons. This is only implemented to avoid crashing issues on Huawei devices. Huawei's
   * stock OS requires this method in order to detect visible buttons.
   *
   * @deprecated Use getActionButton(com.afollestad.materialdialogs.DialogAction)} instead.
   */
  @Deprecated
  public Button getButton(int whichButton) {
    if (whichButton == AlertDialog.BUTTON_POSITIVE) {
      return builder.positiveText != null ? new Button(getContext()) : null;
    } else if (whichButton == AlertDialog.BUTTON_NEUTRAL) {
      return builder.neutralText != null ? new Button(getContext()) : null;
    } else {
      return builder.negativeText != null ? new Button(getContext()) : null;
    }
  }

  /**
   * Retrieves the frame view containing the title and icon. You can manually change visibility and
   * retrieve children.
   */
  public final View getTitleFrame() {
    return titleFrame;
  }

  /**
   * Retrieves the custom view that was inflated or set to the MaterialDialog during building.
   *
   * @return The custom view that was passed into the Builder.
   */
  public final View getCustomView() {
    return builder.customView;
  }

  /**
   * Updates an action button's title, causing invalidation to check if the action buttons should be
   * stacked.
   *
   * @param which The action button to update.
   * @param title The new title of the action button.
   */
  public final void setActionButton(Action which, CharSequence title) {
    switch (which) {
      default:
        builder.positiveText = title;
        break;
      case NEUTRAL:
        builder.neutralText = title;
        break;
      case NEGATIVE:
        builder.negativeText = title;
        break;
    }
    invalidateActions();
  }

  /**
   * Updates an action button's title, causing invalidation to check if the action buttons should be
   * stacked.
   *
   * @param which The action button to update.
   * @param titleRes The string resource of the new title of the action button.
   */
  public final void setActionButton(Action which, @StringRes int titleRes) {
    setActionButton(which, getContext().getString(titleRes));
  }

  /**
   * Gets whether or not the positive, neutral, or negative action button is visible.
   *
   * @return Whether or not 1 or more action buttons is visible.
   */
  public final boolean hasActionButtons() {
    return numberOfActionButtons() > 0;
  }

  /**
   * Gets the number of visible action buttons.
   *
   * @return 0 through 3, depending on how many should be or are visible.
   */
  public final int numberOfActionButtons() {
    int number = 0;
    if (builder.positiveText != null) number++;
    if (builder.neutralText != null) number++;
    if (builder.negativeText != null) number++;
    return number;
  }

  /** Updates the dialog's title. */
  public final void setTitle(CharSequence title) {
    this.title.setText(title);
  }

  public void setIcon(int resId) {
    ((ImageView) icon).setImageResource(resId);
    icon.setVisibility(resId != 0 ? View.VISIBLE : View.GONE);
  }

  public void setIcon(Drawable d) {
    ((ImageView) icon).setImageDrawable(d);
    icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
  }

  public void setIconAttribute(int attrId) {
    Drawable d = resolveDrawable(builder.context, attrId);
    ((ImageView) icon).setImageDrawable(d);
    icon.setVisibility(d != null ? View.VISIBLE : View.GONE);
  }

  public final void setContent(CharSequence content) {
    ((TextView) view.findViewById(R.id.content)).setText(content);
    if (builder.contentTextSize > 0) {
      ((TextView) view.findViewById(R.id.content)).setTextSize(builder.contentTextSize);
    }
  }

  public final void setItems(CharSequence[] items) {
    if (builder.adapter == null)
      throw new IllegalStateException(
          "This MaterialDialog instance does not yet have an adapter set to it. You cannot use setItems().");
    if (builder.adapter instanceof MaterialDialogAdapter) {
      builder.adapter =
          new MaterialDialogAdapter(
              builder.context, ListType.getLayoutForType(listType), R.id.title, items);
    } else {
      throw new IllegalStateException(
          "When using a custom adapter, setItems() cannot be used. Set items through the adapter instead.");
    }
    builder.items = items;
    listView.setAdapter(builder.adapter);
  }

  /**
   * Use this to customize any list-specific logic for this dialog (OnItemClickListener,
   * OnLongItemClickListener, etc.)
   *
   * @return The VList instance used by this dialog, or null if not using a list.
   */
  @Nullable
  public VList getVList() {
    return listView;
  }

  /**
   * Convenience method for getting the currently selected index of a single choice list
   *
   * @return Currently selected index of a single choice list, or -1 if not showing a single choice
   *     list
   */
  public int getSelectedIndex() {
    if (builder.listCallbackSingle != null) {
      return builder.selectedIndex;
    } else {
      return -1;
    }
  }

  /**
   * Convenience method for getting the currently selected indices of a multi choice list
   *
   * @return Currently selected index of a multi choice list, or null if not showing a multi choice
   *     list
   */
  @Nullable
  public Integer[] getSelectedIndices() {
    if (builder.listCallbackMulti != null) {
      return selectedIndicesList.toArray(new Integer[selectedIndicesList.size()]);
    } else {
      return null;
    }
  }

  private class MaterialDialogAdapter extends ArrayAdapter<CharSequence> {

    final int itemColor;

    public MaterialDialogAdapter(
        Context context, int resource, int textViewResourceId, CharSequence[] objects) {
      super(context, resource, textViewResourceId, objects);
      itemColor = resolveColor(getContext(), R.attr.md_item_color, defaultItemColor);
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
      final View view = super.getView(index, convertView, parent);
      TextView tv = (TextView) view.findViewById(R.id.title);
      switch (listType) {
        case SINGLE:
          {
            @SuppressLint("CutPasteId")
            RadioButton radio = (RadioButton) view.findViewById(R.id.control);
            radio.setChecked(builder.selectedIndex == index);
            break;
          }
        case MULTI:
          {
            @SuppressLint("CutPasteId")
            CheckBox checkbox = (CheckBox) view.findViewById(R.id.control);

            checkbox.setChecked(selectedIndicesList.contains(index));
            break;
          }
      }
      tv.setText(builder.items[index]);
      tv.setTextColor(itemColor);
      view.setTag(index + ":" + builder.items[index]);

      Drawable d = builder.selector;
      if (d == null) {
        d = resolveDrawable(builder.context, R.attr.md_selector);
        if (d == null) d = resolveDrawable(getContext(), R.attr.md_selector);
      }
      view.setBackgroundDrawable(d);
      return view;
    }
  }

  private static enum ListType {
    REGULAR,
    SINGLE,
    MULTI;

    public static int getLayoutForType(ListType type) {
      switch (type) {
        case REGULAR:
          return R.layout.md_listitem;
        case SINGLE:
          return R.layout.md_listitem_singlechoice;
        case MULTI:
          return R.layout.md_listitem_multichoice;
        default:
          // Shouldn't be possible
          throw new IllegalArgumentException("Not a valid list type");
      }
    }
  }

  public static interface ListCallback {
    void onSelection(Dialog dialog, View itemView, int which, CharSequence text);
  }

  public static interface ListCallbackMulti {
    void onSelection(Dialog dialog, Integer[] which, CharSequence[] text);
  }

  /** utils */
  public static int adjustAlpha(int color, float factor) {
    int alpha = Math.round(Color.alpha(color) * factor);
    int red = Color.red(color);
    int green = Color.green(color);
    int blue = Color.blue(color);
    return Color.argb(alpha, red, green, blue);
  }

  public static int resolveColor(Context context, int attr) {
    return resolveColor(context, attr, 0);
  }

  public static int resolveColor(Context context, int attr, int fallback) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
    try {
      return a.getColor(0, fallback);
    } finally {
      a.recycle();
    }
  }

  public static Drawable resolveDrawable(Context context, int attr) {
    return resolveDrawable(context, attr, null);
  }

  private static Drawable resolveDrawable(Context context, int attr, Drawable fallback) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
    try {
      Drawable d = a.getDrawable(0);
      if (d == null && fallback != null) d = fallback;
      return d;
    } finally {
      a.recycle();
    }
  }

  public interface OnTouchOutsideListener {
    void onTouchOutside(Dialog dialog);
  }

  @Override
  public boolean onTouchEvent(@NonNull MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(getContext(), event)) {
      if (this.builder.touchOutsideListener != null) {
        this.builder.touchOutsideListener.onTouchOutside(this);
        return true;
      }
    }
    return super.onTouchEvent(event);
  }

  private boolean isOutOfBounds(Context context, MotionEvent event) {
    final int x = (int) event.getX(); // 相对弹窗左上角的x坐标
    final int y = (int) event.getY(); // 相对弹窗左上角的y坐标
    final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop(); // 最小识别距离
    final View decorView = getWindow().getDecorView(); // 弹窗的根View
    return (x < -slop)
        || (y < -slop)
        || (x > (decorView.getWidth() + slop))
        || (y > (decorView.getHeight() + slop));
  }

  public abstract static class DialogTag {}
}
