package com.pxyc.grpc.spring.boot.autoconfigure.server.nacos;

import com.pxyc.grpc.spring.boot.autoconfigure.server.DefaultGrpcServerAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties
@ConditionalOnBean(type = {"com.alibaba.nacos.spring.beans.factory.annotation.AnnotationNacosInjectedBeanPostProcessor",
                           "com.alibaba.nacos.spring.beans.factory.annotation.NamingServiceBeanBuilder"})
@ConditionalOnProperty(name = DefaultGrpcServerNacosAutoConfiguration.CONFIG_PROPS_PREFIX + ".enabled", havingValue = "true")
public class DefaultGrpcServerNacosAutoConfiguration {
    /**
     * 配置属性
     */
    @Bean(BEAN_NAME_REGISTER_CONFIG_PROPS)
    @ConfigurationProperties(prefix = CONFIG_PROPS_PREFIX)
    public DefaultGrpcServerNacosRegisterConfigProps registerConfigProps() {
        return new DefaultGrpcServerNacosRegisterConfigProps();
    }

    /**
     * 服务的注册和注销, 通过 gRPC Server 生命周期回调来实现
     */
    @Bean(BEAN_NAME_REGISTER_LIFECYCLE_CALLBACK)
    @ConditionalOnMissingBean(name = BEAN_NAME_REGISTER_LIFECYCLE_CALLBACK)
    @ConditionalOnBean(name = DefaultGrpcServerAutoConfiguration.BEAN_NAME_LIFE_CYCLE)
    public DefaultGrpcServerNacosRegisterLifecycleCallback registerLifecycleCallback() {
        return new DefaultGrpcServerNacosRegisterLifecycleCallback(registerConfigProps());
    }

    public static final String BEAN_NAME_REGISTER_CONFIG_PROPS = "defaultGrpcServerNacosRegisterConfigProps";
    public static final String BEAN_NAME_REGISTER_LIFECYCLE_CALLBACK = "defaultGrpcServerNacosRegisterLifecycleCallback";
    public static final String CONFIG_PROPS_PREFIX = "best.grpc.server.nacos";
}
