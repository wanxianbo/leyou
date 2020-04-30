package com.leyou.service.impl;

import com.leyou.service.IGoodsHtmlService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodHtmlServiceTest {

    @Autowired
    private IGoodsHtmlService iGoodsHtmlService;

    @Test
    public void testCreateHtml() {
        iGoodsHtmlService.createHtml(141l);
    }
}