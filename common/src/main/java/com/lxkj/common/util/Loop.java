package com.lxkj.common.util;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A utility to provide functional forEach loop with index.
 *
 * @author Hao Peng
 * @author Guang YANG
 * @version 1.0
 */
public final class Loop {

  private Loop() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  /**
   * Performs the given action for each element of the {@code Iterable}
   * until all elements have been processed or the action throws an
   * exception.  Unless otherwise specified by the implementing class,
   * actions are performed in the order of iteration (if an iteration order
   * is specified).  Exceptions thrown by the action are relayed to the
   * caller.
   *
   * @param elements The <code>java.lang.Iterable</code> instance
   * @param action The action to be performed for each element
   * @see Iterable#forEach(Consumer)
   */
  public static <E> void forEach(Iterable<? extends E> elements, Consumer<? super E> action) {
    Objects.requireNonNull(elements);
    elements.forEach(action);
  }

  /**
   * Performs the given action for each element and its index of
   * the {@code Iterable} until all elements have been processed
   * or the action throws an exception.
   *
   * @param elements The <code>java.lang.Iterable</code> instance
   * @param action The action to be performed for each element
   * @see Loop#forEach(Iterable, Consumer)
   */
  public static <E> void forEach(Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
    Objects.requireNonNull(elements);
    Objects.requireNonNull(action);
    int index = 0;
    for (E element : elements) {
      action.accept(index++, element);
    }
  }

  /**
   * Performs the given action for each element of an array until
   * all elements have been processed or the action throws an exception.
   *
   * @param elements The <code>array</code>
   * @param action The action to be performed for each element
   */
  public static <E> void forEach(E[] elements, Consumer<? super E> action) {
    Objects.requireNonNull(elements);
    forEach(Arrays.asList(elements), action);
  }

  /**
   * Performs the given action for each element and its index of
   * an array until all elements have been processed or the action
   * throws an exception.
   *
   * @param elements The <code>array</code>
   * @param action The action to be performed for each element
   * @see Loop#forEach(Object[], Consumer)
   */
  public static <E> void forEach(E[] elements, BiConsumer<Integer, ? super E> action) {
    Objects.requireNonNull(elements);
    forEach(Arrays.asList(elements), action);
  }

  /**
   * Performs the given action for each entry in this map until all entries
   * have been processed or the action throws an exception.   Unless
   * otherwise specified by the implementing class, actions are performed in
   * the order of entry set iteration (if an iteration order is specified.)
   * Exceptions thrown by the action are relayed to the caller.
   *
   * @param map The <code>java.util.Map</code> instance
   * @param action The action to be performed for each element
   * @see Map#forEach(BiConsumer)
   */
  public static <K, V> void forEach(Map<? extends K, ? extends V> map, BiConsumer<? super K, ? super V> action) {
    Objects.requireNonNull(map);
    map.forEach(action);
  }



}
