package com.hello.sandbox.util;

import android.content.Context;
import top.niunaijun.blackboxa.app.App;

/** 使用默认文件存储k-v对 若有大量数据，请使用新文件 */
public class SharedPrefUtils {

  private static final String PREF_APP = "pref_app";

  /**
   * Gets boolean data.
   *
   * @param context the context
   * @param key the key
   * @return the boolean data
   */
  public static boolean getBooleanData(Context context, String key) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, false);
  }

  public static boolean getBooleanWithDefault(Context context, String key, boolean def) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(key, def);
  }

  /**
   * Gets int data.
   *
   * @param context the context
   * @param key the key
   * @return the int data
   */
  public static int getIntData(Context context, String key) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, 0);
  }

  public static int getIntDataWithDefault(Context context, String key, int defValue) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getInt(key, defValue);
  }

  /**
   * Gets string data.
   *
   * @param context the context
   * @param key the key
   * @return the string data
   */
  // Get Data
  public static String getStringData(Context context, String key) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, "");
  }

  public static String getStringDataWithDefaultValue(Context context, String key, String defValue) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getString(key, defValue);
  }

  /**
   * Save data.
   *
   * @param context the context
   * @param key the key
   * @param val the val
   */
  // Save Data
  public static void saveData(Context context, String key, String val) {
    context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putString(key, val).apply();
  }

  /**
   * Save data.
   *
   * @param context the context
   * @param key the key
   * @param val the val
   */
  public static void saveData(Context context, String key, int val) {
    context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putInt(key, val).apply();
  }

  /**
   * Save data.
   *
   * @param context the context
   * @param key the key
   * @param val the val
   */
  public static void saveData(Context context, String key, boolean val) {
    context
        .getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(key, val)
        .apply();
  }

  public static void saveData(Context context, String key, long val) {
    context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().putLong(key, val).apply();
  }

  public static long getLongData(Context context, String key) {
    return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getLong(key, 0L);
  }

  public static void clear() {
    App.mContext.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit().clear().commit();
  }
}
