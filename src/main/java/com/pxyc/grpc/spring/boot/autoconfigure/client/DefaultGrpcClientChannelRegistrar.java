package com.pxyc.grpc.spring.boot.autoconfigure.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * 根据配置属性 {@link DefaultGrpcClientConfigProps#channels channels} 的配置,
 * 自动注册名为 ${key}ChannelBuilder 类型为 {@link ManagedChannelBuilder ManagedChannelBuilder<?>},
 * 以及名为 ${key}Channel 类型为 {@link Channel} 的 bean.
 */
public class DefaultGrpcClientChannelRegistrar implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
        DefaultGrpcClientConfigProps props = Binder
                .get(applicationContext.getEnvironment())
                .bind(DefaultGrpcClientAutoConfiguration.CONFIG_PROPS_PREFIX, Bindable.of(DefaultGrpcClientConfigProps.class))
                .orElseGet(DefaultGrpcClientConfigProps::new);

        boolean basicRegChanBdr =
                props.getBasic().getAutoRegisterChannelBuilderBean() == null || props.getBasic().getAutoRegisterChannelBuilderBean();
        boolean basicRegChan = props.getBasic().getAutoRegisterChannelBean() == null || props.getBasic().getAutoRegisterChannelBean();

        props.getChannels().forEach((name, config) -> {
            String channelBuilderBeanName = name + "ChannelBuilder";
            if(!registry.containsBeanDefinition(channelBuilderBeanName) &&
               (config.getAutoRegisterChannelBuilderBean() == null ? basicRegChanBdr : config.getAutoRegisterChannelBuilderBean())) {
                GenericBeanDefinition channelBuilderBean = new GenericBeanDefinition();
                channelBuilderBean.setBeanClass(ManagedChannelBuilder.class);
                channelBuilderBean.setFactoryBeanName(BEAN_NAME);
                channelBuilderBean.setFactoryMethodName("getChannelBuilder");
                channelBuilderBean.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
                ConstructorArgumentValues channelBuilderArg0 = new ConstructorArgumentValues();
                channelBuilderArg0.addIndexedArgumentValue(0, name);
                channelBuilderBean.setConstructorArgumentValues(channelBuilderArg0);
                channelBuilderBean.setLazyInit(false);
                registry.registerBeanDefinition(channelBuilderBeanName, channelBuilderBean);
                LOGGER.info("Auto register bean [{}]", channelBuilderBeanName);
            }

            String channelBeanName = name + "Channel";
            if(!registry.containsBeanDefinition(channelBeanName) &&
               (config.getAutoRegisterChannelBean() == null ? basicRegChan : config.getAutoRegisterChannelBean())) {
                GenericBeanDefinition channelBean = new GenericBeanDefinition();
                channelBean.setBeanClass(Channel.class);
                channelBean.setFactoryBeanName(BEAN_NAME);
                channelBean.setFactoryMethodName("getChannel");
                channelBean.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
                ConstructorArgumentValues channelArg0 = new ConstructorArgumentValues();
                channelArg0.addIndexedArgumentValue(0, name);
                channelBean.setConstructorArgumentValues(channelArg0);
                channelBean.setLazyInit(false);
                channelBean.setDestroyMethodName("shutdown");
                registry.registerBeanDefinition(channelBeanName, channelBean);
                LOGGER.info("Auto register bean [{}]", channelBeanName);
            }
        });
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) {}

    @SuppressWarnings("unused")
    public Object getChannelBuilder(String name) {
        return applicationContext.getBean(DefaultGrpcClientAutoConfiguration.BEAN_NAME_CHANNEL_BUILDER_MAP, Map.class).get(name);
    }

    @SuppressWarnings("unused")
    public Object getChannel(String name) {
        return applicationContext.getBean(name + "ChannelBuilder", ManagedChannelBuilder.class).build();
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static final String BEAN_NAME = "defaultGrpcClientChannelRegistrar";

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGrpcClientChannelRegistrar.class);
}
