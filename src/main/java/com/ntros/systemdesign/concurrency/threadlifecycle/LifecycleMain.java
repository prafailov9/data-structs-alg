package com.ntros.systemdesign.concurrency.threadlifecycle;

import java.util.ArrayList;
import java.util.List;

public class LifecycleMain {

  private final List<Message> queue = new ArrayList<>();
  private static final Message POISON = Message.ofPoison();
  private final Object lock = new Object();
  private final int capacity;
  private volatile boolean running = true;

  LifecycleMain(int capacity) {
    this.capacity = capacity;
  }

  public static void main(String[] args) throws InterruptedException {
    LifecycleMain obj = new LifecycleMain(10);
    int readIntervalMs = 10;

    Thread producer = new Thread(obj::produce, "producer");
    Thread consumer = new Thread(() -> obj.process(readIntervalMs), "consumer");

    producer.start();
    consumer.start();

    Thread.sleep(5_000);
    obj.running = false;
    producer.join();
    consumer.join();
  }

  void produce() {
    int value = 0;
    synchronized (lock) {
      while (running) {
        while (queue.size() == capacity) {
          try {
            lock.wait();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
        queue.add(new Message(value, false));
        System.out.printf("Added: %s\n", value);
        value++;
        lock.notifyAll();
      }

      System.out.println("Producer cancelled.");
      queue.add(POISON);
    }
  }

  void process(int readIntervalMs) {
    while (true) {
      synchronized (lock) {
        while (queue.isEmpty()) {
          try {
            lock.wait();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
          }
        }

        var message = queue.removeFirst();
        if (message == POISON) {
          System.out.println("Consumer received termination signal. Exiting...\n");
          return;
        }

        System.out.printf("Took: %s\n", message.value);
        lock.notifyAll();

        if (!waitFor(readIntervalMs)) {
          running = false;
          return;
        }
      }
    }
  }

  private boolean waitFor(int intervalMs) {
    try {
      Thread.sleep(intervalMs);
      return true;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  private record Message(int value, boolean terminate) {

    static Message ofMessage(int value) {
      return new Message(value, true);
    }

    static Message ofPoison() {
      return new Message(-1, false);
    }
  }
}
