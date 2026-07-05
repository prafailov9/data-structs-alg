package com.ntros.systemdesign;

import java.util.concurrent.atomic.AtomicInteger;

public class IdSequencer {

  private static final AtomicInteger TARGET_ID_COUNTER = new AtomicInteger(1);
  private static final AtomicInteger HTTP_RESPONSE_ID_COUNTER = new AtomicInteger(1);
  private static final AtomicInteger HTTP_REQUEST_ID_COUNTER = new AtomicInteger(1);

  private static final AtomicInteger TRADER_ID_COUNTER = new AtomicInteger(1);
  private static final AtomicInteger ACCOUNT_ID_COUNTER = new AtomicInteger(1);
  private static final AtomicInteger PORTFOLIO_ID_COUNTER = new AtomicInteger(1);
  private static final AtomicInteger ORDER_FLOW_ID_COUNTER = new AtomicInteger(1);
  private static final AtomicInteger HOLDING_ID_COUNTER = new AtomicInteger(1);

  public static int nextTargetId() {
    return TARGET_ID_COUNTER.getAndIncrement();
  }

  public static int nextHttpResponseId() {
    return HTTP_RESPONSE_ID_COUNTER.getAndIncrement();
  }

  public static int nextHttpRequestId() {
    return HTTP_REQUEST_ID_COUNTER.getAndIncrement();
  }

  public static int nextTraderId() {
    return TRADER_ID_COUNTER.getAndIncrement();
  }

  public static int nextAccountId() {
    return ACCOUNT_ID_COUNTER.getAndIncrement();
  }

  public static int nextPortfolioId() {
    return PORTFOLIO_ID_COUNTER.getAndIncrement();
  }

  public static int nextOrderFlowId() {
    return ORDER_FLOW_ID_COUNTER.getAndIncrement();
  }

  public static int nextHoldingId() {
    return HOLDING_ID_COUNTER.getAndIncrement();
  }
}
