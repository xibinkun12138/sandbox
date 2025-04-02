package v;

import android.view.animation.Interpolator;

public class FastInSlowOutInterpolator implements Interpolator {

  @Override
  public float getInterpolation(float time) {
    float timeQuadratic = time * time;
    float timeCubic = timeQuadratic * time;
    return (-1.7f * timeCubic * timeQuadratic)
        + (8.1f * timeQuadratic * timeQuadratic)
        + (-13.1f * timeCubic)
        + (7.7f * timeQuadratic);
  }
}
