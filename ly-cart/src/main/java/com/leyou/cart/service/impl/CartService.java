package com.leyou.cart.service.impl;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.interceptors.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.ICartService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    //定义redis hash的key的前缀
    private static final String KEY_PREFIX = "cart:uid";

    /**
     * 加入购物车，写入redis
     * @param cart
     */
    @Override
    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //hashKey
        String hashKey = cart.getSkuId().toString();
        //定义商品数量
        Integer num = cart.getNum();
        //得到Operation对象
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //查询商品是否存在
        if (operation.hasKey(hashKey)) {
            //存在,数量改变
            cart = JsonUtils.parseBaen(operation.get(hashKey).toString(), Cart.class);
            //改变数量
            cart.setNum(cart.getNum() + num);
        }
        //写入redis
        operation.put(hashKey, JsonUtils.serialize(cart));
    }

    /**
     * 登录后将本地购物车加入到redis
     * @param carts
     */
    @Override
    public void addCartList(List<Cart> carts) {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //hashKey
        String hashKey = "";
        //得到Operation对象
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //map存入cart
        Map<String, String> map = new HashMap<>();
        for (Cart cart : carts) {
            //查询商品是否存在
            hashKey = cart.getSkuId().toString();
            if (operation.hasKey(hashKey)) {
                //存在,数量改变
                Cart c = JsonUtils.parseBaen(operation.get(hashKey).toString(), Cart.class);
                //改变数量
                cart.setNum(cart.getNum() + c.getNum());
            }
            map.put(hashKey,JsonUtils.serialize(cart));
        }
        //写入redis
        operation.putAll(map);
    }

    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<Cart> queryCarts() {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //判断redis是否存在key
        if (!redisTemplate.hasKey(key)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        //得到Operation对象
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //获取用户登录后的购物车的商品
        List<Cart> carts = operation.values().stream()
                .map(o -> JsonUtils.parseBaen(o.toString(), Cart.class))
                .collect(Collectors.toList());
        return carts;
    }

    @Override
    public void updateCart(Long skuId, Integer num) {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //hashKey
        String hashKey = skuId.toString();
        //得到Operation对象
        BoundHashOperations<String, Object, Object> operation = redisTemplate.boundHashOps(key);
        //判断是否存在hashKey
        if (!operation.hasKey(hashKey)) {
            throw new LyException(ExceptionEnum.CART_NOT_FOUND);
        }
        Cart cart = JsonUtils.parseBaen(operation.get(hashKey).toString(), Cart.class);
        //修改num
        cart.setNum(num);
        //写入redis
        operation.put(hashKey,JsonUtils.serialize(cart));
    }

    @Override
    public void deleteCart(Long skuId) {
        //获取用户信息
        UserInfo user = LoginInterceptor.getUser();
        //key
        String key = KEY_PREFIX + user.getId();
        //hashKey
        String hashKey = skuId.toString();
        //删除
        redisTemplate.opsForHash().delete(key, hashKey);
    }


}
