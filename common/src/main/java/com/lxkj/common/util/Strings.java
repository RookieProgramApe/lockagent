package com.lxkj.common.util;


import com.lxkj.common.util.collection.Lists;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Clob;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A null-safe utility to handle the String instances.
 *
 * @author Guang YANG
 * @version 1.1
 */
public final class Strings {

  public static final String EMPTY = "";
  public static final String INSTANTIATION_PROHIBITED = "Instantiation prohibited.";
  private static final Logger LOGGER = LoggerFactory.getLogger(Strings.class);

  private Strings() {
    throw new AssertionError(INSTANTIATION_PROHIBITED);
  }

  /**
   * Generate random string with alpha and numeric of given length.
   */
  public static String randomAlphanumeric(int length) {
    return new RandomString(length).nextString();
  }

  public static String randomNumeric(int length) {
    return new RandomString(length, new SecureRandom(), "0123456789").nextString();
  }

  /**
   * Null-safe toString, with Array support.
   */
  public static String of(Object o) {
    if (o == null) {
      return EMPTY;
    }
    if (o.getClass().isArray()) {
      return Strings.ofArray(o);
    }
    return o.toString();
  }

  /**
   * A null-safe warp for java.lang.String.
   */
  public static String of(String s) {
    return s == null ? EMPTY : s;
  }

  /**
   * Get full stack logs from a Throwable instance.
   */
  public static String of(Throwable e) {
    if (e == null) {
      return EMPTY;
    }
    StringWriter traceWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(traceWriter);
    e.printStackTrace(printWriter);
    return traceWriter.toString();
  }

  /**
   * Get substring from a clob.
   */
  public static String of(Clob clob) {
    if (clob == null) {
      return EMPTY;
    }
    String string = EMPTY;
    try {
      int size = (int) clob.length();
      string = clob.getSubString(1, size);
    } catch (Exception e) {
      LOGGER.debug(e.getMessage());
    }
    return string;
  }

  /**
   * @see Arrays#toString(long[])
   * @see Arrays#toString(int[])
   * @see Arrays#toString(short[])
   * @see Arrays#toString(char[])
   * @see Arrays#toString(byte[])
   * @see Arrays#toString(boolean[])
   * @see Arrays#toString(float[])
   * @see Arrays#toString(double[])
   * @see Arrays#deepToString(Object[])
   */
  public static String ofArray(Object array) {
    if (array == null || !array.getClass().isArray()) {
      LOGGER.debug("Argument error: non-array instance for Strings#ofArray method.");
      return EMPTY;
    }
    try {
      boolean primitive = array.getClass().getComponentType().isPrimitive();
      if (!primitive) {
        return Arrays.deepToString((Object[]) array);
      }
      return (String) Arrays.class
          .getDeclaredMethod("toString", Class.forName(array.getClass().getName()))
          .invoke(null, array);
    } catch (Exception e) {
      LOGGER.debug(e.getMessage());
      return EMPTY;
    }
  }

  /**
   * Returns a new <code>String</code> composed of copies of an array of
   * objects joined together with an empty string.
   *
   * @since 1.1
   */
  public static String join(Object... objects) {
    return Strings.join(objects, EMPTY);
  }

  /**
   * Returns a new <code>String</code> composed of copies of an array of
   * objects joined together with a copy of the specified {@code delimiter}.
   *
   * @since 1.1
   */
  public static String join(Object[] objects, CharSequence delimiter) {
    if (objects == null) {
      return EMPTY;
    }
    return Strings.join(Lists.of(objects), delimiter);
  }

  /**
   * Returns a new <code>String</code> composed of copies of a list of
   * objects joined together with an empty string.
   */
  public static String join(Collection<?> list) {
    return Strings.join(list, EMPTY);
  }

  /**
   * Returns a new <code>String</code> composed of copies of a list of
   * objects joined together with a copy of the specified {@code delimiter}.
   *
   * @since 1.1
   */
  public static String join(Collection<?> list, CharSequence delimiter) {
    if (list == null || list.isEmpty()) {
      return EMPTY;
    }
    if (delimiter == null) {
      delimiter = EMPTY;
    }
    List<String> strings = list.stream().map(Strings::of).collect(Collectors.toList());
    return String.join(delimiter, strings);
  }

  /**
   * @see MessageFormat#format(String, Object...)
   * @since 1.1
   */
  public static String format(String template, Object... arguments) {
    return MessageFormat.format(template, arguments);
  }

  /**
   * Return true if the string is null or is empty.
   */
  public static boolean isEmpty(String string) {
    return string == null || string.isEmpty();
  }


  /**
   * Return true unless the string is null nor is empty.
   */
  public static boolean isNotEmpty(String string) {
    return !Strings.isEmpty(string);
  }

  /**
   * Return true if the string is null or is empty after trim.
   */
  public static boolean isBlank(String string) {
    return string == null || string.trim().isEmpty();
  }

  /**
   * Return true unless the string is null nor is empty after trim.
   */
  public static boolean isNotBlank(String string) {
    return !Strings.isBlank(string);
  }

  /**
   * Return true if the two strings equal.
   */
  public static boolean equals(String a, String b) {
    return Objects.equals(a, b);
  }

  /**
   * Return true unless the two strings equal.
   */
  public static boolean notEquals(String a, String b) {
    return !Objects.equals(a, b);
  }

  /**
   * Null-safe trim.
   */
  public static String trim(Object o) {
    if (o == null) {
      return EMPTY;
    }
    return Strings.of(o).trim();
  }

  /**
   * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
   * "jar:file:/home/whf/foo.jar!/cn/fh" -> "/home/whf/foo.jar"
   */
  public static String getRootPath(URL url) {
    if (url == null) {
      return EMPTY;
    }
    String fileUrl = url.getFile();
    int pos = fileUrl.indexOf('!');

    if (-1 == pos) {
      return fileUrl;
    }

    return fileUrl.substring(5, pos);
  }

  /**
   * "cn.fh.lightning" -> "cn/fh/lightning"
   */
  public static String dotToSplash(String name) {
    if (isEmpty(name)) {
      return EMPTY;
    }
    return name.replaceAll("\\.", "/");
  }

  /**
   * "Apple.class" -> "Apple"
   */
  public static String trimExtension(String name) {
    if (isEmpty(name)) {
      return EMPTY;
    }
    int pos = name.lastIndexOf('.');
    if (-1 != pos) {
      return name.substring(0, pos);
    }

    return name;
  }

  /**
   * /application/home -> /home
   */
  public static String trimURI(String uri) {
    if (isEmpty(uri)) {
      return EMPTY;
    }
    String trimmed = uri.substring(1);
    int splashIndex = trimmed.indexOf('/');
    if (splashIndex == -1) {
      return uri;
    }
    return trimmed.substring(splashIndex);
  }

  /**
   * Convert a camel-patterned name to an underscore-patterned name.
   */
  public static String underscoreName(String camelName) {
    if (isEmpty(camelName)) {
      return EMPTY;
    }
    StringBuilder name = new StringBuilder();
    char[] chars = camelName.toCharArray();
    for (char c : chars) {
      if (Character.isUpperCase(c)) {
        name.append('_');
      }
      name.append(Character.toUpperCase(c));
    }
    if (name.charAt(0) == '_') {
      name.deleteCharAt(0);
    }
    return name.toString();
  }

  /**
   * Convert a underscore-patterned name to an camel-patterned name.
   */
  public static String camelName(String underscoreName) {
    if (isEmpty(underscoreName)) {
      return EMPTY;
    }
    StringBuilder name = new StringBuilder();
    char[] chars = underscoreName.toUpperCase().toCharArray();
    boolean token = false;
    for (char c : chars) {
      if (c == '_') {
        token = true;
        continue;
      }
      if (token) {
        name.append(c);
        token = false;
      } else {
        name.append(Character.toLowerCase(c));
      }
    }
    return name.toString();
  }

  /**
   * Capitalizes a String changing the first character to title case as
   * per {@link Character#toTitleCase(int)}. No other characters are changed.
   */
  public static String capitalize(String string) {
    if (isEmpty(string)) {
      return string;
    }
    char firstLetter = Character.toTitleCase(string.charAt(0));
    if (firstLetter == string.charAt(0)) {
      return string;
    }
    return firstLetter + string.substring(1);
  }
}
