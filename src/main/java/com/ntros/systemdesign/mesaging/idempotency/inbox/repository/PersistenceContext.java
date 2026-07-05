package com.ntros.systemdesign.mesaging.idempotency.inbox.repository;

public class PersistenceContext {

  private static final InMemoryDatabase database = new InMemoryDatabase();

  public static InMemoryDatabase database() {
    return database;
  }
}
