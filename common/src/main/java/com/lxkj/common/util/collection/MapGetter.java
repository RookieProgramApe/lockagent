package com.lxkj.common.util.collection;


import com.lxkj.common.util.Strings;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

/**
 * @author Guang YANG
 * @version 1.0
 */
public interface MapGetter<K, V> extends Map<K, V> {

  default String getString(K key) {
    Object answer = this.get(key);
    if (answer == null) {
      return Strings.EMPTY;
    }
    return answer.toString();
  }

  default boolean getBoolean(K key) {
    final Object answer = this.get(key);
    Boolean result = null;
    if (answer != null) {
      if (answer instanceof Boolean) {
        result = (Boolean) answer;
      }
      if (answer instanceof String) {
        result = Boolean.valueOf((String) answer);
      }
      if (answer instanceof Number) {
        final Number n = (Number) answer;
        result = n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
      }
    }
    return Boolean.TRUE.equals(result);
  }

  default int getInt(K key) {
    final Number answer = this.getNumber(key);
    if (answer == null) {
      return 0;
    }
    if (answer instanceof Integer) {
      return (Integer) answer;
    }
    return answer.intValue();
  }

  default long getLong(K key) {
    final Number answer = this.getNumber(key);
    if (answer == null) {
      return 0L;
    }
    if (answer instanceof Long) {
      return (Long) answer;
    }
    return answer.longValue();
  }

  default Number getNumber(K key) {
    final Object answer = this.get(key);
    if (answer != null) {
      if (answer instanceof Number) {
        return (Number) answer;
      }
      if (answer instanceof String) {
        try {
          final String text = (String) answer;
          return NumberFormat.getInstance().parse(text);
        } catch (final ParseException e) {
          // failure means null is returned
        }
      }
    }
    return null;
  }

}
