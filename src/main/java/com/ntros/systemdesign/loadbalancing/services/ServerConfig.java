package com.ntros.systemdesign.loadbalancing.services;

public record ServerConfig(ServerType serverType, int statusUpdateIntervalMs, long seed) {}
