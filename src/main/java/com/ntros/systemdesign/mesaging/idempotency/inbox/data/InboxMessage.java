package com.ntros.systemdesign.mesaging.idempotency.inbox.data;

import static com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.ResponseStatus.ACK;
import static com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.ResponseStatus.FAILED;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.Payload;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.PaymentRequestPayload;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.PaymentResponsePayload;

public class InboxMessage {
  private static final String POISON_SIGNAL = "POISON_SIGNAL";
  private static final InboxMessage POISON_MESSAGE =
      new InboxMessage(
          "SYSTEM_SHUTDOWN_REQUEST",
          OperationId.ofSystem(POISON_SIGNAL),
          new PaymentRequestPayload(POISON_SIGNAL, POISON_SIGNAL, -1));

  private final String requestorId;
  private final OperationId operationId;
  private final Payload payload;

  private InboxMessage(String requestorId, OperationId operationId, Payload payload) {
    this.requestorId = requestorId;
    this.operationId = operationId;
    this.payload = payload;
  }

  public static InboxMessage of(String requestorId, OperationId operationId, Payload payload) {
    return new InboxMessage(requestorId, operationId, payload);
  }

  public static InboxMessage ofAck(String requestorId, OperationId operationId) {
    return new InboxMessage(
        requestorId,
        operationId,
        new PaymentResponsePayload(ACK, 111, true, "request acknowledged"));
  }

  public static InboxMessage ofServerError(String requestorId, int code, String desc) {
    return new InboxMessage(
        requestorId, null, new PaymentResponsePayload(FAILED, code, false, desc));
  }

  public static InboxMessage ofPoison() {
    return POISON_MESSAGE;
  }

  public String getRequestorId() {
    return requestorId;
  }

  public OperationId getOperationId() {
    return operationId;
  }

  public Payload getPayload() {
    return payload;
  }

  @Override
  public String toString() {
    return "Message{" + "operationId=" + operationId + ", payload=" + payload + '}';
  }
}
