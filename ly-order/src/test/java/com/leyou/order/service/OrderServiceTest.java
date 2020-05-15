package com.leyou.order.service;


import com.leyou.order.pojo.Order;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private IOrderService orderService;

    @Test
    public void queryOrderById() {
        Order order = orderService.queryOrderById(1259695541667758080l);
        System.out.println(order);
    }

    @Test
    public void generateUrl() {
        String url = orderService.generatePayUrl(1259695541667758080l);
        System.out.println("url = " + url);
    }
}
