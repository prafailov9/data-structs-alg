package com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload;

public class PaymentRequestPayload implements Payload {
  private final String senderId;
  private final String receiverId;

  private final long amount;

  public PaymentRequestPayload(String senderId, String receiverId, long amount) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.amount = amount;
  }

  public String getSenderId() {
    return senderId;
  }

  public String getReceiverId() {
    return receiverId;
  }

  public long getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return "PaymentPayload{"
        + "senderId='"
        + senderId
        + '\''
        + ", receiverId='"
        + receiverId
        + '\''
        + ", amount="
        + amount
        + '}';
  }
}
