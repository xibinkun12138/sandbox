package v;

import android.content.Context;
import android.util.AttributeSet;
import com.hello.sandbox.common.util.MeasureUtil;
import com.hello.sandbox.common.util.MetricsUtil;

/** Created by molikto on 04/23/15. */
public class VList_ScrollableHeight extends VList {
  public VList_ScrollableHeight(Context context) {
    super(context);
  }

  public VList_ScrollableHeight(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public VList_ScrollableHeight(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int wms, int hms) {
    if (getAdapter() != null && getAdapter().getCount() > 0) {
      int h = MeasureUtil.size(hms) - this.getPaddingBottom() - this.getPaddingTop();
      // because we never know
      int num = h / MetricsUtil.dp(48);
      if (num >= getAdapter().getCount()) {
        super.onMeasure(wms, hms);
      } else {
        int a = h % MetricsUtil.dp(48);
        if (num < getAdapter().getCount() - 1 && a >= MetricsUtil.dp(36)) {
          // 最后一个的item只显示一部分，提示用户可以滑动的
          h = h - MetricsUtil.dp(30);
        }
        super.onMeasure(
            wms,
            MeasureSpec.makeMeasureSpec(
                h + this.getPaddingBottom() + this.getPaddingTop(), MeasureSpec.getMode(hms)));
      }
    } else {
      super.onMeasure(wms, hms);
    }
  }
}
