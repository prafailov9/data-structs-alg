package com.ntros.systemdesign.concurrency.pooling.pool;

import com.ntros.systemdesign.concurrency.pooling.connection.Connection;

public interface PoolManager {

  Connection acquire() throws InterruptedException;

  boolean release(Connection connection);

  int activeCount();
  int freeCount();

}
