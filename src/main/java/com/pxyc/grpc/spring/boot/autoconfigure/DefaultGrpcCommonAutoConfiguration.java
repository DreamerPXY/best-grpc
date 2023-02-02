package com.pxyc.grpc.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * gRPC 共用自动配置
 */
@Configuration
@EnableConfigurationProperties
public class DefaultGrpcCommonAutoConfiguration {

    /**
     * 配置属性
     */
    @Bean(BEAN_NAME_CONFIG_PROPS)
    @ConditionalOnMissingBean(name = BEAN_NAME_CONFIG_PROPS)
    @ConfigurationProperties(prefix = CONFIG_PROPS_PREFIX)
    public DefaultGrpcCommonConfigProps configProps() {
        return new DefaultGrpcCommonConfigProps();
    }

    /**
     * 默认提供的线程池执行器
     */
    @Bean(name = BEAN_NAME_EXECUTOR, destroyMethod = "shutdown")
    @ConditionalOnMissingBean(name = BEAN_NAME_EXECUTOR)
    public ThreadPoolExecutor executor() {
        DefaultGrpcCommonConfigProps.ThreadPoolExecutorConfig executorConfig = configProps().getThreadPool();

        BlockingQueue<Runnable> queue;
        if(executorConfig.getWaitQueueSize() == 0) {
            queue = new SynchronousQueue<>();
        } else if(executorConfig.getWaitQueueSize() < 0 || executorConfig.getWaitQueueSize() == Integer.MAX_VALUE) {
            queue = new LinkedBlockingQueue<>();
        } else {
            queue = new ArrayBlockingQueue<>(executorConfig.getWaitQueueSize());
        }

        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(executorConfig.getCorePoolSize(), executorConfig.getMaximumPoolSize(),
                                                               executorConfig.getKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS, queue,
                                                               new DefaultGrpcExecutorThreadFactory(executorConfig.getThreadNamePrefix(),
                                                                                                    executorConfig.isDaemon()));
        threadPool.allowCoreThreadTimeOut(executorConfig.isAllowCoreThreadTimeOut());
        return threadPool;
    }

    public static final String BEAN_NAME_EXECUTOR = "defaultGrpcExecutor";
    public static final String BEAN_NAME_CONFIG_PROPS = "defaultGrpcCommonConfigProps";
    public static final String CONFIG_PROPS_PREFIX = "best.grpc.common";
}
