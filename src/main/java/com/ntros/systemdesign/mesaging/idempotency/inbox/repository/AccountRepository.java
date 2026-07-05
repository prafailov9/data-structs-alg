package com.ntros.systemdesign.mesaging.idempotency.inbox.repository;

import com.ntros.systemdesign.mesaging.idempotency.inbox.data.Account;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRepository {

  private final Map<String, Account> accounts = new HashMap<>();

  public Account loadAccount(String accountId) {
    return accounts.get(accountId);
  }
  public List<Account> getAll() {
    return List.copyOf(accounts.values());
  }

  public void insertAccount(Account account) {
    accounts.put(account.getAccountId(), account);
  }
}
