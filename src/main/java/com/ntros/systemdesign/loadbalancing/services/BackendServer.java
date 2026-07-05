package com.ntros.systemdesign.loadbalancing.services;

import static com.ntros.systemdesign.loadbalancing.services.ServerStatus.HEALTHY;
import static com.ntros.systemdesign.loadbalancing.services.ServerStatus.OFFLINE;

import com.ntros.systemdesign.loadbalancing.data.HttpRequest;
import com.ntros.systemdesign.loadbalancing.data.HttpResponse;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackendServer implements Server {

  private static final Logger log = LoggerFactory.getLogger(BackendServer.class);
  private final String serverName;
  private final AtomicBoolean isLive = new AtomicBoolean(true);

  private final Random rng;
  private volatile boolean updaterRunning = true;
  private final Thread serverStatusUpdater;
  private final ServerType serverType;
  private ServerStatus status;

  public BackendServer(String serverName, ServerConfig serverConfig) {
    if (serverConfig == null) {
      throw new IllegalArgumentException("Empty server config");
    }
    this.serverName = serverName;
    this.serverType = serverConfig.serverType();
    status = HEALTHY;
    rng = new Random(serverConfig.seed());
    serverStatusUpdater = new Thread(this::updateStatus);

    serverStatusUpdater.start();
  }

  @Override
  public HttpResponse process(HttpRequest request) {
    // for now, simulate work, return success res
    if (!isLive.get() || status != HEALTHY) {
      return HttpResponse.ofServerError(
          request.getRequestId(), 500, String.format("%s down", serverName));
    }
    return HttpResponse.ofSuccess(request.getRequestId(), 200, "request processed successfully");
  }

  @Override
  public String serverName() {
    return serverName;
  }

  @Override
  public ServerStatus serverStatus() {
    return status;
  }

  @Override
  public ServerType serverType() {
    return serverType;
  }

  @Override
  public boolean shutdown() {
    try {
      updaterRunning = false;
      isLive.set(false);
      status = OFFLINE;
      serverStatusUpdater.interrupt();
      serverStatusUpdater.join();
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  private void updateStatus() {
    while (updaterRunning) {
      if (!waitUpdaterInterval()) {
        log.info("Interrupted while waiting for next update check. Exiting...");
        return;
      }
      if (isLive.get()) {
        // simulate unexpected crash
        if (status == HEALTHY && rng.nextFloat() < 0.1f) {
          isLive.compareAndSet(true, false);
          // update status
          status = OFFLINE;
        }
      } else {
        isLive.compareAndSet(false, true);
        status = HEALTHY;
      }
    }
  }

  private boolean waitUpdaterInterval() {
    try {
      Thread.sleep(500);
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
