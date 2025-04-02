package com.hello.sandbox.common.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardUtil {

  private static ClipboardManager mClipboardManager =
      (ClipboardManager) ContextHolder.context().getSystemService(Context.CLIPBOARD_SERVICE);

  public static void copy(String content) {
    mClipboardManager.setPrimaryClip(ClipData.newPlainText("text", content));
  }

  public static String getClipboardText() {
    if (!mClipboardManager.hasPrimaryClip()) {
      return "";
    }

    StringBuilder resString = new StringBuilder();
    ClipData clipData = mClipboardManager.getPrimaryClip();
    if (clipData == null) {
      return "";
    }

    int itemCount = clipData.getItemCount();
    for (int i = 0; i < itemCount; i++) {
      ClipData.Item item = clipData.getItemAt(i);
      resString.append(item.coerceToText(ContextHolder.context()));
    }
    return resString.toString();
  }
}
