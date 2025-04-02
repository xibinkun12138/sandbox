package com.hello.sandbox.proxy;

import com.hello.sandbox.SandBoxCore;
import java.util.Locale;

/** Created by Milk on 4/1/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class ProxyManifest {
  public static final int FREE_COUNT = 50;
  public static final String PROXY_PACKAGE_NAME = "com.hello.sandbox";

  public static boolean isProxy(String msg) {
    return getBindProvider().equals(msg) || msg.contains("proxy_content_provider_");
  }

  public static String getBindProvider() {
    return SandBoxCore.getHostPkg() + ".blackbox.SystemCallProvider";
  }

  public static String getProxyAuthorities(int index) {
    return String.format(
        Locale.CHINA, "%s.proxy_content_provider_%d", SandBoxCore.getHostPkg(), index);
  }

  public static String getProxyPendingActivity(int index) {
    return String.format(
        Locale.CHINA, PROXY_PACKAGE_NAME + ".proxy.ProxyPendingActivity$P%d", index);
  }

  public static String getProxyActivity(int index) {
    return String.format(Locale.CHINA, PROXY_PACKAGE_NAME + ".proxy.ProxyActivity$P%d", index);
  }

  public static String TransparentProxyActivity(int index) {
    return String.format(
        Locale.CHINA, PROXY_PACKAGE_NAME + ".proxy.TransparentProxyActivity$P%d", index);
  }

  public static String getProxyService(int index) {
    return String.format(Locale.CHINA, PROXY_PACKAGE_NAME + ".proxy.ProxyService$P%d", index);
  }

  public static String getProxyJobService(int index) {
    return String.format(Locale.CHINA, PROXY_PACKAGE_NAME + ".proxy.ProxyJobService$P%d", index);
  }

  public static String getProxyFileProvider() {
    return SandBoxCore.getHostPkg() + ".blackbox.FileProvider";
  }

  public static String getProxyReceiver() {
    return SandBoxCore.getHostPkg() + ".stub_receiver";
  }

  public static String getProcessName(int bPid) {
    return SandBoxCore.getHostPkg() + ":p" + bPid;
  }
}
