package com.ntros.systemdesign.ratelimiting.leakybucket;

import com.ntros.systemdesign.ratelimiting.message.Message;

public interface Channel {

  boolean tryOffer(Message message, int limit);

  boolean tryOffer(Message message);

  Message take() throws InterruptedException;

  void forceOffer(Message message);
}
