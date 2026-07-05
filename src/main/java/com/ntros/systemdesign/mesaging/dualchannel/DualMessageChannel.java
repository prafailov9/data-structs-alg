package com.ntros.systemdesign.mesaging.dualchannel;

import com.ntros.ds.queue.Queue;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DualMessageChannel implements DualChannel {

  private static final Logger log = LoggerFactory.getLogger(DualMessageChannel.class);
  private final Queue<InboxMessage> requestQueue;
  private final Queue<ServerResponse> responseQueue;

  private final Object responseLock;
  private final Object requestLock;

  private final int requestCapacity;
  private final int responseCapacity;


  public DualMessageChannel(ChannelSettings channelSettings) {
    if (channelSettings == null) {
      throw new IllegalArgumentException("Empty channel settings");
    }
    requestLock = channelSettings.requestLock();
    responseLock = channelSettings.responseLock();
    requestQueue = channelSettings.requestQueue();
    responseQueue = channelSettings.responseQueue();
    requestCapacity = channelSettings.requestCapacity();
    responseCapacity = channelSettings.responseCapacity();
  }

  @Override
  public boolean tryOfferRequest(InboxMessage inboxMessage) {
    synchronized (requestLock) {
      while (requestQueue.size() == requestCapacity) {
        try {
          requestLock.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.info("Interrupted while inbound full. Exiting...");
          return false;
        }
      }
      requestQueue.add(inboxMessage);
      requestLock.notifyAll();
      return true;
    }
  }

  @Override
  public void forceOfferRequest(InboxMessage inboxMessage) {
    synchronized (requestLock) {
      requestQueue.add(inboxMessage);
      requestLock.notifyAll();
    }
  }

  @Override
  public InboxMessage takeRequest() {
    synchronized (requestLock) {
      while (requestQueue.isEmpty()) {
        try {
          requestLock.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.info("Interrupted inbound while empty. Exiting...");
          return null;
        }
      }

      var msg = requestQueue.remove();
      requestLock.notifyAll();
      return msg;
    }
  }

  @Override
  public boolean tryOfferResponse(ServerResponse responseMessage) {
    synchronized (responseLock) {
      while (responseQueue.size() == responseCapacity) {
        try {
          responseLock.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.info("Interrupted while full. Exiting...");
          return false;
        }
      }

      responseQueue.add(responseMessage);
      responseLock.notifyAll();
      return true;
    }
  }

  @Override
  public void forceOfferResponse(ServerResponse responseMessage) {
    synchronized (responseLock) {
      responseQueue.add(responseMessage);
      responseLock.notifyAll();
    }
  }

  @Override
  public ServerResponse takeResponse() {
    synchronized (responseLock) {
      while (responseQueue.isEmpty()) {
        try {
          responseLock.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.info("Interrupted outbound while empty. Exiting...");
          return null;
        }
      }

      var serverResponse = responseQueue.remove();
      responseLock.notifyAll();
      return serverResponse;
    }
  }
}
