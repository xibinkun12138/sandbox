package com.hello.sandbox.util;

import com.meituan.android.walle.WalleChannelReader;
import top.niunaijun.blackboxa.app.App;

public class ChannelHelper {
  private static String sChannel = null;
  public static final String DISTRIBUTOR_ERROR = "unknown";

  public static String getChannel() {
    if (sChannel == null) {
      try {
        sChannel = WalleChannelReader.getChannel(App.app, DISTRIBUTOR_ERROR);
      } catch (Throwable e) {
      }
    }
    return sChannel;
  }

  public static boolean isXiaomi() {
    getChannel();
    return "xiaomi".equals(sChannel);
  }
}
