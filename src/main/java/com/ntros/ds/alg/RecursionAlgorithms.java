package com.ntros.ds.alg;

import java.util.ArrayList;
import java.util.List;

public class RecursionAlgorithms {

  public List<String> generateParentheses(int n) {
    List<String> result = new ArrayList<>();
    backtrack(result, new StringBuilder(), 0, 0, n);
    return result;
  }

  private void backtrack(
      List<String> result,
      StringBuilder current,
      int open,
      int close,
      int n
  ) {
    if (current.length() == n * 2) {
      result.add(current.toString());
      return;
    }

    if (open < n) {
      current.append("(");
      backtrack(result, current, open + 1, close, n);
      current.deleteCharAt(current.length() - 1);
    }

    if (close < open) {
      current.append(")");
      backtrack(result, current, open, close + 1, n);
      current.deleteCharAt(current.length() - 1);
    }
  }
}
