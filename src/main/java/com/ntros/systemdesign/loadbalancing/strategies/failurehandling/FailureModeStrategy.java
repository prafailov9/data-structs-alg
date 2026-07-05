package com.ntros.systemdesign.loadbalancing.strategies.failurehandling;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;

public interface FailureModeStrategy {

  HttpResponse onFailure(HttpRequest request);
}
