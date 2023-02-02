package com.pxyc.grpc.spring.boot.autoconfigure;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * gRPC 共用配置属性
 *
 */
public class DefaultGrpcCommonConfigProps {
    /**
     * 线程池执行器配置
     */
    private ThreadPoolExecutorConfig threadPool = new ThreadPoolExecutorConfig();

    public ThreadPoolExecutorConfig getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPoolExecutorConfig threadPool) {
        this.threadPool = threadPool;
    }

    public static class ThreadPoolExecutorConfig {
        /**
         * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory)
         */
        private int corePoolSize = 1024;

        /**
         * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory)
         */
        private int maximumPoolSize = 1024;

        /**
         * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory)
         */
        private Duration keepAliveTime = Duration.ofSeconds(60);

        /**
         * @see DefaultGrpcCommonAutoConfiguration#executor()
         * @see ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, TimeUnit, BlockingQueue, ThreadFactory)
         */
        private int waitQueueSize = 8192;

        /**
         * @see DefaultGrpcExecutorThreadFactory
         */
        private boolean daemon = true;

        /**
         * @see ThreadPoolExecutor#allowsCoreThreadTimeOut()
         * @see ThreadPoolExecutor#allowCoreThreadTimeOut(boolean)
         */
        private boolean allowCoreThreadTimeOut = false;

        /**
         * 线程名称前缀
         *
         * @see DefaultGrpcExecutorThreadFactory
         */
        private String threadNamePrefix = "DefaultGrpcExecutorThreadPool";

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public Duration getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(Duration keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public int getWaitQueueSize() {
            return waitQueueSize;
        }

        public void setWaitQueueSize(int waitQueueSize) {
            this.waitQueueSize = waitQueueSize;
        }

        public boolean isDaemon() {
            return daemon;
        }

        public void setDaemon(boolean daemon) {
            this.daemon = daemon;
        }

        public boolean isAllowCoreThreadTimeOut() {
            return allowCoreThreadTimeOut;
        }

        public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
    }
}
