package com.lxkj.common.util.collection;

import java.util.Properties;

public class EnhancedProperties extends Properties implements MapGetter<Object, Object> {

  private static final long serialVersionUID = 502972551820070766L;

  public EnhancedProperties() {
    super();
  }

  public EnhancedProperties(Properties defaults) {
    super();
    putAll(defaults);
  }

  public static EnhancedProperties of(Properties defaults) {
    return new EnhancedProperties(defaults);
  }
}
