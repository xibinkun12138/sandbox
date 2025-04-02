package com.hello.sandbox.calc.util;

public class ExceptionUtils {
  public static String exceptionToString(Exception e) {

    String message = e.getMessage();
    StringBuilder sb = new StringBuilder(message == null ? "" : message);
    StackTraceElement[] stackTrace = e.getStackTrace();

    for (StackTraceElement s : stackTrace) {
      sb.append('\n');
      sb.append(s.toString());
    }

    return sb.toString();
  }
}
