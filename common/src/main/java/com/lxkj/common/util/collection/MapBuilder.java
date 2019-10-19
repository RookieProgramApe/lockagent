package com.lxkj.common.util.collection;

/**
 * MapBuilder for fast generating a java.util.Map instance.
 *
 * @author Guang YANG
 */
public class MapBuilder<K, V> {

  private EnhancedMap<K, V> map = Maps.of();

  private MapBuilder() {
    // prevent from instantiation
  }

  static <K, V> MapBuilder<K, V> getInstance() {
    return new <K, V>MapBuilder<K, V>();
  }

  /**
   * @param key Key with which the specific value is to be associated
   * @param value Value to be associated with the specific key
   * @return Current MapBuilder instance for chain-invoking
   */
  public MapBuilder<K, V> put(K key, V value) {
    this.map.put(key, value);
    return this;
  }

  /**
   * Generate a java.util.Map instance.
   *
   * @return Generated map instance
   */
  public EnhancedMap<K, V> build() {
    return this.map;
  }
}
