package com.ntros.systemdesign.ratelimiting;

import com.ntros.systemdesign.ratelimiting.leakybucket.analytics.Stats;
import com.ntros.systemdesign.ratelimiting.message.Message;
import com.ntros.systemdesign.data.Response;
import java.util.List;

public interface Service {

  Response submit(Message message) throws InterruptedException;

  boolean disconnect(int clientId);

  List<Stats> getApiStats();

}
