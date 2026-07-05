package com.ntros.ds.stack;

import java.util.ArrayList;
import java.util.List;

public class LinkedStack<E> implements Stack<E> {

  private Node<E> top;
  private int size;

  @Override
  public void push(E elem) {
    var n = new Node<>(elem);
    n.next = top;
    top = n;
    size++;
  }

  @Override
  public E pop() {
    if (isEmpty()) {
      return null;
    }
    E e = top.elem;
    top = top.next;
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
    return top.elem;
  }

  @Override
  public List<E> toList() {
    List<E> list = new ArrayList<>();
    var t = top;
    while (t != null) {
      list.add(t.elem);
      t = t.next;
    }
    return list;
  }

  private static final class Node<E> {
    E elem;
    Node<E> next;

    Node(E e) {
      elem = e;
    }
  }
}
