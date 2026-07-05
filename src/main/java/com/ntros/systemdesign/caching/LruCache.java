package com.ntros.systemdesign.caching;

import java.util.HashMap;

public class LruCache<K, V> implements Cache<K, V> {

  private final DoublyLinkedList list = new DoublyLinkedList();
  private final HashMap<K, Node<K, V>> map = new HashMap<>();
  private final int capacity;

  public LruCache(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity <= 0");
    }
    this.capacity = capacity;
  }

  @Override
  public synchronized void put(K key, V value) {
    // update val and move to front
    if (map.containsKey(key)) {
      var existing = map.get(key);
      existing.value = value;
      var moved = list.moveToFront(existing);
      map.put(key, moved);
      return;
    }
    var n = new Node<>(key, value);
    // evict on full
    if (size() == capacity) {
      var last = list.removeEnd();
      map.remove(last.key);
    }
    list.addFront(n);
    map.put(key, n);
  }

  @Override
  public synchronized V get(K key) {
    var n = map.get(key);
    // on hit
    if (n != null) {
      list.moveToFront(n);
      return n.value;
    }
    // read from lower-level storage...
    return null;
  }

  @Override
  public synchronized V invalidate(K key) {
    var n = map.get(key);
    if (n == null) {
      return null;
    }

    map.remove(key);
    list.remove(n);
    return n.value;
  }

  @Override
  public synchronized int size() {
    return map.size();
  }

  @Override
  public synchronized void clear() {
    map.clear();
    list.clear();
  }

  private final class DoublyLinkedList {
    Node<K, V> front;
    Node<K, V> end;
    int size;

    void addFront(Node<K, V> node) {
      if (size == 0) {
        front = end = node;
      } else {
        node.next = front;
        front.prev = node;
        front = node;
      }
      size++;
    }

    Node<K, V> removeEnd() {
      if (size == 0) {
        throw new IllegalStateException("Corrupted cache state");
      }
      var n = end;
      remove(n);
      return n;
    }

    void remove(Node<K, V> node) {
      if (size == 0) {
        throw new IllegalStateException("Corrupted cache state");
      }

      // if single element: clear pointers
      if (size == 1) {
        front = end = null;
      } else {
        // if at edges: move pointer, remove its link
        if (node == front) {
          front = front.next;
          front.prev = null;
        } else if (node == end) {
          end = end.prev;
          end.next = null;
        } else {
          // non-edge node: attach its neighbors
          node.prev.next = node.next;
          node.next.prev = node.prev;
        }
      }

      // remove links
      node.prev = null;
      node.next = null;
      size--;
    }

    // remove from current place, add to front
    Node<K, V> moveToFront(Node<K, V> node) {
      list.remove(node);
      list.addFront(node);
      return node;
    }

    void clear() {
      front = end = null;
      size = 0;
    }
  }

  private static final class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;

    Node(K k, V v) {
      key = k;
      value = v;
    }
  }
}
