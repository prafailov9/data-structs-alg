package com.ntros.systemdesign.mesaging.idempotency.inbox;

import com.ntros.systemdesign.mesaging.dualchannel.DualChannel;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.ResponseStatus;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.RuntimeContext;
import com.ntros.systemdesign.mesaging.idempotency.inbox.repository.InMemoryDatabase;
import com.ntros.systemdesign.mesaging.idempotency.inbox.repository.PaymentResult;
import com.ntros.systemdesign.mesaging.idempotency.inbox.repository.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboxPaymentProcessor implements InboxProcessor, Runnable {

  private static final Logger log = LoggerFactory.getLogger(InboxPaymentProcessor.class);

  private final InMemoryDatabase database = PersistenceContext.database();

  private final DualChannel channel;
  private final Thread procThread;
  private final int receiversCount;

  public InboxPaymentProcessor(RuntimeContext runtimeContext) {
    if (runtimeContext == null) {
      throw new IllegalArgumentException("Empty exec context.");
    }

    channel = runtimeContext.dualMessageChannel();
    procThread = new Thread(this, "msg-processor");
    receiversCount = runtimeContext.receivers();
  }

  @Override
  public void run() {
    process();
  }

  @Override
  public void process() {
    while (true) {
      var message = channel.takeRequest();

      if (message == null) {
        log.info("Interrupted while reading inbound requests. Sending error state and exiting...");

        var response =
            new ServerResponse(
                InboxMessage.ofServerError("NO_REQUESTOR", -111, "Server failed to read message"),
                ResponseStatus.FAILED,
                -111,
                "Server failed to read message");

        channel.forceOfferResponse(response);
        return;
      }

      if (message.equals(InboxMessage.ofPoison())) {
        log.info("Termination signal received. Exiting...");

        for (int i = 1; i <= receiversCount; i++) {
          channel.forceOfferResponse(
              ServerResponse.ack(InboxMessage.ofPoison(), "request acknowledged"));
        }

        return;
      }

      PaymentResult result = database.processPayment(message);

      if (result.isProcessed()) {
        if (result.duplicate()) {
          log.info("Duplicate message skipped: {}", result.message());
          channel.forceOfferResponse(
              ServerResponse.ack(result.message(), "message already processed"));
        } else {
          log.info("Message processed. ACK");
          channel.forceOfferResponse(ServerResponse.ack(result.message(), result.description()));
        }
        continue;
      }

      if (result.isFailed()) {
        if (result.duplicate()) {
          log.info("Duplicate failed message skipped: {}", result.message());
        } else {
          log.info("Message failed: {}", result.description());
        }

        channel.forceOfferResponse(
            ServerResponse.serverError(result.message(), result.description()));
      }
    }
  }

  @Override
  public void start() {
    procThread.start();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    procThread.join();
  }
}
