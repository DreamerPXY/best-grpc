package com.pxyc.grpc.spring.boot.autoconfigure.server;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.net.SocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


public class DefaultGrpcServerConfigProps {
    /**
     * 是否启用 gRPC Server
     */
    private boolean enabled = false;

    /**
     * 监听地址
     *
     * @see NettyServerBuilder#forAddress(SocketAddress)
     */
    private String host = "0.0.0.0";

    /**
     * 监听端口
     *
     * @see NettyServerBuilder#forAddress(SocketAddress)
     * @see ServerBuilder#forPort(int)
     * @see NettyServerBuilder#forPort(int)
     */
    private int port = 0;

    /**
     * @see NettyServerBuilder#maxConcurrentCallsPerConnection(int)
     */
    private Integer maxConcurrentCallsPerConnection;

    /**
     * @see NettyServerBuilder#initialFlowControlWindow(int)
     */
    private Integer initialFlowControlWindow;

    /**
     * @see NettyServerBuilder#flowControlWindow(int)
     */
    private Integer flowControlWindow;

    /**
     * @see ServerBuilder#maxInboundMessageSize(int)
     */
    private Integer maxInboundMessageSize;

    /**
     * @see ServerBuilder#maxInboundMetadataSize(int)
     */
    private Integer maxInboundMetadataSize;

    /**
     * @see NettyServerBuilder#keepAliveTime(long, TimeUnit)
     */
    private Duration keepAliveTime;

    /**
     * @see NettyServerBuilder#keepAliveTimeout(long, TimeUnit)
     */
    private Duration keepAliveTimeout;

    /**
     * @see NettyServerBuilder#maxConnectionIdle(long, TimeUnit)
     */
    private Duration maxConnectionIdle;

    /**
     * @see NettyServerBuilder#maxConnectionAge(long, TimeUnit)
     */
    private Duration maxConnectionAge;

    /**
     * @see NettyServerBuilder#maxConnectionAgeGrace(long, TimeUnit)
     */
    private Duration maxConnectionAgeGrace;

    /**
     * @see NettyServerBuilder#permitKeepAliveTime(long, TimeUnit)
     */
    private Duration permitKeepAliveTime;

    /**
     * @see NettyServerBuilder#permitKeepAliveWithoutCalls(boolean)
     */
    private Boolean permitKeepAliveWithoutCalls;

    /**
     * @see ServerBuilder#handshakeTimeout(long, TimeUnit)
     */
    private Duration handshakeTimeout;

    /**
     * 是否启用 grpc 客户端调用日志
     */
    private Boolean callLogEnabled;

    /**
     * 此 {@link ServerBuilder} 配置器的 beanName,
     * 配置器 bean 的类型必须为 {@code Consumer<ServerBuilder<?>>}
     *
     * @see DefaultGrpcServerBuilderSupplier#get()
     */
    private String[] configurerBeanNames;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Integer getMaxConcurrentCallsPerConnection() {
        return maxConcurrentCallsPerConnection;
    }

    public void setMaxConcurrentCallsPerConnection(Integer maxConcurrentCallsPerConnection) {
        this.maxConcurrentCallsPerConnection = maxConcurrentCallsPerConnection;
    }

    public Integer getInitialFlowControlWindow() {
        return initialFlowControlWindow;
    }

    public void setInitialFlowControlWindow(Integer initialFlowControlWindow) {
        this.initialFlowControlWindow = initialFlowControlWindow;
    }

    public Integer getFlowControlWindow() {
        return flowControlWindow;
    }

    public void setFlowControlWindow(Integer flowControlWindow) {
        this.flowControlWindow = flowControlWindow;
    }

    public Integer getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setMaxInboundMessageSize(Integer maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public Integer getMaxInboundMetadataSize() {
        return maxInboundMetadataSize;
    }

    public void setMaxInboundMetadataSize(Integer maxInboundMetadataSize) {
        this.maxInboundMetadataSize = maxInboundMetadataSize;
    }

    public Duration getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public Duration getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(Duration keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public Duration getMaxConnectionIdle() {
        return maxConnectionIdle;
    }

    public void setMaxConnectionIdle(Duration maxConnectionIdle) {
        this.maxConnectionIdle = maxConnectionIdle;
    }

    public Duration getMaxConnectionAge() {
        return maxConnectionAge;
    }

    public void setMaxConnectionAge(Duration maxConnectionAge) {
        this.maxConnectionAge = maxConnectionAge;
    }

    public Duration getMaxConnectionAgeGrace() {
        return maxConnectionAgeGrace;
    }

    public void setMaxConnectionAgeGrace(Duration maxConnectionAgeGrace) {
        this.maxConnectionAgeGrace = maxConnectionAgeGrace;
    }

    public Duration getPermitKeepAliveTime() {
        return permitKeepAliveTime;
    }

    public void setPermitKeepAliveTime(Duration permitKeepAliveTime) {
        this.permitKeepAliveTime = permitKeepAliveTime;
    }

    public Boolean getPermitKeepAliveWithoutCalls() {
        return permitKeepAliveWithoutCalls;
    }

    public void setPermitKeepAliveWithoutCalls(Boolean permitKeepAliveWithoutCalls) {
        this.permitKeepAliveWithoutCalls = permitKeepAliveWithoutCalls;
    }

    public Duration getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(Duration handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public Boolean getCallLogEnabled() {
        return callLogEnabled;
    }

    public void setCallLogEnabled(Boolean callLogEnabled) {
        this.callLogEnabled = callLogEnabled;
    }

    public String[] getConfigurerBeanNames() {
        return configurerBeanNames;
    }

    public void setConfigurerBeanNames(String[] configurerBeanNames) {
        this.configurerBeanNames = configurerBeanNames;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
