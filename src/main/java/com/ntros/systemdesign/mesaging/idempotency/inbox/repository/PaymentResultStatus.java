package com.ntros.systemdesign.mesaging.idempotency.inbox.repository;

public enum PaymentResultStatus {
  PROCESSED,
  DUPLICATE,
  FAILED
}
