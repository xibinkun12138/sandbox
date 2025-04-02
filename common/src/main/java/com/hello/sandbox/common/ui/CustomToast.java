package com.hello.sandbox.common.ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.hello.sandbox.common.R;

/** 抽取snackBar部分逻辑实现Toast的效果 */
class CustomToast {
  static final Handler handler;
  static final int MSG_SHOW = 0;
  static final int MSG_DISMISS = 1;

  private LinearLayout mView;
  private TextView textView;
  private LinearLayout container;
  private ViewGroup parentView;

  private int duration;
  static final int LENGTH_SHORT = -1;
  private static final int LENGTH_LONG = 0;

  /** Indicates that the Toast was dismissed via a timeout. */
  static final int DISMISS_EVENT_TIMEOUT = 2;
  /** Indicates that the Toast was dismissed via a call to {@link #dismiss()}. */
  private static final int DISMISS_EVENT_MANUAL = 3;
  /** Indicates that the Toast was dismissed from a new Toast being shown. */
  static final int DISMISS_EVENT_CONSECUTIVE = 4;

  public static final int MAX_SHOW_TIME = 3500;

  static {
    handler =
        new Handler(
            Looper.getMainLooper(),
            new Handler.Callback() {
              @Override
              public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                  case MSG_SHOW:
                    ((CustomToast) message.obj).showView();
                    return true;
                  case MSG_DISMISS:
                    ((CustomToast) message.obj).hideView();
                    return true;
                  default:
                    return false;
                }
              }
            });
  }

  @NonNull
  public static CustomToast make(
      @NonNull View view, @NonNull CharSequence text, int duration, @ColorInt Integer bgColor) {
    if (duration > MAX_SHOW_TIME) {
      duration = MAX_SHOW_TIME;
    }

    final ViewGroup parent = findSuitableParent(view);
    if (parent == null) {
      throw new IllegalArgumentException(
          "No suitable parent found from the given view. Please provide a valid view.");
    }

    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final LinearLayout ll =
        (LinearLayout) inflater.inflate(R.layout.common_view_toast, parent, false);
    CustomToast customToast = new CustomToast();
    customToast.mView = ll;
    customToast.parentView = parent;
    customToast.textView = ll.findViewById(R.id.toast_content);
    customToast.container = ll.findViewById(R.id.toast_container);
    customToast.setText(text);
    customToast.setDuration(duration);
    if (bgColor != null) {
      Drawable drawable = customToast.container.getBackground().mutate();
      if (drawable instanceof GradientDrawable) {
        ((GradientDrawable) drawable).setColor(bgColor);
        customToast.container.setBackground(drawable);
      }
    }
    return customToast;
  }

  public void show() {
    ToastManager.getInstance().show(getDuration(), managerCallback);
  }

  public void dismiss() {
    ToastManager.getInstance().dismiss(managerCallback, DISMISS_EVENT_MANUAL);
  }

  @NonNull
  ToastManager.Callback managerCallback =
      new ToastManager.Callback() {
        @Override
        public void show() {
          handler.sendMessage(handler.obtainMessage(MSG_SHOW, CustomToast.this));
        }

        @Override
        public void dismiss(int event) {
          handler.sendMessage(handler.obtainMessage(MSG_DISMISS, event, 0, CustomToast.this));
        }
      };

  public int getDuration() {
    return duration;
  }

  @NonNull
  public CustomToast setDuration(int duration) {
    this.duration = duration;
    return this;
  }

  private void setText(CharSequence text) {
    textView.setText(text);
  }

  private void hideView() {
    if (mView.getVisibility() == View.VISIBLE) {
      Animation animation =
          AnimationUtils.loadAnimation(mView.getContext(), R.anim.common_view_alerts_close);
      animation.setAnimationListener(
          new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
              mView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
          });
      mView.startAnimation(animation);

      ToastManager.getInstance().onDismissed(managerCallback);
    }
  }

  private void showView() {
    parentView.addView(mView);
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mView.getLayoutParams();
    layoutParams.gravity = Gravity.CENTER;
    mView.setLayoutParams(layoutParams);
    mView.setVisibility(View.VISIBLE);
    Animation animation =
        AnimationUtils.loadAnimation(mView.getContext(), R.anim.common_view_alerts_show);
    mView.startAnimation(animation);
    ToastManager.getInstance().onShown(managerCallback);
  }

  @Nullable
  private static ViewGroup findSuitableParent(View view) {
    ViewGroup fallback = null;
    do {
      if (view instanceof FrameLayout) {
        if (view.getId() == android.R.id.content) {
          // If we've hit the decor content view, then we didn't find a CoL in the
          // hierarchy, so use it.
          return (ViewGroup) view;
        } else {
          // It's not the content view but we'll use it as our fallback
          fallback = (ViewGroup) view;
        }
      }

      if (view != null) {
        // Else, we will loop and crawl up the view hierarchy and try to find a parent
        final ViewParent parent = view.getParent();
        view = parent instanceof View ? (View) parent : null;
      }
    } while (view != null);

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback;
  }
}
