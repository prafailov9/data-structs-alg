package com.ntros.systemdesign.concurrency.pooling.connection;

public class ResourceConnection extends AbstractResourceConnection {

  @Override
  public Integer getConnectionId() {
    return connectionId;
  }
}
