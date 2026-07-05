package com.ntros.systemdesign.loadbalancing.strategies.nodeselection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundRobinSelectionStrategy implements SelectionStrategy {

  private final Map<String, GroupIdx> groups = new HashMap<>();

  @Override
  public String select(List<String> targets, String groupKey) {
    var group = getGroupIdx(groupKey);
    synchronized (group.lock) {
      String target = targets.get(group.idx);
      group.next(targets.size());
      return target;
    }
  }

  private GroupIdx getGroupIdx(String key) {
    GroupIdx groupIdx;
    if (!groups.containsKey(key)) {
      groupIdx = new GroupIdx();
      groups.put(key, groupIdx);
    } else {
      groupIdx = groups.get(key);
    }

    return groupIdx;
  }

  private static class GroupIdx {
    int idx = 0;
    final Object lock = new Object();

    void next(int len) {
      idx = (idx + 1) % len;
    }
  }
}
