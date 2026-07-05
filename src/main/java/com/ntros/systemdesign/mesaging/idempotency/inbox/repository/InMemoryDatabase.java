package com.ntros.systemdesign.mesaging.idempotency.inbox.repository;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.Account;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.InboxMessage;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.OperationId;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.payload.PaymentRequestPayload;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDatabase {
  // fake db-level lock
  private final Object lock = new Object();

  private final Map<String, Account> accounts = new HashMap<>();
  private final Map<OperationId, PaymentResult> inboxMessages = new HashMap<>();

  public PaymentResult processPayment(InboxMessage message) {
    synchronized (lock) {
      OperationId operationId = message.getOperationId();

      PaymentResult existingResult = inboxMessages.get(operationId);
      if (existingResult != null) {
        return existingResult.asDuplicate();
      }

      if (!(message.getPayload() instanceof PaymentRequestPayload payload)) {
        PaymentResult result = PaymentResult.failed(message, "Invalid payment payload.");

        inboxMessages.put(operationId, result);
        return result;
      }

      Account sender = accounts.get(payload.getSenderId());
      Account receiver = accounts.get(payload.getReceiverId());

      if (sender == null) {
        PaymentResult result = PaymentResult.failed(message, "Sender account not found.");

        inboxMessages.put(operationId, result);
        return result;
      }

      if (receiver == null) {
        PaymentResult result = PaymentResult.failed(message, "Receiver account not found.");

        inboxMessages.put(operationId, result);
        return result;
      }

      long amount = payload.getAmount();

      if (amount <= 0) {
        PaymentResult result = PaymentResult.failed(message, "Payment amount must be positive.");

        inboxMessages.put(operationId, result);
        return result;
      }

      if (sender.getBalance() - amount <= 0) {
        PaymentResult result = PaymentResult.failed(message, "Insufficient funds.");

        inboxMessages.put(operationId, result);
        return result;
      }

      sender.setBalance(sender.getBalance() - amount);
      receiver.setBalance(receiver.getBalance() + amount);

      PaymentResult result = PaymentResult.processed(message);
      inboxMessages.put(operationId, result);

      return result;
    }
  }

  public void insertAccount(Account account) {
    synchronized (lock) {
      accounts.put(account.getAccountId(), account);
    }
  }

  public Account loadAccount(String accountId) {
    synchronized (lock) {
      return accounts.get(accountId);
    }
  }

  public List<Account> getAllAccounts() {
    synchronized (lock) {
      return List.copyOf(accounts.values());
    }
  }
}
