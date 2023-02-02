package com.pxyc.grpc.spring.boot.autoconfigure.server;

import io.grpc.ServerBuilder;

import java.util.function.Consumer;


public interface GrpcServerInternalConfigurer extends Consumer<ServerBuilder<?>> {}
