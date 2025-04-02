package v;

import android.animation.Animator;
import android.view.animation.Interpolator;

public class Anu {

  public static final String TAG = "Anim";

  public static final Interpolator FAST_IN_SLOW_OUT = new FastInSlowOutInterpolator();

  public static Animator end(Animator a, final Runnable listener) {
    a.addListener(getAnimatorListener(null, listener, null));
    return a;
  }

  public static Animator.AnimatorListener getAnimatorListener(
      Runnable onStart, Runnable onEnd, Runnable onCancel) {
    return getAnimatorListener(onStart, onEnd, onCancel, null);
  }

  public static Animator.AnimatorListener getAnimatorListener(
      Runnable onStart, Runnable onEnd, Runnable onCancel, Runnable onRepeat) {
    return new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {
        if (onStart != null) {
          onStart.run();
        }
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        if (onEnd != null) {
          onEnd.run();
        }
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        if (onCancel != null) {
          onCancel.run();
        }
      }

      @Override
      public void onAnimationRepeat(Animator animation) {
        if (onRepeat != null) {
          onRepeat.run();
        }
      }
    };
  }
}
