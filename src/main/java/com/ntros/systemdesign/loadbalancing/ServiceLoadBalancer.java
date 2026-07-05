package com.ntros.systemdesign.loadbalancing;

import static com.ntros.systemdesign.loadbalancing.services.ServerStatus.HEALTHY;

import com.ntros.systemdesign.LoadBalancer;
import com.ntros.systemdesign.loadbalancing.apigateway.ApiGateway;
import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;
import com.ntros.systemdesign.loadbalancing.data.LBSettings;
import com.ntros.systemdesign.loadbalancing.discovery.ServiceDiscovery;
import com.ntros.systemdesign.loadbalancing.strategies.failurehandling.FailureModeStrategy;
import com.ntros.systemdesign.loadbalancing.strategies.nodeselection.SelectionStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Read backend availability snapshot on startup. Populate local state. */
public class ServiceLoadBalancer implements LoadBalancer {

  private static final Logger log = LoggerFactory.getLogger(ServiceLoadBalancer.class);
  // key: group(path), value: server-names
  private final Map<String, List<String>> knownBackends;
  private final Map<String, List<String>> healthyBackends = new HashMap<>();
  private final Object liveMapLock = new Object();
  // components
  private final ApiGateway apiGateway;
  private final ServiceDiscovery discovery;
  // strategies
  private final FailureModeStrategy failureModeStrategy;
  private final SelectionStrategy selectionStrategy;
  private final Thread healthchecker;
  private volatile boolean healthcheckerRunning = true;
  private final int pollDelayMs;

  // initial target group. Group entries are updated dynamically
  public ServiceLoadBalancer(
      Map<String, List<String>> knownBackends,
      LBSettings settings,
      ServiceDiscovery discovery,
      ApiGateway apiGateway) {
    if (knownBackends == null || knownBackends.isEmpty()) {
      throw new IllegalArgumentException("Initial target set cannot be empty.");
    }
    if (settings == null || settings.failureModeStrategy() == null) {
      throw new IllegalArgumentException("Invalid settings for LB.");
    }

    this.knownBackends = knownBackends;
    this.apiGateway = apiGateway;
    this.discovery = discovery;

    failureModeStrategy = settings.failureModeStrategy();
    selectionStrategy = settings.selectionStrategy();

    pollDelayMs = settings.pollDelay();
    healthchecker = new Thread(this::pollLive, "lb-healthchecker-0");
    healthchecker.start();
  }

  @Override
  public HttpResponse accept(HttpRequest request) {
    int requestId = request.getRequestId();
    String path = request.getPath();
    if (path == null || path.isBlank()) {
      return HttpResponse.ofClientError(requestId, 400, "Missing path in the request");
    }
    List<String> knownTargets;
    synchronized (liveMapLock) {
      knownTargets = healthyBackends.get(path);
    }
    if (knownTargets == null || knownTargets.isEmpty()) {
      return HttpResponse.ofClientError(
          requestId, 404, "No healthy service exists for path: " + path);
    }

    String target = selectionStrategy.select(knownTargets, path);
    log.info("LB selected target: {}", target);
    var response = apiGateway.forward(request, target);
    // check response
    String desc = response.getDesc();

    return response;
  }

  @Override
  public boolean shutdown() {
    healthcheckerRunning = false;
    healthchecker.interrupt(); // force exit if sleeping
    try {
      healthchecker.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
    return true;
  }

  private void pollLive() {
    while (healthcheckerRunning) {
      if (!waitForNextPoll()) {
        continue;
      }
      // for each known, query discovery
      // ping status
      // add only healthy
      for (var known : knownBackends.entrySet()) {
        var groupKey = known.getKey();
        var servicesPerGroup = known.getValue();
        List<String> healthy = new ArrayList<>();
        for (var service : servicesPerGroup) {
          var status = discovery.getStatus(service);
          if (status == HEALTHY) {
            healthy.add(service);
          }
        }
        synchronized (liveMapLock) {
          healthyBackends.put(groupKey, healthy);
        }
      }
    }
  }

  private boolean waitForNextPoll() {
    try {
      Thread.sleep(pollDelayMs);
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
