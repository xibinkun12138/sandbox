package com.hello.sandbox.common.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils;
import com.hello.sandbox.common.util.CrashHelper;
import com.hello.sandbox.common.util.PackageUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/** Created by Wencharm on 06/04/2017. */
public class SettingCompatUtil {
  public static String TAG = "SETTING_COMPAT";
  public static int REQUEST_SETTING = 0x111;

  public static void jumpPermissionsSetting(Activity context, String title) {
    if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      if (jumpPermissionsSettingForRom(context, title)) {
        return;
      }
    }
    if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      intent.setData(Uri.parse("package:" + context.getPackageName()));
      context.startActivityForResult(intent, REQUEST_SETTING);
      return;
    }
    context.startActivityForResult(
        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_SETTING);
  }

  private static boolean jumpPermissionsSettingForRom(Activity context, String title) {
    // 小米
    if (isMiui()) {
      return managePermissionsForXiaoMi(context);
    }
    // 华为
    if (isEmui()) {
      return managePermissionsForHuawei(context);
    }
    // 魅族
    if (isFlyme()) {
      return managePermissionsForDiffPhone(context, "com.meizu.safe");
    }
    // OPPO
    if (isOppo()) {
      return managePermissionsForOppo(context);
    }
    // VIVO
    if (isVivo() && VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return managePermissionsForDiffPhone(context, "com.iqoo.secure");
    }

    if (isJinli()) {
      return managePermissionsForJinli(context, title);
    }

    return false;
  }

  /**
   * 判断 Intent 是否有效
   *
   * @param context
   * @param intent
   * @return
   */
  public static boolean isIntentAvailable(Context context, Intent intent) {
    if (context == null || intent == null) return false;
    return PackageUtil.queryIntentActivities(context, intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size()
        > 0;
  }

  private static boolean startSafely(Activity context, Intent intent) {
    if (PackageUtil.queryIntentActivities(context, intent, PackageManager.MATCH_DEFAULT_ONLY).size()
        > 0) {
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivityForResult(intent, REQUEST_SETTING);
      return true;
    } else {
      return false;
    }
  }

  private static boolean managePermissionsForDiffPhone(Activity context, String phoneTag) {
    Intent intent = PackageUtil.getLaunchIntentForPackage(context, phoneTag);
    if (intent != null && startSafely(context, intent)) return true;
    return false;
  }
  // OPPO
  private static boolean managePermissionsForOppo(Activity context) {
    // OPPO A53|5.1.1|2.1
    Intent appIntent = PackageUtil.getLaunchIntentForPackage(context, "com.oppo.safe");
    if (appIntent != null && startSafely(context, appIntent)) {
      return true;
    }
    // OPPO R7s|4.4.4|2.1
    appIntent = PackageUtil.getLaunchIntentForPackage(context, "com.color.safecenter");
    if (appIntent != null && startSafely(context, appIntent)) {
      return true;
    }
    appIntent = PackageUtil.getLaunchIntentForPackage(context, "com.coloros.safecenter");
    if (appIntent != null && startSafely(context, appIntent)) {
      return true;
    }
    return false;
  }

  private static boolean managePermissionsForJinli(Activity context, String title) {
    Intent intent = new Intent();
    intent.setComponent(
        new ComponentName(
            "com.android.settings", "com.android.settings.permission.PermissionAppDetail"));
    intent.putExtra("packagename", context.getPackageName());
    intent.putExtra("title", title);
    return startSafely(context, intent);
  }

  private static boolean managePermissionsForHuawei(Activity context) {
    Intent intent = new Intent();
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    ComponentName comp =
        new ComponentName(
            "com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
    intent.setComponent(comp);
    return startSafely(context, intent);
  }

  // XIAOMI
  private static boolean managePermissionsForXiaoMi(Activity context) {
    Intent intent = new Intent();
    intent.setAction("miui.intent.action.APP_PERM_EDITOR");
    intent.putExtra("extra_pkgname", context.getPackageName());
    return startSafely(
            context,
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"))
        || startSafely(
            context,
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity"));
  }

  public static final String ROM_MIUI = "MIUI";
  public static final String ROM_EMUI = "EMUI";
  public static final String ROM_FLYME = "FLYME";
  public static final String ROM_OPPO = "OPPO";
  public static final String ROM_VIVO = "VIVO";
  public static final String ROM_QIKU = "QIKU";
  public static final String ROM_360 = "360";
  public static final String ROM_SMARTISAN = "SMARTISAN";

  public static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
  private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
  private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
  private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

  public static boolean isEmui() {
    return check(ROM_EMUI);
  }

  public static boolean isMiui() {
    return check(ROM_MIUI);
  }

  public static boolean isVivo() {
    return check(ROM_VIVO);
  }

  public static boolean isOppo() {
    return check(ROM_OPPO);
  }

  public static boolean isSmartisan() {
    return check(ROM_SMARTISAN);
  }

  /** 是否 360 系统 */
  public static boolean isQihoo() {
    return check(ROM_QIKU) || check(ROM_360);
  }

  public static boolean isFlyme() {
    return check(ROM_FLYME);
  }

  public static boolean isQiKu() {
    return Build.MANUFACTURER.contains("QiKU") || Build.MANUFACTURER.contains("360");
  }

  public static boolean isOnePlus() {
    return "oneplus".equalsIgnoreCase(Build.BRAND);
  }

  private static boolean isJinli() {
    return "GiONEE".equalsIgnoreCase(Build.BRAND);
  }

  private static String sName;

  public static String getName() {
    if (sName == null) {
      check("");
    }
    return sName;
  }

  private static String sVersion;

  public static String getVersion() {
    if (sVersion == null) {
      check("");
    }
    return sVersion;
  }

  public static boolean check(String rom) {
    if (sName != null) {
      return sName.equals(rom);
    }

    if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
      sName = ROM_MIUI;
    } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
      sName = ROM_EMUI;
    } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
      sName = ROM_OPPO;
    } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
      sName = ROM_VIVO;
    } else {
      sVersion = Build.DISPLAY;
      if (sVersion.toUpperCase().contains(ROM_FLYME)) {
        sName = ROM_FLYME;
      } else {
        sVersion = Build.UNKNOWN;
        sName = Build.MANUFACTURER.toUpperCase();
      }
    }
    return sName.equals(rom);
  }

  public static String getProp(String name) {
    String line = null;
    BufferedReader input = null;
    try {
      Process p = Runtime.getRuntime().exec("getprop " + name);
      input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
      line = input.readLine();
      input.close();
    } catch (IOException e) {
      // samsung android10 Cannot run program "getprop"
      if (!("samsung".equalsIgnoreCase(Build.BRAND) && VERSION.SDK_INT >= 29)) {
        CrashHelper.reportError(e, 50);
      }
      return null;
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
        }
      }
    }
    return line;
  }

  /** 获取 emui 版本号 */
  public static double getEmuiVersion() {
    try {
      String emuiVersion = getProp("ro.build.version.emui");
      String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
      return Double.parseDouble(version);
    } catch (Exception e) {
    }
    return 4.0;
  }

  public static boolean isChinese() {
    Locale locale = Locale.getDefault();
    boolean isZh = locale != null && "zh".equals(locale.getLanguage());
    return isZh && ("CN".equals(locale.getCountry()));
  }
}
