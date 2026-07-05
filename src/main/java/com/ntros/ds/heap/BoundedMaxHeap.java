package com.ntros.ds.heap;

import java.util.Comparator;

public class BoundedMaxHeap<E> {

  private final Object[] arr;
  private final Comparator<E> comparator;
  private int size;

  public BoundedMaxHeap(int capacity, Comparator<E> comparator) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("");
    }
    this.arr = new Object[capacity];
    this.comparator = comparator;
  }

  public void insert(E elem) {
    if (size == arr.length) {
      throw new IllegalStateException("Heap is full");
    }
    int currentIdx = size;
    arr[currentIdx] = elem;
    size++;

    while (currentIdx > 0) {
      int parentIdx = (currentIdx - 1) / 2;
      if (comparator.compare(elementAt(currentIdx), elementAt(parentIdx)) <= 0) {
        break;
      }
      swap(parentIdx, currentIdx);

      currentIdx = parentIdx;
    }
  }

  public E remove() {
    if (size == 0) {
      throw new IllegalStateException("Heap is empty");
    }
    E root = elementAt(0);
    arr[0] = arr[size - 1];
    arr[size - 1] = null;
    size--;
    int currentIdx = 0;

    while (true) {
      int leftIdx = 2 * currentIdx + 1;
      int rightIdx = 2 * currentIdx + 2;

      if (leftIdx >= size) {
        break;
      }

      int bestIdx = leftIdx;
      if (rightIdx < size && comparator.compare(elementAt(rightIdx), elementAt(leftIdx)) > 0) {
        bestIdx = rightIdx;
      }

      if (comparator.compare(elementAt(currentIdx), elementAt(bestIdx)) >= 0) {
        break;
      }

      swap(bestIdx, currentIdx);
      currentIdx = bestIdx;
    }
    return root;
  }

  private E elementAt(int idx) {
    return (E) arr[idx];
  }

  private void swap(int i, int j) {
    Object temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }
}
