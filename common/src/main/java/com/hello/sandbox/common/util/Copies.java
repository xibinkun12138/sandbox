package com.hello.sandbox.common.util;

import com.hello.sandbox.common.R;
import com.hello.sandbox.common.util.ContextHolder;

/** Created by molikto on 02/29/16. */
public class Copies {

  public static String removeFullStop(String str) {
    if (str != null) {
      String full = ContextHolder.context().getResources().getString(R.string.LANG_FULL_STOP);
      if (str.endsWith(full)) {
        return str.substring(0, str.length() - full.length());
      } else {
        return str;
      }
    } else {
      return null;
    }
  }

  public static String fullStopIfNot(String str) {
    if (Character.isLetter(str.charAt(str.length() - 1))) {
      return str + ContextHolder.context().getResources().getString(R.string.LANG_FULL_STOP);
    } else {
      return str;
    }
  }

  public static String linkSentence(String first, String second) {
    return fullStopIfNot(first)
        + ContextHolder.context().getString(R.string.LANG_SPACE)
        + fullStopIfNot(second);
  }

  public static String linkSentence(int first, int second) {
    return fullStopIfNot(ContextHolder.context().getString(first))
        + ContextHolder.context().getString(R.string.LANG_SPACE)
        + fullStopIfNot(ContextHolder.context().getString(second));
  }

  public static String sentenceCaseToLowSentenceCaseSimple(String str) {
    if (str.length() > 0) {
      return str.substring(0, 1).toLowerCase() + str.substring(1);
    } else {
      return str;
    }
  }
}
