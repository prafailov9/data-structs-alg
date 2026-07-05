package com.ntros.systemdesign.mesaging.idempotency.inbox;

import com.ntros.systemdesign.mesaging.idempotency.RunnableLifecycle;

public interface InboxProcessor extends RunnableLifecycle {

  void process();
  void start();
}
