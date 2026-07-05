package com.ntros.systemdesign.concurrency.resourcesharing;

import static com.ntros.systemdesign.concurrency.resourcesharing.ResourceSharingMain.WorkerState.PREPARING;
import static com.ntros.systemdesign.concurrency.resourcesharing.ResourceSharingMain.WorkerState.READY;
import static com.ntros.systemdesign.concurrency.resourcesharing.ResourceSharingMain.WorkerState.EXITED;
import static com.ntros.systemdesign.concurrency.resourcesharing.ResourceSharingMain.WorkerState.WORKING;

import java.util.ArrayList;
import java.util.List;

/**
 * Dining Philosophers solution via state transitions of individual threads. Any READY thread is
 * allowed to work if both of its neighbors are not WORKING/READY or have higher accessOrder. Allows
 * for any non-neighboring threads to work in parallel
 */
public class ResourceSharingMain {
  enum WorkerState {
    PREPARING,
    READY,
    WORKING,
    EXITED
  }

  private final List<Worker> workers = new ArrayList<>();
  private final int[] accessOrder;
  private final WorkerState[] states;
  private final Object stateLock = new Object();
  private int sequence;

  ResourceSharingMain(int n) {
    accessOrder = new int[n];
    states = new WorkerState[n];

    for (int i = 1; i <= n; i++) {
      int idx = i - 1;
      workers.add(new Worker(i));
      states[idx] = PREPARING;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    var obj = new ResourceSharingMain(5);
    var workers = obj.workers;

    for (var w : workers) {
      w.start();
    }

    Thread.sleep(5_000);
    for (var w : workers) {
      w.stop();
    }

    System.out.println("Sim stopped");
  }

  private class Worker implements Runnable {

    private static final int PREPARING_INTERVAL_MS = 100;
    private static final int WORKING_INTERVAL_MS = 300;

    private final int workerId;
    private final String workerName;
    private final Thread thread;
    private volatile boolean running = true;

    Worker(int workerId) {
      this.workerId = workerId;
      this.workerName = "worker-" + workerId;
      thread = new Thread(this, workerName);
    }

    /**
     * Any non-neighbor workers can work in parallel. The decision-making process of who gets to
     * work is serialized.
     */
    @Override
    public void run() {
      while (running) {
        int idx = workerId - 1;

        if (!prepare()) {
          return;
        }

        synchronized (stateLock) {
          states[idx] = READY;
          accessOrder[idx] = ++sequence;
          while (running && !canWork(idx)) {
            if (!await(stateLock)) {
              // clear val on failed await()
              states[idx] = EXITED;
              return;
            }
          }
          states[idx] = WORKING;
        }

        if (!work()) {
          return;
        }

        synchronized (stateLock) {
          states[idx] = PREPARING;
          stateLock.notifyAll();
        }
      }
    }

    private boolean canWork(int workerIdx) {
      int n = states.length;
      int leftIdx = (workerIdx - 1 + n) % n;
      int rightIdx = (workerIdx + 1) % n;
      // current READY worker can only work if :
      // 1. neighbors are not WORKING and not READY, or
      // 2. READY but have older priority

      return ((states[leftIdx] != WORKING && states[rightIdx] != WORKING)
          && ((states[leftIdx] != READY || accessOrder[leftIdx] > accessOrder[workerIdx])
              && (states[rightIdx] != READY || accessOrder[rightIdx] > accessOrder[workerIdx])));
    }

    void start() {
      thread.start();
    }

    void stop() throws InterruptedException {
      running = false;
      synchronized (stateLock) {
        stateLock.notifyAll();
      }

      thread.join();
    }

    private boolean prepare() {
      System.out.printf("%s preparing...\n", workerName);
      return sleep(PREPARING_INTERVAL_MS);
    }

    private boolean work() {
      System.out.printf("%s working...\n", workerName);
      return sleep(WORKING_INTERVAL_MS);
    }

    private boolean sleep(int ms) {
      try {
        Thread.sleep(ms);
        return true;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
    }

    private boolean await(Object lock) {
      try {
        lock.wait();
        return true;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return false;
      }
    }
  }
}
