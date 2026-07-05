package com.ntros.ds.alg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Algorithms {

  public static List<List<Integer>> mergeIntervals(List<List<Integer>> source) {
    List<List<Integer>> merged = new ArrayList<>();
    List<List<Integer>> sorted =
        source.stream().sorted(Comparator.comparing(List::getFirst)).toList();

    // use merged as bound to check for overlaps.
    // Ensures that the first interval will be added into merged without any modification
    // Example: [1,3], [2,6]
    // Every subsequent iteration checks:
    // the current interval's origin - 2 > last-merged interval's([1, 3]) bound - 3
    // current interval: [2, 6], last-merged interval: [1, 3]
    for (var interval : sorted) {
      if (merged.isEmpty() || interval.getFirst() > merged.getLast().getLast()) {
        merged.add(interval);
      } else {
        var last = merged.getLast();
        List<Integer> l = List.of(last.getFirst(), Math.max(last.getLast(), interval.getLast()));
        merged.set(merged.size() - 1, l);
      }
    }

    return merged;
  }

  public static int[] topKFrequent(int[] arr, int k) {
    Map<Integer, Integer> freq = new HashMap<>();

    for (int num : arr) {
      freq.put(num, freq.getOrDefault(num, 0) + 1);
    }
    int n = arr.length + 1;
    List<Integer>[] buckets = new ArrayList[n];
    for (int i = 0; i < n; i++) {
      buckets[i] = new ArrayList<>();
    }

    for (var e : freq.entrySet()) {
      buckets[e.getValue()].add(e.getKey());
    }

    int[] top = new int[k];
    int j = 0;

    for (int i = n - 1; i >= 0 && j < k; i--) {
      for (int val : buckets[i]) {
        top[j] = val;
        j++;

        if (j == k) {
          break;
        }
      }
    }

    return top;
  }

  ///  LeetCode problems --------------------------------------------------------------------------
  public static int[] pivotArray(int[] nums, int pivot) {
    int n = nums.length;
    if (n == 0) {
      throw new IllegalArgumentException("Empty array given.");
    }
    List<Integer> smaller = new ArrayList<>();
    List<Integer> larger = new ArrayList<>();
    List<Integer> p = new ArrayList<>();

    for (int num : nums) {
      if (num < pivot) {
        smaller.add(num);
      } else if (num > pivot) {
        larger.add(num);
      } else {
        p.add(num);
      }
    }
    smaller.addAll(p);
    smaller.addAll(larger);
    int k = 0;
    for (Integer value : smaller) {
      nums[k++] = value;
    }

    return nums;
  }

  /**
   * Maintain two indexes to the result array: low and high. low will set values at the beginning of
   * the array, moving to the left. High sets them at the end, moves to the right. Both indexes move
   * only when they write.
   */
  public static int[] pivotArrayTwoPointer(int[] nums, int pivot) {
    int n = nums.length;
    if (n == 0) {
      throw new IllegalArgumentException("Empty array given.");
    }

    int low = 0;
    int high = n - 1;
    int[] ans = new int[n];
    for (int i = 0, j = high; i < n; i++, j--) {
      if (nums[i] < pivot) {
        ans[low++] = nums[i];
      }
      if (nums[j] > pivot) {
        ans[high--] = nums[j];
      }
    }

    while (low <= high) {
      ans[low++] = pivot;
    }

    return ans;
  }

  public static int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
      int complement = target - nums[i];
      if (!map.containsKey(complement)) {
        map.put(nums[i], i);
      } else {
        return new int[] {map.get(complement), i};
      }
    }

    return new int[] {-1, -1};
  }

  public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode sentinel = new ListNode(0);
    ListNode t = sentinel;
    int carry = 0;
    while (l1 != null || l2 != null || carry != 0) {
      int sum = carry;

      if (l1 != null) {
        sum += l1.val;
        l1 = l1.next;
      }

      if (l2 != null) {
        sum += l2.val;
        l2 = l2.next;
      }

      carry = sum / 10;
      t.next = new ListNode(sum % 10);
      t = t.next;
    }
    return sentinel.next;
  }

  public static ListNode reverseList(ListNode list) {
    if (list == null) {
      return null;
    }
    if (list.next == null) {
      return list;
    }
    var n = reverseList(list.next);
    list.next.next = list;
    list.next = null;

    return n;
  }

  public static String longestCommonPrefix(String[] strs) {
    if (strs.length == 0) {
      return "";
    }
    String prefix = strs[0];
    for (int i = 1; i < strs.length; i++) {
      int j = 0;
      int len = Math.min(prefix.length(), strs[i].length());
      while (j < len && prefix.charAt(j) == strs[i].charAt(j)) {
        j++;
      }
      if (j == 0) {
        return "";
      }
      prefix = prefix.substring(0, j);
    }
    return prefix;
  }

  public static int maxArea(int[] height) {
    if (height.length == 0) {
      return 0;
    }
    int total = 0;
    int low = 0;
    int high = height.length - 1;
    while (low < high) {
      int len = high - low;
      int area = Math.min(height[low], height[high]) * len;
      total = Math.max(total, area);
      if (height[low] < height[high]) {
        low++;
      } else {
        high--;
      }
    }
    return total;
  }

  /** finds sets of 3 unique-position numbers such that their sum produces 0; */
  public static List<List<Integer>> threeSum(int[] nums) {
    if (nums.length == 0) {
      return new ArrayList<>();
    }

    List<List<Integer>> ans = new ArrayList<>();

    return ans;
  }

  /**
   *
   *
   * <pre>
   *   Input: nums = [0,1,2,2,3,0,4,2], val = 2
   *   Output: 5, nums = [0,1,4,0,3,_,_,_]
   * </pre>
   */
  public static int removeElement(int[] nums, int val) {
    int k = 0;
    for (int i = 0; i < nums.length; i++) {
      if (nums[i] != val) {
        nums[k++] = nums[i];
      }
    }

    return k;
  }

  public static int searchInsert(int[] nums, int target) {
    int low = 0;
    int high = nums.length - 1;

    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (nums[mid] == target) {
        return mid;
      }
      if (nums[mid] > target) {
        high = mid - 1;
      } else {
        low = mid + 1;
      }
    }

    return low;
  }
}
