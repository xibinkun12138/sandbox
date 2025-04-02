package com.hello.sandbox.util;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import com.hello.sandbox.ui.WebviewAct;
import com.hello.sandbox.common.util.HanziToPinyin;
import java.util.Locale;

public class StringUtils {

  public static String[] ALL_LETTERS = {
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
    "T", "U", "V", "W", "X", "Y", "Z", "#"
  };

  public static Pair<String, String> getSortLetter(String name) {
    String letter = "#";
    if (name == null) {
      return new Pair<>(letter, "");
    }
    String pinyin = HanziToPinyin.getInstance().getSpelling(name);
    if (pinyin != null && pinyin.length() > 0) {
      String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

      if (sortString.matches("[A-Z]")) {
        letter = sortString.toUpperCase(Locale.CHINESE);
      }
    }
    return new Pair<>(letter, pinyin.toUpperCase(Locale.CHINESE));
  }

  public static SpannableStringBuilder getLinkSubstringWithColorToWebView(
      Activity act, int hightLightColor, String o, String... pairs) {
    SpannableStringBuilder clickableHtmlBuilder = new SpannableStringBuilder(o);
    for (int i = 0; i < pairs.length; i += 2) {
      String text = pairs[i], link = pairs[i + 1];
      clickableHtmlBuilder.setSpan(
          new ClickableSpan() {
            public void onClick(View view) {
              String curTitle = text.replace("《", "").replace("》", "");
              WebviewAct.Companion.start(act, link, curTitle);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
              ds.setColor(hightLightColor);
            }
          },
          o.indexOf(text),
          o.indexOf(text) + text.length(),
          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    return clickableHtmlBuilder;
  }
}
