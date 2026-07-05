package com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload;

public class PaymentResponsePayload implements Payload {

  private final ResponseStatus responseStatus;
  private final int statusCode;
  private final boolean success;
  private final String description;

  public PaymentResponsePayload(
      ResponseStatus responseStatus, int statusCode, boolean success, String description) {
    this.responseStatus = responseStatus;
    this.statusCode = statusCode;
    this.success = success;
    this.description = description;
  }

  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "PaymentResponsePayload{"
        + "responseStatus="
        + responseStatus
        + ", statusCode="
        + statusCode
        + ", success="
        + success
        + ", description='"
        + description
        + '\''
        + '}';
  }
}
