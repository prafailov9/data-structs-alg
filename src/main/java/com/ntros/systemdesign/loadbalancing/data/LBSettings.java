package com.ntros.systemdesign.loadbalancing.data;

import com.ntros.systemdesign.loadbalancing.strategies.failurehandling.FailureModeStrategy;
import com.ntros.systemdesign.loadbalancing.strategies.nodeselection.SelectionStrategy;

public record LBSettings(
    int pollDelay, FailureModeStrategy failureModeStrategy, SelectionStrategy selectionStrategy) {}
