package com.ntros.systemdesign.mesaging.idempotency.inbox.data;

public class Account {

  private final String accountId;
  private long balance;

  public Account(String accountId, long balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  public String getAccountId() {
    return accountId;
  }

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }
}
