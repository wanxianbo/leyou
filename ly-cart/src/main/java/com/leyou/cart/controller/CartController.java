package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    /**
     * 加入单个购物车，写入redis
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 登录后将本地购物车加入到redis
     * @param carts 购物车的list
     */
    @PostMapping("/carts")
    public ResponseEntity<Void> addCartList(@RequestBody List<Cart> carts) {
        cartService.addCartList(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> queryCarts() {
        return ResponseEntity.ok(cartService.queryCarts());
    }

    /**
     * 修改购物车数量
     * @param cart
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCart(@RequestBody Cart cart) {
        cartService.updateCart(cart.getSkuId(),cart.getNum());
        return ResponseEntity.status(HttpStatus.CREATED.NO_CONTENT).build();
    }

    /**
     * 删除购物车
     * @param skuId
     * @return
     */
    @DeleteMapping({"{skuId}"})
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId) {
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
