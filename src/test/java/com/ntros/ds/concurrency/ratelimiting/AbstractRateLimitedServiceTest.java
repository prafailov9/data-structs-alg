package com.ntros.ds.concurrency.ratelimiting;

import com.ntros.systemdesign.ratelimiting.Service;
import com.ntros.systemdesign.ratelimiting.leakybucket.analytics.Stats;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRateLimitedServiceTest {

  private static final Logger log = LoggerFactory.getLogger(AbstractRateLimitedServiceTest.class);

  protected ClientWorker[] buildClientWorkers(
      Service service, int clientCount, int maxRequestsPerClient, int sendDelayMs) {
    ClientWorker[] clients = new ClientWorker[clientCount];
    for (int i = 0; i < clientCount; i++) {
      clients[i] = new ClientWorker(i + 1, service, maxRequestsPerClient, sendDelayMs);
    }
    return clients;
  }

  protected int accumulateFunctionTotal(Service service, Function<Stats, Integer> f) {
    return service.getApiStats().stream().map(f).reduce(0, Integer::sum);
  }

  protected void startAndWaitTermination(ClientWorker[] clients) {
    log.info("Starting client threads...");
    long start = System.nanoTime();
    for (var c : clients) {
      c.start();
    }

    for (var c : clients) {
      try {
        c.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    long end = System.nanoTime();
    log.info("Sim finished. Elapsed time: {} seconds", (end - start) / 1_000_000_000);
  }

  protected void logStats(int accepted, int rejected) {
    log.info("Stats [accepted: {}; rate-limited: {}]", accepted, rejected);
  }
}
