package com.hello.sandbox.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import com.hello.sandbox.common.util.MarketUtil;
import java.util.ArrayList;
import java.util.List;
import top.niunaijun.blackboxa.R;

/** Created by san on 10/01/2018. */
public final class MarketHelper extends MarketUtil {
  private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

  public static boolean shouldOpenOtherAppMarket() {
    // only none-google channel with none-googlePlay installed is true
    return MarketUtil.shouldOpenOtherAppMarket();
  }

  public static Intent getGoToAppMargetIntent(Context context, String packageName) {
    // none-google channel should not open Google Play.
    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
    marketIntent.setData(Uri.parse("market://details?id=" + packageName));

    final List<ResolveInfo> resInfo =
        context.getPackageManager().queryIntentActivities(marketIntent, 0);
    List<Intent> targetedShareIntents = new ArrayList<Intent>();
    for (ResolveInfo info : resInfo) {
      if (GOOGLE_PLAY_PACKAGE_NAME.equals(info.activityInfo.applicationInfo.packageName)) {
        continue;
      }
      Intent targeted = new Intent(Intent.ACTION_VIEW);
      targeted.setData(Uri.parse("market://details?id=" + packageName));
      ActivityInfo activityInfo = info.activityInfo;
      targeted.setPackage(activityInfo.packageName);
      targeted.setClassName(activityInfo.packageName, activityInfo.name);
      targetedShareIntents.add(targeted);
    }
    if (targetedShareIntents.isEmpty()) {
      return null;
    }
    if (targetedShareIntents.size() > 1) {
      // 如果有多个商店，让用户选择
      Intent chooserIntent =
          Intent.createChooser(
              targetedShareIntents.remove(0), context.getString(R.string.UPDATE_GO_TO_MARKET));
      if (chooserIntent == null) {
        return null;
      }
      chooserIntent.putExtra(
          Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[0]));
      return chooserIntent;
    } else {
      // 如果只有一个符合条件的商店，直接跳转
      return targetedShareIntents.get(0);
    }
  }

  public static void goToAppMarket(Activity act, String url) {
    String packageName = getPackageNameFromUrl(url);
    Intent intent = getGoToAppMargetIntent(act, packageName);
    if (intent != null) {
      act.startActivity(intent);
    } else {
      intent = new Intent();
      intent.setAction(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      if (intent.resolveActivity(act.getPackageManager()) != null) {
        act.startActivity(Intent.createChooser(intent, "请选择浏览器"));
      }
    }
  }

  private static String getPackageNameFromUrl(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }
}
