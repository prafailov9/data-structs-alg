package com.ntros.ds.alg;

public class GraphRecursionAlgorithms {

  /** n = 5 edges = [[0,1], [1,2], [3,4]] start = 0 */
  static int countReachable(int[][] graph, int start, int n) {
    if (start == n) {
      return 0;
    }

    return 1 + countReachable(graph, start + 1, n);
  }
}
