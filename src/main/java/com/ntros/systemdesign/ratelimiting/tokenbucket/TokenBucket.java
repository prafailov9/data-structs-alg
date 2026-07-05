package com.ntros.systemdesign.ratelimiting.tokenbucket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenBucket {

  private static final Logger log = LoggerFactory.getLogger(TokenBucket.class);

  private int availableTokens;
  private final Object lock = new Object();
  private final int bucketCapacity;

  public TokenBucket(int bucketCapacity) {
    if (bucketCapacity <= 0) {
      throw new IllegalArgumentException("wtf");
    }
    this.bucketCapacity = availableTokens = bucketCapacity;
  }

  public void refill() {
    synchronized (lock) {
      if (availableTokens == bucketCapacity) {
        return;
      }
      availableTokens++;
    }
  }

  public boolean tryAcquire() {
    synchronized (lock) {
      if (availableTokens == 0) {
        return false;
      }
      availableTokens--;
      return true;
    }
  }
}
