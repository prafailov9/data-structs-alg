package com.ntros.systemdesign.mesaging.idempotency.inbox.repository;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;

public record PaymentResult(
    Status status, InboxMessage message, String description, boolean duplicate) {

  public enum Status {
    PROCESSED,
    FAILED
  }

  public static PaymentResult processed(InboxMessage message) {
    return new PaymentResult(Status.PROCESSED, message, "message processed", false);
  }

  public static PaymentResult failed(InboxMessage message, String description) {
    return new PaymentResult(Status.FAILED, message, description, false);
  }

  public PaymentResult asDuplicate() {
    return new PaymentResult(status, message, description, true);
  }

  public boolean isProcessed() {
    return status == Status.PROCESSED;
  }

  public boolean isFailed() {
    return status == Status.FAILED;
  }
}
