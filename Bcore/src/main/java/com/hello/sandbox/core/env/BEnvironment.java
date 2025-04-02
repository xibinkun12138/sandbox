package com.hello.sandbox.core.env;

import com.hello.sandbox.SandBoxCore;
import com.hello.sandbox.app.BActivityThread;
import com.hello.sandbox.utils.FileUtils;
import com.hello.sandbox.utils.compat.BuildCompat;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Locale;

/** Created by Milk on 4/22/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class BEnvironment {
  private static final File sVirtualRoot =
      new File(SandBoxCore.getContext().getCacheDir().getParent(), "sandbox");
  private static final File sExternalVirtualRoot =
      SandBoxCore.getContext().getExternalFilesDir("sandbox");

  public static File JUNIT_JAR = new File(getCacheDir(), "junit.apk");
  public static File EMPTY_JAR = new File(getCacheDir(), "empty.apk");

  public static void load() {
    FileUtils.mkdirs(sVirtualRoot);
    FileUtils.mkdirs(sExternalVirtualRoot);
    FileUtils.mkdirs(getSystemDir());
    FileUtils.mkdirs(getCacheDir());
    FileUtils.mkdirs(getProcDir());
  }

  public static ArrayList<String> getAllDex(String str) {
    File appDir = getAppDir(str);
    ArrayList<String> arrayList = new ArrayList<>();
    File[] listFiles = appDir.listFiles((FilenameFilter) new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return str.endsWith(".apk");
      }
    });
    if (listFiles != null) {
      for (File file : listFiles) {
        if (BuildCompat.isU()) {
          try {
            file.setReadOnly();
          } catch (Throwable th) {
            th.printStackTrace();
          }
        }
        arrayList.add(file.getAbsolutePath());
      }
    }
    return arrayList;
  }

  public static File getVirtualRoot() {
    return sVirtualRoot;
  }

  public static File getExternalVirtualRoot() {
    return sExternalVirtualRoot;
  }

  public static File getSystemDir() {
    return new File(sVirtualRoot, "system");
  }

  public static File getProcDir() {
    return new File(sVirtualRoot, "proc");
  }

  public static File getCacheDir() {
    return new File(sVirtualRoot, "cache");
  }

  public static File getUserInfoConf() {
    return new File(getSystemDir(), "user.conf");
  }

  public static File getAccountsConf() {
    return new File(getSystemDir(), "accounts.conf");
  }

  public static File getUidConf() {
    return new File(getSystemDir(), "uid.conf");
  }

  public static File getSharedUserConf() {
    return new File(getSystemDir(), "shared-user.conf");
  }

  public static File getXPModuleConf() {
    return new File(getSystemDir(), "xposed-module.conf");
  }

  public static File getFakeLocationConf() {
    return new File(getSystemDir(), "fake-location.conf");
  }

  public static File getPackageConf(String packageName) {
    return new File(getAppDir(packageName), "package.conf");
  }

  public static File getExternalUserDir(int userId) {
    return new File(
        sExternalVirtualRoot, String.format(Locale.CHINA, "storage/emulated/%d/", userId));
  }

  public static File getUserDir(int userId) {
    return new File(sVirtualRoot, String.format(Locale.CHINA, "data/user/%d", userId));
  }

  public static File getDeDataDir(String packageName, int userId) {
    return new File(
        sVirtualRoot, String.format(Locale.CHINA, "data/user_de/%d/%s", userId, packageName));
  }

  public static File getExternalDataDir(String packageName, int userId) {
    return new File(
        getExternalUserDir(userId), String.format(Locale.CHINA, "Android/data/%s", packageName));
  }

  public static File getDataDir(String packageName, int userId) {
    return new File(
        sVirtualRoot, String.format(Locale.CHINA, "data/user/%d/%s", userId, packageName));
  }

  public static File getProcDir(int pid) {
    File file = new File(getProcDir(), String.format(Locale.CHINA, "%d", pid));
    FileUtils.mkdirs(file);
    return file;
  }

  public static File getExternalDataFilesDir(String packageName, int userId) {
    return new File(getExternalDataDir(packageName, userId), "files");
  }

  public static File getDataFilesDir(String packageName, int userId) {
    return new File(getDataDir(packageName, userId), "files");
  }

  public static File getExternalDataCacheDir(String packageName, int userId) {
    return new File(getExternalDataDir(packageName, userId), "cache");
  }

  public static File getDataCacheDir(String packageName, int userId) {
    return new File(getDataDir(packageName, userId), "cache");
  }

  public static File getDataLibDir(String packageName, int userId) {
    return new File(getDataDir(packageName, userId), "lib");
  }

  public static File getDataDatabasesDir(String packageName, int userId) {
    return new File(getDataDir(packageName, userId), "databases");
  }

  public static File getAppRootDir() {
    return getAppDir("");
  }

  public static File getAppDir(String packageName) {
    return new File(sVirtualRoot, "data/app/" + packageName);
  }

  public static File getBaseApkDir(String packageName) {
    return new File(sVirtualRoot, "data/app/" + packageName + "/base.apk");
  }

  public static File getAppLibDir(String packageName) {
    return new File(getAppDir(packageName), "lib");
  }

  public static File getXSharedPreferences(String packageName, String prefFileName) {
    return new File(
        BEnvironment.getDataDir(packageName, BActivityThread.getUserId()),
        "shared_prefs/" + prefFileName + ".xml");
  }
}
