package com.ntros.ds.alg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SlidingWindowProblems {

  static int maxSubarraySum(int[] arr, int k) {
    if (k < 1) {
      throw new IllegalArgumentException(String.format("invalid window size:%s", k));
    }
    if (arr.length <= 1) {
      throw new IllegalArgumentException(String.format("invalid array length:%s", arr.length));
    }

    int sum = 0;
    int max = Integer.MIN_VALUE;
    int left = 0;
    for (int right = 0; right < arr.length; right++) {
      sum += arr[right];
      max = Math.max(sum, max);
      if ((right - left) + 1 == k) {
        sum -= arr[left++];
      }
    }
    return max;
  }

  /** nums = [1, 3, 2, 6, -1, 4, 1, 8, 2] k = 5 */
  static List<Float> allAverages(int[] arr, int k) {
    if (k < 1) {
      throw new IllegalArgumentException(String.format("invalid window size:%s", k));
    }
    if (arr.length <= 1) {
      throw new IllegalArgumentException(String.format("invalid array length:%s", arr.length));
    }

    float sum = 0.0f;
    int left = 0;
    List<Float> averages = new ArrayList<>();
    for (int right = 0; right < arr.length; right++) {
      sum += arr[right];
      if ((right - left) + 1 == k) {
        averages.add(sum / k);
        sum -= arr[left++];
      }
    }
    return averages;
  }

  static int smallestSubArraySum(int[] arr, int target) {
    return 1;
  }

  static int longestSubstring(String text) {
    if (text == null || text.isEmpty()) {
      throw new IllegalArgumentException("Input string cannot be blank");
    }

    int left = 0;
    int maxLen = 0;
    Set<Character> visited = new HashSet<>();

    for (int right = 0; right < text.length(); right++) {
      var current = text.charAt(right);

      while (visited.contains(current)) {
        visited.remove(text.charAt(left));
        left++;
      }

      visited.add(current);
      maxLen = Math.max(maxLen, visited.size());
    }
    return maxLen;
  }

  /// numbers that can be arranged in order with no gaps: 1, 2, 3
  ///  a[100, 4, 200, 1, 3, 2, 5] => 5
  /// The following numbers exist which create a valid sequence when arranged in
  ///  ASC order: 1, 2, 3, 4, 5
  static int longestSequence(int[] arr) {
    Set<Integer> set = new HashSet<>();
    for (int x : arr) {
      set.add(x);
    }

    int max = 0;
    for (int x : arr) {
      // if start, count all values that exist have no gap between them and increase
      // by 1
      if (!set.contains(x - 1)) {
        int len = 1;

        int next = x + 1;
        while (set.contains(next)) {
          len++;
          next++;
        }
        // record the maximum of the current sequence len with prev
        max = Math.max(max, len);
      }
    }
    return max;
  }
}
