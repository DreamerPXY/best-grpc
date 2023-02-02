package com.pxyc.grpc.spring.boot.autoconfigure.server;

import com.google.common.base.Strings;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;


class DefaultGrpcServerBuilderSupplier implements Supplier<ServerBuilder<?>>, ApplicationContextAware {
    private final DefaultGrpcServerConfigProps props;
    private final List<? extends GrpcServerInternalConfigurer> internalConfigurers;
    private ApplicationContext applicationContext;

    public DefaultGrpcServerBuilderSupplier(DefaultGrpcServerConfigProps props,
                                            List<? extends GrpcServerInternalConfigurer> internalConfigurers) {
        this.props = props;
        this.internalConfigurers = internalConfigurers;
    }

    @Override
    public ServerBuilder<?> get() {
        NettyServerBuilder builder = Strings.isNullOrEmpty(props.getHost()) ? NettyServerBuilder.forPort(props.getPort()) :
                                     NettyServerBuilder.forAddress(new InetSocketAddress(props.getHost(), props.getPort()));
        if(props.getCallLogEnabled() != null && props.getCallLogEnabled()) {
            boolean needLog = true;
            try {
                Class.forName("org.apache.logging.log4j.Logger");
//                builder.intercept(Log4j2ServerCallLoggingInterceptor.INSTANCE);
                needLog = false;
                LOGGER.info("grpc server using Log4j2ServerCallLoggingInterceptor");
            } catch(ClassNotFoundException ignored) {}
            if(needLog) {
                try {
                    Class.forName("org.slf4j.Logger");
//                    builder.intercept(Slf4jServerCallLoggingInterceptor.INSTANCE);
                    needLog = false;
                    LOGGER.info("grpc client using Slf4jServerCallLoggingInterceptor");
                } catch(ClassNotFoundException ignored) {}
            }
        }

        if(props.getMaxConcurrentCallsPerConnection() != null) {
            builder.maxConcurrentCallsPerConnection(props.getMaxConcurrentCallsPerConnection());
        }
        if(props.getInitialFlowControlWindow() != null) {
            builder.initialFlowControlWindow(props.getInitialFlowControlWindow());
        }
        if(props.getFlowControlWindow() != null) {
            builder.flowControlWindow(props.getFlowControlWindow());
        }
        if(props.getMaxInboundMessageSize() != null) {
            builder.maxInboundMessageSize(props.getMaxInboundMessageSize());
        }
        if(props.getMaxInboundMetadataSize() != null) {
            builder.maxInboundMetadataSize(props.getMaxInboundMetadataSize());
        }
        if(props.getKeepAliveTime() != null) {
            builder.keepAliveTime(props.getKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(props.getKeepAliveTimeout() != null) {
            builder.keepAliveTimeout(props.getKeepAliveTimeout().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(props.getMaxConnectionIdle() != null) {
            builder.maxConnectionIdle(props.getMaxConnectionIdle().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(props.getMaxConnectionAge() != null) {
            builder.maxConnectionAge(props.getMaxConnectionAge().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(props.getMaxConnectionAgeGrace() != null) {
            builder.maxConnectionAgeGrace(props.getMaxConnectionAgeGrace().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(props.getPermitKeepAliveTime() != null) {
            builder.permitKeepAliveTime(props.getPermitKeepAliveTime().toMillis(), TimeUnit.MILLISECONDS);
        }
        if(props.getPermitKeepAliveWithoutCalls() != null) {
            builder.permitKeepAliveWithoutCalls(props.getPermitKeepAliveWithoutCalls());
        }
        if(props.getHandshakeTimeout() != null) {
            builder.handshakeTimeout(props.getHandshakeTimeout().toMillis(), TimeUnit.MILLISECONDS);
        }

        for(GrpcServerInternalConfigurer configurer : internalConfigurers) {
            configurer.accept(builder);
        }

        if(props.getConfigurerBeanNames() != null) {
            for(String configurerBeanName : props.getConfigurerBeanNames()) {
                @SuppressWarnings("unchecked") Consumer<ServerBuilder<?>> configurer =
                        applicationContext.getBean(configurerBeanName, Consumer.class);
                configurer.accept(builder);
            }
        }

        return builder;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGrpcServerBuilderSupplier.class);
}
