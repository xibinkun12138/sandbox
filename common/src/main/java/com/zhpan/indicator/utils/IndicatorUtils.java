package com.zhpan.indicator.utils;

import android.content.Context;
import com.hello.sandbox.common.util.ContextHolder;
import com.zhpan.indicator.option.IndicatorOptions;

public class IndicatorUtils {
  public static int dp2px(Context context, float dpValue) {
    return (int) (context.getResources().getDisplayMetrics().density * dpValue + 0.5f);
  }

  public static int dp2px(float dpValue) {
    return (int)
        (ContextHolder.context().getResources().getDisplayMetrics().density * dpValue + 0.5f);
  }

  public static float getCoordinateY(float maxDiameter) {
    return maxDiameter / 2;
  }

  public static float getCoordinateX(
      IndicatorOptions indicatorOptions, float maxDiameter, int index) {
    float normalIndicatorWidth = indicatorOptions.getNormalSliderWidth();
    return maxDiameter / 2 + (normalIndicatorWidth + indicatorOptions.getSliderGap()) * index;
  }
}
