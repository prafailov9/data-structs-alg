package com.ntros.systemdesign.mesaging.idempotency;

public interface RunnableLifecycle {

  void start();

  void awaitTermination() throws InterruptedException;
}
