package com.pxyc.grpc.spring.boot.autoconfigure.server;

import com.pxyc.grpc.spring.boot.autoconfigure.DefaultGrpcCommonAutoConfiguration;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;


@Configuration
@ConditionalOnClass(name = {"io.grpc.ServerBuilder"})
@ConditionalOnProperty(name = DefaultGrpcServerAutoConfiguration.CONFIG_PROPS_PREFIX + ".enabled", havingValue = "true")
public class DefaultGrpcServerAutoConfiguration {
    /**
     * 配置属性
     */
    @Bean
    @ConfigurationProperties(prefix = CONFIG_PROPS_PREFIX)
    public DefaultGrpcServerConfigProps configProps() {
        return new DefaultGrpcServerConfigProps();
    }

    /**
     * gRPC Server 生命周期 bean
     */
    @Bean(BEAN_NAME_LIFE_CYCLE)
    @ConditionalOnMissingBean(name = BEAN_NAME_LIFE_CYCLE)
    public DefaultGrpcServerLifecycle lifecycle(@Autowired @Qualifier(BEAN_NAME_BUILDER_FACTORY) Supplier<ServerBuilder<?>> builderSupplier,
                                                @Autowired List<? extends GrpcServerLifecycleCallback> callbacks) {
        return new DefaultGrpcServerLifecycle(builderSupplier, callbacks);
    }

    /**
     * 应用了配置的 gRPC ServerBuilder 工厂
     */
    @Bean(BEAN_NAME_BUILDER_FACTORY)
    @ConditionalOnMissingBean(name = BEAN_NAME_BUILDER_FACTORY)
    public Supplier<ServerBuilder<?>> serverBuilderFactory(@Autowired List<? extends GrpcServerInternalConfigurer> internalConfigurers) {
        return new DefaultGrpcServerBuilderSupplier(configProps(), internalConfigurers);
    }

    /**
     * 默认的业务服务发现配置器
     */
    @Bean(BEAN_NAME_BIZ_SERVICE_FINDER)
    @ConditionalOnMissingBean(name = BEAN_NAME_BIZ_SERVICE_FINDER)
    @Order(100)
    public DefaultGrpcServerBizServiceFinder bizServiceFinder() {
        return new DefaultGrpcServerBizServiceFinder();
    }

    /**
     * 默认的 gRPC Server 的执行器配置器
     */
    @Bean(BEAN_NAME_EXECUTOR_CONFIGURER)
    @ConditionalOnMissingBean(name = BEAN_NAME_EXECUTOR_CONFIGURER)
    @ConditionalOnBean(name = DefaultGrpcCommonAutoConfiguration.BEAN_NAME_EXECUTOR)
    @Order(200)
    public GrpcServerInternalConfigurer executorConfigurer(
        @Autowired @Qualifier(DefaultGrpcCommonAutoConfiguration.BEAN_NAME_EXECUTOR) Executor executor) {
        return serverBuilder -> serverBuilder.executor(executor);
    }

    public static final String BEAN_NAME_BUILDER_FACTORY = "defaultGrpcServerBuilderFactory";
    public static final String BEAN_NAME_LIFE_CYCLE = "defaultGrpcServerLifecycle";
    public static final String BEAN_NAME_BIZ_SERVICE_FINDER = "defaultGrpcServerBizServiceFinder";
    public static final String BEAN_NAME_EXECUTOR_CONFIGURER = "defaultGrpcServerExecutorConfigurer";
    public static final String CONFIG_PROPS_PREFIX = "best.grpc.server";
}
