package com.ntros.ds.queue;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArrayBufferTest {

  private final Queue<Integer> arrayQueue = new ArrayBuffer<>(5);

  @Test
  public void randomAddRemoveTest() {

    arrayQueue.add(1);
    arrayQueue.add(2);
    arrayQueue.add(3);

    System.out.printf("%s\n", arrayQueue.remove());
    System.out.printf("%s\n", arrayQueue.remove());
    System.out.printf("%s\n", arrayQueue.remove());

    arrayQueue.add(4);
    arrayQueue.add(5);
    arrayQueue.add(6);

    System.out.printf("%s\n", arrayQueue.remove());
    System.out.printf("%s\n", arrayQueue.remove());

    arrayQueue.add(7);

    assertEquals(2, arrayQueue.size());
  }

  @Test
  public void fullThenOverwriteTest() {
    for (int i = 1; i <= 5; i++) {
      arrayQueue.add(i);
    }

    arrayQueue.add(6);
    assertEquals(5, arrayQueue.size());
  }
}
