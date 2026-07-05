package com.ntros.systemdesign.data;

import java.util.concurrent.atomic.AtomicInteger;

public final class ResponseBuilder {

  private static final String RATE_LIMITED_RESPONSE = "RATE_LIMITED";
  private static final String ACCEPTED_RESPONSE = "ACCEPTED";
  private static final String EMPTY_MESSAGE = "EMPTY_MESSAGE";
  private static final String EMPTY_REQUEST = "EMPTY_REQUEST";
  private static final String NO_CALLBACK = "NON";

  private static final AtomicInteger NEXT_RESPONSE_ID = new AtomicInteger(1);

  public static Response buildProcessed(Request request) {
    return new Response(
        NEXT_RESPONSE_ID.getAndIncrement(),
        request.requestId(),
        request.clientId(),
        true,
        "Processed successfully",
        NO_CALLBACK);
  }

  public static Response buildEmptyMessageReceived() {
    return new Response(
        NEXT_RESPONSE_ID.getAndIncrement(), -1, -1, false, EMPTY_MESSAGE, NO_CALLBACK);
  }

  public static Response buildEmptyRequestReceived() {
    return new Response(
        NEXT_RESPONSE_ID.getAndIncrement(), -1, -1, false, EMPTY_REQUEST, NO_CALLBACK);
  }

  public static Response buildRateLimited(Request request) {
    return new Response(
        NEXT_RESPONSE_ID.getAndIncrement(),
        request.requestId(),
        request.clientId(),
        false,
        RATE_LIMITED_RESPONSE,
        NO_CALLBACK);
  }

  public static Response buildAccepted(Request request) {
    int responseId = NEXT_RESPONSE_ID.getAndIncrement();
    return new Response(
        responseId,
        request.requestId(),
        request.clientId(),
        false,
        ACCEPTED_RESPONSE,
        "v1/processed/" + responseId);
  }

  public static Response buildTermination(Request request) {
    return new Response(
        NEXT_RESPONSE_ID.getAndIncrement(),
        request.requestId(),
        request.clientId(),
        true,
        "Worker stopped",
        NO_CALLBACK);
  }
}
