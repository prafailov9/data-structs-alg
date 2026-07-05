package com.ntros.ds.caching;

import static org.junit.jupiter.api.Assertions.*;

import com.ntros.systemdesign.caching.Cache;
import com.ntros.systemdesign.caching.LruCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LruCacheTest {

  private static final int CAPACITY = 3;

  private Cache<Integer, String> cache;

  @BeforeEach
  public void setup() {
    cache = new LruCache<>(CAPACITY);
  }

  @AfterEach
  public void teardown() {
    cache.clear();
  }

  @Test
  public void putTest() {
    cache.put(1, "a");
    cache.put(2, "b");
    cache.put(3, "c");

    assertEquals(3, cache.size());
  }

  @Test
  public void addWhenFullTest() {
    cache.put(1, "a");
    cache.put(2, "b");
    cache.put(3, "c");

    cache.put(4, "replaced-a");
    cache.put(5, "replaced-b");
    cache.put(6, "replaced-c");

    assertEquals(3, cache.size());
  }

  // updating happens only on existing key
  @Test
  public void addUpdatesExistingNodesTest() {
    // extending the capacity for this test to show update logic
    cache = new LruCache<>(10);
    cache.put(1, "a");
    cache.put(2, "b");
    cache.put(3, "c");

    cache.put(1, "updated-a");
    cache.put(2, "updated-b");
    cache.put(3, "updated-c");

    assertEquals(3, cache.size());
  }

  @Test
  public void removeFromMiddleTest() {
    cache = new LruCache<>(10);
    cache.put(1, "a");
    cache.put(2, "b");
    cache.put(3, "c");

    String removed = cache.invalidate(2);
    assertEquals(2, cache.size());
    assertEquals("b", removed);
  }

  @Test
  public void removeSingleElementTest() {
    cache.put(1, "a");
    var res = cache.invalidate(1);
    assertEquals("a", res);
  }
}
