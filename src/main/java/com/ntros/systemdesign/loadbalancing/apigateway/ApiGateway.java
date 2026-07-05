package com.ntros.systemdesign.loadbalancing.apigateway;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;

public interface ApiGateway {

  HttpResponse forward(HttpRequest request, String backendTarget);
}
