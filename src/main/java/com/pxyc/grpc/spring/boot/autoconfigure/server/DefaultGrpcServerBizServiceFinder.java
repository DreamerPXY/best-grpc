package com.pxyc.grpc.spring.boot.autoconfigure.server;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import static org.slf4j.LoggerFactory.getLogger;


class DefaultGrpcServerBizServiceFinder implements GrpcServerInternalConfigurer, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void accept(ServerBuilder<?> serverBuilder) {
        for(ServerServiceDefinition serverServiceDefinition : applicationContext.getBeansOfType(ServerServiceDefinition.class).values()) {
            serverBuilder.addService(serverServiceDefinition);
            LOGGER.info("Grpc add biz service [" + serverServiceDefinition.getServiceDescriptor().getName() + "]");
        }
        for(BindableService bindableService : applicationContext.getBeansOfType(BindableService.class).values()) {
            ServerServiceDefinition serverServiceDefinition = bindableService.bindService();
            serverBuilder.addService(serverServiceDefinition);
            LOGGER.info("Grpc add biz service [" + serverServiceDefinition.getServiceDescriptor().getName() + "], added to grpc server");
        }
    }

    private static final Logger LOGGER = getLogger(DefaultGrpcServerBizServiceFinder.class);
}
