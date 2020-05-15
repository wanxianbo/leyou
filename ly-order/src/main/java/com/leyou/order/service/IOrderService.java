package com.leyou.order.service;

import com.leyou.common.vo.PageResult;
import com.leyou.order.pojo.Order;

import java.util.Map;

public interface IOrderService {
    Long createOrder(Order order);

    Order queryOrderById(Long id);

    String generatePayUrl(Long orderId);

    Boolean updateStatus(Long orderId, int i);

    PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status);
}
