package com.ntros.systemdesign.ratelimiting.leakybucket;

import static com.ntros.systemdesign.data.ResponseBuilder.buildAccepted;
import static com.ntros.systemdesign.data.ResponseBuilder.buildTermination;

import com.ntros.systemdesign.ratelimiting.Service;
import com.ntros.systemdesign.ratelimiting.leakybucket.analytics.Stats;
import com.ntros.systemdesign.ratelimiting.message.Message;
import com.ntros.systemdesign.data.Response;
import com.ntros.systemdesign.data.ResponseBuilder;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeakyBucketService implements Service {

  private static final Logger log = LoggerFactory.getLogger(LeakyBucketService.class);

  private final List<FixedProcessor> processors;
  private final List<MessageChannel> channels = new ArrayList<>();
  private final List<Stats> statsByClient = new ArrayList<>();

  // int clientCount, int requestsPerSecond, int intervalMs
  public LeakyBucketService(int clientCount, int maxRequests, int intervalMs) {
    log.info("initializing service...");
    validate(clientCount, maxRequests, intervalMs);

    processors = new ArrayList<>();
    for (int clientId = 1; clientId <= clientCount; clientId++) {
      // Each client has its own isolated channel + stats + processor thread, accessed by id - 1
      var clientStats = Stats.ofEmpty();
      statsByClient.add(clientStats);

      var channel = MessageChannel.ofChannel(maxRequests);
      channels.add(channel);
      processors.add(new FixedProcessor(channel, clientStats, clientId, intervalMs));
    }

    log.info("starting processors...");
    for (var p : processors) {
      p.start();
    }
  }

  @Override
  public Response submit(Message message) {
    var request = message.request();
    if (request == null) {
      return ResponseBuilder.buildEmptyRequestReceived();
    }

    // TODO: submit() should not receive termination messages. Rewrite Message class
    if (message.terminate()) {
      stopWorker(message);
      return buildTermination(request);
    }

    int idx = request.clientId() - 1;
    var channel = channels.get(idx);
    if (!channel.tryOffer(message)) {
      log.info("0: REJECTED");
      statsByClient.get(idx).incrementRejected();
      return ResponseBuilder.buildRateLimited(request);
    }
    log.info("1: ACCEPTED");
    statsByClient.get(idx).incrementAccepted();
    return buildAccepted(request);
  }

  @Override
  public boolean disconnect(int clientId) {
    var idx = clientId - 1;
    var channel = channels.get(idx);
    channel.forceOffer(Message.ofPoison());
    var p = processors.get(idx);
    try {
      p.awaitTermination();
    } catch (InterruptedException e) {
      return false;
    }
    return true;
  }

  @Override
  public List<Stats> getApiStats() {
    return statsByClient;
  }

  private void stopWorker(Message poison) {
    var idx = poison.request().clientId() - 1;
    var channel = channels.get(idx);
    channel.forceOffer(poison);
    var p = processors.get(idx);
    try {
      p.awaitTermination();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void validate(int clientCount, int maxRequests, int intervalMs) {

    if (clientCount <= 0 || maxRequests <= 0 || intervalMs <= 0) {
      throw new IllegalArgumentException(
          String.format(
              "Client count, maximum requests and the interval must be larger than 0. clientCount: %s, maxRequests: %s, intervalMs: %s",
              clientCount, maxRequests, intervalMs));
    }
  }
}
