package com.pxyc.grpc.spring.boot.autoconfigure.client;

import com.google.common.collect.ImmutableMap;
import com.pxyc.grpc.spring.boot.autoconfigure.DefaultGrpcCommonAutoConfiguration;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(name = DefaultGrpcClientAutoConfiguration.CONFIG_PROPS_PREFIX + ".enabled", havingValue = "true")
public class DefaultGrpcClientAutoConfiguration {
    /**
     * 客户端配置属性
     */
    @Bean(BEAN_NAME_CONFIG_PROPS)
    @ConfigurationProperties(prefix = CONFIG_PROPS_PREFIX)
    public DefaultGrpcClientConfigProps configProps() {
        return new DefaultGrpcClientConfigProps();
    }

    /**
     * 默认的 {@link NettyChannelBuilder} 工厂, 用户可根据需求覆盖 Bean 实现
     */
    @Bean(BEAN_NAME_DEFAULT_CHANNEL_BUILDER_FACTORY)
    @ConditionalOnMissingBean(name = BEAN_NAME_DEFAULT_CHANNEL_BUILDER_FACTORY)
    @ConditionalOnClass(name = {"io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder"})
    public GrpcClientDefaultChannelBuilderFactory defaultChannelBuilderFactory(
            @Autowired List<? extends GrpcClientInternalConfigurer> internalConfigurerList) {
        return new GrpcClientDefaultChannelBuilderFactory(internalConfigurerList);
    }

    /**
     * 用户自定义配置的 {@link ManagedChannelBuilder} Map, Key 与 {@link DefaultGrpcClientConfigProps#channels 配置属性} 中相同
     */
    @Bean(BEAN_NAME_CHANNEL_BUILDER_MAP)
    @ConditionalOnMissingBean(name = BEAN_NAME_CHANNEL_BUILDER_MAP)
    public Map<String, ManagedChannelBuilder<?>> channelBuilderMap(
            @Autowired @Qualifier(BEAN_NAME_DEFAULT_CHANNEL_BUILDER_FACTORY) BiFunction<String, DefaultGrpcClientConfigProps.ChannelBuilderConfig, ManagedChannelBuilder<?>> factory) {
        ImmutableMap.Builder<String, ManagedChannelBuilder<?>> builder = ImmutableMap.builder();
        DefaultGrpcClientConfigProps props = configProps();
        props.getChannels().forEach((name, config) -> {
            DefaultGrpcClientConfigProps.ChannelBuilderConfig mergeConfig = props.getBasic().clone();
            mergeConfig.mergeFrom(config);
            builder.put(name, factory.apply(name, mergeConfig));
        });
        return builder.build();
    }

    /**
     * gRPC 客户端执行器配置器
     */
    @Bean(BEAN_NAME_EXECUTOR_CONFIGURER)
    @ConditionalOnMissingBean(name = BEAN_NAME_EXECUTOR_CONFIGURER)
    @ConditionalOnBean(name = DefaultGrpcCommonAutoConfiguration.BEAN_NAME_EXECUTOR)
    @Order(100)
    public GrpcClientInternalConfigurer executorConfigurer(
            @Autowired @Qualifier(DefaultGrpcCommonAutoConfiguration.BEAN_NAME_EXECUTOR) Executor executor) {
        return channelBuilder -> channelBuilder.directExecutor().offloadExecutor(executor);
    }

    public static final String BEAN_NAME_EXECUTOR_CONFIGURER = "defaultGrpcClientExecutorConfigurer";
    public static final String BEAN_NAME_CHANNEL_BUILDER_MAP = "defaultGrpcClientChannelBuilderMap";
    public static final String BEAN_NAME_DEFAULT_CHANNEL_BUILDER_FACTORY = "grpcClientDefaultChannelBuilderFactory";
    public static final String BEAN_NAME_CONFIG_PROPS = "defaultGrpcClientConfigProps";
    public static final String CONFIG_PROPS_PREFIX = "best.grpc.client";

    @Bean(DefaultGrpcClientChannelRegistrar.BEAN_NAME)
    public static DefaultGrpcClientChannelRegistrar channelRegistrar() {
        return new DefaultGrpcClientChannelRegistrar();
    }
}
