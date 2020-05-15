package com.leyou.item.service;

import com.leyou.common.dto.CartDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTest {

    @Autowired
    private IGoodsService goodsService;

    @Test
    public void decreaseStock() {
        List<CartDTO> list = new ArrayList<>();
        CartDTO cartDTO = new CartDTO();
        cartDTO.setSkuId(2600242l);
        cartDTO.setNum(2);
        CartDTO cartDTO1 = new CartDTO();
        cartDTO1.setSkuId(2600248l);
        cartDTO1.setNum(1);
        list.add(cartDTO);
        list.add(cartDTO1);

        goodsService.decreaseStock(list);
    }
}