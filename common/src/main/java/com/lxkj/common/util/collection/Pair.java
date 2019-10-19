package com.lxkj.common.util.collection;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * @author Guang YANG
 */
public class Pair<K, V> extends SimpleEntry<K, V> {

  private static final long serialVersionUID = -6443666735668828903L;

  public Pair(K key, V value) {
    super(key, value);
  }

  public Pair(Entry<? extends K, ? extends V> entry) {
    super(entry);
  }

  public static <K, V> Pair<K, V> of(K key, V value) {
    return new Pair<>(key, value);
  }
}
