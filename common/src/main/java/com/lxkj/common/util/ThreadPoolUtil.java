package com.lxkj.common.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 */
public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPool;

    private ThreadPoolUtil() {

    }
    /**
     * Param:
     * corePoolSize - 池中所保存的线程数，包括空闲线程。
     * maximumPoolSize - 池中允许的最大线程数(采用LinkedBlockingQueue时没有作用)。
     * keepAliveTime -当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间，线程池维护线程所允许的空闲时间。
     * unit - keepAliveTime参数的时间单位，线程池维护线程所允许的空闲时间的单位:秒 。
     * workQueue - 执行前用于保持任务的队列（缓冲队列）。此队列仅保持由execute 方法提交的 Runnable 任务。
     * RejectedExecutionHandler -线程池对拒绝任务的处理策略(重试添加当前的任务，自动重复调用execute()方法)
     */
    public static ThreadPoolExecutor getInstance() {
        if (threadPool == null) {
            threadPool = new ThreadPoolExecutor(5, 10, 20, TimeUnit.SECONDS, new ArrayBlockingQueue(10),
                    new ThreadPoolExecutor.DiscardOldestPolicy());
        }
        return threadPool;
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        threadPool.shutdown();
    }
}
