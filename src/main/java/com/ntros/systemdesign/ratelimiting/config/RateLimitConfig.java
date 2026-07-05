package com.ntros.systemdesign.ratelimiting.config;

public class RateLimitConfig {

  private final int clientCount;
  private final int requestsPerSecond;
  private final int actEveryMs;

  public RateLimitConfig(int clientCount, int requestsPerSecond, int actEveryMs) {
    this.clientCount = clientCount;
    this.requestsPerSecond = requestsPerSecond;
    this.actEveryMs = actEveryMs;
  }

  public int getClientCount() {
    return clientCount;
  }

  public int getRequestsPerSecond() {
    return requestsPerSecond;
  }

  public int getActEveryMs() {
    return actEveryMs;
  }
}
