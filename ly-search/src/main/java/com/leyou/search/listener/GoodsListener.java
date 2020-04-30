package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private SearchService searchService;

    /**
     * 监听商品的增改
     * @param id 商品spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.create.index.queue",durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item.insert","item.update"}))
    public void listenCreateOrUpdate(Long id) {
        if (id == null) {
            return;
        }
        //创建索引库
        searchService.createIndex(id);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.delete.index.queue",durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"
            ),
            key = "item.delete"
    ))
    public void listenDelete(Long id) {
        if (id == null) {
            return;
        }
        //删除索引库
        searchService.deleteIndex(id);
    }
}
