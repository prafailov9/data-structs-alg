package com.ntros.systemdesign.concurrency.ds;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedBlockingQueue<E> implements BlockingQueue<E> {

  private final int capacity;
  // queue size is atomic
  private final AtomicInteger count = new AtomicInteger();

  // sentinel head node
  // actual elements start at head.next
  private Node<E> head;
  private Node<E> tail;

  private final ReentrantLock putLock = new ReentrantLock();
  private final Condition notFull = putLock.newCondition();

  private final ReentrantLock takeLock = new ReentrantLock();
  private final Condition notEmpty = takeLock.newCondition();

  public LinkedBlockingQueue(int capacity) {
    if (capacity < 1) {
      throw new IllegalArgumentException("Invalid capacity: " + capacity);
    }

    this.capacity = capacity;
    this.head = this.tail = new Node<>(null);
  }

  @Override
  public void put(E value) throws InterruptedException {
    Objects.requireNonNull(value);

    Node<E> node = new Node<>(value);
    int previousCount;

    putLock.lockInterruptibly();
    try {
      // if at capacity, lock holder waits. While loop to protect from spurious wake-ups.
      while (count.get() == capacity) {
        notFull.await();
      }

      enqueue(node);
      previousCount = count.getAndIncrement();

      // if still room in queue, signal a waiting producer.
      if (previousCount + 1 < capacity) {
        notFull.signal();
      }
    } finally {
      putLock.unlock();
    }

    // queue was empty before this put - wake one waiting consumer.
    if (previousCount == 0) {
      signalNotEmpty();
    }
  }

  @Override
  public E take() throws InterruptedException {
    E value;
    int previousCount;

    takeLock.lockInterruptibly();
    try {
      while (count.get() == 0) {
        notEmpty.await();
      }
      value = dequeue();
      previousCount = count.getAndDecrement();

      // if there are still elements after the take, signal a waiting consumer.
      if (previousCount > 1) {
        notEmpty.signal();
      }
    } finally {
      takeLock.unlock();
    }

    //  queue was full before this take - wake one waiting producer
    if (previousCount == capacity) {
      signalNotFull();
    }

    return value;
  }

  @Override
  public boolean isEmpty() {
    return count.get() == 0;
  }

  @Override
  public int size() {
    return count.get();
  }

  @Override
  public E peek() {
    if (count.get() == 0) {
      return null;
    }

    takeLock.lock();
    try {
      Node<E> first = head.next;
      return first == null ? null : first.value;
    } finally {
      takeLock.unlock();
    }
  }

  private void enqueue(Node<E> node) {
    tail.next = node;
    tail = node;
  }

  private E dequeue() {
    Node<E> first = head.next;

    head.next = head; // Help GC.
    head = first;

    E value = first.value;
    first.value = null; // Help GC.

    return value;
  }

  private void signalNotEmpty() {
    takeLock.lock();
    try {
      notEmpty.signal();
    } finally {
      takeLock.unlock();
    }
  }

  private void signalNotFull() {
    putLock.lock();
    try {
      notFull.signal();
    } finally {
      putLock.unlock();
    }
  }

  private static class Node<E> {
    E value;
    Node<E> next;

    Node(E value) {
      this.value = value;
    }
  }
}
