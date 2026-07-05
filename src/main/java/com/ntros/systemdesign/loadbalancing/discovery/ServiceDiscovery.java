package com.ntros.systemdesign.loadbalancing.discovery;

import com.ntros.systemdesign.loadbalancing.services.Server;
import com.ntros.systemdesign.loadbalancing.services.ServerStatus;

public interface ServiceDiscovery {

  void addBackend(String key, Server server);

  ServerStatus getStatus(String key);

  int backendCount();
}
