package com.ntros.systemdesign.mesaging.dualchannel;

import com.ntros.ds.queue.LinkedQueue;
import com.ntros.ds.queue.Queue;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;

public record ChannelSettings(
    Object requestLock,
    Object responseLock,
    Queue<InboxMessage> requestQueue,
    Queue<ServerResponse> responseQueue,
    int requestCapacity,
    int responseCapacity) {

  public static ChannelSettings ofSettings(int requestCapacity, int responseCapacity) {
    return new ChannelSettings(
        new Object(),
        new Object(),
        new LinkedQueue<>(),
        new LinkedQueue<>(),
        requestCapacity,
        responseCapacity);
  }
}
