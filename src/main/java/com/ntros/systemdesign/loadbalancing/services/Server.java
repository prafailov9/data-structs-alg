package com.ntros.systemdesign.loadbalancing.services;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;

public interface Server {

  String serverName();

  HttpResponse process(HttpRequest request);

  ServerStatus serverStatus();
  ServerType serverType();
  boolean shutdown();


}
