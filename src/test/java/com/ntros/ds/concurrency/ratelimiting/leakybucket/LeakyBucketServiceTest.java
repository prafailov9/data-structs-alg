package com.ntros.ds.concurrency.ratelimiting.leakybucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ntros.ds.concurrency.ratelimiting.AbstractRateLimitedServiceTest;
import com.ntros.systemdesign.ratelimiting.Service;
import com.ntros.ds.concurrency.ratelimiting.ClientWorker;
import com.ntros.systemdesign.ratelimiting.leakybucket.LeakyBucketService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LeakyBucketServiceTest extends AbstractRateLimitedServiceTest {

  private static final Logger log = LoggerFactory.getLogger(LeakyBucketServiceTest.class);
  private static final int MAX_REQUESTS_PER_CLIENT = 100;
  private static final int CLIENT_COUNT = 3;
  private static final int TOTAL_REQUESTS = MAX_REQUESTS_PER_CLIENT * CLIENT_COUNT;
  private static final int REQUESTS_PER_SECOND = 3;
  private static final int READ_DELAY_MS = 1000 / REQUESTS_PER_SECOND;
  private final Service leakyBucketService =
      new LeakyBucketService(CLIENT_COUNT, REQUESTS_PER_SECOND, READ_DELAY_MS);

  @Test
  public void leakyBucket_EqualRateTest() {
    log.info(
        "Starting sim. Client threads: {}, requests per second: {}, interval: {} ms",
        CLIENT_COUNT,
        REQUESTS_PER_SECOND,
        READ_DELAY_MS);

    ClientWorker[] clientWorkers =
        buildClientWorkers(
            leakyBucketService,
            CLIENT_COUNT,
            MAX_REQUESTS_PER_CLIENT,
            READ_DELAY_MS);

    startAndWaitTermination(clientWorkers);

    int accepted = accumulateFunctionTotal(leakyBucketService, x -> x.getAccepted().get());
    int rejected = accumulateFunctionTotal(leakyBucketService, x -> x.getRejected().get());
    logStats(accepted, rejected);

    assertTrue(accepted < TOTAL_REQUESTS);
    assertTrue(rejected > 0);
  }

  @Test
  public void leakyBucket_SendRateOverLimitTest() {
    int requestsPerSecond = 10;

    ClientWorker[] clientWorkers =
        buildClientWorkers(
            leakyBucketService, CLIENT_COUNT, MAX_REQUESTS_PER_CLIENT, 1000 / requestsPerSecond);

    startAndWaitTermination(clientWorkers);

    int accepted = accumulateFunctionTotal(leakyBucketService, x -> x.getAccepted().get());
    int rejected = accumulateFunctionTotal(leakyBucketService, x -> x.getRejected().get());
    logStats(accepted, rejected);

    assertTrue(accepted < TOTAL_REQUESTS);
    assertTrue(rejected > 0);
  }

  @Test
  public void leakyBucket_SendRateUnderLimitTest() {
    int requestsPerSecond = 1;

    ClientWorker[] clientWorkers =
        buildClientWorkers(
            leakyBucketService, CLIENT_COUNT, MAX_REQUESTS_PER_CLIENT, 1000 / requestsPerSecond);

    startAndWaitTermination(clientWorkers);

    int accepted = accumulateFunctionTotal(leakyBucketService, x -> x.getAccepted().get());
    int rejected = accumulateFunctionTotal(leakyBucketService, x -> x.getRejected().get());
    logStats(accepted, rejected);

    assertEquals(TOTAL_REQUESTS, accepted);
    assertEquals(0, rejected);
  }
}
