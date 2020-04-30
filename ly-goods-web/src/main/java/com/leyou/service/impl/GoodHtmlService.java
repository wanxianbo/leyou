package com.leyou.service.impl;

import com.leyou.config.DestPathProperties;
import com.leyou.service.IGoodsHtmlService;
import com.leyou.service.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Service
@Slf4j
@EnableConfigurationProperties(DestPathProperties.class)
public class GoodHtmlService implements IGoodsHtmlService {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private DestPathProperties prop;
    @Override
    public void createHtml(Long spuId) {
        //获取页面数据
        Map<String, Object> map = goodsService.loadModel(spuId);
        //创建context上下文对象
        Context context = new Context();
        //将数据赋给上下文域中
        context.setVariables(goodsService.loadModel(spuId));
        //创建write输出流
        File file = getDestFile(spuId);
        //如果文件存在
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            templateEngine.process("item",context,writer);
        } catch (Exception e) {
            log.error("[页面静态化异常]", e);
        }
        //执行页面静态化
    }

    /**
     * 删除静态页面
     * @param spuId
     */
    @Override
    public void deleteHtml(Long spuId) {
        File dest = getDestFile(spuId);
        if (dest.exists()) {
            dest.delete();
        }
    }

    private File getDestFile(Long spuId) {
        File dest = new File(prop.getDestPath());
        if (!dest.exists()) {
            dest.mkdirs();
        }
        return new File(dest, spuId + ".html");
    }
}
