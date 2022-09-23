package com.gyl.order.service;

public interface OrderDubboService {

    void cancelOrder(String orderNo, Integer userId);
}
