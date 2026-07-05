package com.ntros.systemdesign.concurrency.pooling.pool;

import com.ntros.systemdesign.concurrency.pooling.connection.Connection;
import com.ntros.systemdesign.concurrency.pooling.connection.ResourceConnection;
import java.util.ArrayList;
import java.util.List;

public class ResourcePoolManager implements PoolManager {

  private static final int INITIAL_SIZE = 5;

  private final List<Connection> free = new ArrayList<>();
  private final List<Connection> active = new ArrayList<>();

  private final Object lock = new Object();

  private ResourcePoolManager() {
    // read resource properties from file...

    for (int i = 1; i <= INITIAL_SIZE; i++) {
      free.add(new ResourceConnection());
    }
  }

  public static ResourcePoolManager getInstance() {
    return InstanceHolder.INSTANCE;
  }

  @Override
  public Connection acquire() throws InterruptedException {
    Connection con;
    synchronized (lock) {
      while (free.isEmpty()) {
          lock.wait();
      }
      con = free.removeLast();
      active.add(con);
    }
    return con;
  }

  @Override
  public boolean release(Connection connection) {
    synchronized (lock) {
      if (active.remove(connection)) {
        free.addFirst(connection);
        lock.notifyAll();
        return true;
      }
    }
    return false;
  }

  @Override
  public int activeCount() {
    synchronized (lock) {
      return active.size();
    }
  }

  @Override
  public int freeCount() {
    synchronized (lock) {
      return free.size();
    }
  }

  /// JVM guarantees thread-safe class initialization.
  /// InstanceHolder class and its INSTANCE field are initialized on
  /// the very first call of ResourcePool.getInstance();
  private static class InstanceHolder {
    static final ResourcePoolManager INSTANCE = new ResourcePoolManager();
  }
}
