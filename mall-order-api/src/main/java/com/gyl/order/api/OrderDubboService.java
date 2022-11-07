package com.gyl.order.api;


import com.gyl.order.vo.OrderVo;

import java.util.List;

public interface OrderDubboService {

    void cancelOrder(String orderNo, Integer userId);

    void create(String receiverName, String receiverMobile, String receiverAddress, Integer userId);

    List<OrderVo> list(Integer pageNum, Integer pageSize, Integer userId);

    OrderVo detail(String orderNo, Integer userId);

    void finishOrder(String orderNo, Integer userId);

    void createQrcode(String orderNo);

    void pay(String orderNo, Integer userId);

    List<OrderVo> listByAdmin(Integer pageNum, Integer pageSize);

    void delivered(String orderNo);
}
