package com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate;

import com.ntros.systemdesign.mesaging.dualchannel.DualChannel;

public record RuntimeContext(DualChannel dualMessageChannel, Object dbLock, int receivers) {}
