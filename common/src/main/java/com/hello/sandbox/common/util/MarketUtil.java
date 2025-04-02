package com.hello.sandbox.common.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import java.util.List;

/** Created by san on 10/01/2018. */
public class MarketUtil {
  private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";

  public static boolean shouldOpenGooglePlay() {
    // only google channel with googlePlay installed is true
    final String packageName = ContextHolder.context().getPackageName();
    final Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    final List<ResolveInfo> apps =
        ContextHolder.context().getPackageManager().queryIntentActivities(intent, 0);
    for (ResolveInfo info : apps) {
      if (GOOGLE_PLAY_PACKAGE_NAME.equals(info.activityInfo.applicationInfo.packageName)) {
        return true;
      }
    }
    return false;
  }

  public static boolean shouldOpenOtherAppMarket() {
    // only none-google channel with none-googlePlay installed is true
    final String packageName = ContextHolder.context().getPackageName();
    final Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    final List<ResolveInfo> apps =
        ContextHolder.context().getPackageManager().queryIntentActivities(intent, 0);
    for (ResolveInfo info : apps) {
      if (!GOOGLE_PLAY_PACKAGE_NAME.equals(info.activityInfo.applicationInfo.packageName)) {
        return true;
      }
    }
    return false;
  }

  public static Intent getOpenGooglePlayIntent(Context context) {
    final String packageName = context.getPackageName();
    final Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
    final List<ResolveInfo> apps = PackageUtil.queryIntentActivities(context, intent, 0);
    for (ResolveInfo info : apps) {
      if (GOOGLE_PLAY_PACKAGE_NAME.equals(info.activityInfo.applicationInfo.packageName)) {
        ActivityInfo activityInfo = info.activityInfo;
        ComponentName componentName =
            new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setComponent(componentName);
        return intent;
      }
    }

    Intent webIntent =
        new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
    return webIntent;
  }

  public static void openGooglePlay(Activity act) {
    Intent intent = getOpenGooglePlayIntent(act);
    if (intent != null) {
      act.startActivity(intent);
    }
  }
}
