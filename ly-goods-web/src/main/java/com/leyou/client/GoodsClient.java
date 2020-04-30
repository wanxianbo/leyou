package com.leyou.client;

import com.leyou.item.api.GoodsAPI;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "item-service")
public interface GoodsClient extends GoodsAPI {
}
