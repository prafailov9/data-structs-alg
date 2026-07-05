package com.ntros.ds.stack;

import java.util.List;

public interface Stack<E> {

  void push(E elem);
  E pop();
  boolean isEmpty();
  int size();
  E top();
  List<E> toList();

}
