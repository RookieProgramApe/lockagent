package com.lxkj.common.util.collection;


import com.lxkj.common.util.json.JSON;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Guang YANG
 */
public class EnhancedArrayList<E> extends ArrayList<E> implements EnhancedList<E> {

  private static final long serialVersionUID = -7331558616214269785L;

  public EnhancedArrayList() {
    super();
  }

  public EnhancedArrayList(int initialCapacity) {
    super(initialCapacity);
  }

  public EnhancedArrayList(Collection<? extends E> c) {
    super(c);
  }

  @Override
  public EnhancedList<E> first(int limit) {
    EnhancedList<E> copy = this.clone();
    if (limit < 0 || limit > copy.size() - 1) {
      return copy;
    }
    return new EnhancedArrayList<>(copy.subList(0, limit));
  }

  /**
   * @see Object#toString()
   */
  @Override
  public String toString() {
    return JSON.stringify(this);
  }

  /**
   * @see Object#clone()
   */
  @Override
  public EnhancedArrayList<E> clone() {
    return new EnhancedArrayList<>(this);
  }

}
