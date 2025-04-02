package com.hello.sandbox.common.util;

import android.graphics.Color;
import java.util.Random;

/** User: molikto Date: 01/05/15 Time: 12:54 */
public class RandUtil {

  public static Random rand = new Random();

  public static int color(int trans) {
    return Color.argb(trans, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
  }

  public static int color() {
    return color(255);
  }

  public static float between(float start, float end) {
    return (rand.nextFloat() - 0.5f) * (end - start) + start;
  }

  // left close right open
  public static int lcroInterval(int a, int until) {
    return (rand.nextInt(until - a) + a);
  }

  public static int nextInt(int num) {
    return (rand.nextInt(num));
  }

  public static int sign() {
    return rand.nextBoolean() ? 1 : -1;
  }

  public static boolean bool() {
    return rand.nextBoolean();
  }

  public static String str(int size) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < size; i++) {
      sb.append(RandUtil.lcroInterval(' ', 'z'));
    }
    return sb.toString();
  }

  public static void bytes(byte[] random) {
    rand.nextBytes(random);
  }

  public static byte byt() {
    return (byte) rand.nextInt();
  }
}
