package com.lxkj.common.util.collection;


import com.lxkj.common.util.json.JSON;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Guang YANG
 */
public class EnhancedLinkedHashMap<K, V> extends LinkedHashMap<K, V> implements EnhancedMap<K, V> {

  private static final long serialVersionUID = -9115034395997341576L;

  public EnhancedLinkedHashMap() {
    super();
  }

  public EnhancedLinkedHashMap(int initialCapacity) {
    super(initialCapacity);
  }

  public EnhancedLinkedHashMap(Map<? extends K, ? extends V> m) {
    super(m);
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
  public EnhancedMap<K, V> clone() {
    return new EnhancedLinkedHashMap<>(this);
  }
}
