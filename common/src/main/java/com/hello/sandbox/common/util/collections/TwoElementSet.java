package com.hello.sandbox.common.util.collections;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class TwoElementSet<E> extends AbstractSet<E> implements Serializable {
  final E element1;
  final E element2;

  public TwoElementSet(E object, E object2) {
    element1 = object;
    element2 = object2;
  }

  @Override
  public boolean contains(Object object) {
    if (object == null) {
      return element1 == null || element2 == null;
    } else {
      return object.equals(element1) || object.equals(element2);
    }
  }

  @Override
  public int size() {
    return 2;
  }

  @NonNull
  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      int cur = 0;

      @Override
      public boolean hasNext() {
        return cur <= 1;
      }

      @Override
      public E next() {
        if (cur == 0) {
          cur = 1;
          return element1;
        } else if (cur == 1) {
          cur = 2;
          return element2;
        }
        throw new NoSuchElementException();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
