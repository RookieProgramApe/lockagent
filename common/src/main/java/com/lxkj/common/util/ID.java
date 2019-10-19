package com.lxkj.common.util;

import java.util.UUID;

/**
 * A utility to generate id using various strategies.
 *
 * @author Guang YANG
 * @version 1.0
 */
public class ID {

  private static final SnowflakeIdGenerator snowflake = new SnowflakeIdGenerator(0);

  private ID() {
    throw new AssertionError(Strings.INSTANTIATION_PROHIBITED);
  }

  /**
   * Generate a random guid string of 32 byte.
   */
  public static String nextGUID() {
    return UUID.randomUUID().toString().replace("-", Strings.EMPTY).toUpperCase();
  }

  /**
   * Generate a random uuid string of 36 byte.
   */
  public static String nextUUID() {
    return UUID.randomUUID().toString();
  }

  /**
   * Generate a long number of 20 bit which is monotonically increasing by each call.
   */
  public static long nextSnowflakeId() {
    return snowflake.nextId();
  }

  public static class SnowflakeIdGenerator {

    private final long workerIdBits = 10L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
      if (workerId > maxWorkerId || workerId < 0) {
        throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
      }
      this.workerId = workerId;
    }

    public synchronized long nextId() {
      long timestamp = System.currentTimeMillis();
      if (timestamp < lastTimestamp) {
        throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
      }
      if (lastTimestamp == timestamp) {
        sequence = (sequence + 1) & sequenceMask;
        if (sequence == 0) {
          timestamp = tilNextMillis(lastTimestamp);
        }
      } else {
        sequence = 0L;
      }

      lastTimestamp = timestamp;

      return (timestamp << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
      long timestamp = System.currentTimeMillis();
      while (timestamp <= lastTimestamp) {
        timestamp = System.currentTimeMillis();
      }
      return timestamp;
    }

  }

}
