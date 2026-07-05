package com.ntros.systemdesign.ratelimiting;

public interface Processor extends Runnable {

  void start();

  void awaitTermination() throws InterruptedException;
}
