package com.lxkj.common.util.collection;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Guang YANG
 */
public class ExpiryMap<K, V> extends EnhancedLinkedHashMap<K, V> {

  private final ExpiryMap<K, V> self = this;
  private final Map<K, Long> clock = new HashMap<>();

  public ExpiryMap(long expireInSeconds) {
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        synchronized (self) {
          long currentTime = System.currentTimeMillis() / 1000;
          for (Iterator<Entry<K, Long>> iterator = clock.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<K, Long> entry = iterator.next();
            long savedTime = entry.getValue();
            // Remove if expires
            if (currentTime - savedTime > expireInSeconds) {
              self.remove(entry.getKey());
            }
          }
        }
      }
    }, 0, expireInSeconds);
  }

  @Override
  public synchronized V put(K key, V value) {
    this.clock.put(key, System.currentTimeMillis() / 1000);
    return super.put(key, value);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    if (m != null) {
      m.forEach(this::put);
    }
  }

  @Override
  public V putIfAbsent(K key, V value) {
    if (!this.reallyHas(key)) {
      return this.put(key, value);
    }
    return this.get(key);
  }

  @Override
  public V replace(K key, V value) {
    if (this.reallyHas(key)) {
      return this.put(key, value);
    }
    return null;
  }

}
