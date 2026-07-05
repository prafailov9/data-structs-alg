package com.ntros.systemdesign.mesaging.idempotency;

import com.ntros.systemdesign.mesaging.idempotency.inbox.InboxConnector;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.ResponseStatus;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.ExecutionContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceiver implements Runnable, RunnableLifecycle {

  private static final Logger log = LoggerFactory.getLogger(MessageReceiver.class);
  private final int receiverId;
  private final Thread receiverThread;
  private final InboxConnector connector;
  private final int readDelayMs;
  private final List<ServerResponse> processedMessages;
  private final Object processedLock;

  public MessageReceiver(int id, ExecutionContext executionContext) {
    if (executionContext == null) {
      throw new IllegalArgumentException("Empty execution context");
    }
    receiverId = id;
    receiverThread = new Thread(this, "msg-receiver-" + receiverId);

    connector = executionContext.connector();
    readDelayMs = executionContext.delayMs();
    processedMessages = executionContext.processedMessages();
    processedLock = executionContext.processedLock();
  }

  @Override
  public void run() {
    while (true) {
      // read at given interval
      if (!waitForNextRead()) {
        return;
      }
      var response = connector.receive();
      var message = response.inboxMessage();
      if (message.equals(InboxMessage.ofPoison())) {
        log.info("Received Termination signal. Exiting");
        return;
      }

      if (response.status() == ResponseStatus.ACK && response.code() != 500) {
        synchronized (processedLock) {
          processedMessages.add(response);
        }
      }
    }
  }

  @Override
  public void start() {
    receiverThread.start();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    receiverThread.join();
  }

  private boolean waitForNextRead() {
    try {
      Thread.sleep(readDelayMs);
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
