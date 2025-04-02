package com.hello.sandbox.common.util;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;

/** Created by Wencharm on 06/04/2017. */
public class SettingCompatHelper extends SettingCompatUtil {

  public static void jumpPermissionsSetting(Activity context) {
    // todo title 应用名称待定
    SettingCompatUtil.jumpPermissionsSetting(context, "SandBox");
  }

  public static boolean isCnAndNotIntl() {
    return isChinese();
  }

  public static boolean isOppoHighVersion() {
    return isOppo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
  }

  public static boolean isHuawei() {
    if (!TextUtils.isEmpty(Build.BRAND)
        && ((Build.BRAND.toLowerCase()).contains("huawei")
            || (Build.BRAND.toLowerCase()).contains("honor")
            || (Build.BRAND.toLowerCase()).contains("hw"))) {
      return true;
    }
    return isEmui();
  }
}
