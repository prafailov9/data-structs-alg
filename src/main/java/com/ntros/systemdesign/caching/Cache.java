package com.ntros.systemdesign.caching;

public interface Cache<K, V> {

  void put(K key, V value);

  V get(K key);

  V invalidate(K key);

  int size();

  void clear();
}
