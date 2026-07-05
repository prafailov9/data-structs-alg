package com.ntros.systemdesign.loadbalancing.data;

import static com.ntros.systemdesign.loadbalancing.data.ResponseStatus.CLIENT_ERROR;
import static com.ntros.systemdesign.loadbalancing.data.ResponseStatus.SERVER_ERROR;
import static com.ntros.systemdesign.loadbalancing.data.ResponseStatus.SUCCESS;

import com.ntros.systemdesign.IdSequencer;

public class HttpResponse {

  private final int responseId = IdSequencer.nextHttpResponseId();
  private final int requestId;
  private final ResponseStatus responseStatus;
  private final int code;
  private final String desc;

  private HttpResponse(int requestId, ResponseStatus responseStatus, int code, String desc) {
    this.requestId = requestId;
    this.responseStatus = responseStatus;
    this.code = code;
    this.desc = desc;
  }

  public static HttpResponse ofSuccess(int requestId, int code, String desc) {
    return new HttpResponse(requestId, SUCCESS, code, desc);
  }

  public static HttpResponse ofServerError(int requestId, int code, String err) {
    return new HttpResponse(requestId, SERVER_ERROR, code, err);
  }

  public static HttpResponse ofClientError(int requestId, int code, String err) {
    return new HttpResponse(requestId, CLIENT_ERROR, code, err);
  }

  public int getResponseId() {
    return responseId;
  }

  public int getRequestId() {
    return requestId;
  }

  public ResponseStatus getResponseStatus() {
    return responseStatus;
  }

  public int getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  @Override
  public String toString() {
    return "HttpResponse{"
        + "responseId="
        + responseId
        + ", requestId="
        + requestId
        + ", responseStatus="
        + responseStatus
        + ", code="
        + code
        + ", desc='"
        + desc
        + '\''
        + '}';
  }
}
