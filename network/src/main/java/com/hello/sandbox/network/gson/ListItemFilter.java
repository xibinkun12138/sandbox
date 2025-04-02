package com.hello.sandbox.network.gson;

import java.util.ArrayList;
import java.util.List;

public class ListItemFilter {
  /** 过滤为null的元素 */
  @SuppressWarnings("SuspiciousMethodCalls")
  public static <V> List<V> filterNullElement(List<V> list) {
    ArrayList<?> nullGroup = new ArrayList<>();
    nullGroup.add(null);
    list.removeAll(nullGroup);
    return list;
  }
}
