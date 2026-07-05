package com.ntros.ds.concurrency.pooling.pool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ntros.systemdesign.concurrency.pooling.pool.PoolManager;
import com.ntros.systemdesign.concurrency.pooling.pool.ResourcePoolManager;
import org.junit.jupiter.api.Test;

class ResourcePoolTest {

  private final PoolManager poolManager = ResourcePoolManager.getInstance();

  @Test
  public void getConnectionTest() throws InterruptedException {
    var con = poolManager.acquire();

    assertNotNull(con);
    assertEquals(1, con.getConnectionId());
    assertEquals(1, poolManager.activeCount());
    assertEquals(4, poolManager.freeCount());
  }

  @Test
  public void acquireThenReleaseTest() throws InterruptedException {
    var con = poolManager.acquire();

    assertNotNull(con);
    assertEquals(1, con.getConnectionId());
    assertEquals(1, poolManager.activeCount());

    poolManager.release(con);
    assertEquals(0, poolManager.activeCount());
    assertEquals(5, poolManager.freeCount());
  }

  @Test
  public void multithreadedSimTest() throws InterruptedException {
    int n = 3;
    Thread[] threads = new Thread[n];
    for (int i = 0; i < n; i++) {
      threads[i] =
          new Thread(
              () -> {
                try {
                  work();
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              },
              (i + 1) + "");
    }

    for (var t : threads) {
      t.start();
    }

    for (var t : threads) {
      t.join();
    }
  }

  void work() throws InterruptedException {
    while (true) {
      // get
      var con = poolManager.acquire();
      // work
      sleep(300);
      if (con != null) {
        // release
        poolManager.release(con);
      }
    }
  }

  void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
