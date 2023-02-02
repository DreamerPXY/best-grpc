package com.pxyc.grpc.spring.boot.autoconfigure.server.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.pxyc.grpc.spring.boot.autoconfigure.server.GrpcServerLifecycleCallback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.math.BigInteger;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;


class DefaultGrpcServerNacosRegisterLifecycleCallback implements GrpcServerLifecycleCallback {
    private final String instanceId = UUID.randomUUID().toString();
    private final DefaultGrpcServerNacosRegisterConfigProps props;
    private final ThreadPoolExecutor executor;
    private Instance instance;
    @NacosInjected
    private NamingService namingService;

    public DefaultGrpcServerNacosRegisterLifecycleCallback(DefaultGrpcServerNacosRegisterConfigProps props) {
        this.props = props;
        executor = new ThreadPoolExecutor(0, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r, "NacosNamingServiceOperationThread");
                thread.setDaemon(false);
                return thread;
            }
        });
        executor.allowCoreThreadTimeOut(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            onStop();
            executor.shutdown();
            try {
                executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch(InterruptedException ignored) { }
            executor.shutdownNow();
        }, "Shutdown hook of [" + this + "]"));
    }

    @Override
    public synchronized void onStart(ServerInfo serverInfo) {
        if(instance != null || !props.isEnabled()) {
            return;
        }

        HashSet<InetSocketAddress> socketAddressSet = new HashSet<>();
        for(SocketAddress socketAddress : serverInfo.getGrpcServer().getListenSockets()) {
            if(socketAddress instanceof InetSocketAddress) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                InetAddress inetAddress = inetSocketAddress.getAddress();

                if(inetAddress.isAnyLocalAddress()) {
                    SYSTEM_INET_ADDRESS_LIST
                            .stream()
                            .filter(this::ipMatch)
                            .forEach(addr -> socketAddressSet.add(new InetSocketAddress(addr, inetSocketAddress.getPort())));
                } else if(ipMatch(inetAddress)) {
                    socketAddressSet.add(inetSocketAddress);
                }
            }
        }

        List<InetSocketAddress> publishList = applySelectPolicy(socketAddressSet);
        if(publishList.isEmpty()) {
            return;
        }

        instance = new Instance();
        instance.setInstanceId(instanceId);
        instance.setIp(publishList.get(0).getAddress().getHostAddress());
        instance.setPort(publishList.get(0).getPort());
        instance.setEphemeral(true);
        instance.setEnabled(true);
        instance.setHealthy(true);
        instance.setClusterName(props.getClusterName());
        instance.setServiceName(props.getServiceName());
        instance.setWeight(props.getWeight());
        instance.addMetadata(PreservedMetadataKeys.HEART_BEAT_INTERVAL, String.valueOf(props.getHeartBeatInterval().toMillis()));
        instance.addMetadata(PreservedMetadataKeys.HEART_BEAT_TIMEOUT, String.valueOf(props.getHeartBeatTimeout().toMillis()));
        instance.addMetadata(PreservedMetadataKeys.IP_DELETE_TIMEOUT, String.valueOf(props.getIpDeleteTimeout().toMillis()));

        final Instance finalInstance = instance;
        executor.submit(() -> {
            while(true) {
                try {
                    namingService.registerInstance(props.getServiceName(), props.getGroupName(), finalInstance);
                    break;
                } catch(NacosException e) {
                    LOGGER.error("Nacos registerInstance() error. Retry after [" + NACOS_RETRY_INTERVAL + "]", e);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(NACOS_RETRY_INTERVAL.toMillis());
                } catch(InterruptedException e) {
                    LOGGER.error("Sleep interrupted", e);
                }
            }
            LOGGER.info("Nacos Instance registered, " + finalInstance);
        });
    }

    @Override
    public synchronized void onStop() {
        if(instance == null) {
            return;
        }
        final Instance finalInstance = instance;
        executor.submit(() -> {
            while(true) {
                try {
                    namingService.deregisterInstance(props.getServiceName(), props.getGroupName(), finalInstance);
                    break;
                } catch(NacosException e) {
                    LOGGER.error("Nacos deregisterInstance() error. Retry after [" + NACOS_RETRY_INTERVAL + "]", e);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(NACOS_RETRY_INTERVAL.toMillis());
                } catch(InterruptedException e) {
                    LOGGER.error("Sleep interrupted", e);
                }
            }
            LOGGER.info("Nacos Instance deregistered, " + finalInstance);
        });
        instance = null;
    }

    private <T> List<T> applySelectPolicy(Collection<T> inList) {
        DefaultGrpcServerNacosRegisterConfigProps.AutoDetectIpConfig autoDetectIpConfig = props.getAutoDetectIp();
        switch(autoDetectIpConfig.getSelectPolicy()) {
            case EXACT_ONE:
                if(inList.size() != 1) {
                    throw new RuntimeException("Expect one, but [" + inList.size() + "] ip detected");
                }
                return ImmutableList.copyOf(inList);
            case PICK_NONE_OR_FIRST:
                return inList.isEmpty() ? Collections.emptyList() : ImmutableList.of(inList.iterator().next());
            case PICK_EXACT_FIRST:
                if(inList.isEmpty()) {
                    throw new RuntimeException("No ip detected");
                }
                return ImmutableList.of(inList.iterator().next());
            case AT_LEAST_ONE:
                if(inList.isEmpty()) {
                    throw new RuntimeException("No ip detected");
                }
            case ANY:
                return ImmutableList.copyOf(inList);
            default:
                throw new RuntimeException("Unknown ip match policy [" + autoDetectIpConfig.getSelectPolicy() + "]");
        }
    }

    private boolean ipMatch(InetAddress inetAddress) {
        DefaultGrpcServerNacosRegisterConfigProps.AutoDetectIpConfig autoDetectIpConfig = props.getAutoDetectIp();
        byte[] matchAddress;
        try {
            matchAddress = InetAddress.getByName(autoDetectIpConfig.getMatch()).getAddress();
        } catch(UnknownHostException e) {
            throw new RuntimeException("Ip match format error [" + autoDetectIpConfig.getMatch() + "]", e);
        }
        if(inetAddress.isLoopbackAddress()) {
            return false;
        }
        byte[] address = inetAddress.getAddress();
        return address.length == matchAddress.length && new BigInteger(1, address)
                .shiftRight(address.length * 8 - autoDetectIpConfig.getMaskBitLen())
                .equals(new BigInteger(1, matchAddress).shiftRight(matchAddress.length * 8 - autoDetectIpConfig.getMaskBitLen()));
    }

    private static final Duration NACOS_RETRY_INTERVAL = Duration.ofSeconds(5);
    private static final Logger LOGGER = getLogger(DefaultGrpcServerNacosRegisterLifecycleCallback.class);
    private static final ImmutableList<InetAddress> SYSTEM_INET_ADDRESS_LIST;
    static {
        Builder<InetAddress> inetAddressListbuilder = ImmutableList.builder();
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch(SocketException e) {
            throw new RuntimeException(e);
        }
        while(networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            for(InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                inetAddressListbuilder.add(interfaceAddress.getAddress());
            }
        }
        SYSTEM_INET_ADDRESS_LIST = inetAddressListbuilder.build();
    }
}
