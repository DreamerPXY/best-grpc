package com.pxyc.grpc.spring.boot.autoconfigure.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.grpc.health.v1.HealthCheckResponse.ServingStatus;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.slf4j.Logger;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;


class DefaultGrpcServerLifecycle implements SmartLifecycle {
    private final Supplier<? extends ServerBuilder<?>> serverBuilderSupplier;
    private final List<? extends GrpcServerLifecycleCallback> grpcServerLifecycleCallbacks;
    private Server grpcServer;
    private io.grpc.protobuf.services.HealthStatusManager healthStatusManager;

    public DefaultGrpcServerLifecycle(Supplier<? extends ServerBuilder<?>> serverBuilderSupplier,
                                      List<? extends GrpcServerLifecycleCallback> grpcServerLifecycleCallbacks) {
        this.serverBuilderSupplier = serverBuilderSupplier;
        this.grpcServerLifecycleCallbacks = grpcServerLifecycleCallbacks;
        healthStatusManager = new io.grpc.protobuf.services.HealthStatusManager();
    }

    @Override
    public synchronized void start() {
        if(grpcServer != null) {
            return;
        }

        healthStatusManager = new io.grpc.protobuf.services.HealthStatusManager();
        grpcServer = serverBuilderSupplier
                .get()
//                .intercept(ExtServerInterceptor.INSTANCE)
                .addService(ProtoReflectionService.newInstance())
                .addService(healthStatusManager.getHealthService())
                .build();
        for(ServerServiceDefinition service : grpcServer.getServices()) {
            healthStatusManager.setStatus(service.getServiceDescriptor().getName(), ServingStatus.SERVING);
        }

        try {
            grpcServer.start();
        } catch(IOException e) {
            throw new RuntimeException("Failed to start grpc server", e);
        }

        LOGGER.info("grpc server started on " +
                    grpcServer.getListenSockets().stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]")));
        LOGGER.info("grpc serving such services: " + grpcServer
                .getServices()
                .stream()
                .map(ssd -> ssd.getServiceDescriptor().getName())
                .collect(Collectors.joining(", ", "[", "]")));

        Thread thread = new Thread("GRPC-Server-Termination-Waiter-" + SERVER_WAITER_COUNTER.getAndIncrement()) {
            @Override
            public void run() {
                LOGGER.info("Thread [" + getName() + "] running");
                try {
                    grpcServer.awaitTermination();
                } catch(InterruptedException e) {
                    LOGGER.error("Thread [" + getName() + "] interrupted and quit", e);
                }
            }
        };
        thread.setDaemon(false);
        thread.start();

        for(GrpcServerLifecycleCallback callback : grpcServerLifecycleCallbacks) {
            callback.onStart(new GrpcServerLifecycleCallback.ServerInfo(grpcServer));
        }
    }

    @Override
    public synchronized void stop() {
        if(grpcServer != null) {
            for(GrpcServerLifecycleCallback callback : grpcServerLifecycleCallbacks) {
                callback.onStop();
            }
            healthStatusManager.enterTerminalState();
            grpcServer.shutdown();
            grpcServer = null;
            LOGGER.info("GRPC server is shutting down");
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return grpcServer != null && !grpcServer.isShutdown() && !grpcServer.isTerminated();
    }

    @Override
    public synchronized void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    private static final AtomicInteger SERVER_WAITER_COUNTER = new AtomicInteger(0);
    private static final Logger LOGGER = getLogger(DefaultGrpcServerLifecycle.class);
}
