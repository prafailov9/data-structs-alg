package com.ntros.ds.concurrency.pooling.connection;

import static org.junit.jupiter.api.Assertions.*;

import com.ntros.systemdesign.concurrency.pooling.connection.Connection;
import com.ntros.systemdesign.concurrency.pooling.connection.ResourceConnection;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResourceConnectionTest {

  @Test
  public void createNConnections_incrementalIdsTest() {
    List<Connection> connections = new ArrayList<>();
    int n = 5;
    for (int i = 1; i <= n; i++) {
      connections.add(new ResourceConnection());
    }


    for (int i = 1; i <= n; i++) {
      assertEquals(i, connections.get(i - 1).getConnectionId());
    }
  }

}
