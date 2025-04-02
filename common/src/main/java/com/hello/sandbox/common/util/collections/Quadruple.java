package com.hello.sandbox.common.util.collections;

import java.util.Objects;

/** Created by chenyanzhang on 2019/6/15. */
public class Quadruple<A, B, C, D> {
  public A first;
  public B second;
  public C third;
  public D fourth;

  public Quadruple(A first, B second, C third, D fourth) {
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    com.hello.sandbox.common.util.collections.Quadruple<?, ?, ?, ?> quadruple =
        (com.hello.sandbox.common.util.collections.Quadruple<?, ?, ?, ?>) o;
    return Objects.equals(first, quadruple.first)
        && Objects.equals(second, quadruple.second)
        && Objects.equals(third, quadruple.third)
        && Objects.equals(fourth, quadruple.fourth);
  }

  @Override
  public int hashCode() {
    return Objects.hash(first, second, third, fourth);
  }

  @Override
  public String toString() {
    return "Quadruple{"
        + "first="
        + first
        + ", second="
        + second
        + ", third="
        + third
        + ", fourth="
        + fourth
        + '}';
  }
}
