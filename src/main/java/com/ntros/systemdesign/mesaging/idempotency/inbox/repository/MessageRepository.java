package com.ntros.systemdesign.mesaging.idempotency.inbox.repository;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.OperationId;
import java.util.HashMap;
import java.util.Map;

public class MessageRepository {

  private final Map<OperationId, InboxMessage> messages = new HashMap<>();

  public InboxMessage getMessage(OperationId operationId) {
    return messages.get(operationId);
  }

  public void addMessage(InboxMessage inboxMessage) {
    messages.put(inboxMessage.getOperationId(), inboxMessage);
  }
}
