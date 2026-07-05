package com.ntros.systemdesign.concurrency.pooling.connection;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractResourceConnection implements Connection {

  private static final AtomicInteger NEXT_CONN_ID = new AtomicInteger(1);

  protected final int connectionId = NEXT_CONN_ID.getAndIncrement();
}
