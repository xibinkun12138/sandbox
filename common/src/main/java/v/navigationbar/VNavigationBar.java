package v.navigationbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.MetricsUtil;
import com.hello.sandbox.common.util.Cu;
import java.util.Arrays;
import java.util.List;
import v.VFrame;
import v.VIcon;
import v.VLinear;
import v.VText;

public class VNavigationBar extends VFrame {
  private final VLinear linear;
  private final VFrame leftIconContainer;
  private final VFrame titleContainer;
  private final VLinear rightIconContainer;

  public VNavigationBar(Context context) {
    this(context, null);
  }

  public VNavigationBar(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VNavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    inflate(context, R.layout.common_view_navigation_bar, this);
    setBackgroundColor(getResources().getColor(R.color.app_theme_color));

    linear = findViewById(R.id.linear);
    leftIconContainer = findViewById(R.id.left_icon_container);
    titleContainer = findViewById(R.id.title_container);
    rightIconContainer = findViewById(R.id.right_icon_container);

    if (attrs != null) {
      TypedArray typedArray =
          context.obtainStyledAttributes(attrs, R.styleable.VNavigationBar, defStyleAttr, 0);
      Drawable leftIcon = typedArray.getDrawable(R.styleable.VNavigationBar_leftIcon);
      CharSequence title = typedArray.getText(R.styleable.VNavigationBar_title);
      typedArray.recycle();

      setLeftIconDrawable(leftIcon);
      setTitle(title);
    } else {
      setLeftIconDrawable(null);
      setTitle(null);
    }
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    if (getChildCount() > 2) {
      // 因为自身 inflate R.layout.common_view_navigation_bar 有一个子 view, 所以在使用时只能添加一个子 view 但是
      // getChildCount 为 2
      throw new RuntimeException("布局中最多只能有一个子 View");
    }
    if (getChildCount() == 2) {
      View view = getChildAt(1);
      removeView(view);
      setTitleView(view);
    }
  }

  public VFrame getLeftIconContainer() {
    return leftIconContainer;
  }

  public void setLeftIconResource(@DrawableRes int drawableId) {
    if (drawableId == 0) {
      setLeftIconDrawable(null);
    } else {
      setLeftIconDrawable(getContext().getDrawable(drawableId));
    }
  }

  public void setLeftIconDrawable(@Nullable Drawable drawable) {
    if (drawable == null) {
      setLeftIconView(null);
    } else {
      VIcon icon = new VIcon(getContext());
      icon.setIconStyle(VIcon.IconStyle.SMALL);
      icon.setImageDrawable(drawable);
      setLeftIconView(icon);
    }
  }

  public void setLeftIconView(@Nullable View view) {
    leftIconContainer.removeAllViews();
    if (view == null) {
      leftIconContainer.setVisibility(GONE);
    } else {
      leftIconContainer.setVisibility(VISIBLE);
      leftIconContainer.addView(view);
    }
  }

  public void setLeftIconOnClick(@Nullable OnClickListener onClick) {
    leftIconContainer.setOnClickListener(onClick);
  }

  public void setLeftIconAsBack(@NonNull Activity activity) {
    setLeftIconResource(R.drawable.common_navigation_back);
    setLeftIconOnClick(v -> activity.finish());
  }

  public VFrame getTitleContainer() {
    return titleContainer;
  }

  public void setTitle(@StringRes int stringId) {
    if (stringId == 0) {
      setTitle(null);
    } else {
      setTitle(getContext().getText(stringId));
    }
  }

  public void setTitle(@Nullable CharSequence text) {
    if (text == null) {
      setTitleView(null);
    } else {
      VText title = new VText(getContext());
      title.setText(text);
      title.setTextSize(21f);
      title.setTextColor(getResources().getColor(R.color.app_title_color));
      title.setTypeface(null, Typeface.BOLD);
      title.setMaxLines(1);
      title.setEllipsize(TextUtils.TruncateAt.END);
      title.setPadding(0, MetricsUtil.DP_2, 0, 0); // 视觉上标题偏高，手动加 paddingTop 让视觉垂直居中
      setTitleView(title);
    }
  }

  public void setTitleView(@Nullable View view) {
    titleContainer.removeAllViews();
    if (view == null) {
      titleContainer.setVisibility(INVISIBLE);
    } else {
      titleContainer.setVisibility(VISIBLE);
      titleContainer.addView(view);
    }
  }

  public VLinear getRightIconContainer() {
    return rightIconContainer;
  }

  public void setRightIconViews(@NonNull View... views) {
    internalSetRightIconViews(rightIconContainer, Arrays.asList(views), false);
  }

  public void addRightIconResource(@DrawableRes int drawableId, @Nullable OnClickListener onClick) {
    addRightIconDrawable(getContext().getDrawable(drawableId), onClick);
  }

  public void addRightIconDrawable(@NonNull Drawable drawable, @Nullable OnClickListener onClick) {
    VIcon icon = new VIcon(getContext());
    icon.setIconStyle(VIcon.IconStyle.SMALL);
    icon.setImageDrawable(drawable);
    icon.setOnClickListener(onClick);
    addRightIconViews(icon);
  }

  public void addRightIconViews(@NonNull View... views) {
    internalSetRightIconViews(rightIconContainer, Arrays.asList(views), true);
  }

  public void clearRightIconViews() {
    internalSetRightIconViews(rightIconContainer, null, false);
  }

  private void internalSetRightIconViews(
      @NonNull ViewGroup viewGroup, @Nullable List<View> views, boolean add) {
    if (!add) {
      viewGroup.removeAllViews();
    }
    List<View> validViews = Cu.filter(views, view -> view != null);
    if (!Cu.isEmpty(validViews)) {
      for (View view : validViews) {
        MarginLayoutParams params =
            new MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (viewGroup.getChildCount() > 0) {
          params.leftMargin = MetricsUtil.DP_8;
        }
        viewGroup.addView(view, params);
      }
    }
    if (viewGroup.getChildCount() > 0) {
      viewGroup.setVisibility(VISIBLE);
    } else {
      viewGroup.setVisibility(GONE);
    }
  }

  public void setRightIconClip(boolean clip) {
    linear.setClipToPadding(clip);
    linear.setClipChildren(clip);
    rightIconContainer.setClipToPadding(clip);
    rightIconContainer.setClipChildren(clip);
  }
}
