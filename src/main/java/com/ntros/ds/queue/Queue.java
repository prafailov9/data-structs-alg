package com.ntros.ds.queue;

public interface Queue<E> {

  void add(E value);

  E remove();

  boolean isEmpty();

  int size();

  E peek();
}
