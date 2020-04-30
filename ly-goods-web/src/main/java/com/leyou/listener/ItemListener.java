package com.leyou.listener;


import com.leyou.service.IGoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {

    @Autowired
    private IGoodsHtmlService htmlService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.create.web.queue", durable = "true"),
            exchange = @Exchange(
                    name = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item.insert","item.update"}
    ))
    public void listenCreateOrUpdate(Long id) {
        if (id == null) {
            return;
        }
        //创建、修改静态页面
        htmlService.createHtml(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.delete.web.queue", durable = "true"),
            exchange = @Exchange(
                    name = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = "item.delete"
    ))
    public void listenDelete(Long id) {
        if (id == null) {
            return;
        }
        //创建、修改静态页面
        htmlService.deleteHtml(id);
    }
}
