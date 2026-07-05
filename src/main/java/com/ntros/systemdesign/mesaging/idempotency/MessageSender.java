package com.ntros.systemdesign.mesaging.idempotency;

import com.ntros.systemdesign.mesaging.idempotency.inbox.InboxConnector;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.CancellationToken;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.OperationId;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.PaymentRequestPayload;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.ExecutionContext;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSender implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
  private final int workerId;
  private final InboxConnector inboxConnector;
  private final CancellationToken token;
  private final Thread worker;
  private final List<String> accountIds;
  private final Random rng;
  private final int sendDelayMs;
  private final List<ServerResponse> processedMessages;
  private final Object processedLock;

  public MessageSender(
      int workerId, ExecutionContext executionContext, CancellationToken cancellationToken) {
    this.workerId = workerId;
    this.inboxConnector = executionContext.connector();
    this.token = cancellationToken;
    this.accountIds = executionContext.accountIds();
    this.rng = executionContext.rng();
    this.sendDelayMs = executionContext.delayMs();
    processedMessages = executionContext.processedMessages();
    processedLock = executionContext.processedLock();

    worker = new Thread(this, "inbox-connector-" + workerId);
  }

  @Override
  public void run() {
    while (!token.isCancelled()) {
      // prepare and send the message
      InboxMessage inboxMessage;

      // 10% of the time, send a processed message
      InboxMessage existingProcessedMessage = null;

      synchronized (processedLock) {
        if (!processedMessages.isEmpty()) {
          existingProcessedMessage =
              processedMessages.get(rng.nextInt(processedMessages.size())).inboxMessage();
        }
      }

      if (existingProcessedMessage != null && rng.nextFloat() <= 0.50f) {
        inboxMessage = existingProcessedMessage;
      } else {
        String requestorId = UUID.randomUUID().toString();
        OperationId operationId = OperationId.ofPayment(requestorId);
        PaymentRequestPayload payload = createPaymentRequestPayload(1, 30);
        inboxMessage = InboxMessage.of(requestorId, operationId, payload);
      }

      inboxConnector.send(inboxMessage);
      if (!waitForNextSendSlot()) {
        log.info("Interrupted while waiting on next send slot. exiting...");
        return;
      }
    }
    log.info("Sender exiting.");
  }

  public void start() {
    worker.start();
  }

  public void awaitTermination() throws InterruptedException {
    worker.join();
  }

  private boolean waitForNextSendSlot() {
    try {
      Thread.sleep(sendDelayMs);
      return true;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();

      return false;
    }
  }

  private PaymentRequestPayload createPaymentRequestPayload(long moneyOrigin, long moneyBound) {
    int senderIdx = rng.nextInt(accountIds.size());
    int receiverIdx = rng.nextInt(accountIds.size());
    while (senderIdx == receiverIdx) {
      receiverIdx = rng.nextInt(accountIds.size());
    }

    String senderId = accountIds.get(senderIdx);
    String receiverId = accountIds.get(receiverIdx);

    return new PaymentRequestPayload(senderId, receiverId, rng.nextLong(moneyOrigin, moneyBound));
  }
}
