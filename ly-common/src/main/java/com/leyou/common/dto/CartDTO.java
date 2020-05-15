package com.leyou.common.dto;

import lombok.Data;

@Data
public class CartDTO {
    private Long skuId;// 商品skuId
    private Integer num; // 购买数量
}
