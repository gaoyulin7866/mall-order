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
