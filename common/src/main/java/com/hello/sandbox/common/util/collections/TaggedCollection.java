package com.hello.sandbox.common.util.collections;

import java.util.Collection;

public class TaggedCollection<Tag, E> {
  public final Tag tag;
  public final Collection<? extends E> collection;

  public TaggedCollection(Tag tag, Collection<? extends E> collection) {
    this.tag = tag;
    this.collection = collection;
  }

  public int count() {
    return collection.size();
  }
}
