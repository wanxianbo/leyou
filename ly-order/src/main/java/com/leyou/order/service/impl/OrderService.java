package com.leyou.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.auth.entity.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.interceptors.LoginInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.service.IOrderService;
import com.leyou.order.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService implements IOrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private PayHelper payHelper;
    /**
     * 生成订单
     * @param order
     * @return
     */
    @Override
    @Transactional
    public Long createOrder(Order order) {
        //生成orderId
        long orderId = idWorker.nextId();
        //订单编号，基本信息
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        //获取userId
        UserInfo user = LoginInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);

        List<OrderDetail> orderDetails = order.getOrderDetails();
        // 订单金额
        Map<Long, Integer> numMap = orderDetails.stream().
                collect(Collectors.toMap(OrderDetail::getSkuId, OrderDetail::getNum));
        Set<Long> skuIds = numMap.keySet();
        List<Sku> skus = goodsClient.querySkuBySkuIds(new ArrayList<>(skuIds));
        long totalPay = 0l;
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * numMap.get(sku.getId());

        }
        order.setTotalPay(totalPay);
        order.setActualPay(totalPay + order.getPostFee() -0);
        //order写入数据库
        int count = orderMapper.insertSelective(order);
        log.debug("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());
        if (count != 1) {
            throw new LyException(ExceptionEnum.ORDER_CREATE_ERROR);
        }
        //新增订单详情
        //将订单id写入订单详情表
        orderDetails.forEach(detail -> detail.setOrderId(orderId));
        count = orderDetailMapper.insertList(orderDetails);
        if (count != orderDetails.size()) {
            throw new LyException(ExceptionEnum.ORDER_DETAIL_CREATE_ERROR);
        }
        //新增订单状态
        OrderStatus status = new OrderStatus();
        status.setOrderId(orderId);
        status.setCreateTime(order.getCreateTime());
        status.setStatus(OrderStatusEnum.UN_PAY.value());
        count = orderStatusMapper.insertSelective(status);
        if (count != 1) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_CREATE_ERROR);
        }
        //减库存
        List<CartDTO> carts = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            CartDTO cartDTO = new CartDTO();
            cartDTO.setSkuId(orderDetail.getSkuId());
            cartDTO.setNum(orderDetail.getNum());
            carts.add(cartDTO);
        }
        goodsClient.decreaseStock(carts);
        return orderId;
    }

    /**
     * 根据id查询订单
     *
     * @param id
     * @return
     */
    @Override
    public Order queryOrderById(Long id) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(detail);
        if (CollectionUtils.isEmpty(details)) {
            throw new LyException(ExceptionEnum.ORDER_DETAIL_FOUND);
        }
        //查询订单状态
        OrderStatus status = orderStatusMapper.selectByPrimaryKey(id);
        if (status == null) {
            throw new LyException(ExceptionEnum.ORDER_STATUS_NOT_FOUND);
        }
        order.setOrderDetails(details);
        order.setOrderStatus(status);
        return order;
    }

    /**
     * 生成微信支付链接
     * @param orderId
     * @return
     */
    @Override
    public String generatePayUrl(Long orderId) {
        // 根据订单id查询总价,描述
        Order order = queryOrderById(orderId);
        // 判断状态
        if (OrderStatusEnum.UN_PAY.value() != order.getOrderStatus().getStatus()) {
            throw new LyException(ExceptionEnum.ODER_STATUS_ERROR);
        }
        // 获取金额
        Long actualPay = order.getActualPay();
        // 商品描述
        OrderDetail detail = order.getOrderDetails().get(0);
        String url = payHelper.createOrder(orderId, actualPay, detail.getTitle());
        return url;
    }



    @Override
    @Transactional
    public Boolean updateStatus(Long id, int status) {
        OrderStatus record = new OrderStatus();
        record.setOrderId(id);
        record.setStatus(status);
        // 根据状态判断要修改的时间
        switch (status) {
            case 2:
                record.setPaymentTime(new Date());// 付款
                break;
            case 3:
                record.setConsignTime(new Date());// 发货
                break;
            case 4:
                record.setEndTime(new Date());// 确认收获，订单结束
                break;
            case 5:
                record.setCloseTime(new Date());// 交易失败，订单关闭
                break;
            case 6:
                record.setCommentTime(new Date());// 评价时间
                break;
            default:
                return null;
        }
        int count = orderStatusMapper.updateByPrimaryKeySelective(record);
        return count == 1;
    }

    @Override
    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status) {
        // 分页
        PageHelper.startPage(page, rows);
        //  获取userId
        UserInfo user = LoginInterceptor.getUser();
        List<Order> orders = orderMapper.queryOrderList(user.getId(), status);
        if (CollectionUtils.isEmpty(orders)) {
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }

        PageInfo<Order> pageInfo = new PageInfo<>(orders);

        return new PageResult<>(pageInfo.getTotal(),pageInfo.getList());
    }
}
