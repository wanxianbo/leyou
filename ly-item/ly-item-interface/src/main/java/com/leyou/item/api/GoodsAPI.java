package com.leyou.item.api;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface GoodsAPI {
    /**
     * 分页查询商品spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
   PageResult<Spu> querySpuVoByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,
            @RequestParam(name = "saleable",required = false) Boolean saleable,
            @RequestParam(name = "key",required = false) String key);

    /**
     * 根据spuId查询spu_detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id") Long spuId);

    /**
     * 根据spuId查询sku列表
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    @GetMapping("/sku/list/ids")
    List<Sku> querySkuBySkuIds(@RequestParam("ids") List<Long> ids);


    /**
     * 根据id查询spu
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    Spu querySpuById(@PathVariable("id") Long spuId);

    /**
     * 减库存
     * @param carts
     * @return
     */
    @PostMapping("stock/decrease")
    Void decreaseStock(@RequestBody List<CartDTO> carts);
}
