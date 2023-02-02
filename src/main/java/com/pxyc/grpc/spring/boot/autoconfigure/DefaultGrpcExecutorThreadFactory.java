package com.pxyc.grpc.spring.boot.autoconfigure;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


class DefaultGrpcExecutorThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;
    private final boolean daemon;
    private final AtomicInteger threadCounter = new AtomicInteger(0);

    DefaultGrpcExecutorThreadFactory(String threadNamePrefix, boolean daemon) {
        this.threadNamePrefix = threadNamePrefix + "-" + POOL_COUNTER.getAndIncrement() + "-";
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r, threadNamePrefix + threadCounter.getAndIncrement());
        thread.setDaemon(daemon);
        return thread;
    }

    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);
}
