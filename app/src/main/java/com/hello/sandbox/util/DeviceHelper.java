package com.hello.sandbox.util;

import android.webkit.WebSettings;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import top.niunaijun.blackboxa.app.App;

public class DeviceHelper {

  public static String getUa() {
    try {
      return WebSettings.getDefaultUserAgent(App.app);
    } catch (Exception e) {
    }
    return "";
  }

  public static String getLocale() {
    Locale locale = Locale.getDefault();
    String language = locale.getLanguage();
    String country = locale.getCountry();
    return language + "_" + country;
  }

  public static String getIPAddress(boolean useIPv4) {
    try {
      List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface intf : interfaces) {
        List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
        for (InetAddress addr : addrs) {
          if (!addr.isLoopbackAddress()) {
            String sAddr = addr.getHostAddress();
            boolean isIPv4 = sAddr.indexOf(':') < 0;
            if (useIPv4) {
              if (isIPv4) return sAddr;
            } else {
              if (!isIPv4) {
                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
              }
            }
          }
        }
      }
    } catch (NullPointerException ignored) { // we've observed NPE in NetworkManagementSocketTagger
    } catch (SocketException ignored) {
      // for now eat exceptions
    } catch (ArrayIndexOutOfBoundsException ignored) {
      // https://seiya.p1staff.com/apps/2/issues/dc78173a855e4c5a8bce22a6f0ee6b24?issues_trend_filters=%257B%2522timeGranularity%2522%253A%2522hour%2522%252C%2522timeShift%2522%253A%255B86400%255D%257D
      //        临时加上catch，崩溃在源码层
    }
    return "";
  }
}
