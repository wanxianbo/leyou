package com.leyou.item.service;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;

import java.util.List;


public interface IGoodsService {
    PageResult<Spu> querySpuVoByPage(Integer page, Integer rows, Boolean saleable, String key);

    void saveGoods(Spu spu);//保存商品

    SpuDetail queryDetailById(Long spuId);

    List<Sku> querySkuBySpuId(Long id);

    void updateGoods(Spu spu);//更新商品

    void deleteGoods(Long spuId);//删除商品

    void updateSaleable(Long spuId);

    Spu querySpuById(Long spuId);

    List<Sku> querySkuBySpuIds(List<Long> ids);

    void decreaseStock(List<CartDTO> carts);
}
