package com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate;

import com.ntros.systemdesign.mesaging.idempotency.inbox.InboxConnector;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import java.util.List;
import java.util.Random;

public record ExecutionContext(
    InboxConnector connector,
    List<String> accountIds,
    Random rng,
    int delayMs,
    List<ServerResponse> processedMessages,
    Object processedLock) {}
