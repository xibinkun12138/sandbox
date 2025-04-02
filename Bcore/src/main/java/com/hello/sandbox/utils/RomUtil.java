package com.hello.sandbox.utils;

import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtil {
  private static String sName;
  private static String sVersion;
  private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
  private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";
  public static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
  private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
  public static final String ROM_EMUI = "EMUI";
  public static final String ROM_MIUI = "MIUI";
  public static final String ROM_OPPO = "OPPO";
  public static final String ROM_VIVO = "VIVO";
  public static final String ROM_FLYME = "FLYME";
  public static boolean isHuawei() {
    if (!TextUtils.isEmpty(Build.BRAND)
        && ((Build.BRAND.toLowerCase()).contains("huawei")
        || (Build.BRAND.toLowerCase()).contains("honor")
        || (Build.BRAND.toLowerCase()).contains("hw"))) {
      return true;
    }
    return isEmui();
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
  @Nullable
  public static String getProp(String name) {
    String line = null;
    BufferedReader input = null;
    try {
      Process p = Runtime.getRuntime().exec("getprop " + name);
      input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
      line = input.readLine();
      input.close();
    } catch (IOException e) {
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
  public static boolean isEmui() {
    return check(ROM_EMUI);
  }
}
