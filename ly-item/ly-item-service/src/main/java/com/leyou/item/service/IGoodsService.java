package com.leyou.item.service;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;


public interface IGoodsService {
    PageResult<Spu> querySpuVoByPage(Integer page, Integer rows, Boolean saleable, String key);

    void saveGoods(Spu spu);

    SpuDetail queryDetailById(Long spuId);

    List<Sku> querySkuBySpuId(Long id);

    void updateGoods(Spu spu);

    void deleteGoods(Long spuId);

    void updateSaleable(Long spuId);

    Spu querySpuById(Long spuId);
}
