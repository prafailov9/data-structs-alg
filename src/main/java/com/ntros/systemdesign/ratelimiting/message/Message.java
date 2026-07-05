package com.ntros.systemdesign.ratelimiting.message;

import com.ntros.systemdesign.data.Request;

public class Message {
  private final Request request;
  private final boolean terminate;

  private Message(Request request, boolean terminate) {
    this.request = request;
    this.terminate = terminate;
  }

  public static Message ofRequest(Request request) {
    return new Message(request, false);
  }

  public static Message ofPoison() {
    return new Message(null, true);
  }

  public Request request() {
    return request;
  }

  public boolean terminate() {
    return terminate;
  }
}
