package com.lxkj.common.util.collection;


import com.lxkj.common.util.Strings;

import java.util.Map;

/**
 * A utility to generate an ordered <code>java.util.Map</code> instance.
 *
 * @author Guang YANG
 * @version 1.0
 */
public final class Maps {

  private Maps() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  /**
   * Generate an empty map.
   *
   * @return An empty map
   */
  public static <K, V> EnhancedMap<K, V> of() {
    return new EnhancedLinkedHashMap<>();
  }

  /**
   * Generate a java.util.Map of a specific key-value pair.
   *
   * @param key Key of this map
   * @param value Value of this map
   * @return Generated java.util.Map instance
   */
  public static <K, V> EnhancedMap<K, V> of(K key, V value) {
    EnhancedMap<K, V> m = Maps.of();
    m.put(key, value);
    return m;
  }

  /**
   * Generate a copy of the original map instance.
   * Modification applied to the work copy will not affect the original map instance.
   *
   * @param map Original map instance
   * @return A fresh copy
   */
  public static <K, V> EnhancedMap<K, V> of(Map<? extends K, ? extends V> map) {
    EnhancedMap<K, V> m = Maps.of();
    if (map != null) {
      m.putAll(map);
    }
    return m;
  }

  /**
   * Generate a MapBuilder for fast generating a java.util.Map.
   *
   * @return MapBuilder
   */
  public static <K, V> MapBuilder<K, V> builder() {
    return MapBuilder.getInstance();
  }

}
