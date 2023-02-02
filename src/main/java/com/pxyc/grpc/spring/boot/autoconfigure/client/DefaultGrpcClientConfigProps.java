package com.pxyc.grpc.spring.boot.autoconfigure.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class DefaultGrpcClientConfigProps implements Cloneable {
    /**
     * 是否启用 gRPC Client 自动配置
     */
    private boolean enabled = false;

    /**
     * 共用属性配置
     */
    private ChannelBuilderConfig basic = new ChannelBuilderConfig();

    /**
     * 配置多个 {@link ManagedChannelBuilder ChannelBuilder}, 将根据每项配置自动将创建一个 {@link ManagedChannelBuilder ChannelBuilder} 对象.
     * Key 为自定义字符串,
     * 后续可从 name 为 {@link DefaultGrpcClientAutoConfiguration#channelBuilderMap(java.util.function.BiFunction)} defaultGrpcClientChannels}
     * 的 bean 中以相同的 Key 获取配置好的 ChannelBuilder 对象.
     */
    private Map<String, ChannelBuilderConfig> channels = new HashMap<>();

    public ChannelBuilderConfig getBasic() { return basic; }

    public void setBasic(ChannelBuilderConfig basic) { this.basic = basic; }

    public Map<String, ChannelBuilderConfig> getChannels() { return channels; }

    public void setChannels(Map<String, ChannelBuilderConfig> channels) { this.channels = channels; }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public static class ChannelBuilderConfig {
        /**
         * @see ManagedChannelBuilder#forTarget(String)
         * @see NettyChannelBuilder#forTarget(String)
         * 直连请使用 localhost:{port}
         */
        private String target;

        /**
         * @see NettyChannelBuilder#initialFlowControlWindow(int)
         */
        private Integer initialFlowControlWindow;

        /**
         * @see NettyChannelBuilder#flowControlWindow(int)
         */
        private Integer flowControlWindow;

        /**
         * @see ManagedChannelBuilder#maxInboundMetadataSize(int)
         */
        private Integer maxInboundMetadataSize;

        /**
         * @see NettyChannelBuilder#negotiationType(NegotiationType)
         */
        private NegotiationType negotiationType = NegotiationType.PLAINTEXT;

        /**
         * @see ManagedChannelBuilder#keepAliveTime(long, TimeUnit)
         */
        private Duration keepAliveTime;

        /**
         * @see ManagedChannelBuilder#keepAliveTimeout(long, TimeUnit)
         */
        private Duration keepAliveTimeout;

        /**
         * @see ManagedChannelBuilder#keepAliveWithoutCalls(boolean)
         */
        private Boolean keepAliveWithoutCalls;

        /**
         * @see ManagedChannelBuilder#maxInboundMessageSize(int)
         */
        private Integer maxInboundMessageSize;

        /**
         * @see ManagedChannelBuilder#userAgent(String)
         */
        private String userAgent;

        /**
         * @see ManagedChannelBuilder#overrideAuthority(String)
         */
        private String overrideAuthority;

        /**
         * @see ManagedChannelBuilder#defaultLoadBalancingPolicy(String)
         */
        private String defaultLoadBalancingPolicy;

        /**
         * @see ManagedChannelBuilder#enableFullStreamDecompression()
         */
        private Boolean fullStreamDecompression;

        /**
         * @see ManagedChannelBuilder#idleTimeout(long, TimeUnit)
         */
        private Duration idleTimeout;

        /**
         * @see ManagedChannelBuilder#maxRetryAttempts(int)
         */
        private Integer maxRetryAttempts;

        /**
         * @see ManagedChannelBuilder#maxHedgedAttempts(int)
         */
        private Integer maxHedgedAttempts;

        /**
         * @see ManagedChannelBuilder#retryBufferSize(long)
         */
        private Long retryBufferSize;

        /**
         * @see ManagedChannelBuilder#perRpcBufferLimit(long)
         */
        private Long perRpcBufferLimit;

        /**
         * @see ManagedChannelBuilder#enableRetry()
         * @see ManagedChannelBuilder#disableRetry()
         */
        private Boolean retry;

        /**
         * @see ManagedChannelBuilder#maxTraceEvents(int)
         */
        private Integer maxTraceEvents;

        /**
         * @see ManagedChannelBuilder#disableServiceConfigLookUp()
         */
        private Boolean serviceConfigLookUp;

        /**
         * 默认超时时间
         */
        private Duration defaultTimeout;

        /**
         * 是否启用 grpc 客户端调用日志
         */
        private Boolean callLogEnabled;

        /**
         * 此 {@link ManagedChannelBuilder ChannelBuilder} 配置器的 beanName,
         * 配置器 bean 的类型必须为 {@code Consumer<ManagedChannelBuilder<?>>}
         *
         * @see GrpcClientDefaultChannelBuilderFactory#apply(String, ChannelBuilderConfig)
         */
        private String[] configurerBeanNames;

        /**
         * 自动注册名为 ${key}ChannelBuilder 类型为 {@link ManagedChannelBuilder ManagedChannelBuilder<?>} 的 bean
         *
         * @see DefaultGrpcClientChannelRegistrar
         */
        private Boolean autoRegisterChannelBuilderBean;

        /**
         * 自动注册名为 ${key}Channel 类型为 {@link Channel} 的 bean
         */
        private Boolean autoRegisterChannelBean;

        public String getTarget() { return target; }

        public void setTarget(String target) { this.target = target; }

        public Integer getInitialFlowControlWindow() { return initialFlowControlWindow; }

        public void setInitialFlowControlWindow(Integer initialFlowControlWindow) {
            this.initialFlowControlWindow = initialFlowControlWindow;
        }

        public Integer getFlowControlWindow() { return flowControlWindow; }

        public void setFlowControlWindow(Integer flowControlWindow) { this.flowControlWindow = flowControlWindow; }

        public Integer getMaxInboundMetadataSize() { return maxInboundMetadataSize; }

        public void setMaxInboundMetadataSize(Integer maxInboundMetadataSize) {
            this.maxInboundMetadataSize = maxInboundMetadataSize;
        }

        public NegotiationType getNegotiationType() { return negotiationType; }

        public void setNegotiationType(NegotiationType negotiationType) { this.negotiationType = negotiationType; }

        public Duration getKeepAliveTime() { return keepAliveTime; }

        public void setKeepAliveTime(Duration keepAliveTime) { this.keepAliveTime = keepAliveTime; }

        public Duration getKeepAliveTimeout() { return keepAliveTimeout; }

        public void setKeepAliveTimeout(Duration keepAliveTimeout) { this.keepAliveTimeout = keepAliveTimeout; }

        public Boolean getKeepAliveWithoutCalls() { return keepAliveWithoutCalls; }

        public void setKeepAliveWithoutCalls(Boolean keepAliveWithoutCalls) {
            this.keepAliveWithoutCalls = keepAliveWithoutCalls;
        }

        public Integer getMaxInboundMessageSize() { return maxInboundMessageSize; }

        public void setMaxInboundMessageSize(Integer maxInboundMessageSize) {
            this.maxInboundMessageSize = maxInboundMessageSize;
        }

        public String getUserAgent() { return userAgent; }

        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

        public String getOverrideAuthority() { return overrideAuthority; }

        public void setOverrideAuthority(String overrideAuthority) { this.overrideAuthority = overrideAuthority; }

        public String getDefaultLoadBalancingPolicy() { return defaultLoadBalancingPolicy; }

        public void setDefaultLoadBalancingPolicy(String defaultLoadBalancingPolicy) {
            this.defaultLoadBalancingPolicy = defaultLoadBalancingPolicy;
        }

        public Boolean getFullStreamDecompression() { return fullStreamDecompression; }

        public void setFullStreamDecompression(Boolean fullStreamDecompression) {
            this.fullStreamDecompression = fullStreamDecompression;
        }

        public Duration getIdleTimeout() { return idleTimeout; }

        public void setIdleTimeout(Duration idleTimeout) { this.idleTimeout = idleTimeout; }

        public Integer getMaxRetryAttempts() { return maxRetryAttempts; }

        public void setMaxRetryAttempts(Integer maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }

        public Integer getMaxHedgedAttempts() { return maxHedgedAttempts; }

        public void setMaxHedgedAttempts(Integer maxHedgedAttempts) { this.maxHedgedAttempts = maxHedgedAttempts; }

        public Long getRetryBufferSize() { return retryBufferSize; }

        public void setRetryBufferSize(Long retryBufferSize) { this.retryBufferSize = retryBufferSize; }

        public Long getPerRpcBufferLimit() { return perRpcBufferLimit; }

        public void setPerRpcBufferLimit(Long perRpcBufferLimit) { this.perRpcBufferLimit = perRpcBufferLimit; }

        public Boolean getRetry() { return retry; }

        public void setRetry(Boolean retry) { this.retry = retry; }

        public Integer getMaxTraceEvents() { return maxTraceEvents; }

        public void setMaxTraceEvents(Integer maxTraceEvents) { this.maxTraceEvents = maxTraceEvents; }

        public Boolean getServiceConfigLookUp() { return serviceConfigLookUp; }

        public void setServiceConfigLookUp(Boolean serviceConfigLookUp) {
            this.serviceConfigLookUp = serviceConfigLookUp;
        }

        public Duration getDefaultTimeout() {
            return defaultTimeout;
        }

        public void setDefaultTimeout(Duration defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
        }

        public Boolean getCallLogEnabled() {
            return callLogEnabled;
        }

        public void setCallLogEnabled(Boolean callLogEnabled) {
            this.callLogEnabled = callLogEnabled;
        }

        public String[] getConfigurerBeanNames() { return configurerBeanNames; }

        public void setConfigurerBeanNames(String[] configurerBeanNames) {
            this.configurerBeanNames = configurerBeanNames;
        }

        public Boolean getAutoRegisterChannelBuilderBean() { return autoRegisterChannelBuilderBean; }

        public void setAutoRegisterChannelBuilderBean(Boolean autoRegisterChannelBuilderBean) {
            this.autoRegisterChannelBuilderBean = autoRegisterChannelBuilderBean;
        }

        public Boolean getAutoRegisterChannelBean() { return autoRegisterChannelBean; }

        public void setAutoRegisterChannelBean(Boolean autoRegisterChannelBean) {
            this.autoRegisterChannelBean = autoRegisterChannelBean;
        }

        @Override
        public ChannelBuilderConfig clone() {
            ChannelBuilderConfig cloned = new ChannelBuilderConfig();
            cloned.mergeFrom(this);
            return cloned;
        }

        public void mergeFrom(ChannelBuilderConfig otherConfig) {
            if(otherConfig.target != null) { target = otherConfig.target; }
            if(otherConfig.initialFlowControlWindow != null) {
                initialFlowControlWindow = otherConfig.initialFlowControlWindow;
            }
            if(otherConfig.flowControlWindow != null) { flowControlWindow = otherConfig.flowControlWindow; }
            if(otherConfig.maxInboundMetadataSize != null) {
                maxInboundMetadataSize = otherConfig.maxInboundMetadataSize;
            }
            if(otherConfig.negotiationType != null) { negotiationType = otherConfig.negotiationType; }
            if(otherConfig.keepAliveTime != null) { keepAliveTime = otherConfig.keepAliveTime; }
            if(otherConfig.keepAliveTimeout != null) { keepAliveTimeout = otherConfig.keepAliveTimeout; }
            if(otherConfig.keepAliveWithoutCalls != null) { keepAliveWithoutCalls = otherConfig.keepAliveWithoutCalls; }
            if(otherConfig.maxInboundMessageSize != null) { maxInboundMessageSize = otherConfig.maxInboundMessageSize; }
            if(otherConfig.userAgent != null) { userAgent = otherConfig.userAgent; }
            if(otherConfig.overrideAuthority != null) { overrideAuthority = otherConfig.overrideAuthority; }
            if(otherConfig.defaultLoadBalancingPolicy != null) {
                defaultLoadBalancingPolicy = otherConfig.defaultLoadBalancingPolicy;
            }
            if(otherConfig.fullStreamDecompression != null) {
                fullStreamDecompression = otherConfig.fullStreamDecompression;
            }
            if(otherConfig.idleTimeout != null) { idleTimeout = otherConfig.idleTimeout; }
            if(otherConfig.maxRetryAttempts != null) { maxRetryAttempts = otherConfig.maxRetryAttempts; }
            if(otherConfig.maxHedgedAttempts != null) { maxHedgedAttempts = otherConfig.maxHedgedAttempts; }
            if(otherConfig.retryBufferSize != null) { retryBufferSize = otherConfig.retryBufferSize; }
            if(otherConfig.perRpcBufferLimit != null) { perRpcBufferLimit = otherConfig.perRpcBufferLimit; }
            if(otherConfig.retry != null) { retry = otherConfig.retry; }
            if(otherConfig.maxTraceEvents != null) { maxTraceEvents = otherConfig.maxTraceEvents; }
            if(otherConfig.serviceConfigLookUp != null) { serviceConfigLookUp = otherConfig.serviceConfigLookUp; }
            if(otherConfig.defaultTimeout != null) { defaultTimeout = otherConfig.defaultTimeout; }
            if(otherConfig.callLogEnabled != null) { callLogEnabled = otherConfig.callLogEnabled; }
            if(otherConfig.configurerBeanNames != null) {
                HashSet<String> newBeanNameSet = new HashSet<>();
                if(configurerBeanNames != null) { newBeanNameSet.addAll(Arrays.asList(configurerBeanNames)); }
                newBeanNameSet.addAll(Arrays.asList(otherConfig.configurerBeanNames));
                configurerBeanNames = newBeanNameSet.toArray(new String[0]);
            }
            if(otherConfig.autoRegisterChannelBuilderBean != null) {
                autoRegisterChannelBuilderBean = otherConfig.autoRegisterChannelBuilderBean;
            }
            if(otherConfig.autoRegisterChannelBean != null) {
                autoRegisterChannelBean = otherConfig.autoRegisterChannelBean;
            }
        }
    }
}
