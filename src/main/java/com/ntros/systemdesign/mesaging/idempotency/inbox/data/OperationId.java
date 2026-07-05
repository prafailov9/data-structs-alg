package com.ntros.systemdesign.mesaging.idempotency.inbox.data;

import java.util.Objects;
import java.util.UUID;

public record OperationId(String paymentId, String orderId, String shipmentId, String systemOperationId) {
  private OperationId(String paymentId, String orderId, String shipmentId) {
    this(paymentId, orderId, shipmentId, UUID.randomUUID().toString());
  }

  public static OperationId ofPayment(String paymentId) {
    return new OperationId(paymentId, "NO_OP", "NO_OP");
  }

  public static OperationId ofSystem(String systemOperationId) {
    return new OperationId("NO_OP", "NO_OP", "NO_OP", systemOperationId);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof OperationId that)) return false;
    return Objects.equals(paymentId, that.paymentId)
        && Objects.equals(orderId, that.orderId)
        && Objects.equals(shipmentId, that.shipmentId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentId, orderId, shipmentId);
  }

  @Override
  public String toString() {
    return "OperationId{"
        + "paymentId='"
        + paymentId
        + '\''
        + ", orderId='"
        + orderId
        + '\''
        + ", shipmentId='"
        + shipmentId
        + '\''
        + '}';
  }
}
