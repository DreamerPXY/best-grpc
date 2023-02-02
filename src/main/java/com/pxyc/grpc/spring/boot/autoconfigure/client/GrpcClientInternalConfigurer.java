package com.pxyc.grpc.spring.boot.autoconfigure.client;

import io.grpc.ManagedChannelBuilder;

import java.util.function.Consumer;


public interface GrpcClientInternalConfigurer extends Consumer<ManagedChannelBuilder<?>> {}
