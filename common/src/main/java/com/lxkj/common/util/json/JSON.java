package com.lxkj.common.util.json;


import com.lxkj.common.util.Strings;

import java.util.List;
import java.util.Map;

/**
 * @author Guang YANG
 */
public final class JSON {

  private static final JsonConverter CONVERTER;

  static {
    CONVERTER = new JacksonImpl();
  }

  private JSON() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  @SuppressWarnings("unchecked")
  public static <T> T parse(String json) {
    return (T) CONVERTER.parse(json, Object.class);
  }

  public static <T> T parse(String json, Class<T> objectType) {
    return CONVERTER.parse(json, objectType);
  }

  public static <K, V> Map<K, V> parseMap(String json) {
    return CONVERTER.parseMap(json);
  }

  @SuppressWarnings("unchecked")
  public static <E> List<E> parseList(String json) {
    return CONVERTER.parse(json, List.class);
  }

  public static String stringify(Object o) {
    return CONVERTER.stringify(o);
  }

}


