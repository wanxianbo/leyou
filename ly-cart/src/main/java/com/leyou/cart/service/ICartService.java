package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;

import java.util.List;

public interface ICartService {
    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateCart(Long skuId, Integer num);

    void deleteCart(Long skuId);

    void addCartList(List<Cart> carts);
}
