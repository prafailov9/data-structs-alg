package com.ntros.systemdesign;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;

public interface LoadBalancer {

  HttpResponse accept(HttpRequest request);

  boolean shutdown();

}
