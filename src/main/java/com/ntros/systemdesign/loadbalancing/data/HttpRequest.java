package com.ntros.systemdesign.loadbalancing.data;

import java.util.Map;

public class HttpRequest {

  private final int requestId;
  private final HttpMethod method;
  private final Map<String, String> headers;
  private final String host;
  private final String path;
  private final String payload;

  public HttpRequest(
      int requestId,
      HttpMethod method,
      Map<String, String> headers,
      String host,
      String path,
      String payload) {
    this.requestId = requestId;
    this.method = method;
    this.headers = headers;
    this.host = host;
    this.path = path;
    this.payload = payload;
  }

  public int getRequestId() {
    return requestId;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getHost() {
    return host;
  }

  public String getPath() {
    return path;
  }

  public String getPayload() {
    return payload;
  }
}
