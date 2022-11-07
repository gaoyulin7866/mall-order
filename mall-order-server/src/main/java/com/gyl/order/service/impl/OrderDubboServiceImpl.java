package com.gyl.order.service.impl;

import com.google.zxing.WriterException;
import com.gyl.order.api.OrderDubboService;
import com.gyl.order.dao.OrderItemMapper;
import com.gyl.order.dao.OrderMapper;
import com.gyl.order.dto.Order;
import com.gyl.order.dto.OrderItem;
import com.gyl.order.vo.OrderItemVo;
import com.gyl.order.vo.OrderVo;
import com.gyl.shopping.api.CartDubboService;
import com.gyl.shopping.common.*;
import com.gyl.shopping.dto.Cart;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service(
        version = "${demo.service.version}",
        group = "${demo.service.group}"
)
@Component
public class OrderDubboServiceImpl implements OrderDubboService {

    @Value("${qrcode.ip}")
    private String qrcodeAddr;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Reference(version = "${demo.service.version}", group = "${demo.service.group}", check = false)
    private CartDubboService cartDubboService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(String receiverName, String receiverMobile, String receiverAddress, Integer userId) {

        List<com.gyl.shopping.dto.Cart> carts = cartDubboService.selectAllSelected(userId);
        if (carts.size() == 0){
            throw new MallException(ExceptionEnum.EMPTY_CART);
        }

        int totalPrice = 0;
        String orderNo = OrderNoUtil.generateNo();
        List<OrderItem> orderItems = new ArrayList<>();
        for (Cart cart : carts) {
            OrderItem orderItem = new OrderItem();
            com.gyl.shopping.dto.Product product = cartDubboService.selectByPrimaryKey(cart.getId());
            totalPrice += cart.getQuantity() * product.getPrice();
            orderItem.setCreateTime(new Date());
            orderItem.setProductId(product.getId());
            orderItem.setProductImg(product.getImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(product.getPrice() * cart.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setUpdateTime(new Date());
            orderItem.setOrderNo(orderNo);
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverMobile(receiverMobile);
        order.setReceiverName(receiverName);
        order.setOrderStatus(OrderStatus.ORDER_NOT_PAY.getCode());
        order.setPostage(10);
        order.setPaymentType(Constants.ONLINE_PAY);
        order.setPayTime(new Date());
        order.setCreateTime(new Date());
        orderMapper.insert(order);

        int i = orderItemMapper.batchInsert(orderItems);
        if (i < 0) {
            throw new MallException(ExceptionEnum.ORDER_ERROR);
        }

    }

    @Override
    public OrderVo detail(String orderNo, Integer userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if (order != null) {
            OrderVo orderVo = new OrderVo();
            orderVo.setCreateTime(order.getCreateTime());
            orderVo.setDeliveryTime(order.getDeliveryTime());
            orderVo.setEndTime(order.getEndTime());
            orderVo.setOrderNo(orderNo);
            orderVo.setOrderStatus(order.getOrderStatus());
            orderVo.setOrderStatusName(OrderStatus.getStatusByCode(order.getOrderStatus()).getDesc());
            orderVo.setPaymentType(order.getPaymentType());
            orderVo.setPayTime(order.getPayTime());
            orderVo.setPostage(order.getPostage());
            orderVo.setReceiverAddress(order.getReceiverAddress());
            orderVo.setReceiverMobile(order.getReceiverMobile());
            orderVo.setReceiverName(order.getReceiverName());
            orderVo.setTotalPrice(order.getTotalPrice());
            orderVo.setUserId(order.getUserId());
            orderVo.setOrderItemList(getOrderItemVos(orderNo));
            return orderVo;
        } else {
            throw new MallException(ExceptionEnum.ORDER_NOT_FOUND);
        }
    }

    @Override
    public void finishOrder(String orderNo, Integer userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if (order == null){
            throw new MallException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        order.setOrderStatus(OrderStatus.ORDER_FINALLY.getCode());

        int i = orderMapper.updateByPrimaryKey(order);
        if(i < 1){
            throw new MallException(ExceptionEnum.UPDATE_ERROR);
        }

    }

    @Override
    public void createQrcode(String orderNo) {
        if (StringUtils.isEmpty(orderNo)){
            throw new MallException(ExceptionEnum.PARAMS_ERROR);
        }
        try {
//            String path = "https://" + qrcodeAddr + "/" + System.currentTimeMillis() + ".png";
            QrcodeUtil.createQrcode(orderNo, "/Users/gaoyulin/Desktop/img/download/" + System.currentTimeMillis() + ".png", QrcodeUtil.width, QrcodeUtil.height);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pay(String orderNo, Integer userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if (order == null){
            throw new MallException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        order.setPayTime(new Date());
        order.setOrderStatus(OrderStatus.ORDER_HAS_PAY.getCode());

        int i = orderMapper.updateByPrimaryKey(order);
        if (i < 1) {
            throw new MallException(ExceptionEnum.PAY_ERROR);
        }
    }

    @Override
    public List<OrderVo> listByAdmin(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1){
            pageSize = 10;
        }
        Integer offset = (pageNum-1)*pageSize;
        List<Order> orders = orderMapper.selectByAdminPage(offset, pageSize);
        List<OrderVo> list = new ArrayList<>();
        for (Order order : orders) {
            OrderVo orderVo = new OrderVo();
            orderVo.setUserId(order.getUserId());
            orderVo.setTotalPrice(order.getTotalPrice());
            orderVo.setReceiverName(order.getReceiverName());
            orderVo.setReceiverMobile(order.getReceiverMobile());
            orderVo.setReceiverAddress(order.getReceiverAddress());
            orderVo.setPostage(order.getPostage());
            orderVo.setPayTime(order.getPayTime());
            orderVo.setPaymentType(order.getPaymentType());
            orderVo.setOrderStatusName(OrderStatus.getStatusByCode(order.getOrderStatus()).getDesc());
            orderVo.setOrderNo(order.getOrderNo());
            orderVo.setEndTime(order.getEndTime());
            orderVo.setDeliveryTime(order.getDeliveryTime());
            orderVo.setCreateTime(order.getCreateTime());
            orderVo.setOrderItemList(getOrderItemVos(order.getOrderNo()));
            list.add(orderVo);
        }
        return list;
    }

    @Override
    public void delivered(String orderNo) {
        if(StringUtils.isEmpty(orderNo)){
            throw new MallException(ExceptionEnum.PARAMS_ERROR);
        }

        Order order = orderMapper.selectByAdmin(orderNo);
        if (order == null) {
            throw new MallException(ExceptionEnum.NOT_PRODUCT);
        }

        order.setOrderStatus(OrderStatus.ORDER_HAS_RECEIVER.getCode());
        order.setPostage(30);
        order.setDeliveryTime(new Date());
        order.setUpdateTime(new Date());
        int i = orderMapper.updateByPrimaryKey(order);
        if (i < 1) {
            throw new MallException(ExceptionEnum.UPDATE_ERROR);
        }
    }

    @Override
    public List<OrderVo> list(Integer pageNum, Integer pageSize, Integer userId) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1){
            pageSize = 10;
        }
        Integer offset = (pageNum-1)*pageSize;
        List<Order> orders = orderMapper.selectByPage(offset, pageSize, userId);
        List<OrderVo> list = new ArrayList<>();
        for (Order order : orders) {
            OrderVo orderVo = new OrderVo();
            orderVo.setUserId(userId);
            orderVo.setTotalPrice(order.getTotalPrice());
            orderVo.setReceiverName(order.getReceiverName());
            orderVo.setReceiverMobile(order.getReceiverMobile());
            orderVo.setReceiverAddress(order.getReceiverAddress());
            orderVo.setPostage(order.getPostage());
            orderVo.setPayTime(order.getPayTime());
            orderVo.setPaymentType(order.getPaymentType());
            orderVo.setOrderStatusName(OrderStatus.getStatusByCode(order.getOrderStatus()).getDesc());
            orderVo.setOrderNo(order.getOrderNo());
            orderVo.setEndTime(order.getEndTime());
            orderVo.setDeliveryTime(order.getDeliveryTime());
            orderVo.setCreateTime(order.getCreateTime());
            orderVo.setOrderItemList(getOrderItemVos(order.getOrderNo()));
            list.add(orderVo);
        }
        return list;
    }

    public List<com.gyl.shopping.vo.OrderItemVo> getOrderItemVos(String orderNo) {
        List<com.gyl.shopping.vo.OrderItemVo> orderItemVoList = new ArrayList<>();
        List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
        for (OrderItem orderItem : orderItems) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(orderNo);
            orderItemVo.setProductImg(orderItem.getProductImg());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVo.setUnitPrice(orderItem.getUnitPrice());
            orderItemVoList.add(orderItemVo);
        }
        return orderItemVoList;
    }

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
