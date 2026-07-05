package com.ntros.systemdesign.loadbalancing.discovery;

import static com.ntros.systemdesign.loadbalancing.services.ServerStatus.OFFLINE;

import com.ntros.systemdesign.loadbalancing.services.Server;
import com.ntros.systemdesign.loadbalancing.services.ServerStatus;
import java.util.HashMap;
import java.util.Map;

public class ApiServiceDiscovery implements ServiceDiscovery {

  private final Map<String, Server> registeredBackends = new HashMap<>();

  public ApiServiceDiscovery() {}

  @Override
  public void addBackend(String key, Server server) {
    if (server.serverStatus() == OFFLINE) {
      // reject offline backend
      return;
    }
    registeredBackends.putIfAbsent(key, server);
  }

  @Override
  public ServerStatus getStatus(String key) {
    var server = registeredBackends.getOrDefault(key, null);
    if (server == null) {
      return ServerStatus.UNKNOWN;
    }
    return server.serverStatus();
  }

  @Override
  public int backendCount() {
    return registeredBackends.size();
  }
}
