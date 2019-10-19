package com.lxkj.common.util.collection;


import com.lxkj.common.util.Strings;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;
import java.util.stream.Collectors;

/**
 * @author Guang YANG
 */
public interface EnhancedList<E> extends List<E>, RandomAccess, Cloneable, Serializable {

  /**
   * 根据给定数字截取前1条元素并生成新列表。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @return 截取后的列表
   */
  default E first() {
    EnhancedList<E> first = this.first(1);
    return first.isEmpty() ? null : first.get(0);
  }

  /**
   * 根据给定数字截取前若干条元素并生成新列表。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @param limit 限制条数
   * @return 截取后的列表
   */
  EnhancedList<E> first(int limit);

  /**
   * 根据给定数字截取后1条元素并生成新列表。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @return 截取后的列表
   */
  default E last() {
    EnhancedList<E> last = this.last(1);
    return last.isEmpty() ? null : last.get(0);
  }

  /**
   * 根据给定数字截取后若干条元素并生成新列表。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @param limit 限制条数
   * @return 截取后的列表
   */
  default EnhancedList<E> last(int limit) {
    return this.reverse().first(limit).reverse();
  }

  /**
   * 生成一个顺序与原有列表相反的列表。
   * 对新的列表进行操作不会对原有列表进行改动。
   *
   * @return 顺序相反的列表
   */
  default EnhancedList<E> reverse() {
    EnhancedList<E> copy = this.clone();
    Collections.reverse(copy);
    return copy;
  }

  /**
   * 生成一个不可变的列表副本
   *
   * @return 不可变的列表副本
   */
  default List<E> lock() {
    return Collections.unmodifiableList(this.clone());
  }

  /**
   * 生成一个线程安全的列表副本
   *
   * @return 线程安全的列表副本
   */
  default List<E> sync() {
    return Collections.synchronizedList(this.clone());
  }

  /**
   * @see Collectors#joining()
   * @see String#join(CharSequence, CharSequence...)
   * @see String#join(CharSequence, Iterable)
   */
  default String join() {
    return this.stream().map(Strings::of).collect(Collectors.joining());
  }

  /**
   * @see Collectors#joining(CharSequence)
   * @see String#join(CharSequence, CharSequence...)
   * @see String#join(CharSequence, Iterable)
   */
  default String join(CharSequence delimiter) {
    return this.stream().map(Strings::of).collect(Collectors.joining(delimiter));
  }

  /**
   * @see Collectors#joining(CharSequence, CharSequence, CharSequence)
   */
  default String join(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
    return this.stream().map(Strings::of).collect(Collectors.joining(delimiter, prefix, suffix));
  }

  EnhancedList<E> clone();
}
