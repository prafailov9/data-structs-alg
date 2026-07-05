package com.ntros.ds.queue;

public class LinkedQueue<E> implements Queue<E> {

  private Node<E> head;
  private Node<E> tail;
  private int size;

  @Override
  public void add(E value) {
    var n = new Node<>(value);

    if (isEmpty()) {
      head = tail = n;
    } else {
      head.next = n;
      head = n;
    }
    size++;
  }

  @Override
  public E remove() {
    if (isEmpty()) {
      return null;
    }
    E e = tail.elem;
    tail = tail.next;
    if (tail == null) {
      head = null;
    }
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

    return tail.elem;
  }

  private static final class Node<E> {
    E elem;
    Node<E> next;

    Node(E e) {
      elem = e;
    }
  }
}
