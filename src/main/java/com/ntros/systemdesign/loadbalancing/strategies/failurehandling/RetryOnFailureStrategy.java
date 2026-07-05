package com.ntros.systemdesign.loadbalancing.strategies.failurehandling;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;

public class RetryOnFailureStrategy implements FailureModeStrategy {

  @Override
  public HttpResponse onFailure(HttpRequest request) {
    return null;
  }
}
