package com.ntros.ds.stack;

import java.util.Arrays;
import java.util.List;

public class ArrayStack<E> implements Stack<E> {
  private static final int DEFAULT_CAPACITY = 1024;
  private Object[] arr = new Object[DEFAULT_CAPACITY];
  private int topIdx;
  private int size;

  @Override
  public void push(E elem) {
    if (size  >= DEFAULT_CAPACITY) {
      resize();
    }
    arr[topIdx++] = elem;
    size++;
  }

  @Override
  public E pop() {
    if (isEmpty()) {
      return null;
    }

    E e = elementAt(topIdx);
    arr[topIdx] = null;
    topIdx--;
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
  public E top() {
    if (isEmpty()) {
      return null;
    }

    return elementAt(topIdx - 1);
  }

  @Override
  public List<E> toList() {
    return (List<E>) Arrays.asList(Arrays.copyOf(arr, size));
  }

  private void resize() {
    Object[] a1 = new Object[arr.length + (DEFAULT_CAPACITY / 2)];
    System.arraycopy(arr, 0, a1, 0, size);
    arr = a1;
  }

  @SuppressWarnings("unchecked")
  private E elementAt(int index) {
    return (E) arr[index];
  }
}
