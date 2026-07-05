package com.ntros.ds.alg;

import static com.ntros.ds.alg.Algorithms.addTwoNumbers;
import static com.ntros.ds.alg.Algorithms.longestCommonPrefix;
import static com.ntros.ds.alg.Algorithms.maxArea;
import static com.ntros.ds.alg.Algorithms.mergeIntervals;
import static com.ntros.ds.alg.Algorithms.pivotArray;
import static com.ntros.ds.alg.Algorithms.pivotArrayTwoPointer;
import static com.ntros.ds.alg.Algorithms.removeElement;
import static com.ntros.ds.alg.Algorithms.reverseList;
import static com.ntros.ds.alg.Algorithms.topKFrequent;
import static com.ntros.ds.alg.Algorithms.twoSum;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class AlgorithmsTest {

  @Test
  public void mergeIntervalsTest() {
    var first = List.of(1, 3);
    var second = List.of(2, 6);
    var third = List.of(8, 10);
    var fourth = List.of(15, 18);

    var res = mergeIntervals(List.of(first, second, third, fourth));

    assertNotNull(res);
    //    assertEquals(3, res.size());

    assertEquals(List.of(1, 6), res.getFirst());
    assertEquals(third, res.get(1));
    assertEquals(fourth, res.getLast());
  }

  @Test
  public void mergeIntervalsWithSortingTest() {
    var first = List.of(2, 5);
    var second = List.of(3, 5);
    var third = List.of(5, 8);
    var fourth = List.of(1, 3);
    var fifth = List.of(2, 3);

    var res = mergeIntervals(List.of(first, second, third, fourth, fifth));

    assertNotNull(res);
    assertEquals(1, res.size());
    assertEquals(List.of(1, 8), res.getFirst());
  }

  @Test
  public void topKFrequentTest() {
    int[] res = topKFrequent(new int[] {1, 1, 1, 2, 2, 3}, 2);

    assertEquals(2, res.length);
    assertEquals(1, res[0]);
    assertEquals(2, res[1]);
  }

  @Test
  public void pivotArrayTest() {
    assertArrayEquals(
        new int[] {9, 5, 3, 10, 10, 12, 14}, pivotArray(new int[] {9, 12, 5, 10, 14, 3, 10}, 10));

    assertArrayEquals(new int[] {-3, 2, 4, 3}, pivotArray(new int[] {-3, 4, 3, 2}, 2));

    assertArrayEquals(
        new int[] {9, 5, 3, 10, 10, 12, 14},
        pivotArrayTwoPointer(new int[] {9, 12, 5, 10, 14, 3, 10}, 10));

    assertArrayEquals(new int[] {-3, 2, 4, 3}, pivotArrayTwoPointer(new int[] {-3, 4, 3, 2}, 2));
  }

  @Test
  public void twoSumTest() {
    assertArrayEquals(new int[] {0, 1}, twoSum(new int[] {2, 7, 11, 15}, 9));

    assertArrayEquals(new int[] {1, 2}, twoSum(new int[] {3, 2, 4}, 6));
  }

  @Test
  public void addTwoNumbersTest() {
    ListNode l1 = new ListNode(2, new ListNode(4, new ListNode(3)));
    ListNode l2 = new ListNode(5, new ListNode(6, new ListNode(4)));

    ListNode res = addTwoNumbers(l1, l2);
    var r = res;
    while (r != null) {
      System.out.println(r.val);
      r = r.next;
    }
    assertNotNull(res);
  }

  @Test
  public void reverseListLinkedListTest() {
    ListNode list = new ListNode(2, new ListNode(4, new ListNode(3)));

    var res = reverseList(list);
    var r = res;
    while (r != null) {
      System.out.println(r.val);
      r = r.next;
    }
    assertNotNull(res);
  }

  @Test
  public void longestCommonPrefixTest() {
    assertEquals("fl", longestCommonPrefix(new String[] {"flower", "flow", "flight"}));
    assertEquals("", longestCommonPrefix(new String[] {"dog", "racecar", "car"}));
    assertEquals("", longestCommonPrefix(new String[] {"abc", "ab", "efg"}));
    assertEquals("ab", longestCommonPrefix(new String[] {"abcde", "abc", "abxyz"}));
    assertEquals("fl", longestCommonPrefix(new String[] {"flower", "flow", "flock", "fl"}));
    assertEquals("a", longestCommonPrefix(new String[] {"ab", "a", "ac"}));
    assertEquals("", longestCommonPrefix(new String[] {"", ""}));
    assertEquals("", longestCommonPrefix(new String[] {"abc", ""}));
    assertEquals("", longestCommonPrefix(new String[] {"reflower", "flow", "flight"}));
    assertEquals("a", longestCommonPrefix(new String[] {"acc", "aaa", "aaba"}));
  }

  @Test
  public void maxAreaTest() {
    assertEquals(49, maxArea(new int[] {1, 8, 6, 2, 5, 4, 8, 3, 7}));
  }

  @Test
  public void removeElementTest() {

    int[] arr = new int[] {2, 3, 2};
    assertEquals(1, removeElement(arr, 2));

    int[] arr2 = new int[] {2, 2, 3, 3};
    assertEquals(2, removeElement(arr2, 2));

    int[] arr3 = new int[] {3, 2, 2, 3};
    assertEquals(2, removeElement(arr3, 3));

    int[] arr4 = new int[] {0, 1, 2, 2, 3, 0, 4, 2};
    assertEquals(5, removeElement(arr4, 2));
  }
}
