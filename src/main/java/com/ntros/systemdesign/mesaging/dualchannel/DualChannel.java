package com.ntros.systemdesign.mesaging.dualchannel;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;

public interface DualChannel {

  boolean tryOfferRequest(InboxMessage inboxMessage);

  void forceOfferRequest(InboxMessage inboxMessage);

  InboxMessage takeRequest();

  boolean tryOfferResponse(ServerResponse responseMessage);

  void forceOfferResponse(ServerResponse responseMessage);

  ServerResponse takeResponse();
}
