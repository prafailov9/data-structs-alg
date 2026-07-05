package com.ntros.systemdesign.loadbalancing.apigateway;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;
import com.ntros.systemdesign.loadbalancing.services.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendApiGateway implements ApiGateway {

  private static final Logger log = LoggerFactory.getLogger(BackendApiGateway.class);
  private final Map<String, Server> serversByName = new HashMap<>();

  public BackendApiGateway(List<Server> servers) {
    if (servers == null || servers.isEmpty()) {
      throw new IllegalArgumentException("empty server set");
    }
    for (var s : servers) {
      serversByName.put(s.serverName(), s);
    }
  }

  @Override
  public HttpResponse forward(HttpRequest request, String backendTarget) {
    var server = serversByName.get(backendTarget);
    if (server == null) {
      log.info("no server found for backend target: {}", backendTarget);
      return HttpResponse.ofClientError(
          request.getRequestId(),
          400,
          String.format("requested target %s not found", backendTarget));
    }
    return server.process(request);
  }
}
