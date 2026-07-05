package com.ntros.ds.alg;

import static com.ntros.ds.alg.BackTrackingAlgorithms.generateParentheses;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class BackTrackingAlgorithmsTest {

  @Test
  public void parenTest() {
    var res = generateParentheses(3);
    assertEquals(List.of("((()))", "(()())", "(())()", "()(())", "()()()"), res);
  }
}
