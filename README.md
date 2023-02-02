
# best-grpc

***grpc的Java版本最佳实践，结合springboot-starter快速开发***


> ***注意：***
>
> 强依赖springboot,请不要在无springboot环境使用
>
> 默认开启日志打印、熔断降级等微服务功能，不需要功能请再配置文件显示指定，具体配置请看文档
>
> 无任何代码侵入，类似Dubbo开发模式，轻松上手，让grpc用户只需要关注业务逻辑

## 0. best-grpc 介绍

best-grpc 希望能成为grpc Java用户的最佳实践，grpc框架性能高、跨语言，几乎成为了多语言系统中rpc协议的唯一选择。
但在Java环境中使用grpc成本并不小，甚至相比其他rpc框架使用起来并不方便。best-grpc-springboot-starter因此产生，
它将大大降低grpc的使用成本，让开发人员只用关注业务逻辑，且对代码无任何侵入
使用 best-grpc 的好处：

* 轻松获得支撑千万日活服务的稳定性
* 内建级联超时控制、限流、自适应熔断、自适应降载等微服务治理能力，无需配置和额外代码
* 微服务治理中间件可无缝集成到其它现有框架使用
* 结合gradle、maven plugin 一键生成代码


![架构图](https://raw.githubusercontent.com/zeromicro/zero-doc/main/doc/images/architecture.png)


## 1. best-grpc 框架设计思考

rpc协议方面，在云原生时代，grpc协议基本已经一统江湖了。所以没有必要再去重复造轮子，写一个xrpc。
grpc更关注于rpc多语言协议。虽然内置了一些微服务治理能力，但是基本都需要进行改造后才能应用到生产环境。
所以best-grpc应运而生。


## 3. best-grpc 项目实现和特点

best-grpc 有如下主要特点：

* 强大的工具支持，尽可能少的代码编写
* 极简的接口
* 支持中间件，方便扩展
* 高性能
* 面向故障编程，弹性设计
* 内建服务发现、负载均衡
* 内建限流、熔断、降载，且自动触发，自动恢复
* 链路跟踪、统计报警等

如下图，我们从多个层面保障了整体服务的高可用：

![弹性设计](https://raw.githubusercontent.com/zeromicro/zero-doc/main/doc/images/resilience.jpg)

## 4. 我们使用 best-grpc 的基本架构图



## 5. Quick Start


Gradle依赖:
```kotlin
implementation("com.pxyc.fun:best-grpc-spring-boot-starter:${version}")
```
client:

application.properties 或通过其他方式配置属性:
```properties
# 启用 gRPC Client 相关功能
best.grpc.client.enabled=true

# 注册并配置一个 gRPC Client Channel 的 Spring bean
# 配置格式为 best.grpc.client.channels.${target名称}.xxx=xxx
# .target=xds:///tj-greeter.default.wifi 表示以 xds 协议发现 tj-greeter 服务
# (关于 Channel 和 target 等概念和配置, 详见 gRPC 官方文档和本项目源码实现)
best.grpc.client.channels.apGreeter.target=xds:///tj-greeter.default.rpc
# 支持直连
#best.grpc.client.channels.apGreeter.target=localhost:8081
```

gRPC Client 使用:
```java
@Component
public class SomeClient {
    @Resource
    private Channel apGreeterChannel; // bean名为: ${target名}Channel

    public String sayHello(String name) {
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply reply = GreeterGrpc.newBlockingStub(apGreeterChannel).sayHello(request);
        return reply.getWords();
    }
}
```
server:
application.properties 或通过其他方式属性配置:
```properties
# 启用 gRPC Server 相关功能
best.grpc.server.enabled=true
```

如果要开启 nacos 服务注册功能, 则加入如下配置:
```properties
# nacos 服务器配置 (关于 nacos 配置详官方文档)
nacos.discovery.server-addr=127.0.0.1:8848

# 启用 nacos 自动服务注册
best.grpc.server.nacos.enabled=true

# 配置服务注册名称, 将在 nacos 中以此名称注册服务 (关于 nacos 配置详官方文档)
best.grpc.server.nacos.service-name=tj-greeter
```

服务接口实现:
```java
@Service
public class GreeterServiceImpl extends GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        String name = request.getName();
        HelloReply reply = HelloReply.newBuilder().setWords("Hello " + name).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
```