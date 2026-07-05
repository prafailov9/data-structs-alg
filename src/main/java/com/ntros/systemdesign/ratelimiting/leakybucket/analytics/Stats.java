package com.ntros.systemdesign.ratelimiting.leakybucket.analytics;

import java.util.concurrent.atomic.AtomicInteger;

public class Stats {

  private final AtomicInteger accepted = new AtomicInteger(0);
  private final AtomicInteger rejected = new AtomicInteger(0);
  private final AtomicInteger processed = new AtomicInteger(0);

  private Stats() {}

  public static Stats ofEmpty() {
    return new Stats();
  }

  public void incrementAccepted() {
    accepted.incrementAndGet();
  }

  public AtomicInteger getAccepted() {
    return accepted;
  }

  public void incrementRejected() {
    rejected.incrementAndGet();
  }

  public AtomicInteger getRejected() {
    return rejected;
  }

  public void incrementProcessed() {
    processed.incrementAndGet();
  }

  public AtomicInteger getProcessed() {
    return processed;
  }

  @Override
  public String toString() {
    return "Stats{"
        + "accepted="
        + accepted.get()
        + ", rejected="
        + rejected.get()
        + ", processed="
        + processed.get()
        + '}';
  }
}
