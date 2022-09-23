package com.gyl.order.api;

public interface OrderDubboService {

    void cancelOrder(String orderNo, Integer userId);
}
