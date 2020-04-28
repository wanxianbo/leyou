package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    /**
     * 分页查询商品spu
     * @param page
     * @param rows
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuVoByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,
            @RequestParam(name = "saleable",required = false) Boolean saleable,
            @RequestParam(name = "key",required = false) String key) {
        //http://api.leyou.com/api/item/spu/page?key=&saleable=true&page=1&rows=5
        return ResponseEntity.ok(goodsService.querySpuVoByPage(page,rows,saleable,key));
    }

    /**
     * 根据spuId查询spu_detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable("id") Long spuId) {
        SpuDetail spuDetail =goodsService.queryDetailById(spuId);
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuId查询sku列表
     * @param id
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id) {
        List<Sku> list = goodsService.querySkuBySpuId(id);
        return ResponseEntity.ok(list);
    }

    /**
     * 新增商品
     * @param spu
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu) {
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品
     * @param spu
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu) {
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spuId删除商品
     * @param spuId
     * @return
     */
    @DeleteMapping("/goods/{id}")
    public ResponseEntity<Void> deleteGoods(@PathVariable("id") Long spuId) {
        goodsService.deleteGoods(spuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/goods/saleable/{id}")
    public ResponseEntity<Void> updateSaleable(@PathVariable("id") Long spuId) {
        goodsService.updateSaleable(spuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
