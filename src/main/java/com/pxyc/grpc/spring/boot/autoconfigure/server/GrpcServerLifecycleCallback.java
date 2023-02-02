package com.pxyc.grpc.spring.boot.autoconfigure.server;

import io.grpc.Server;


public interface GrpcServerLifecycleCallback {
    void onStart(ServerInfo serverInfo);

    void onStop();

    class ServerInfo {
        private final Server grpcServer;

        public ServerInfo(Server grpcServer) {
            this.grpcServer = grpcServer;
        }

        public Server getGrpcServer() {
            return grpcServer;
        }
    }
}
