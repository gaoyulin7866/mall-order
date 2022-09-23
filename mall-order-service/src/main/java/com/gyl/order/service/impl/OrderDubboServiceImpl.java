package com.gyl.order.service.impl;

import com.gyl.order.dao.OrderMapper;
import com.gyl.order.dto.Order;
import com.gyl.order.service.OrderDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Service(
        version = "${demo.service.version}",
        group = "${demo.service.group}"
)
@Component
public class OrderDubboServiceImpl implements OrderDubboService {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public void cancelOrder(String orderNo, Integer userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if(order == null){
            log.error("订单不存在!");
            throw new RuntimeException("订单不存在!");
        }

        order.setOrderStatus(1);

        int i = orderMapper.updateByPrimaryKey(order);
        if (i < 1){
            log.error("订单取消失败!");
            throw new RuntimeException("订单取消失败!");
        }
    }


}
