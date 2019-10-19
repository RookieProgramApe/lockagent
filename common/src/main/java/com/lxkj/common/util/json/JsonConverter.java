package com.lxkj.common.util.json;

import java.util.List;
import java.util.Map;

interface JsonConverter {

  <T> T parse(String json);

  <T> T parse(String json, Class<T> objectType);

  <K, V> Map<K, V> parseMap(String json);

  <E> List<E> parseList(String json);

  String stringify(Object o);
}
