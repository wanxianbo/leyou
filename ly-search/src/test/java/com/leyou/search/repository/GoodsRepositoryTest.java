package com.leyou.search.repository;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SearchService searchService;
    @Test
    public void testCreateIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void loadData() {
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            //查询商品的信息
            PageResult<Spu> pageResult = goodsClient.querySpuVoByPage(page, rows, true, null);
            List<Spu> spus = pageResult.getItems();
            if (CollectionUtils.isEmpty(spus)) {
                break;
            }
            size = spus.size();

            List<Goods> goodsList = new ArrayList<>();

            spus.forEach(spu -> {
                Goods goods = searchService.goodsBuilder(spu);
                goodsList.add(goods);
            });
            goodsRepository.saveAll(goodsList);
            page++;
        } while (size == 100);


    }
}