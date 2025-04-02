package com.hello.sandbox.common.util;

import android.text.Html;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

/** Created by molikto on 03/01/15. */
public class StringsUtil {

  static Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z ]+");

  public static boolean isEnglishName(String name) {
    return NAME_PATTERN.matcher(name).matches();
  }

  public static CharSequence linkSubstring(String o, String l, String link) {
    return Html.fromHtml(o.replace(l, "<a href=\"" + link + "\">" + l + "</a>"));
  }

  public static CharSequence linkSubstring(String o, String... pairs) {
    for (int i = 0; i < pairs.length; i += 2) {
      String text = pairs[i], link = pairs[i + 1];
      o = o.replace(text, "<a href=\"" + link + "\">" + text + "</a>");
    }
    return Html.fromHtml(o);
  }

  public static CharSequence linkSubstringNextLine(String o, String l, String link) {
    return Html.fromHtml(o.replace(l, "<br> <a href=\"" + link + "\">" + l + "</a>"));
  }

  public static String readableFileSize(long size) {
    if (size <= 0) return "0 B";
    final String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))
        + " "
        + units[digitGroups];
  }

  public static String getActivitiesRingDotStr(int num) {
    String str = null;
    if (num > 99) {
      str = "99";
    } else if (num > 0) {
      str = String.valueOf(num);
    }
    return str;
  }

  public static String getDotStr(int num) {
    return getDotStrMax(num, 99);
  }

  public static String getDotStrMax(int num) {
    return getDotStrMax(num, 9999);
  }

  public static String getDotStrMax(int num, int max) {
    String str = null;
    if (num > max) {
      str = String.valueOf(max) + "+";
    } else if (num > 0) {
      str = String.valueOf(num);
    }
    return str;
  }

  public static String formatNumWithMax(long num, long max) {
    String str;
    if (num > max) {
      str = max + "+";
    } else if (num > 0) {
      str = String.valueOf(num);
    } else {
      str = "0";
    }
    return str;
  }

  public static String formatNumberWithOneDecimal(double num) {
    return new DecimalFormat("#.#").format(num);
  }

  public static String formatNumberWithTwoDecimal(double num) {
    return new DecimalFormat("#.##").format(num);
  }

  public static String formatNumberWithOneDecimalSeparatorDot(double num) {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    symbols.setDecimalSeparator('.');
    DecimalFormat df = new DecimalFormat("#.##", symbols);
    return df.format(num);
  }

  public static String formatNumberAlwaysWithTwoDecimal(double num) {
    return new DecimalFormat("0.00").format(num);
  }

  public static String formatNumberWithOneAndRoundDown(double num) {
    return new DecimalFormat("0.0")
        .format(new BigDecimal(num).setScale(1, BigDecimal.ROUND_DOWN).doubleValue());
  }

  public static boolean isEmojiCharacter(char codePoint) {
    return !((codePoint == 0x0)
        || (codePoint == 0x9)
        || (codePoint == 0xA)
        || (codePoint == 0xD)
        || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
        || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
        || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
  }

  /** 根据unicode判断字符是否为中文 */
  public static boolean isChinese(char c) {
    Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
    if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
        || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
        || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
        || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
        || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
        || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
      return true;
    }
    return false;
  }

  /** source == null && toEqual== null return true */
  public static boolean safeEqual(String source, String toEqual) {
    if (source != null) {
      return source.equals(toEqual);
    }
    return toEqual == null;
  }

  public static String formatStringSafely(String formatter, String pack) {
    try {
      return String.format(formatter, pack);
    } catch (Exception e) {
      // if formatter is from server, exception might happen when format it
      return formatter;
    }
  }
}
