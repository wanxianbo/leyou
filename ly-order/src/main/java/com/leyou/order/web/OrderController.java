package com.leyou.order.web;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.order.pojo.Order;
import com.leyou.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 生成订单
     * @param order
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    /**
     * 根据id查询订单信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResult<Order>> queryUserOrderList(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "status", required = false) Integer status
    ){
        PageResult<Order> result = orderService.queryUserOrderList(page, rows, status);
        return ResponseEntity.ok(result);
    }
    /**
     * 生成微信支付链接
     * @param orderId
     * @return
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> generatePayUrl(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.generatePayUrl(orderId));
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<Boolean> queryPayState(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        Boolean boo = orderService.updateStatus(id, status);
        if (boo == null) {
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> queryPayState(@PathVariable("id") Long orderId) {
        int payState = orderService.queryOrderById(orderId).getOrderStatus().getStatus();
        return ResponseEntity.ok(payState);
    }

}
