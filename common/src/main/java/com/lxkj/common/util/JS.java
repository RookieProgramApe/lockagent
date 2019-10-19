package com.lxkj.common.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Guang YANG
 * @version 1.0
 */
public final class JS {

  private static final ThreadLocal<ScriptEngine> localEngine = new ThreadLocal<>();
  private static final String defaultEngineMimeType = "application/javascript";

  private JS() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  /**
   * 使用指定ScriptEngine通过Javascript实现指定接口
   *
   * @param aInterface 接口
   * @param impl 接口的Javascript函数实现
   * @param <T> 接口类型
   * @return 指定接口的Javascript实现
   * @throws ScriptException 异常
   */
  public static <T> T implement(ScriptEngine engine, Class<T> aInterface, String impl) throws ScriptException {
    engine.eval(impl);
    Invocable invoke = (Invocable) engine;
    return invoke.getInterface(aInterface);
  }

  /**
   * 通过Javascript实现指定接口
   *
   * @param aInterface 接口
   * @param impl 接口的Javascript函数实现
   * @param <T> 接口类型
   * @return 指定接口的Javascript实现
   * @throws ScriptException 异常
   */
  public static <T> T implement(Class<T> aInterface, String impl) throws ScriptException {
    return implement(localEngine(), aInterface, impl);
  }

  /**
   * 使用指定ScriptEngine执行Javascript语句
   *
   * @param expression Javascript表达式
   * @param <T> 返回类型
   * @return 计算结果
   * @throws ScriptException 异常
   */
  @SuppressWarnings("unchecked")
  public static <T> T eval(ScriptEngine engine, String expression) throws ScriptException {
    return (T) engine.eval(expression);
  }

  /**
   * 执行Javascript语句
   *
   * @param expression Javascript表达式
   * @param <T> 返回类型
   * @return 计算结果
   * @throws ScriptException 异常
   */
  public static <T> T eval(String expression) throws ScriptException {
    return eval(localEngine(), expression);
  }

  private static ScriptEngine localEngine() {
    if (localEngine.get() == null) {
      synchronized (localEngine) {
        if (localEngine.get() == null) {
          localEngine.set(newEngine());
        }
      }
    }
    return localEngine.get();
  }

  private static ScriptEngine newEngine() {
    ScriptEngineManager manager = new ScriptEngineManager();
    return manager.getEngineByMimeType(defaultEngineMimeType);
  }
}
