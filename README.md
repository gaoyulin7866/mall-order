# 版本
```
 - java-1.8
 - mysql-8.0.26
 - zookeeper-3.6.3
 - redis-7.0.0
```

# 文件目录

```
- mall-order
 - mall-order-api
   - pom.xml
   - com.gyl.order
     - api
       - OrderDubboService // 给mall提供dubbo服务调用
     - dto
       - Order
       - OrderItem
     - vo
       - OrderVo
       - OrderItemVo
 - mall-order-server
   - pom.xml
   - resources
     - config
       - local.properties // 本地启动服务配置 暂未上传
       - online.properties// 阿里云启动服务配置 暂未上传
     - mappers
       - ...
     - application.properties // 暂未上传
     - generatorConfig.xml // 生成mappers等配置 暂未上传
   - com.gyl.order
     - dao
       - ...
     - service
       - impl
         - OrderDubboServiceImpl  // 实现订单逻辑代码
     - MallOrderApplication
 - shopping-service
   - pom.xml
 - pom.xml
```
