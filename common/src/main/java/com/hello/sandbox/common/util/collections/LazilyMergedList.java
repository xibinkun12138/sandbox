package com.hello.sandbox.common.util.collections;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Created by molikto on 11/03/15. */
public class LazilyMergedList<T> extends AbstractList<T> {

  private final List<T> left;
  private final List<T> right;
  ArrayList<T> base = new ArrayList<>();
  private final Comparator<T> comparator;

  public LazilyMergedList(List<T> left, List<T> right, Comparator<T> comparator) {
    super();
    this.left = left;
    this.right = right;
    this.comparator = comparator;
  }

  @Override
  public int size() {
    return left.size() + right.size();
  }

  int merged = 0;
  int leftMerged = 0, rightMerged = 0;

  @Override
  public T get(int location) {
    if (merged <= location) {
      synchronized (this) {
        while (base.size() <= location) {
          base.add(null);
        }
        for (; merged <= location; merged++) {
          if (leftMerged >= left.size()) {
            base.set(merged, right.get(rightMerged++));
          } else if (rightMerged >= right.size()) {
            base.set(merged, left.get(leftMerged++));
          } else {
            if (comparator.compare(left.get(leftMerged), right.get(rightMerged)) > 0) {
              base.set(merged, right.get(rightMerged++));
            } else {
              base.set(merged, left.get(leftMerged++));
            }
          }
        }
      }
    }
    return base.get(location);
  }
}
