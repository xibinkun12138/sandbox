package com.hello.sandbox.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.hello.sandbox.Constant;
import com.hello.sandbox.common.util.Vu;
import com.hello.sandbox.ui.home.HomeBannerInfo;
import com.hello.sandbox.ui.home.ViewPager2Adapter;
import com.hello.sandbox.view.pager2banner.Banner;
import com.hello.sandbox.view.pager2banner.IndicatorView;
import com.hello.sandbox.view.pager2banner.IndicatorView.IndicatorStyle;
import com.zhpan.indicator.utils.IndicatorUtils;
import java.util.ArrayList;
import top.niunaijun.blackboxa.R;

public class FakeHomeView extends FrameLayout {

  private Banner banner;

  public FakeHomeView(@NonNull Context context) {
    super(context);
    initView(context);
  }

  public FakeHomeView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public FakeHomeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context);
  }

  public FakeHomeView(
      @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView(context);
  }

  public void initView(Context context) {
    LayoutInflater.from(context).inflate(R.layout.fake_home_view, this, true);
    setContent(context);
  }

  private void setContent(Context context) {
    banner = findViewById(R.id.fake_home_banner);
    banner.post(
        () -> {
          banner.getLayoutParams().height = (int) (Vu.screenWidth() / 2.5);
          banner.requestLayout();
        });
    banner.setIndicator(
        new IndicatorView(context)
            .setIndicatorColor(ContextCompat.getColor(context, R.color.indicator_normal))
            .setIndicatorSpacing(IndicatorUtils.dp2px(4f))
            .setIndicatorSelectorColor(ContextCompat.getColor(context, R.color.indicator_selected))
            .setIndicatorStyle(IndicatorStyle.INDICATOR_CIRCLE));

    banner.setAdapter(new ViewPager2Adapter(getData()));

    ((ImageView) findViewById(R.id.icon))
        .setImageDrawable(getResources().getDrawable(R.drawable.home_icon_add));
    ((TextView) findViewById(R.id.name)).setText("添加应用");
    findViewById(R.id.cornerLabel).setVisibility(View.INVISIBLE);
  }

  private ArrayList<HomeBannerInfo> getData() {
    ArrayList<HomeBannerInfo> list = new ArrayList<>();
    HomeBannerInfo info1 =
        new HomeBannerInfo("", getResources().getDrawable(R.drawable.home_banner_1));
    HomeBannerInfo info2 =
        new HomeBannerInfo(
            Constant.RECOMMEND_MOMO_APP_URL, getResources().getDrawable(R.drawable.home_banner_2));
    HomeBannerInfo info3 =
        new HomeBannerInfo(
            Constant.RECOMMEND_TANTAN_APP_URL,
            getResources().getDrawable(R.drawable.home_banner_3));
    list.add(info1);
    list.add(info2);
    list.add(info3);
    return list;
  }
}
