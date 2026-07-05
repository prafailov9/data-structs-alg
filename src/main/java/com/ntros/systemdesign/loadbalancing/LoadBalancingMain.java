package com.ntros.systemdesign.loadbalancing;

import static com.ntros.systemdesign.loadbalancing.services.ServerType.ORDERS;
import static com.ntros.systemdesign.loadbalancing.services.ServerType.PAYMENTS;
import static com.ntros.systemdesign.loadbalancing.services.ServerType.USERS;

import com.ntros.systemdesign.LoadBalancer;
import com.ntros.systemdesign.loadbalancing.apigateway.ApiGateway;
import com.ntros.systemdesign.loadbalancing.apigateway.BackendApiGateway;
import com.ntros.systemdesign.loadbalancing.data.LBSettings;
import com.ntros.systemdesign.loadbalancing.discovery.ApiServiceDiscovery;
import com.ntros.systemdesign.loadbalancing.discovery.ServiceDiscovery;
import com.ntros.systemdesign.loadbalancing.services.ServerConfig;
import com.ntros.systemdesign.loadbalancing.services.ServerType;
import com.ntros.systemdesign.loadbalancing.strategies.failurehandling.RetryOnFailureStrategy;
import com.ntros.systemdesign.loadbalancing.services.BackendServer;
import com.ntros.systemdesign.loadbalancing.services.Server;
import com.ntros.systemdesign.loadbalancing.strategies.nodeselection.RoundRobinSelectionStrategy;
import com.ntros.systemdesign.mesaging.idempotency.inbox.data.runtimestate.CancellationToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadBalancingMain {

  private static final Logger log = LoggerFactory.getLogger(LoadBalancingMain.class);

  public static void main(String[] args) throws InterruptedException {
    int senderCount = 3;
    int serversCount = 3;
    long seed = new Random().nextLong(1, 100);
    Random rng = new Random(seed);
    int[] senderTimingDelayOffsetRange = new int[] {-50, 50};
    int sendDelayMs = 333;
    int lbPollDelayMs = 100;
    int serverStatusUpdateIntervalMs = 300;
    int simTimeMs = 10_000;
    CancellationToken token = new CancellationToken();

    List<String> knownHosts = List.of("api.store.com");

    ServiceDiscovery discovery = new ApiServiceDiscovery();

    // each list entry starts with the given prefix, example: payment-1, payment-2, payment-3...
    List<List<String>> serviceNameGroups =
        buildServiceNames(List.of("payment", "user", "order"), serversCount);

    Map<String, List<String>> backendNamesPerGroup = new HashMap<>();
    List<Server> servers = new ArrayList<>();
    for (var names : serviceNameGroups) {
      var name = names.getFirst();
      // first entry is always the prefix, add "s" to match group names
      var groupKey = name.split("-")[0] + "s";
      backendNamesPerGroup.put(groupKey, names);
      names.forEach(
          n ->
              servers.add(
                  new BackendServer(
                      n,
                      new ServerConfig(
                          ServerType.valueOf(groupKey.toUpperCase()),
                          serverStatusUpdateIntervalMs,
                          seed))));
    }

    for (var s : servers) {
      discovery.addBackend(s.serverName(), s);
    }

    log.info("BackEnd count: {}", discovery.backendCount());

    ApiGateway apiGateway = new BackendApiGateway(servers);
    List<String> serviceKeys = List.copyOf(backendNamesPerGroup.keySet());
    LBSettings lbSettings =
        new LBSettings(
            lbPollDelayMs, new RetryOnFailureStrategy(), new RoundRobinSelectionStrategy());
    LoadBalancer loadBalancer =
        new ServiceLoadBalancer(backendNamesPerGroup, lbSettings, discovery, apiGateway);

    var senders =
        buildSenders(
            senderCount,
            rng,
            sendDelayMs,
            senderTimingDelayOffsetRange[0],
            senderTimingDelayOffsetRange[1],
            loadBalancer,
            knownHosts,
            serviceKeys,
            token);

    for (var s : senders) {
      s.start();
    }

    Thread.sleep(simTimeMs);

    token.cancel();
    // stop senders
    for (var s : senders) {
      s.awaitTermination();
    }
    // stop LB
    loadBalancer.shutdown();

    // stop servers
    for (var s : servers) {
      s.shutdown();
    }
  }

  private static List<List<String>> buildServiceNames(
      List<String> namePrefixes, int countPerService) {

    List<List<String>> ans = new ArrayList<>();
    for (var n : namePrefixes) {
      int i = 1;
      List<String> names = new ArrayList<>();
      while (i <= countPerService) {
        names.add(n + "-service-" + i);
        i++;
      }
      ans.add(names);
    }
    return ans;
  }

  private static List<RequestSender> buildSenders(
      int senderCount,
      Random rng,
      int sendDelayMs,
      int perSenderOffsetOrigin,
      int perSenderOffsetBound,
      LoadBalancer loadBalancer,
      List<String> knownHosts,
      List<String> serviceKeys,
      CancellationToken token) {
    List<RequestSender> senders = new ArrayList<>();

    for (int i = 1; i <= senderCount; i++) {
      int randomDelayOffset = rng.nextInt(perSenderOffsetOrigin, perSenderOffsetBound);

      senders.add(
          new RequestSender(
              i,
              loadBalancer,
              sendDelayMs + randomDelayOffset,
              knownHosts,
              serviceKeys,
              rng,
              token));
    }
    return senders;
  }
}
