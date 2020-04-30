package com.leyou.service.impl;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import com.leyou.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsService implements IGoodsService {

    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Override
    public Map<String, Object> loadModel(Long spuId) {
        Map<String, Object> map = new HashMap<>();
       /* const a = *//*[[${groups}]]*//* [];
        const b = *//*[[${paramMap}]]*//* [];
        const c = *//*[[${categories}]]*//* [];
        const d = *//*[[${spu}]]*//* {};
        const e = *//*[[${spuDetail}]]*//* {};
        const f = *//*[[${skus}]]*//* [];
        const g = *//*[[${brand}]]*//* {};*/
        //根据id查询spu对象
        Spu spu = goodsClient.querySpuById(spuId);
        //根据spuId查询sku集合
        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);
        //查询spuDetail
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询categories
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询规格组
        List<SpecGroup> groups = specificationClient.queryGroupAndParamByCid(spu.getCid3());
        // 查询特殊的规格参数
        List<SpecParam> params = specificationClient.querySpecParams(null, spu.getCid3(), false, null);
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(specParam -> {
            paramMap.put(specParam.getId(), specParam.getName());
        });
        //封装数据
        map.put("spu", spu);
        map.put("spuDetail", spuDetail);
        map.put("skus", skus);
        map.put("brand", brand);
        map.put("categories", categories);
        map.put("groups", groups);
        map.put("paramMap", paramMap);
        return map;
    }
}
