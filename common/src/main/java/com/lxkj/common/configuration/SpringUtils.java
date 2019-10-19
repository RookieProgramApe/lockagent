package com.lxkj.common.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * NOTICE: NEVER USE IT WHEN REGISTERING A SPRING BEAN.
 *
 * @author Guang YANG
 */
@Component
public class SpringUtils implements ApplicationContextAware {

  private static ApplicationContext CTX;

  public static ApplicationContext get() {
    return CTX;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    CTX = applicationContext;
  }
}
