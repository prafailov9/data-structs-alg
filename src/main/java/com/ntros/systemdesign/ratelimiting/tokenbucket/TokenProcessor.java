package com.ntros.systemdesign.ratelimiting.tokenbucket;

import com.ntros.systemdesign.ratelimiting.Processor;

public class TokenProcessor implements Processor {

  private final Thread worker;
  private final TokenBucket bucket;
  private final int refillIntervalMs;
  private volatile boolean running = true;

  public TokenProcessor(int ownerId, TokenBucket bucket, int refillIntervalMs) {
    if (refillIntervalMs < 1) {
      throw new IllegalArgumentException(
          String.format("Invalid refill interval: %s", refillIntervalMs));
    }
    this.worker = new Thread(this, "worker-" + ownerId);
    this.bucket = bucket;
    this.refillIntervalMs = refillIntervalMs;
  }

  /** Refill one at each step and wait for given interval. */
  @Override
  public void run() {
    while (running) {
      bucket.refill();
      if (!waitForNextRefillSlot()) {
        cancel();
      }
    }
  }

  private boolean waitForNextRefillSlot() {
    try {
      Thread.sleep(refillIntervalMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
    return true;
  }

  @Override
  public void start() {
    worker.start();
  }

  private void cancel() {
    running = false;
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    cancel();
    // interrupt worker to wake it up if waiting
    worker.interrupt();
    worker.join();
  }
}
