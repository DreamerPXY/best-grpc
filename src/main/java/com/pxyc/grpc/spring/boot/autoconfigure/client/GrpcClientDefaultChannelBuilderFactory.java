package com.pxyc.grpc.spring.boot.autoconfigure.client;


import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GrpcClientDefaultChannelBuilderFactory
        implements BiFunction<String, DefaultGrpcClientConfigProps.ChannelBuilderConfig, ManagedChannelBuilder<?>>, ApplicationContextAware {
    private final List<? extends GrpcClientInternalConfigurer> internalConfigurerList;
    private ApplicationContext applicationContext;

    public GrpcClientDefaultChannelBuilderFactory(List<? extends GrpcClientInternalConfigurer> internalConfigurerList) {
        this.internalConfigurerList = internalConfigurerList;
    }

    @Override
    public ManagedChannelBuilder<?> apply(String name, DefaultGrpcClientConfigProps.ChannelBuilderConfig config) {
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forTarget(config.getTarget());
        for(GrpcClientInternalConfigurer configurer : internalConfigurerList) {
            configurer.accept(channelBuilder);
        }
        configChannelBuilder(channelBuilder, config);
        return channelBuilder;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void configChannelBuilder(ManagedChannelBuilder<?> channelBuilder, DefaultGrpcClientConfigProps.ChannelBuilderConfig config) {
        if(config.getCallLogEnabled() != null && config.getCallLogEnabled()) {
            boolean needLog = true;
            try {
                Class.forName("org.apache.logging.log4j.Logger");
//                channelBuilder.intercept(Log4j2ClientCallLoggingInterceptor.INSTANCE);
                needLog = false;
                LOGGER.info("grpc client using Log4j2ClientCallLoggingInterceptor");
            } catch(ClassNotFoundException ignored) {}
            if(needLog) {
                try {
                    Class.forName("org.slf4j.Logger");
//                    channelBuilder.intercept(Slf4jClientCallLoggingInterceptor.INSTANCE);
                    needLog = false;
                    LOGGER.info("grpc client using Slf4jClientCallLoggingInterceptor");
                } catch(ClassNotFoundException ignored) {}
            }
        }

        if(channelBuilder instanceof NettyChannelBuilder) {
            NettyChannelBuilder nettyChannelBuilder = (NettyChannelBuilder) channelBuilder;
            if(config.getInitialFlowControlWindow() != null) {
                nettyChannelBuilder.initialFlowControlWindow(config.getInitialFlowControlWindow());
            }
            if(config.getFlowControlWindow() != null) {
                nettyChannelBuilder.flowControlWindow(config.getFlowControlWindow());
            }
            if(config.getNegotiationType() != null) {
                nettyChannelBuilder.negotiationType(config.getNegotiationType());
            }
        }

        if(config.getMaxInboundMetadataSize() != null) {
            channelBuilder.maxInboundMetadataSize(config.getMaxInboundMetadataSize());
        }

        if(config.getKeepAliveTime() != null) {
            channelBuilder.keepAliveTime(config.getKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(config.getKeepAliveTimeout() != null) {
            channelBuilder.keepAliveTimeout(config.getKeepAliveTimeout().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(config.getKeepAliveWithoutCalls() != null) {
            channelBuilder.keepAliveWithoutCalls(config.getKeepAliveWithoutCalls());
        }
        if(config.getMaxInboundMessageSize() != null) {
            channelBuilder.maxInboundMessageSize(config.getMaxInboundMessageSize());
        }
        if(config.getUserAgent() != null) {
            channelBuilder.userAgent(config.getUserAgent());
        }
        if(config.getOverrideAuthority() != null) {
            channelBuilder.overrideAuthority(config.getOverrideAuthority());
        }
        if(config.getDefaultLoadBalancingPolicy() != null) {
            channelBuilder.defaultLoadBalancingPolicy(config.getDefaultLoadBalancingPolicy());
        }
        if(config.getFullStreamDecompression() != null && config.getFullStreamDecompression()) {
            channelBuilder.enableFullStreamDecompression();
        }
        if(config.getIdleTimeout() != null) {
            channelBuilder.idleTimeout(config.getIdleTimeout().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(config.getMaxRetryAttempts() != null) {
            channelBuilder.maxRetryAttempts(config.getMaxRetryAttempts());
        }
        if(config.getMaxHedgedAttempts() != null) {
            channelBuilder.maxHedgedAttempts(config.getMaxHedgedAttempts());
        }
        if(config.getRetryBufferSize() != null) {
            channelBuilder.retryBufferSize(config.getRetryBufferSize());
        }
        if(config.getPerRpcBufferLimit() != null) {
            channelBuilder.perRpcBufferLimit(config.getPerRpcBufferLimit());
        }
        if(config.getRetry() != null) {
            if(config.getRetry()) {
                channelBuilder.enableRetry();
            } else {
                channelBuilder.disableRetry();
            }
        }
        if(config.getMaxTraceEvents() != null) {
            channelBuilder.maxTraceEvents(config.getMaxTraceEvents());
        }
        if(config.getServiceConfigLookUp() != null && !config.getServiceConfigLookUp()) {
            channelBuilder.disableServiceConfigLookUp();
        }

//        if(config.getDefaultTimeout() != null && !config.getDefaultTimeout().isNegative()) {
//            channelBuilder.intercept(new UnaryCallDefaultTimeoutClientInterceptor(config.getDefaultTimeout()));
//        }

        if(config.getConfigurerBeanNames() != null) {
            for(String configurerBeanName : config.getConfigurerBeanNames()) {
                @SuppressWarnings("unchecked") Consumer<? super ManagedChannelBuilder<?>> configurer =
                        applicationContext.getBean(configurerBeanName, Consumer.class);
                configurer.accept(channelBuilder);
            }
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcClientDefaultChannelBuilderFactory.class);
}
