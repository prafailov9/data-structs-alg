package com.ntros.systemdesign.ratelimiting.leakybucket;

import com.ntros.systemdesign.ratelimiting.message.Message;
import com.ntros.ds.queue.LinkedQueue;
import com.ntros.ds.queue.Queue;

public class MessageChannel implements Channel {

  private final Queue<Message> messageQueue;
  private final Object lock;
  private final int capacity;

  private MessageChannel(Queue<Message> messageQueue, Object lock, int capacity) {
    this.messageQueue = messageQueue;
    this.lock = lock;
    this.capacity = capacity;
  }

  public static MessageChannel ofChannel(int capacity) {
    return new MessageChannel(new LinkedQueue<>(), new Object(), capacity);
  }

  @Override
  public boolean tryOffer(Message message) {
    return tryOffer(message, capacity);
  }

  @Override
  public boolean tryOffer(Message message, int limit) {
    synchronized (lock) {
      if (messageQueue.size() >= limit) {
        return false;
      }
      messageQueue.add(message);
      lock.notifyAll();
      return true;
    }
  }

  @Override
  public void forceOffer(Message message) {
    synchronized (lock) {
      messageQueue.add(message);
      lock.notifyAll();
    }
  }

  @Override
  public Message take() throws InterruptedException {
    synchronized (lock) {
      while (messageQueue.isEmpty()) {
        lock.wait();
      }
      return messageQueue.remove();
    }
  }
}
