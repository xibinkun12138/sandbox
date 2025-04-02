package v.pushbubble;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hello.sandbox.common.Au;
import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.MetricsUtil;
import com.hello.sandbox.common.util.NullChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rx.functions.Action0;
import rx.functions.Func0;
import rx.functions.Func1;
import v.Anu;

public class SimplePushBubble extends SequencePushBubbleInfo {
  private final Builder builder;

  private SimplePushBubble(@NonNull Builder builder) {
    this.builder = builder;
  }

  private SimplePushPopupWindow popupWindow;

  @Override
  public int showBubble() {
    boolean canShow = showCondition == null || showCondition.call();
    Activity act = builder.act;
    if (!canShow || act.isFinishing()) {
      // 条件不满足则不展示气泡，同时调用隐藏回调，便于后续气泡展示
      if (NullChecker.notNull(dismissCallBack)) {
        dismissCallBack.call();
      }
      return 0;
    }

    popupWindow = new SimplePushPopupWindow(builder);
    Runnable autoDismiss = popupWindow::dismiss;
    popupWindow.setOnDismissListener(
        () -> {
          Au.removeCallbacks(autoDismiss);
          if (NullChecker.notNull(dismissCallBack)) {
            dismissCallBack.call();
          }
        });
    popupWindow.show();
    if (NullChecker.notNull(showCallBack)) {
      showCallBack.call();
    }
    if (builder.autoDismiss) {
      Au.postDelayed(builder.act, autoDismiss, builder.duration);
    }
    return builder.duration;
  }

  public void hidePush() {
    if (popupWindow == null) {
      return;
    }
    if (!popupWindow.isShowing()) {
      return;
    }
    popupWindow.dismiss();
  }

  @Override
  public String getBubbleId() {
    return builder.bubbleId;
  }

  @Override
  public String getBubbleGroup() {
    return builder.bubbleGroup;
  }

  @Override
  public int getActId() {
    return builder.act.hashCode();
  }

  private static class PushBubbleFrame extends VFrame {
    private static final int TRIGGER_DISTANCE = MetricsUtil.DP_5;
    private float mDownY;

    private Action0 onExitAction;

    private boolean slideToHide;

    public PushBubbleFrame(Context context) {
      super(context);
    }

    public void setOnExitAction(Action0 onExitAction) {
      this.onExitAction = onExitAction;
    }

    public void setSlideToHide(boolean slideToHide) {
      this.slideToHide = slideToHide;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
      if (!slideToHide) {
        return super.onInterceptTouchEvent(event);
      }

      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          mDownY = event.getRawY();
          break;
        case MotionEvent.ACTION_MOVE:
          float distanceY = mDownY - event.getRawY();
          // 滑动超过10dp，就拦截，自己处理
          if (distanceY > TRIGGER_DISTANCE) {
            return true;
          }
          break;
      }
      return super.onInterceptTouchEvent(event);
    }

    /** 处理上滑自动退出 */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
      if (!slideToHide) {
        return super.onTouchEvent(event);
      }

      if (onExitAction == null) {
        return super.onTouchEvent(event);
      }
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          break;
        case MotionEvent.ACTION_MOVE:
          break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
          float distanceY = mDownY - event.getRawY();
          if (distanceY > TRIGGER_DISTANCE) {
            onExitAction.call();
            onExitAction = null;
            return true;
          }
          break;
      }
      return super.onTouchEvent(event);
    }
  }

  private static class SimplePushPopupWindow extends PopupWindow {
    private final Animator showAnim;
    private final Animator hideAnim;

    private final Builder builder;

    public SimplePushPopupWindow(@NonNull Builder builder) {
      this.builder = builder;
      PushBubbleFrame frame = new PushBubbleFrame(builder.act);
      frame.setClickable(true);
      // padding, minWidth, minHeight 不能随便改，依赖于背景 .9 图
      frame.setPadding(MetricsUtil.DP_20, MetricsUtil.DP_20, MetricsUtil.DP_20, MetricsUtil.DP_20);
      frame.setBackgroundResource(R.drawable.common_view_push_bubble_bg);
      frame.setMinimumWidth(DP_92);
      frame.setMinimumHeight(DP_92);
      frame.setSlideToHide(builder.slideToHide);
      frame.setOnExitAction(
          () ->
              dismiss(
                  builder.slideToHideAnim == null ? null : builder.slideToHideAnim.call(frame)));
      builder.layoutParams.gravity = Gravity.CENTER;
      frame.addView(builder.contentView, builder.layoutParams);
      this.showAnim = builder.showAnim == null ? null : builder.showAnim.call(frame);
      this.hideAnim = builder.hideAnim == null ? null : builder.hideAnim.call(frame);
      if (builder.layoutParams.width == LayoutParams.MATCH_PARENT) {
        setWidth(LayoutParams.MATCH_PARENT);
      } else {
        setWidth(LayoutParams.WRAP_CONTENT);
      }
      setHeight(LayoutParams.WRAP_CONTENT);
      setContentView(frame);
    }

    public void show() {
      if (showAnim != null && showAnim.isRunning()) {
        return;
      }
      Window window = builder.act.getWindow();
      if (window == null
          || window.getDecorView() == null
          || window.getDecorView().getWindowToken() == null) {
        return;
      }
      if (showAnim != null) {
        showAnim.start();
      }
      showAtLocation(window.getDecorView(), Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
    }

    @Override
    public void dismiss() {
      dismiss(hideAnim);
    }

    public void dismiss(@Nullable Animator anim) {
      if (anim == null) {
        super.dismiss();
        return;
      }
      if (anim.isRunning()) {
        return;
      }
      Anu.end(
          anim,
          () -> {
            try {
              SimplePushPopupWindow.super.dismiss();
            } catch (IllegalArgumentException ignored) {
            }
          });
      anim.start();
    }
  }

  /** 不同的 style 只有高度的区别. */
  @IntDef({PushStyle.SMALL, PushStyle.MEDIUM, PushStyle.LARGE})
  @Retention(RetentionPolicy.SOURCE)
  public @interface PushStyle {
    int SMALL = 1;
    int MEDIUM = 2;
    int LARGE = 3;
  }

  private static final int DP_92 = MetricsUtil.dp(92f);

  public static class Builder {
    private static final Func0<String> DEFAULT_BUBBLE_ID = () -> "" + System.currentTimeMillis();
    private static final String DEFAULT_BUBBLE_GROUP = "default";
    private static final int DEFAULT_DURATION = 3000;
    private static final long DEFAULT_ANIMATION_DURATION = 300L;
    private static final Func1<View, Animator> DEFAULT_SHOW_ANIM =
        view -> {
          AnimatorSet animatorSet = new AnimatorSet();
          Animator animator1 = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
          Animator animator2 = ObjectAnimator.ofFloat(view, "translationY", -DP_92, 0f);
          animator2.setInterpolator(new OvershootInterpolator());
          animatorSet.playTogether(animator1, animator2);
          animatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
          return animatorSet;
        };
    private static final Func1<View, Animator> DEFAULT_HIDE_ANIM =
        view -> {
          AnimatorSet animatorSet = new AnimatorSet();
          Animator animator1 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
          animatorSet.playTogether(animator1);
          animatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
          return animatorSet;
        };
    private static final Func1<View, Animator> DEFAULT_SLIDE_TO_HIDE_ANIM =
        view -> {
          AnimatorSet animatorSet = new AnimatorSet();
          Animator animator1 = ObjectAnimator.ofFloat(view, "translationY", 0f, -DP_92);
          animatorSet.playTogether(animator1);
          animatorSet.setDuration(DEFAULT_ANIMATION_DURATION);
          return animatorSet;
        };
    private static final FrameLayout.LayoutParams DEFAULT_LAYOUT_PARAMS =
        new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    private final Activity act;
    private final View contentView;

    private String bubbleId = DEFAULT_BUBBLE_ID.call();
    private String bubbleGroup = DEFAULT_BUBBLE_GROUP;
    private int duration = DEFAULT_DURATION;
    private Func1<View, Animator> showAnim = DEFAULT_SHOW_ANIM;
    private Func1<View, Animator> hideAnim = DEFAULT_HIDE_ANIM;
    private Func1<View, Animator> slideToHideAnim = DEFAULT_SLIDE_TO_HIDE_ANIM;
    private FrameLayout.LayoutParams layoutParams = DEFAULT_LAYOUT_PARAMS;
    private boolean slideToHide = true;
    private boolean autoDismiss = true;

    public Builder(@NonNull Activity act, @NonNull View contentView) {
      this.act = act;
      this.contentView = contentView;
    }

    public Builder setBubbleId(@NonNull String bubbleId) {
      this.bubbleId = bubbleId;
      return this;
    }

    public Builder setBubbleGroup(@NonNull String bubbleGroup) {
      this.bubbleGroup = bubbleGroup;
      return this;
    }

    public Builder setDuration(int duration) {
      this.duration = duration;
      return this;
    }

    public Builder setShowAnim(@Nullable Func1<View, Animator> showAnim) {
      this.showAnim = showAnim;
      return this;
    }

    public Builder setHideAnim(@Nullable Func1<View, Animator> hideAnim) {
      this.hideAnim = hideAnim;
      return this;
    }

    public Builder setSlideToHideAnim(@Nullable Func1<View, Animator> slideToHideAnim) {
      this.slideToHideAnim = slideToHideAnim;
      return this;
    }

    public Builder setAutoDismiss(boolean autoDismiss) {
      this.autoDismiss = autoDismiss;
      return this;
    }

    public Builder setLayoutParams(@NonNull FrameLayout.LayoutParams layoutParams) {
      this.layoutParams = layoutParams;
      return this;
    }

    public Builder setPushStyle(@PushStyle int pushStyle) {
      switch (pushStyle) {
        case PushStyle.SMALL:
          this.layoutParams =
              new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, MetricsUtil.dp(52f));
          break;
        case PushStyle.MEDIUM:
          this.layoutParams =
              new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, MetricsUtil.dp(72f));
          break;
        case PushStyle.LARGE:
          this.layoutParams =
              new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, MetricsUtil.dp(96f));
          break;
        default:
          this.layoutParams = DEFAULT_LAYOUT_PARAMS;
          break;
      }
      return this;
    }

    public Builder setSlideToHide(boolean slideToHide) {
      this.slideToHide = slideToHide;
      return this;
    }

    public SimplePushBubble build() {
      return new SimplePushBubble(this);
    }
  }
}
