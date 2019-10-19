package com.lxkj.common.util.collection;

import com.lxkj.common.util.Strings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A utility to generate a <code>java.util.List</code> instance.
 *
 * @author Guang YANG
 * @version 1.0
 */
public final class Lists {

  private Lists() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  /**
   * 生成一个空的列表。
   *
   * @return 空的列表
   */
  public static <T> EnhancedList<T> of() {
    return new EnhancedArrayList<>();
  }

  /**
   * 根据指定元素生成一个新的列表。
   *
   * @param a 元素
   * @return 新的列表
   */
  @SafeVarargs
  public static <T> EnhancedList<T> of(T... a) {
    return Lists.of(Arrays.asList(a));
  }

  /**
   * 拷贝原始列表并返回新的副本。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @param list 原始列表
   * @return 新的列表副本
   */
  public static <T> EnhancedList<T> of(Collection<? extends T> list) {
    EnhancedList<T> l = Lists.of();
    if (list != null) {
      l.addAll(list);
    }
    return l;
  }

  /**
   * 拷贝原始列表并返回新的副本。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @param iterable An iterable instance
   * @return 新的列表副本
   */
  public static <T> EnhancedList<T> of(Iterable<? extends T> iterable) {
    EnhancedList<T> l = Lists.of();
    if (iterable != null) {
      iterable.forEach(l::add);
    }
    return l;
  }

  /**
   * 按照指定数量，将原始列表分为若干区。
   * 不会对原有列表进行改动。
   *
   * @param origin 原始列表
   * @param size 拟分区数量
   * @return 分区后的列表
   */
  public static <T> List<List<T>> partition(List<T> origin, int size) {
    if (origin == null || origin.isEmpty() || size <= 0) {
      return Lists.of();
    }
    int block = origin.size() / size + (origin.size() % size > 0 ? 1 : 0);
    List<List<T>> partitions = IntStream.range(0, block)
        .boxed()
        .map(i -> {
          int start = i * size;
          int end = Math.min(start + size, origin.size());
          return origin.subList(start, end);
        })
        .collect(Collectors.toList());
    return Lists.of(partitions);
  }
}
