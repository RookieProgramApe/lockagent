package com.lxkj.common.util.collection;


import com.lxkj.common.util.Strings;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Guang YANG
 */
public interface EnhancedMap<K, V> extends Map<K, V>, MapGetter<K, V> {

  /**
   * Generate a map without nulls or empty strings.
   * Modification applied to the returning map will not affect the original map instance.
   *
   * @return Map without nulls or empty strings
   */
  @SuppressWarnings("unchecked")
  default EnhancedMap<K, V> trim() {
    EnhancedMap<K, V> m = this.clone();
    for (Iterator<Entry<K, V>> iterator = m.entrySet().iterator(); iterator.hasNext(); ) {
      Entry<K, V> entry = iterator.next();
      if (entry.getValue() == null) {
        iterator.remove();
      } else if (entry.getValue() instanceof String) {
        String string = (String) entry.getValue();
        if (Strings.isBlank(string)) {
          iterator.remove();
        } else {
          m.put(entry.getKey(), (V) Strings.trim(entry.getValue()));
        }
      }
    }
    return m;
  }

  /**
   * Test whether the map really has a value associated with the key.
   *
   * @param key key whose presence in this map is to be tested
   * @return Whether the map really has a value associated with the key.
   */
  default boolean reallyHas(K key) {
    V answer = this.get(key);
    if (answer == null) {
      return false;
    }
    if (answer instanceof String) {
      String string = (String) answer;
      return Strings.isNotEmpty(string);
    }
    return true;
  }

  /**
   * Put-and-return for chain invoke.
   */
  default EnhancedMap<K, V> of(K key, V value) {
    this.put(key, value);
    return this;
  }

  /**
   * Generate a unmodifiable map copy.
   */
  default Map<K, V> lock() {
    return Collections.unmodifiableMap(this.clone());
  }

  default Map<K, V> sync() {
    return Collections.synchronizedMap(this.clone());
  }

  EnhancedMap<K, V> clone();

}
