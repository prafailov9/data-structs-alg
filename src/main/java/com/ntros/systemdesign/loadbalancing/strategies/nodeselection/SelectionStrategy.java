package com.ntros.systemdesign.loadbalancing.strategies.nodeselection;

import java.util.List;

public interface SelectionStrategy {

  String select(List<String> targets, String groupKey);
}
