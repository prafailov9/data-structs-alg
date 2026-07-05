package com.ntros.systemdesign.ratelimiting.leakybucket;

import static com.ntros.systemdesign.data.ResponseBuilder.buildProcessed;

import com.ntros.systemdesign.ratelimiting.Processor;
import com.ntros.systemdesign.ratelimiting.leakybucket.analytics.Stats;
import com.ntros.systemdesign.ratelimiting.message.Message;
import com.ntros.systemdesign.data.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FixedProcessor implements Processor {

  private static final Logger log = LoggerFactory.getLogger(FixedProcessor.class);
  private final int ownerId;
  private final int waitTimePerMessageMs;
  private final Stats ownerStats;
  private final Channel channel;
  private final Thread workerThread;

  public FixedProcessor(Channel channel, Stats ownerStats, int ownerId, int waitTimePerMessageMs) {
    this.ownerId = ownerId;
    this.waitTimePerMessageMs = waitTimePerMessageMs;
    this.ownerStats = ownerStats;
    this.channel = channel;
    this.workerThread = new Thread(this, "proc-" + ownerId);
  }

  @Override
  public void run() {
    while (true) {
      Message message;
      try {
        message = channel.take();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.info("Processor thread interrupted while reading messages. Exiting...");
        return;
      }
      if (message.terminate()) {
        log.info("Termination signal by Client Thread {} received. Exiting...", ownerId);
        return;
      }

      Request request = message.request();

      // TODO: write responses into external storage
      var res = buildProcessed(request);
      log.info("X: PROCESSED");
      ownerStats.incrementProcessed();
      // pause for the given time interval
      if (!waitForNextDrainSlot(waitTimePerMessageMs)) {
        log.info("Processor thread interrupted while waiting. Exiting...");
        return;
      }
    }
  }

  @Override
  public void start() {
    workerThread.start();
  }

  @Override
  public void awaitTermination() throws InterruptedException {
    workerThread.join();
  }

  private boolean waitForNextDrainSlot(int ms) {
    try {
      Thread.sleep(ms);
      return true;
    } catch (InterruptedException e) {
      // catching the interrupt ex clears the thread's interrupt flag. Restore it and exit cleanly
      Thread.currentThread().interrupt();
      return false;
    }
  }
}
