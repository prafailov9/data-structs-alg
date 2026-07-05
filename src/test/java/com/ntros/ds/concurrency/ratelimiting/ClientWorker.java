package com.ntros.ds.concurrency.ratelimiting;

import com.ntros.systemdesign.ratelimiting.Service;
import com.ntros.systemdesign.ratelimiting.message.Message;
import com.ntros.systemdesign.data.Request;
import com.ntros.systemdesign.data.Response;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientWorker implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(ClientWorker.class);
  private final int clientId;
  private final Thread worker;
  private final Service service;
  private final int totalRequests;
  private final int sendDelayMs;

  public ClientWorker(int clientId, Service service, int totalRequests, int sendDelayMs) {
    this.clientId = clientId;
    this.service = service;
    this.totalRequests = totalRequests;
    this.sendDelayMs = sendDelayMs;
    worker = new Thread(this, "client-" + clientId);
  }

  public int getClientId() {
    return clientId;
  }

  /**
   * Send requests to the service at every step until totalRequests, then disconnect from the
   * service.
   */
  @Override
  public void run() {
    int requestId = 1;
    while (requestId <= totalRequests) {
      try {
        Thread.sleep(sendDelayMs);
        Response response =
            service.submit(
                Message.ofRequest(new Request(requestId, clientId, UUID.randomUUID().toString())));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      requestId++;
    }
    boolean success = service.disconnect(clientId);
    if (!success) {
      throw new RuntimeException("Could not disconnect client from service");
    }
  }

  public void start() {
    worker.start();
  }

  public void join() throws InterruptedException {
    worker.join();
  }
}
