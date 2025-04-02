package com.hello.sandbox.core.system.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import com.hello.sandbox.SandBoxCore;

/** Created by BlackBox on 2022/3/18. */
public class NotificationChannelManager {
  private static final NotificationChannelManager sManager = new NotificationChannelManager();

  public static NotificationChannel APP_CHANNEL;

  public static NotificationChannelManager get() {
    return sManager;
  }

  private NotificationChannelManager() {}

  public void registerAppChannel() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      return;
    }
    NotificationManager nm =
        (NotificationManager)
            SandBoxCore.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    String CHANNEL_ONE_ID = SandBoxCore.getContext().getPackageName();
    String CHANNEL_ONE_NAME = "black-box-app";
    APP_CHANNEL =
        new NotificationChannel(
            CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
    APP_CHANNEL.enableLights(true);
    APP_CHANNEL.setLightColor(Color.RED);
    APP_CHANNEL.setShowBadge(true);
    APP_CHANNEL.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
    nm.createNotificationChannel(APP_CHANNEL);
  }
}
