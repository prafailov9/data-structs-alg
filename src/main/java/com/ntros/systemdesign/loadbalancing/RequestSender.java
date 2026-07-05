package com.ntros.systemdesign.loadbalancing;

import com.ntros.systemdesign.IdSequencer;
import com.ntros.systemdesign.LoadBalancer;
import com.ntros.systemdesign.loadbalancing.data.HttpMethod;
import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.CancellationToken;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RequestSender implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(RequestSender.class);

  private final int senderId;
  private final Thread senderThread;
  private final LoadBalancer loadBalancer;
  private final int sendDelayMs;
  private final List<String> knownHosts;
  private final List<String> serviceKeys;
  private final Random rng;
  private final CancellationToken token;

  RequestSender(
      int senderId,
      LoadBalancer loadBalancer,
      int sendDelayMs,
      List<String> knownHosts,
      List<String> serviceKeys,
      Random rng,
      CancellationToken token) {
    this.senderId = senderId;
    this.loadBalancer = loadBalancer;
    this.sendDelayMs = sendDelayMs;
    this.knownHosts = knownHosts;
    this.serviceKeys = serviceKeys;
    this.rng = rng;
    this.token = token;

    senderThread = new Thread(this, "sender-" + senderId);
  }

  void start() {
    senderThread.start();
  }

  void awaitTermination() throws InterruptedException {
    senderThread.join();
  }

  @Override
  public void run() {
    // use only 1 host for now;
    String host = knownHosts.getFirst();
    while (!token.isCancelled()) {

      // build request
      String path = serviceKeys.get(rng.nextInt(serviceKeys.size()));
      var response = buildAndSendRequest(host, path);
      log.info("received {}", response);
      // wait for a short period
      if (!waitForNextSendPeriod()) {
        log.info("Interrupted while waiting on sent. Exiting");
        return;
      }
    }

    log.info("Termination signal received. Exiting...");
  }

  private HttpResponse buildAndSendRequest(String host, String servicePath) {
    String payload = UUID.randomUUID().toString();
    HttpRequest request =
        new HttpRequest(
            IdSequencer.nextHttpRequestId(), HttpMethod.GET, Map.of(), host, servicePath, payload);

    return loadBalancer.accept(request);
  }

  private boolean waitForNextSendPeriod() {
    try {
      Thread.sleep(sendDelayMs);
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
