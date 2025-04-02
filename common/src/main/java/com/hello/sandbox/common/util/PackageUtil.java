package com.hello.sandbox.common.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Pair;
import androidx.annotation.NonNull;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Created by kingty on 12/09/2017. */
public class PackageUtil {

  /**
   * Get current process name
   *
   * @return
   */
  public static String currentProcessName() {

    BufferedReader cmdlineReader = null;
    try {
      cmdlineReader =
          new BufferedReader(new FileReader("/proc/" + android.os.Process.myPid() + "/cmdline"));
      int c;
      StringBuilder processName = new StringBuilder();
      while ((c = cmdlineReader.read()) > 0) {
        processName.append((char) c);
      }
      return processName.toString();
    } catch (FileNotFoundException e) {
      // continue to next method below
    } catch (IOException e) {
    } finally {
      try {
        if (cmdlineReader != null) {
          cmdlineReader.close();
        }
      } catch (IOException e) {
        com.hello.sandbox.common.util.CrashHelper.reportError(e);
      }
    }

    int pid = android.os.Process.myPid();
    ActivityManager activityManager =
        (ActivityManager) ContextHolder.context().getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcess :
        activityManager.getRunningAppProcesses()) {
      if (appProcess.pid == pid) {
        return appProcess.processName;
      }
    }
    return null;
  }

  /**
   * Get all installed app info
   *
   * @param context
   * @param flag
   * @return
   */
  public static List<PackageInfo> getAllPackageInfo(@NonNull Context context, int flag) {
    return context.getPackageManager().getInstalledPackages(flag);
  }

  /**
   * Get all app (filter non-system and system)
   *
   * @param context
   * @return
   */
  public static Pair<List<PackageInfo>, List<PackageInfo>> getPackageInfo(
      @NonNull Context context) {
    ArrayList<PackageInfo> appList1 = new ArrayList<>();
    ArrayList<PackageInfo> appList2 = new ArrayList<>();
    PackageManager pm = context.getPackageManager();
    // Return a List of all packages that are installed on the device.
    List<PackageInfo> packages = getAllPackageInfo(context, 0);
    for (PackageInfo packageInfo : packages) {
      if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
        // non-system app
        appList1.add(packageInfo);
      } else {
        // system app
        appList2.add(packageInfo);
      }
    }
    return new Pair<>(appList1, appList2);
  }

  /**
   * Check app is installed
   *
   * @param packageName
   * @return
   */
  public static boolean checkPackageInstalled(String packageName) {
    try {
      ContextHolder.context().getPackageManager().getApplicationInfo(packageName, 0);
      return true;
    } catch (PackageManager.NameNotFoundException ignored) {
      return false;
    }
  }

  public static boolean checkPackageInstalled(String[] packageNames) {
    for (String p : packageNames) {
      if (checkPackageInstalled(p)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get Launch package
   *
   * @param context
   * @param tag
   * @return
   */
  public static Intent getLaunchIntentForPackage(@NonNull Context context, String tag) {
    return context.getPackageManager().getLaunchIntentForPackage(tag);
  }

  /**
   * query activities by intent
   *
   * @param context
   * @param intent
   * @param flag
   * @return
   */
  public static List<ResolveInfo> queryIntentActivities(
      @NonNull Context context, @NonNull Intent intent, int flag) {
    return context.getPackageManager().queryIntentActivities(intent, flag);
  }

  private static int targetSdkVersion = 0;

  public static int targetSdkVersion() {
    if (targetSdkVersion <= 0) {
      try {
        ApplicationInfo info =
            ContextHolder.context()
                .getPackageManager()
                .getApplicationInfo(ContextHolder.context().getPackageName(), 0);
        targetSdkVersion = info.targetSdkVersion;
      } catch (PackageManager.NameNotFoundException e) {
        // Never going to happen
      }
    }
    return targetSdkVersion;
  }

  private static String versionName = "";
  private static int versionCode = 0;

  public static String getVersionName(Context context) {
    if (!TextUtils.isEmpty(versionName)) {
      return versionName;
    }
    initVersionInfo(context);
    return versionName;
  }

  public static int getVersionCode(Context context) {
    if (versionCode != 0) {
      return versionCode;
    }
    initVersionInfo(context);
    return versionCode;
  }

  private static void initVersionInfo(Context context) {
    PackageManager packageManager = context.getPackageManager();
    PackageInfo packageInfo;
    try {
      packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
      versionName = packageInfo.versionName;
      versionCode = packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
    }
  }
}
