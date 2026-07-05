package com.ntros.ds.concurrency.ratelimiting.tokenbucket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ntros.ds.concurrency.ratelimiting.AbstractRateLimitedServiceTest;
import com.ntros.ds.concurrency.ratelimiting.ClientWorker;
import com.ntros.systemdesign.ratelimiting.Service;
import com.ntros.systemdesign.ratelimiting.tokenbucket.TokenBucketService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TokenBucketServiceTest extends AbstractRateLimitedServiceTest {

  private static final Logger log = LoggerFactory.getLogger(TokenBucketServiceTest.class);
  private static final int MAX_REQUESTS_PER_CLIENT = 100;
  private static final int CLIENT_COUNT = 3;
  private static final int TOTAL_REQUESTS = MAX_REQUESTS_PER_CLIENT * CLIENT_COUNT;
  private static final int REQUESTS_PER_SECOND = 3;
  private static final int REFILL_INTERVAL_MS = 1000 / REQUESTS_PER_SECOND;

  private final Service tokenBucketService =
      new TokenBucketService(CLIENT_COUNT, REQUESTS_PER_SECOND, REFILL_INTERVAL_MS);

  @Test
  public void tokenBucket_EqualRateTest() {
    // production to consumption is 1:1 => there should always be tokens available, with some
    // jitter.

    ClientWorker[] clients =
        buildClientWorkers(
            tokenBucketService, CLIENT_COUNT, MAX_REQUESTS_PER_CLIENT, REFILL_INTERVAL_MS);

    startAndWaitTermination(clients);

    int accepted = accumulateFunctionTotal(tokenBucketService, x -> x.getAccepted().get());
    int rejected = accumulateFunctionTotal(tokenBucketService, x -> x.getRejected().get());
    logStats(accepted, rejected);

    assertEquals(TOTAL_REQUESTS, accepted);
    assertTrue(rejected <= CLIENT_COUNT);
  }

  @Test
  public void tokenBucket_SendRateOverLimitTest() {
    // more tokens consumed than produced => expect more rejections
    int requestsPerSecond = 10;
    ClientWorker[] clients =
        buildClientWorkers(
            tokenBucketService, CLIENT_COUNT, MAX_REQUESTS_PER_CLIENT, 1000 / requestsPerSecond);

    startAndWaitTermination(clients);

    int accepted = accumulateFunctionTotal(tokenBucketService, x -> x.getAccepted().get());
    int rejected = accumulateFunctionTotal(tokenBucketService, x -> x.getRejected().get());
    logStats(accepted, rejected);

    assertTrue(accepted < TOTAL_REQUESTS);
    assertTrue(rejected > 0);
  }

  @Test
  public void tokenBucket_SendRateBelowLimitTest() {
    // more tokens produced => ideally no rejections
    int requestsPerSecond = 1;
    ClientWorker[] clients =
        buildClientWorkers(
            tokenBucketService, CLIENT_COUNT, MAX_REQUESTS_PER_CLIENT, 1000 / requestsPerSecond);

    startAndWaitTermination(clients);

    int accepted = accumulateFunctionTotal(tokenBucketService, x -> x.getAccepted().get());
    int rejected = accumulateFunctionTotal(tokenBucketService, x -> x.getRejected().get());
    logStats(accepted, rejected);

    assertEquals(TOTAL_REQUESTS, accepted);
    assertEquals(0, rejected);
  }
}
