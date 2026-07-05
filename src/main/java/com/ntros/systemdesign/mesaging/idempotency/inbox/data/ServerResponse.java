package com.ntros.systemdesign.mesaging.idempotency.inbox.data;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.ResponseStatus;

public record ServerResponse(InboxMessage inboxMessage, ResponseStatus status, int code, String description) {

  public static ServerResponse ack(InboxMessage inboxMessage, String description) {
    return new ServerResponse(inboxMessage, ResponseStatus.ACK, 250, description);
  }

  public static ServerResponse serverError(InboxMessage inboxMessage, String description) {
    return new ServerResponse(inboxMessage, ResponseStatus.FAILED, 500, description);
  }

}
