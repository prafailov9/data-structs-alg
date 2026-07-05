package com.ntros.systemdesign.data;

public record Response(
    int responseId, int requestId, int clientId, boolean success, String payload, String callback) {}
