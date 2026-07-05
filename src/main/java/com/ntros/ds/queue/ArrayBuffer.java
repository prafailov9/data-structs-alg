package com.ntros.ds.queue;

public class ArrayBuffer<E> implements Queue<E> {

  private final Object[] buf;
  private int headIdx;
  private int tailIdx;
  private final int capacity;
  private int size;

  public ArrayBuffer(int capacity) {
    this.capacity = capacity;
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity cannot be 0");
    }
    buf = new Object[capacity];
  }

  @Override
  public void add(E value) {
    buf[tailIdx] = value;
    if (size == capacity) {

      // gets the reminder. If tail+1 == cap, then reminder is 0
      tailIdx = (tailIdx + 1) % capacity;
      headIdx = (headIdx + 1) % capacity;
      return;
    }

    tailIdx = (tailIdx + 1) % capacity;
    size++;
  }

  @Override
  public E remove() {
    if (isEmpty()) {
      return null;
    }

    E e = elementAt(headIdx);
    buf[headIdx] = null;
    headIdx = (headIdx + 1) % capacity;
    size--;
    return e;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public E peek() {
    if (isEmpty()) {
      return null;
    }
    return elementAt(headIdx);
  }

  private E elementAt(int index) {
    return (E) buf[index];
  }
}
