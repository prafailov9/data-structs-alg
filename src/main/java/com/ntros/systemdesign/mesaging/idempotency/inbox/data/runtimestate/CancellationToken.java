package com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate;

public class CancellationToken {

  private volatile boolean cancelled = false;

  public boolean isCancelled() {
    return cancelled;
  }

  public void cancel() {
    cancelled = true;
  }
}
