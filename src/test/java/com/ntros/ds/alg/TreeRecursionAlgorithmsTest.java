package com.ntros.ds.alg;

import static com.ntros.ds.alg.TreeRecursionAlgorithms.height;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TreeRecursionAlgorithmsTest {

  @Test
  public void heightTest() {
    TreeNode a = new TreeNode(1);
    TreeNode b = new TreeNode(2);
    TreeNode c = new TreeNode(3);
    TreeNode d = new TreeNode(4);
    TreeNode e = new TreeNode(5);
    TreeNode f = new TreeNode(6);
    TreeNode g = new TreeNode(7);
    TreeNode h = new TreeNode(8);

    a.left = b;
    a.right = c;
    b.left = d;
    d.right = e;

    c.right = f;
    f.right = g;
    e.right = h;

    int res = height(a);
    assertEquals(5, res);
  }
}
