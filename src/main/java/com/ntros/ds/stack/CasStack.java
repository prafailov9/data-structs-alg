package com.ntros.ds.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CasStack<E> implements Stack<E> {

  private final AtomicReference<Node<E>> top = new AtomicReference<>();
  private final AtomicInteger size = new AtomicInteger();

  @Override
  public void push(E elem) {
    Node<E> n = new Node<>(elem);
    Node<E> oldTop;
    do {
      oldTop = top.get();
      n.next = oldTop;
    } while (!top.compareAndSet(oldTop, n));

    size.incrementAndGet();
  }

  @Override
  public E pop() {
    Node<E> oldTop;
    Node<E> newTop;

    do {
      oldTop = top.get();
      if (oldTop == null) {
        return null;
      }
      newTop = oldTop.next;
    } while (!top.compareAndSet(oldTop, newTop));

    size.decrementAndGet();
    return oldTop.elem;
  }

  @Override
  public boolean isEmpty() {
    return top.get() == null;
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  public E top() {
    Node<E> n = top.get();
    return n == null ? null : n.elem;
  }

  @Override
  public List<E> toList() {
    List<E> list = new ArrayList<>();
    Node<E> curr = top.get();

    while (curr != null) {
      list.add(curr.elem);
      curr = curr.next;
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
