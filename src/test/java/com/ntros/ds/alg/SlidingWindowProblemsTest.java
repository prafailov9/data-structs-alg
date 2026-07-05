package com.ntros.ds.alg;

import static com.ntros.ds.alg.SlidingWindowProblems.allAverages;
import static com.ntros.ds.alg.SlidingWindowProblems.longestSequence;
import static com.ntros.ds.alg.SlidingWindowProblems.longestSubstring;
import static com.ntros.ds.alg.SlidingWindowProblems.maxSubarraySum;
import static com.ntros.ds.alg.SlidingWindowProblems.smallestSubArraySum;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class SlidingWindowProblemsTest {

  @Test
  public void maxSubarraySumTest() {
    assertEquals(9, maxSubarraySum(new int[] {2, 1, 5, 1, 3, 2}, 3));
  }

  @Test
  public void allAveragesTest() {
    List<Float> averages = new ArrayList<>();
    averages.add(2.2f);
    averages.add(2.8f);
    averages.add(2.4f);
    averages.add(3.6f);
    averages.add(2.8f);

    assertEquals(averages, allAverages(new int[] {1, 3, 2, 6, -1, 4, 1, 8, 2}, 5));
  }

  @Test
  public void smallestSubArraySumTest() {
//    assertEquals(2, smallestSubArraySum(new int[] {2, 1, 5, 2, 3, 2}, 7));
  }

  @Test
  public void longestSubstringTest() {
    assertEquals(3, longestSubstring("abcabcbb"));
    assertEquals(3, longestSubstring("pwwkew"));
    assertEquals(1, longestSubstring("bbbbb"));
  }

  @Test
  public void longestSequenceTest() {
    assertEquals(5, longestSequence(new int[] {100, 4, 200, 1, 3, 2, 5}));
    assertEquals(10, longestSequence(new int[] {0, 3, 7, 2, 5, 8, 4, 6, 0, 1, 9}));
  }
}
