package com.pxyc.grpc.spring.boot.autoconfigure.server.nacos;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.time.Duration;


public class DefaultGrpcServerNacosRegisterConfigProps {
    /**
     * 是否启用 gRPC Server Nacos 自动配置
     */
    private boolean enabled = false;

    /**
     * @see Instance#clusterName
     */
    private String clusterName = Constants.DEFAULT_CLUSTER_NAME;

    /**
     * @see NamingService#registerInstance(String, String, Instance)
     */
    private String groupName = Constants.DEFAULT_GROUP;

    /**
     * @see Instance#serviceName
     * @see NamingService#registerInstance(String, String, Instance)
     */
    private String serviceName = "";

    /**
     * @see Instance#weight
     */
    private double weight = 1.0;

    /**
     * @see Instance#addMetadata(String, String)
     * @see Instance#getInstanceHeartBeatInterval()
     * @see PreservedMetadataKeys#HEART_BEAT_INTERVAL
     */
    private Duration heartBeatInterval = Duration.ofSeconds(10);

    /**
     * @see Instance#addMetadata(String, String)
     * @see Instance#getInstanceHeartBeatTimeOut() ()
     * @see PreservedMetadataKeys#HEART_BEAT_TIMEOUT
     */
    private Duration heartBeatTimeout = Duration.ofSeconds(30);

    /**
     * @see Instance#addMetadata(String, String)
     * @see Instance#getIpDeleteTimeout() ()
     * @see PreservedMetadataKeys#IP_DELETE_TIMEOUT
     */
    private Duration ipDeleteTimeout = Duration.ofSeconds(90);

    /**
     * 自动检测识别 IP 地址策略, 通过策略识别到的 IP 将被发布到服务注册中心.
     */
    private AutoDetectIpConfig autoDetectIp = new AutoDetectIpConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public AutoDetectIpConfig getAutoDetectIp() {
        return autoDetectIp;
    }

    public void setAutoDetectIp(AutoDetectIpConfig autoDetectIp) {
        this.autoDetectIp = autoDetectIp;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Duration getHeartBeatInterval() {
        return heartBeatInterval;
    }

    public void setHeartBeatInterval(Duration heartBeatInterval) {
        this.heartBeatInterval = heartBeatInterval;
    }

    public Duration getHeartBeatTimeout() {
        return heartBeatTimeout;
    }

    public void setHeartBeatTimeout(Duration heartBeatTimeout) {
        this.heartBeatTimeout = heartBeatTimeout;
    }

    public Duration getIpDeleteTimeout() {
        return ipDeleteTimeout;
    }

    public void setIpDeleteTimeout(Duration ipDeleteTimeout) {
        this.ipDeleteTimeout = ipDeleteTimeout;
    }

    public static class AutoDetectIpConfig {
        /**
         * 左端掩码位数
         */
        private int maskBitLen = 0;

        /**
         * 应用掩码后的匹配地址
         */
        private String match = "0.0.0.0";

        /**
         * 选择策略
         */
        private SelectPolicy selectPolicy = SelectPolicy.EXACT_ONE;

        public int getMaskBitLen() {
            return maskBitLen;
        }

        public void setMaskBitLen(int maskBitLen) {
            this.maskBitLen = maskBitLen;
        }

        public String getMatch() {
            return match;
        }

        public void setMatch(String match) {
            this.match = match;
        }

        public SelectPolicy getSelectPolicy() {
            return selectPolicy;
        }

        public void setSelectPolicy(SelectPolicy selectPolicy) {
            this.selectPolicy = selectPolicy;
        }

        public enum SelectPolicy {
            /**
             * 精确匹配一个 IP 地址
             */
            EXACT_ONE,

            /**
             * 若存在则选取第一个匹配, 若不存在则返回空匹配
             */
            PICK_NONE_OR_FIRST,

            /**
             * 精确选取第一个(下标为0)作为匹配结构
             */
            PICK_EXACT_FIRST,

            /**
             * 允许匹配一个或多个
             */
            AT_LEAST_ONE,

            /**
             * 允许任何匹配结果
             */
            ANY,
            ;
        }
    }
}
