package com.ntros.systemdesign.concurrency.ds;

public interface BlockingQueue<E> {

  void put(E value) throws InterruptedException;

  E take() throws InterruptedException;

  boolean isEmpty();

  int size();

  E peek();
}
