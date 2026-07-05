package com.ntros.systemdesign.mesaging.idempotency.inbox;

import com.ntros.systemdesign.mesaging.dualchannel.DualChannel;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.RuntimeContext;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboxPaymentConnector implements InboxConnector {

  private static final Logger log = LoggerFactory.getLogger(InboxPaymentConnector.class);
  private final DualChannel channel;
  private final InboxProcessor processor;

  public InboxPaymentConnector(RuntimeContext context) {
    if (context == null) {
      throw new IllegalArgumentException("Empty exec context");
    }
    channel = context.dualMessageChannel();
    processor = new InboxPaymentProcessor(context);

    processor.start();
  }

  @Override
  public void send(InboxMessage inboxMessage) {
    if (!channel.tryOfferRequest(inboxMessage)) {
      log.info("Failed to publish: {}", inboxMessage);
    }
  }

  @Override
  public ServerResponse receive() {
    return channel.takeResponse();
  }

  @Override
  public boolean shutdown() {
    channel.forceOfferRequest(InboxMessage.ofPoison());
    try {
      processor.awaitTermination();
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
