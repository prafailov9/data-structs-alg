package com.ntros.systemdesign.mesaging.idempotency.inbox;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;

public interface InboxConnector {

  void send(InboxMessage inboxMessage);
  ServerResponse receive();
  boolean shutdown();
}
