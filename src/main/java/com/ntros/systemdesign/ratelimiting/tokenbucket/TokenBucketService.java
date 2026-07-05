package com.ntros.systemdesign.ratelimiting.tokenbucket;

import com.ntros.systemdesign.ratelimiting.Processor;
import com.ntros.systemdesign.ratelimiting.Service;
import com.ntros.systemdesign.ratelimiting.leakybucket.analytics.Stats;
import com.ntros.systemdesign.ratelimiting.message.Message;
import com.ntros.systemdesign.data.Response;
import com.ntros.systemdesign.data.ResponseBuilder;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenBucketService implements Service {

  private static final Logger log = LoggerFactory.getLogger(TokenBucketService.class);

  private final List<TokenBucket> buckets = new ArrayList<>();
  private final List<Processor> processors = new ArrayList<>();
  private final List<Stats> clientStats = new ArrayList<>();

  public TokenBucketService(int clientCount, int requestsPerSecond, int refillIntervalMs) {
    log.info("Initialising TokenBucketService...");
    for (int clientId = 1; clientId <= clientCount; clientId++) {
      clientStats.add(Stats.ofEmpty());
      var bucket = new TokenBucket(requestsPerSecond);
      buckets.add(bucket);
      processors.add(new TokenProcessor(clientId, bucket, refillIntervalMs));
    }
    // TODO: use executionState

    log.info("Starting bucket workers...");
    for (var t : processors) {
      t.start();
    }
  }

  @Override
  public Response submit(Message message) throws InterruptedException {
    // discard/termination checks
    if (message == null || message.request() == null) {
      return ResponseBuilder.buildEmptyRequestReceived();
    }
    // TODO: submit() path should not receive termination signals. Rewrite Message class
    if (message.terminate()) {
      stopWorker(message);
      return ResponseBuilder.buildTermination(message.request());
    }

    var idx = toIndex(message.request().clientId(), buckets.size());
    var bucket = buckets.get(idx);

    if (!bucket.tryAcquire()) {
      clientStats.get(idx).incrementRejected();
      log.info("RATE_LIMITED");
      return ResponseBuilder.buildRateLimited(message.request());
    }
    clientStats.get(idx).incrementAccepted();
    log.info("ACCEPTED");
    return ResponseBuilder.buildAccepted(message.request());
  }

  @Override
  public boolean disconnect(int clientId) {
    int idx = toIndex(clientId, processors.size());
    var worker = processors.get(idx);
    boolean success;
    try {
      worker.awaitTermination();
      success = true;
    } catch (InterruptedException ex) {
      success = false;
    }
    return success;
  }

  private void stopWorker(Message message) {
    int clientId = message.request().clientId();
    var success = disconnect(clientId);
    if (!success) {
      throw new IllegalStateException(
          String.format("Could not shut down worker with clientId:%s", clientId));
    }
  }

  @Override
  public List<Stats> getApiStats() {
    return clientStats;
  }

  private int toIndex(int id, int size) {
    int idx = id - 1;
    if (idx < 0 || idx >= size) {
      throw new IllegalArgumentException("Unknown id: " + id);
    }
    return idx;
  }
}
